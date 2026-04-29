package client;

import shared.model.Person;
import shared.network.PacketSplitter;
import shared.network.Request;
import shared.network.Response;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static final int MAX_PACKET_SIZE = 65507;
    private static final int TIMEOUT = 5000;
    private static final int MAX_RETRIES = 3;

    private final String host;
    private final int port;
    private final DatagramSocket socket;

    public Client(String host, int port) throws SocketException {
        this.host = host;
        this.port = port;
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(TIMEOUT);
        this.socket.setReceiveBufferSize(1024 * 1024);
        this.socket.setSendBufferSize(1024 * 1024);
    }

    public Response sendRequest(String commandName, String[] args, Person person) throws IOException, ClassNotFoundException {
        Request request = new Request(commandName, args, person);

        byte[] sendData = serialize(request);

        if (sendData.length > MAX_PACKET_SIZE) {
            throw new IOException("Размер запроса (" + sendData.length + " байт) превышает максимальный размер UDP пакета");
        }

        DatagramPacket sendPacket = new DatagramPacket(
                sendData,
                sendData.length,
                InetAddress.getByName(host),
                port
        );

        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                socket.send(sendPacket);
                return receiveResponse();

            } catch (SocketTimeoutException e) {
                retries++;
                System.err.println("Таймаут, попытка " + retries + " из " + MAX_RETRIES);
                if (retries == MAX_RETRIES) {
                    throw new IOException("Сервер не отвечает после " + MAX_RETRIES + " попыток");
                }
            }
        }

        throw new IOException("Не удалось отправить запрос");
    }

    private Response receiveResponse() throws IOException, ClassNotFoundException {
        List<byte[]> receivedPackets = new ArrayList<>();

        while (true) {
            byte[] receiveData = new byte[MAX_PACKET_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            byte[] actualData = new byte[receivePacket.getLength()];
            System.arraycopy(receivePacket.getData(), 0, actualData, 0, receivePacket.getLength());

            if (actualData.length == 1 && actualData[0] == 0) {
                break;
            }

            receivedPackets.add(actualData);
        }

        return PacketSplitter.join(receivedPackets);
    }

    private byte[] serialize(Request request) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
        }
        return byteArrayOutputStream.toByteArray();
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}