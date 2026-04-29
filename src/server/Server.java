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
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.channel.socket().bind(new InetSocketAddress(PORT));
        this.channel.socket().setReceiveBufferSize(1024 * 1024);
        this.channel.socket().setSendBufferSize(1024 * 1024);

        this.selector = Selector.open();
        this.channel.register(selector, SelectionKey.OP_READ);

        this.consoleScanner = new Scanner(System.in);
        this.running = true;

        logger.info("Сервер запущен на порту " + PORT);
        System.out.println("Сервер запущен");
    }

    public void start() {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKET_SIZE);

        while (running) {
            try {
                if (System.in.available() > 0) {
                    String input = consoleScanner.nextLine().trim().toLowerCase();
                    if (input.equals("save")) {
                        saveCollection();
                    } else if (!input.isEmpty()) {
                        System.out.println("Неизвестная команда. Доступные: save");
                    }
                }

                if (selector.isOpen()) {
                    selector.select(100);
                } else {
                    break;
                }

                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isReadable()) {
                        buffer.clear();
                        SocketAddress clientAddress = channel.receive(buffer);

                        if (clientAddress != null) {
                            buffer.flip();
                            byte[] receivedData = new byte[buffer.remaining()];
                            buffer.get(receivedData);
                            processRequest(receivedData, clientAddress);
                        }
                    }
                }

                selector.selectedKeys().clear();

            } catch (IOException e) {
                if (running) {
                    logger.severe("Ошибка: " + e.getMessage());
                }
            }
        }

        logger.info("Основной цикл сервера завершён");
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
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream) {
                 @Override
                 protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                     try {
                         return Class.forName(desc.getName(), false, Thread.currentThread().getContextClassLoader());
                     } catch (ClassNotFoundException e) {
                         return super.resolveClass(desc);
                     }
                 }
             }) {

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

    private void sendResponse(Response response, SocketAddress clientAddress) throws IOException {
        List<byte[]> packets = PacketSplitter.split(response);

        for (byte[] packetData : packets) {
            ByteBuffer buffer = ByteBuffer.wrap(packetData);
            channel.send(buffer, clientAddress);
        }

        ByteBuffer endMarker = ByteBuffer.wrap(new byte[]{0});
        channel.send(endMarker, clientAddress);

        logger.info("Ответ отправлен клиенту " + clientAddress + " (" + packets.size() + " пакетов)");
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