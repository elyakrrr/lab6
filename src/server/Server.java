package server;

import server.handler.RequestHandler;
import shared.network.Request;
import shared.network.Response;
import shared.network.PacketSplitter;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final int MAX_PACKET_SIZE = 65507;
    private static final int PORT = 8080;

    private final DatagramChannel channel;
    private final RequestHandler requestHandler;
    private final Selector selector;
    private final Scanner consoleScanner;
    private volatile boolean running;

    public Server(RequestHandler requestHandler) throws IOException {
        this.requestHandler = requestHandler;
        this.consoleScanner = new Scanner(System.in);
        this.running = true;

        this.channel = initializeChannel();
        this.selector = initializeSelector();

        logger.info("Сервер запущен на порту " + PORT);
        System.out.println("Сервер запущен");
    }

    private DatagramChannel initializeChannel() throws IOException {
        DatagramChannel ch = DatagramChannel.open();
        ch.configureBlocking(false);
        ch.socket().bind(new InetSocketAddress(PORT));
        ch.socket().setReceiveBufferSize(1024 * 1024);
        ch.socket().setSendBufferSize(1024 * 1024);
        return ch;
    }

    private Selector initializeSelector() throws IOException {
        Selector sel = Selector.open();
        channel.register(sel, SelectionKey.OP_READ);
        return sel;
    }

    public void start() {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKET_SIZE);

        while (running) {
            try {
                handleConsoleInput();
                waitForActivities();
                processSelectedKeys(buffer);
                clearSelectedKeys();
            } catch (IOException e) {
                if (running) {
                    logger.severe("Ошибка: " + e.getMessage());
                }
            }
        }

        logger.info("Основной цикл сервера завершён");
    }

    private void handleConsoleInput() {
        try {
            if (System.in.available() > 0) {
                String input = consoleScanner.nextLine().trim().toLowerCase();
                processConsoleCommand(input);
            }
        } catch (IOException e) {
            logger.warning("Ошибка чтения консоли: " + e.getMessage());
        }
    }

    private void processConsoleCommand(String command) {
        if (command.equals("save")) {
            saveCollection();
        } else if (!command.isEmpty()) {
            System.out.println("Неизвестная команда. Доступные: save");
        }
    }

    private void waitForActivities() throws IOException {
        if (selector.isOpen()) {
            selector.select(100);
        }
    }

    private void processSelectedKeys(ByteBuffer buffer) throws IOException {
        for (SelectionKey key : selector.selectedKeys()) {
            if (key.isReadable() && key.channel() == channel) {
                handleClientRequest(buffer);
            }
        }
    }

    private void handleClientRequest(ByteBuffer buffer) throws IOException {
        buffer.clear();
        SocketAddress clientAddress = channel.receive(buffer);

        if (clientAddress != null) {
            buffer.flip();
            byte[] receivedData = new byte[buffer.remaining()];
            buffer.get(receivedData);
            processRequest(receivedData, clientAddress);
        }
    }

    private void clearSelectedKeys() {
        selector.selectedKeys().clear();
    }

    private void saveCollection() {
        try {
            if (requestHandler != null) {
                requestHandler.saveCollection();
                System.out.println("Коллекция сохранена");
                logger.info("Коллекция сохранена по команде save");
            }
        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            logger.severe("Ошибка сохранения: " + e.getMessage());
        }
    }

    private void processRequest(byte[] data, SocketAddress clientAddress) throws IOException {
        try (ObjectInputStream objectInputStream = createObjectInputStream(data)) {
            Request request = (Request) objectInputStream.readObject();
            logger.info("Обработка команды: " + request.getCommandName() + " от " + clientAddress);

            Response response = requestHandler.handle(request);
            sendResponse(response, clientAddress);

        } catch (ClassNotFoundException e) {
            logger.severe("Ошибка десериализации: " + e.getMessage());
            Response errorResponse = new Response.Builder()
                    .success(false)
                    .message("Ошибка формата запроса")
                    .build();
            sendResponse(errorResponse, clientAddress);
        } catch (Exception e) {
            logger.severe("Ошибка обработки запроса: " + e.getMessage());
            Response errorResponse = new Response.Builder()
                    .success(false)
                    .message("Внутренняя ошибка сервера: " + e.getMessage())
                    .build();
            sendResponse(errorResponse, clientAddress);
        }
    }

    private ObjectInputStream createObjectInputStream(byte[] data) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        return new ObjectInputStream(byteArrayInputStream) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                try {
                    return Class.forName(desc.getName(), false, Thread.currentThread().getContextClassLoader());
                } catch (ClassNotFoundException e) {
                    return super.resolveClass(desc);
                }
            }
        };
    }

    private void sendResponse(Response response, SocketAddress clientAddress) throws IOException {
        List<byte[]> packets = PacketSplitter.split(response);

        sendAllPackets(packets, clientAddress);
        sendEndMarker(clientAddress);

        logger.info("Ответ отправлен клиенту " + clientAddress + " (" + packets.size() + " пакетов)");
    }

    private void sendAllPackets(List<byte[]> packets, SocketAddress clientAddress) throws IOException {
        for (byte[] packetData : packets) {
            ByteBuffer buffer = ByteBuffer.wrap(packetData);
            channel.send(buffer, clientAddress);
        }
    }

    private void sendEndMarker(SocketAddress clientAddress) throws IOException {
        ByteBuffer endMarker = ByteBuffer.wrap(new byte[]{0});
        channel.send(endMarker, clientAddress);
    }

    public void stop() {
        running = false;
        try {
            selector.wakeup();
        } catch (Exception e) {
            logger.severe("Ошибка при остановке: " + e.getMessage());
        }
        logger.info("Сервер остановлен");
    }

    public void close() {
        try {
            if (selector != null && selector.isOpen()) {
                selector.close();
            }
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        } catch (IOException e) {
            logger.severe("Ошибка при закрытии ресурсов: " + e.getMessage());
        }
    }
}