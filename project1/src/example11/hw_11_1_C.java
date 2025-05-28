package example11;

import java.io.*;
import java.net.*;

public class hw_11_1_C {
    public final static int DAYTIME_PORT = 9;
    public final static int BUFFER_SIZE = 65508;
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");

            // 빈 요청 패킷 전송
            byte[] emptyData = new byte[1];
            DatagramPacket request = new DatagramPacket(
                    emptyData,
                    emptyData.length,
                    serverAddress,
                    DAYTIME_PORT
            );
            socket.send(request);

            // 응답 수신
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            String timeString = new String(response.getData(), 0, response.getLength());
            System.out.println("서버로부터 받은 시간: " + timeString);

        } catch (IOException e) {
            System.out.println("IOException: " + e);
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
