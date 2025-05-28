//서버 구현 쓰레드 있는 버전(이러면 동시 접속을 할 수 있음)
package example7;

import java.io.*;
import java.net.*;
import java.util.*; // List 사용할 꺼니까 호출

public class hw_7_2_server_thred {
    private static final int PORT = 5173; // 포트
    private static final int MAX_PLAYERS = 2; // 플레이어 수
    private static List<ClientHandler> players = Collections.synchronizedList(new ArrayList<>());
    // 쓰레드 객체를 리스트에 저장함(동시에 리스트에 접근할 수 있음 -> 동기화 안하면 동시 수정 예외 발생(synchronizedList사용))
    // 메서드 동시 접근을 안전하게 처리 가능함

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // 서버 소켓 객체 생성
            System.out.println("서버 시작, 최대 2명의 플레이어를 기다립니다");

            while (players.size() < MAX_PLAYERS) {
                Socket socket = serverSocket.accept(); // 클라이언트의 연결 요청을 수락
                ClientHandler handler = new ClientHandler(socket); // 쓰레드 이용해서 플레이어 객체 생성
                players.add(handler); // 리스트에 쓰레드를 추가
                handler.start(); // 쓰레드 실행 시작
            }

            // 플레이어 2명 모두 이름을 입력했는 지 확인하기(람다식)
            while (true) { // 무한 반복
                synchronized (players) { // 동기화 진행
                    if (players.stream().allMatch(p -> !p.isReady())){ //List를 스트림으로 변환해서 순회한다. p-> !p.isReady()검사
                        break; // 전부 준비(allMatch)상태면 탈출
                    }
                }
                Thread.sleep(100); // 100ms(0.1초) 간격으로 대기(모두 준비 완료면 false)
            }

            //람다식 안쓴 버전
//            boolean allReady = false;
//            while (!allReady) { // 준비 될 때 까지 반복
//                allReady = true; // 무한루프 막기
//                synchronized (players) { // 여러 쓰레드가 동시에 접하기에 충돌을 막기 위해 동기화 진행 -> 반복문은 접근이 위험(반복 도중 수정 시 동시 수정 예외 발생)하니까 동기화 진행
//                    for (ClientHandler p : players) { // 범위기반 for 루프
//                        if (!p.isReady()) { // 모든 플레이어가 준비되었는지 확인
//                            allReady = false; // 거짓이면 다시 while문으로 돌아감
//                            break;
//                        }
//                    }
//                }
//            }

            int score1 = 0, score2 = 0;
            String name1 = players.get(0).getName_2();
            String name2 = players.get(1).getName_2(); // 리스트에 저장된 각 플레어의 이름을 받아 저장

            for (int round = 1; round <= 10; round++) {
                players.get(0).sendMessage(round + "/10 가위, 바위, 보 중 하나를 입력하세요:");
                players.get(1).sendMessage(round + "/10 가위, 바위, 보 중 하나를 입력하세요:"); // 클라이언트에 메시지 전송

                String choice1 = players.get(0).readMessage();
                String choice2 = players.get(1).readMessage(); // 양쪽 플레이어 에게 입력을 요청

                String result = name1 + "(" + choice1 + ") vs " + name2 + "(" + choice2 + ")\n"; // 플레이어1 (주먹) vs 플레이어2 (보) 형식
                int r = judge(choice1, choice2);
                if (r == 1) {
                    score1++; // 플레이어 1 승리
                    result += name1 + " 승!";
                } else if (r == -1) {
                    score2++; // 플레이어 2승리
                    result += name2 + " 승!";
                } else {
                    result += "무승부!"; // 0이 결과면 무승부
                }

                players.get(0).sendMessage(result);
                players.get(1).sendMessage(result); // 결과 전송
            }

            String finalResult_2 = "최종 점수: " + name1 + " " + score1 + " : " + score2 + " " + name2 + "\n"; // 최종결과 저장
            if (score1 > score2) finalResult_2 += name1 + " 승리";
            else if (score1 < score2) finalResult_2 += name2 + " 승리";
            else finalResult_2 += "무승부"; // 최종 결과 저장

            for (ClientHandler p : players) {
                p.sendMessage(finalResult_2); // 각 플레이어 에게 결과 출력
            }
            for (ClientHandler p : players) {
                p.close(); // 소켓 닫기
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int judge(String c1, String c2) {
        if (c1.equals(c2)) return 0; // 무승부
        if ((c1.equals("가위") && c2.equals("보")) ||
                (c1.equals("바위") && c2.equals("가위")) ||
                (c1.equals("보") && c2.equals("바위"))) {
            return 1; // 1승리
        }
        return -1; // 2승리
    }
}
class ClientHandler extends Thread { // 쓰레드 상속
    // ClientHandler를 직렬화 하지 않고 버전 동기화를 할 필요가 없음(사용자 객체를 송수신 하지 않으니까)
    private Socket socket; // 소켓 선언
    private ObjectInputStream in; // 객체 읽기 선언
    private ObjectOutputStream out; // 객체 쓰기 선언
    private String name = null; // 이름

    //생성자
    public ClientHandler(Socket socket) {
        this.socket = socket; // 받음 소켓을 이용해 생성
    }

    public void run() { // 쓰레드 실행 메서드
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream()); // 클라이언트와 입출력 스트림 연결
            sendMessage("플레이어 이름을 입력하세요:");
            name = readMessage(); // 입력값을 받아 name에 저장
        } catch (IOException e) {
            e.printStackTrace(); // 예외가 발생한 스택의 경로를 콘솔에 차례대로 출력함(보안상 민감한 정보가 드러날 수 있음)
        }
    }
    public String getName_2() {
        return name; // 입력 이름 리턴
    }

    public boolean isReady() {
        return name != null; // 이름 입력 있으면 참 반환
    }

    public void sendMessage(String msg) { // 문자열 보내기 메서드
        try {
            out.writeObject(msg); //wirteObjecet(ObjectOutputStream에 명시) 메시지 출력하기
            // 문자열은 Serializable을 내부적으로 구현하여 writeObject가 가능함
            out.flush(); // 플러쉬
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readMessage() { // 문자열 읽기 메서드
        try {
            return (String) in.readObject(); // 클라이언트로 부터 객체를 받아 역직렬화 수행 후 String으로 캐스팅
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }catch(ClassNotFoundException e){ // 수신이 String이 아니라면
            e.printStackTrace();
            return "";
        }
    }

    public void close() { // 종료
        try {
            socket.close(); // 소켓 닫기
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

