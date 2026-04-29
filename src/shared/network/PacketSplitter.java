package shared.network;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PacketSplitter {
    private static final int MAX_PACKET_SIZE = 64000;

    public static List<byte[]> split(Response response) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
        }

        byte[] fullData = byteArrayOutputStream.toByteArray();
        List<byte[]> packets = new ArrayList<>();

        for (int i = 0; i < fullData.length; i += MAX_PACKET_SIZE) {
            int end = Math.min(i + MAX_PACKET_SIZE, fullData.length);
            byte[] packet = new byte[end - i + 1];
            packet[0] = (byte) (i / MAX_PACKET_SIZE);
            System.arraycopy(fullData, i, packet, 1, end - i);
            packets.add(packet);
        }

        return packets;
    }

    public static Response join(List<byte[]> packets) throws IOException, ClassNotFoundException {
        int totalSize = 0;
        for (byte[] p : packets) {
            totalSize += p.length - 1;
        }

        byte[] fullData = new byte[totalSize];
        int offset = 0;
        for (byte[] p : packets) {
            System.arraycopy(p, 1, fullData, offset, p.length - 1);
            offset += p.length - 1;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fullData);
        try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (Response) objectInputStream.readObject();
        }
    }
}