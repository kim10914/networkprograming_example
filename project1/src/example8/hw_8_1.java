package example8;

import java.io.*;
import java.net.*;

public class hw_8_1 {
    public static void main(String[] args) {
        ServerSocket serverSocket = null; // 소켓 선언

        try {
            serverSocket = new ServerSocket(7);// 소켓 객체 생성
            serverSocket.setReuseAddress(true); // 주소 재사용 메서드 참으로 설정
            System.out.println("서버 시작 포트: 7 ");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // 클라이언트 접속 대기
                System.out.println("클라이언트 연결됨: " + clientSocket.getInetAddress()); // 상대 IP주소 가져와서 출력

                new EchoClientHandler(clientSocket).start(); // 클라이언트 처리 전용 쓰레드 run
            }
        } catch (IOException e) {
            System.err.println(e.getMessage()); // 입출력 예외 출력
        } finally {
            try {
                if (serverSocket != null) // 소켓이 비어 있지 않으면
                    serverSocket.close(); // 소켓 닫기
            } catch (IOException e) {
                System.err.println(e.getMessage()); // 입출력 예외 출력
            }
        }
    }
}

class EchoClientHandler extends Thread { // 사용자 쓰레드 클래스
    private Socket socket; // 소켓 맴버 변수

    public EchoClientHandler(Socket socket) { // 생성자
        this.socket = socket;
    }

    public void run() { // start 호출 했을 때 런
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 버퍼 리더 선언
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // 버퍼 라이터 선언
        ) {
            String line; // 사용자 문자열 저장
            while ((line = reader.readLine()) != null) { // 리드라인을 통해 입력 받음
                System.out.println(socket.getInetAddress() + line); // 사용자 IP와 문자열 출력
                writer.write(line + "\r\n"); // 클라이언트 입력을 그대로 다시 보냄
                writer.flush(); // 플러쉬
            }
        } catch (IOException e) {
            System.err.println("클라이언트 통신 오류: " + e.getMessage());
        } finally {
            try {
                if (socket != null) // 소켓이 안 비었으면
                    socket.close(); // 닫기
            } catch (IOException e) {
                System.err.println("클라이언트 소켓 종료 오류: " + e.getMessage());
            }
        }
    }
}
