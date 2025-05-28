//서버는 웹 브라우저가 주소창에 입력한 파일을 찾아 브라우저로 전송할 수 있도록 수정
package example8;

import java.io.*;
import java.net.*;

public class hw_8_2_Exception {
    public static void main(String args[]) {
        int port = 80; // 기본 포트(http)
//        try {
//            if (args.length > 0) { // 입력 받은 인수로 포트 지정
//                port = Integer.parseInt(args[0]);
//            }
//        } catch (Exception e) {
//            port = 80;
//        } // 가변으로 포트 지정할 수 있음

        try (ServerSocket server = new ServerSocket(port)) { // 서버 소켓 자동으로 끄기
            System.out.println("서버 실행 중 포트: " + port);

            while (true) { //args[0] 파일을 미리 읽지 않음
                Socket connection = server.accept(); // 사용자의 입력을 받음
                new FileDownload_2_1(connection, port).start(); // 파일 다운로드 객체 생성하고 쓰레드 run
            }
        } catch (IOException e) {
            System.out.println("서버 오류: " + e);
        }
    }
}
class FileDownload_2_1 extends Thread { // 쓰레드 상속한 파일 다운 사용자 클래스
    private int port; // 포트번호 선언
    Socket connection; // 소켓 객체 선언
    BufferedOutputStream out; // 버퍼 출력
    BufferedInputStream in; // 버퍼 입력

    public FileDownload_2_1(Socket connection, int port) { // 생성자
        this.connection = connection;
        this.port = port;
    }

    public void run() {
        try {
            out = new BufferedOutputStream(connection.getOutputStream()); // 버퍼 출력
            in = new BufferedInputStream(connection.getInputStream()); // 버퍼 입력

            StringBuffer request = new StringBuffer(); // 가변 문자열 객체 선언(읽기전용)
            int c; // 한 글자(byte)를 읽어서 저장
            while ((c = in.read()) != -1) { //한 글자씩 읽으면서 주소 검사
                if (c == '\r') continue; // 캐리지 리턴 이면 계속(윈도우는 줄 끝이 \r\n임)
                if (c == '\n') break; // 뉴라인 이면 멈춤
                request.append((char) c); //c의 값을 추가
            }

            System.out.println("요청: " + request); // 요청된 파일 출력
            
            String requestLine = request.toString(); // 주소 저장(처리전용)
//            if (!requestLine.startsWith("GET")) { // GET으로 시작하지 않으면
//                sendError("400 Bad Request", "지원하지 않는 요청입니다."); // 에러 출력
//                return;
//            } // 오류 처리 코드

            String[] parts = requestLine.split(" "); // 공백을 기준으로 문자열 배열을 만들어 마디 씩 저장
//            if (parts.length < 2) { // 공백이 2개 이상 있으면
//                sendError("400 Bad Request", "요청이 올바르지 않습니다."); //오류 출력
//                return;
//            }

            String fileName = parts[1].equals("/") ? "index.html" : parts[1].substring(1); // 파일 이름이 /만 있으면 index.html을 반환 하고 아니면 /를 지우고 파일명 저장
            File file = new File(fileName); // 파일 객체 반환
//
//            if (!file.exists()) { // 파일 존재 안하면
//                sendError("404 Not Found", "파일이 존재하지 않습니다: " + fileName); // 오류 출력
//                return;
//            }

            String mimeType = getMimeType(fileName); // 파일 미디어 타입 식별해서 저장
            byte[] content = readFileBytes(file); // 파일 읽어서 바이트 단위로 저장

            String header = "HTTP/1.0 200 OK\r\n" + "Content-length: " + content.length + "\r\n" + "Content-type: " + mimeType + "\r\n\r\n";
            // http버전과 상태코드, 파일 크기, 미디어 타입을 저장

            out.write(header.getBytes("ASCII")); //ASCII 인코딩 방식으로 통해 헤더를 바이트로 전달
            out.write(content); // 본문을 전송
            out.flush(); // 비우기

        } catch (IOException e) {
            System.out.println("서버 처리 오류: " + e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (IOException e) {
                System.out.println("소켓 종료 오류: " + e);
            }
        }
    }

    private byte[] readFileBytes(File file) throws IOException { // 파일을 parameter로 받아서 바이트 배열로 반환
        ByteArrayOutputStream out = new ByteArrayOutputStream(); // 메모리 기반 바이트 출력 스트림 toByteArray()메서드를 위해 사용
        FileInputStream fis = new FileInputStream(file); // 파일 읽기 객체
        int b; // byte 저장할 임시 변수
        while ((b = fis.read()) != -1) // 파일 끝까지 읽고
            out.write(b); // 읽은 바이트 저장
        fis.close(); // 파일 닫기
        return out.toByteArray(); // 저장한 바이트를 바이트 배열로 저장하는 메서드를 호출해서 반환
    }

    private String getMimeType(String fileName) { // 미디어 타입 반환
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) return "text/html";
        if (fileName.endsWith(".txt")) return "text/plain";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".png")) return "image/png";
        return "application/octet-stream"; // 기본 값을 반환(바이트 데이터)
    }

//    private void sendError(String status, String msg) throws IOException { // 오류 메시지 브라우저 출력
//        String body = "<html><body><h1>" + status + "</h1><p>" + msg + "</p></body></html>"; // 오류코드, 메시지 받아서 바디에 저장
//        String header = "HTTP/1.0 " + status + "\r\n" +"Content-length: " + body.length() + "\r\n" +"Content-type: text/html\r\n\r\n";
        // // 헤더에 HTTP버전, 오류코드, 바디 길이, 미디어 타입 저장
//        out.write(header.getBytes("ASCII")); // header를 전송함(ASCII인코딩) -> 헤더는 네트워크 자원이라 개발자 도구에서 확인 가능
//        out.write(body.getBytes("UTF-8")); //  바디 전송(UTF-8인코딩)
//        out.flush(); // 비우기
//    }
}
