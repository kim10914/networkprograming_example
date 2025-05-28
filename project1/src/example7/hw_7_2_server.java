// 서버 구현(문제가 1번 플레이어가 접속을 끝내야지 2번 플레이어가 접속할 수 있음)
package example7;

import java.io.*;
import java.net.*;

public class hw_7_2_server {
    private static final int PORT = 5173; // React 기본 호스트를 쓰고싶달까
    private static Socket player1, player2; // 소켓은 2개
    private static ObjectInputStream in1, in2; // 사용자 입력 읽기
    private static ObjectOutputStream out1, out2; // 사용자 출력

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("가위 바위 보 대결에 오신 것을 환영합니다.");

            player1 = serverSocket.accept(); // 플레이어 1 입력 대기중
            out1 = new ObjectOutputStream(player1.getOutputStream()); // 송신용 플레이어 1 객체 생성 (직렬화)
            in1 = new ObjectInputStream(player1.getInputStream()); // 수신용 플레이어 1 객체 생성 (역직렬화)
            out1.writeObject("플레이어 1 이름을 입력하세요:"); // 클라이언트에게 이름 입력 요청 메시지 전송
            String name1 = (String) in1.readObject(); // 오브젝트 문자열로 읽어서 저장

            player2 = serverSocket.accept();
            out2 = new ObjectOutputStream(player2.getOutputStream());
            in2 = new ObjectInputStream(player2.getInputStream());
            out2.writeObject("플레이어 2 이름을 입력하세요:");
            String name2 = (String) in2.readObject();

            int score1 = 0, score2 = 0; // 각 승점 저장
            String[] options = {"가위", "바위", "보"};

            for (int round = 1; round <= 10; round++) { // 총 10번의 라운드 진행
                out1.writeObject(round + "/10 가위, 바위, 보 중 하나를 입력하세요:");
                out2.writeObject(round + "/10 가위, 바위, 보 중 하나를 입력하세요:"); // 양쪽 플레이어 에게 입력을 요청

                String choice1 = (String) in1.readObject();
                String choice2 = (String) in2.readObject(); // 두 플레이어가 각각 보낸 입력값을 받음

                String result = name1 + "(" + choice1 + ") vs " + name2 + "(" + choice2 + ")\n"; // 플레이어1 (바위) vs 플레이어2 (보) 형식
                int r = judge(choice1, choice2); // 승패 판정 저장
                if (r == 1) {
                    score1++; // 플레이어 1 승리
                    result += name1 + " 승!";
                } else if (r == -1) {
                    score2++; // 플레이어 2승리
                    result += name2 + " 승!";
                } else {
                    result += "무승부!"; // 0이 결과면 무승부
                }

                out1.writeObject(result);
                out2.writeObject(result); // 결과 전송
            }

            String finalResult = "최종 점수: " + name1 + " " + score1 + " : " + score2 + " " + name2 + "\n";
            if (score1 > score2) finalResult += name1 + " 승리";
            else if (score1 < score2) finalResult += name2 + " 승리";
            else finalResult += "무승부"; // 결과 출력

            out1.writeObject(finalResult);
            out2.writeObject(finalResult); // 최종 결과 저장

            player1.close();
            player2.close(); // 소켓 종료
        } catch (Exception e) {
            e.printStackTrace(); // 예외가 발생한 스택의 경로를 차례대로 출력함(보안상 민감한 정보가 드러날 수 있음)
        }
    }
    private static int judge(String c1, String c2) { //사용자가 이상값 입력해도 그냥 equals함수 때문에 다르게 해석해서 플레이어 2가 이김...(예외 처리 만들어야하는데...)
        if (c1.equals(c2)) return 0; // 무승부
        if ((c1.equals("가위") && c2.equals("보")) ||
                (c1.equals("바위") && c2.equals("가위")) ||
                (c1.equals("보") && c2.equals("바위"))) {
            return 1; // 1승리
        }
        return -1; // 2승리
    }
}

