package example11;

import java.io.*;
import java.net.*;
import java.util.Date;

public class hw_11_1_S {
    public static final int PORT = 9;
    public static final int MAX_PACKET_SIZE = 65508;
    public static void main(String[] args) throws IOException {
        DatagramSocket socket;
        socket = new DatagramSocket(PORT);
        byte[] buffer = new byte[MAX_PACKET_SIZE];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                InetAddress client_address = packet.getAddress();
                int client_port = packet.getPort();

                Date now = new Date();
                String timeString = now.toString();
                byte[] timeData = timeString.getBytes();

                DatagramPacket response = new DatagramPacket(timeData, timeData.length, client_address, client_port);
                socket.send(response);
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
        }
    }
}
