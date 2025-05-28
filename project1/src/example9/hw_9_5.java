/*1. 로그온 후 plabel을 로그아웃 버튼으로 교체 -> GUI의 기존 로그인 패널을 숨기고 동적으로 로그아웃 버튼을 생성해서 교체
 * 2. 로그아웃 버튼 클릭 시 서버에 로그아웃 메시지 전송 -> 로그아웃 메시지 전송 및 로그아웃 내부 처리(코드 1002)
 * 3. 윈도우 종료 시 소켓 해체 -> client.close() 호출 */

package example9;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.event.*;

/**
 * 파일 클래스
 */
public class hw_9_5 extends JFrame {
    JTextArea display; // 사용자 텍스트 창
    JLabel info; // 정보 나타내는 라벨
    List<ServerThread> list; // 사용자 쓰레드 리스트 컬랙션으로 관리

    /**
     * GUI 생성자
     */
    public hw_9_5() {
        super("서버");
        // 상단 정보 출력 창
        info = new JLabel();
        add(info, BorderLayout.NORTH);

        // 중앙 메시지 출력 창
        display = new JTextArea("", 0, 0);
        display.setEditable(false);
        add(display, BorderLayout.CENTER);

        addWindowListener(new WinListener());
        setSize(300, 250);
        setVisible(true);
    }
    /**
     * 서버 실행 메서드
     */
    public void runServer() {
        ServerSocket server; // 서버 객체
        Socket sock; // 엔드 포인트
        ServerThread SThread; // 쓰레드 객체
        try {
            list = new ArrayList<ServerThread>(); // 쓰레드를 배열 객체로 관리
            server = new ServerSocket(5000, 100); // 포트, 백로그 지정 객체 생성
            try {
                //사용자 입력 블럭
                while (true) {
                    sock = server.accept(); // 접속 수락
                    SThread = new ServerThread(this, sock, display, info); // 쓰레드 객체 생성
                    SThread.start(); // 쓰레드 run
                    info.setText(sock.getInetAddress().getHostName() + " 서버는 클라이언트와 연결됨");
                }
            } catch (IOException ioe) {
                server.close();
                ioe.printStackTrace();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     *  메인
     */
    public static void main(String args[]) {
        hw_9_5 s = new hw_9_5();
        s.runServer();
    }

    /**
     * 윈도우 x로 닫기
     */
    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    /**
     * 사용자 쓰레드 구현
     */
    class ServerThread extends Thread {
        Socket sock; // 소켓
        BufferedWriter output; // 쓰기
        BufferedReader input; // 읽기
        JTextArea display; // 메시지 출력창
        JLabel info; // 정보 출력창
        //        JTextField text;
        String clientdata; // 클라이언트 메시지
        //        String serverdata = "";
        hw_9_5 cs; // 파일 클래스를 선언하는 객체
        // 사용이유 : 서버간의 통신만 이용할 경우 상관 없지만 파일에 있는 GUI를 통해 메시지를 출력할 경우 해당 요소에 접근하기 때문

        // 서버간 메시지 프로토콜을 정의한 상수
        private static final String SEPARATOR = "|"; // |를 통해 분리
        private static final int REQ_LOGON = 1001; // 로그온 코드
        private static final int REQ_SENDWORDS = 1021; // 메시지 전송 코드
        private static final int REQ_LOGOUT = 1002; // 로그아웃 코드 추가

        /**
         * 서버 쓰레드 생성자
         */
        public ServerThread(hw_9_5 c, Socket s, JTextArea ta, JLabel l) {
            sock = s;
            display = ta;
            info = l;
            cs = c;
            try {
                input = new BufferedReader(new InputStreamReader(sock.getInputStream())); // 소켓 바이트 스트림 읽기
                output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())); // 소켓 바이트 스트림 출력
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        /**
         * 쓰레드 run 메서드 오버라이딩
         */
        public void run() {
            cs.list.add(this); // 현재 생성한 쓰레드를 리스트에 추가
            try {
                // 사용자 입력을 받아 처리하는 블럭
                while ((clientdata = input.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR); // 구분자 단위로 문자열 나눠 토큰으로 저장(해당 문자열, 구분자) -> 명령을 판단하기 위해 사용
                    // 예 : 1021|user1|안녕하세요 라는 입력을 받았을 경우 -> 1021, user1, 안녕하세요 를 각 토큰을 저장
                    int command = Integer.parseInt(st.nextToken()); // 코드 저장
                    // nextToken() 호출 -> 현재 토큰을 반환하고 다음 요소를 가리킴
                    // nextToken의 특징 다음 요소에 접근 했을 경우 이전 토큰을 읽을 수 없음
                    // 마지막 토큰을 가리키면 내부적으로 포인터를 더 이상 전지하지 못함(마지막 호출 후 재호출하면 NoSuchElementException 발생)
                    int cnt = cs.list.size(); // 현재 접속자 수 저장
                    switch (command) { // command 처리
                        case REQ_LOGON: { // 로그온
                            String ID = st.nextToken();
                            display.append("클라이언트가 " + ID + "으로 로그인 하였습니다.\n");
                            break;
                        }
                        case REQ_SENDWORDS: { // 메시지 전송
                            String ID = st.nextToken();
                            String message = st.nextToken();
                            display.append(ID + " : " + message + "\n");
                            for (int i = 0; i < cnt; i++) { // 모든 쓰레드에게 메시지를 출력
                                ServerThread SThread = cs.list.get(i);
                                SThread.output.write(ID + " : " + message + "\r\n");
                                SThread.output.flush();
                            }
                            break;
                        }
                        case REQ_LOGOUT: { // 로그아웃
                            String ID = st.nextToken();
                            display.append("클라이언트 " + ID + " 로그아웃 처리됨\n");
                            for (int i = 0; i < cnt; i++) {
                                ServerThread SThread = cs.list.get(i);
                                SThread.output.write(ID + "님이 로그아웃하였습니다.\r\n");
                                SThread.output.flush();
                            }
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            cs.list.remove(this); // 리스트 삭제
            try {
                sock.close(); // 소켓 닫기
            } catch (IOException ea) {
                ea.printStackTrace();
            }
        }
    }
}



