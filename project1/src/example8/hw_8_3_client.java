package example8;

import java.io.*;
import java.net.*;

public class hw_8_3_client {
    public static void main(String[] args) {
        String serverIp = "localhost"; // 연결 호스트
        int port = 3000; // 포트
        String filePath = "example4_11_1.txt"; // 전송할 원본 파일
        String saveAs = "example4_11_1.zip"; // 저장할 압축 파일 이름

        try (
                Socket socket = new Socket(serverIp, port); //소켓 열고
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream())); // 출력
                DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream())); // 입력
        ) {
            File file = new File(filePath); // 파일 객체 생성
            if (!file.exists()) { // 파일 있음?
                System.out.println("전송할 파일이 존재하지 않습니다."); // 힝ㅠ
                return; // 종료
            }

            dos.writeUTF(file.getName()); // 파일 이름 받아서 전송
            dos.flush(); // 버퍼 비우기

            // 파일 내용 전달
            try (FileInputStream fis = new FileInputStream(file)) { // 파일 읽고
                byte[] buffer = new byte[4096]; // 4KB
                int len; // 문자열
                while ((len = fis.read(buffer)) != -1) { // 파일 끝까지 읽고
                    dos.write(buffer, 0, len); // 쓰기
                }
                dos.flush(); // 비우기
                socket.shutdownOutput(); // 출력 채널 내리기 메서드 호출
            }

            // 압축된 파일 수신
            try (FileOutputStream fos = new FileOutputStream(saveAs)) { // hw_7_1.zip 이름 출력 스트림으로 인수 받음
                byte[] buffer = new byte[4096]; 
                int len;
                while ((len = dis.read(buffer)) != -1) { // 읽은 내용 
                    fos.write(buffer, 0, len); // 압축파일 작성
                }
            }

            System.out.println(" 압축파일 생성 : " + saveAs);

        } catch (IOException e) {
            System.err.println("클라이언트 오류: " + e.getMessage());
        }
    }
}
