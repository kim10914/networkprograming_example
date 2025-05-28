package example8;

import java.io.*;
import java.net.*;
import java.util.zip.*; // 압축 클래스 호출

public class hw_8_3_server {
    public static void main(String[] args) throws IOException {
        int port = 3000; // 포트
        ServerSocket server = new ServerSocket(port); // 소켓
        System.out.println("압축 서버 열림 : " + port);

        while (true) {
            Socket socket = server.accept(); // 접속 기다림
            System.out.println("클라이언트 접속: " + socket.getInetAddress());

            new Thread(() -> handleClient(socket)).start(); // 쓰레드 생성해서 handleClient()메서드를 전달함 쓰레드run시 전달된 메서드 호출(비동기 처리)

//            new Thread(new Runnable() { // 쓰레드 생성 Runnable 인터페이스 구현 객체를 넣어 run시 메서드를 실행할 수 있음
//                public void run() { 
//                    handleClient(socket); // 메서드 호출
//                }
//            }); // 람다식 안쓴 버전
        }
    }
    // 단일 파일 압축 메서드
    private static void handleClient(Socket socket) {
        try (
                DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream())); // 소켓으로 받은 바이트를 버퍼에 담아 읽음
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream())); // 소켓으로 보낼 바이트를 버퍼에 담아 출력함(둘다 바이트 단위 출력)
        ) {
            String fileName = dis.readUTF(); // UTF 인코딩 방식으로 저장
            System.out.println("파일 이름 : " + fileName);

            // 파일 입력 받아서 저장하기
            File receivedFile = new File(fileName); // 압축 파일 객체 생성
            try (FileOutputStream fos = new FileOutputStream(receivedFile)) { // 압축파일 파일 출력 스트림 인자로 넣음
                byte[] buffer = new byte[4096]; // 데이터를 한 번에 4KB씩 읽기 위한 버퍼
                int len; // 문자열 저장
                while ((len = dis.read(buffer)) != -1) { // 파일을 읽고 buffer에 저장하고 len에 대입
                    fos.write(buffer, 0, len); // 0부터 시작해서 읽은 바이트 만큼 출력
                }
            }
            System.out.println("파일 수신 완료: " + receivedFile.getAbsolutePath()); // 절대경로 출력

            //파일 압축하기
            File zipFile = new File(fileName + ".zip"); // 파일 객체 생성
            try (
                    FileInputStream fis = new FileInputStream(receivedFile); // 원본 파일 읽음
                    ZipOutputStream zos = new ZipOutputStream (new FileOutputStream(zipFile)) // Zip 형식으로 데이터를 압축해서 저장하는 출력 스트림
            ) {
                ZipEntry entry = new ZipEntry(receivedFile.getName()); // Zip 파일에 저장할 파일이름을 엔트리로 등록
                zos.putNextEntry(entry); // Zip 파일 하려면 필수임(엔트리는 파일 구조 하나 하나를 객체로 처리할 수 있도록 순서를 부여하는 것)

                byte[] buffer = new byte[4096]; // 4KB 버퍼
                int len; // 문자열
                while ((len = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, len); // 원본 파일 4KB씩 읽음
                }
            }
            System.out.println("압축 완료: " + zipFile.getName());

            // 압축된 파일 다시 전송
            try (FileInputStream zipFis = new FileInputStream(zipFile)) { // 압축파일 읽어서 저장
                byte[] buffer = new byte[4096];
                int len;
                while ((len = zipFis.read(buffer)) != -1) { //파일 읽어서
                    dos.write(buffer, 0, len); // 파일 쓰고
                }
                dos.flush(); // 버퍼 비우기
            }
            System.out.println("압축 파일 전송 완료");

        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
