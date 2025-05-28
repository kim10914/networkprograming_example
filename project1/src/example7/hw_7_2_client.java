// 클라이언트 구현
package example7;

import java.io.*;
import java.net.*;
//import java.util.*; // 스캐너 사용

public class hw_7_2_client {
    public static void main(String[] args) {
        String host = (args.length > 0) ? args[0] : "localhost"; // 삼항 연산자 사용해서 참이면 입력 호스트 아니면 로컬호스트 연결
        try (Socket socket = new Socket(host, 5173)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); // 소켓 출력 스트림을 이용해 직렬화할 객체를 저장
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); // 소켓 인풋 스트림에서 데이터를 역직렬화 하여 객체로 생성 후 저장
//            Scanner scanner = new Scanner(System.in); // 스캐너 쓰기 싫다
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in)); // 버퍼 사용하기

            while (true) {
                String serverMsg = (String) in.readObject(); // 서버 메시지를 읽어 문자열로 바꾸고 저장
                System.out.println(serverMsg); // 문자 출력
                if (serverMsg.contains("입력하세요")) { // 서버 메시지에 "입력하세요"가 포함되어 있다면
                    String input = userInput.readLine(); // 사용자에게 입력 받음
                    out.writeObject(input); // 사용자 입력을 전송
                } else if (serverMsg.contains("최종 점수")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
