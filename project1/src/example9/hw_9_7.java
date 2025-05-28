/*로그온 기능 개선
* 중복 로그온 방지
* 로그온 성공 시 서버는 현재 로그온 사용자의 목록을 새로운 사용자에게 전달
*
* 로그아웃 기능 구현
* 로그아웃 후 재로그온이 가능
* 클라이언트 원도우를 닫으면 로그아웃 후 프로그램 종료
* 로그아웃이 성고하면 클라이언트는 초기화면으로 돌아감
* 로그아웃 시 서버는 다른 로그온 사용자들에게 로그아웃 메시지를 전송
*
* 서버는 로그온 상태의 클라이언트 들에만 대홧말 메시지를 전송함*/
package example9;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;

/**
 * JFrame 상속받은 파일 클래스
 */
public class hw_9_7 extends JFrame {
    JTextArea display; // 텍스틑 쓰는 곳
    JLabel info; // 정보 출력
    List<ServerThread> list; // 쓰레드를 저장하는 배열
    Hashtable<String, ServerThread> hash; // 해시 테이블을 사용 ID - 쓰레드 형식으로 컬럼 저장
    /*내부적으로 해시 함수를 사용해 데이터를 저장하고 검색함 중복키를 허용하지 않음*/

    /**
     * GUI 생성자
     */
    public hw_9_7() {
        super("서버");
        //정보 표시(상단)
        info = new JLabel();
        add(info, BorderLayout.NORTH);

        //내용 표시(중단
        display = new JTextArea("", 0, 0);
        display.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(display, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    
        addWindowListener(new WinListener());
        setSize(300, 250);
        setVisible(true);
    }

    /**
     * 서버 구동 메서드
     */
    public void runServer() {
        ServerSocket server; // 서버
        Socket sock; // 소켓
        ServerThread SThread; // 쓰레드
        try {
            server = new ServerSocket(5000, 100); // 포트, 백로그(사용자얼마나 대기열)
            hash = new Hashtable<>(); // 사용자 저장 헤시테이블 객체 생성
            list = new ArrayList<>(); // 쓰레드 저장할 배열

            // 사용자 접속 처리 블럭
            while (true) {
                sock = server.accept(); // 수락
                SThread = new ServerThread(this, sock, display, info); // 쓰레드 객체 생성
//                list.add(SThread);
                SThread.start(); // 쓰레드 run
                info.setText(sock.getInetAddress().getHostName() + " 서버는 클라이언트와 연결됨");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * 메인
     * @param args 
     */
    public static void main(String[] args) {
        hw_9_7 s = new hw_9_7();
        s.runServer();
    }

    /**
     * x닫기
     */
    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    /**
     * 사용자 정의 쓰레드 클래스
     */
    class ServerThread extends Thread {
        Socket sock; // 소켓
        BufferedWriter output;
        BufferedReader input; // 입출력 스트림
        JTextArea display; // 텍스트 창
        JLabel info; // 정보창
        hw_9_7 cs; // 파일 객체
        String clientdata; // 사용자 정보
        String clientID; // 사용자 계정 저장

        // 코드 상수 정의
        private static final String SEPARATOR = "|"; // 구분자
        private static final int REQ_LOGON = 1001; // 로그온
        private static final int REQ_LOGOUT = 1002; // 로그아웃
        private static final int REQ_SENDWORDS = 1021; // 메시지 보내기
        private static final int REQ_WISPERSEND = 1022; // 귓말 보내기

        /**
         * 생성자
         * @param c
         * @param s
         * @param ta
         * @param l
         */
        public ServerThread(hw_9_7 c, Socket s, JTextArea ta, JLabel l) {
            sock = s;
            display = ta;
            info = l;
            cs = c;

            try {
                input = new BufferedReader(new InputStreamReader(sock.getInputStream())); // 입력
                output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())); // 출력
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        /**
         * 쓰레드 run 메서드
         */
        public void run() {
            try {
                // 사용자 입력 읽어 서 처리하는 블럭
                while ((clientdata = input.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(clientdata,SEPARATOR); // 1001|ID|MSG 형식으로 토큰 구성
                    int command = Integer.parseInt(st.nextToken()); // 첫 토큰 저장
//                    int cnt = cs.list.size(); // 사용자 수

                    // 사용자 입력 코드에 따른 처리
                    switch (command) {
                        case REQ_LOGON: { // 로그온
                            String ID = st.nextToken();
                            if (cs.hash.containsKey(ID)) {
                                output.write("[서버] 중복된 ID입니다. 다른 ID를 사용하세요.\r\n");
                                output.flush();
                            }else {
                                this.clientID = ID;
                                cs.hash.put(ID, this);
                                cs.list.add(this);
                                display.append("클라이언트가 " + ID + "으로 로그인 하였습니다.\n");
                                // 접속중인 유저 출력
                                for (String existingID : cs.hash.keySet()) {
                                    output.write("[시스템] 접속 중인 유저는 : " + existingID + "\r\n");
                                }
                                output.flush();
                                broadcast("[시스템] " + ID + "님이 접속하였습니다.", ID);
                            }
                            break;
                        }
                        case REQ_SENDWORDS: { // 메시지 보내기
                            String ID = st.nextToken();
                            String message = st.nextToken();
                            display.append(ID + " : " + message + "\n");
//                            for (int i = 0; i < cnt; i++) { // 모든 클라이언트에 메시지를 보냄
//                                ServerThread SThread = cs.list.get(i);
//                                SThread.output.write(ID + " : " + message + "\r\n");
//                                SThread.output.flush();
//                            }
                            broadcast(ID + " : " + message,null);
                            break;
                        }
                        case REQ_WISPERSEND: { // 귓말 보내기
                            String ID = st.nextToken();
                            String WID = st.nextToken(); // 귓말 대상 아이디
                            String message = st.nextToken();
                            ServerThread SThread = (ServerThread) cs.hash.get(ID); // 쓰레드리스트에서 키 값으로 사용자 찾기
                            // 사용자 존재 여부를 파악하여 메시지를 전송하는 블럭
                            if (SThread != null) { // 쓰레드 중 키 가 있으면
                                display.append(ID + " => " + WID + " : " + message + "\r\n");
                                SThread.output.write(ID + " => " + WID + " : " + message + "\r\n");
                                SThread.output.flush();
                                SThread = (ServerThread) cs.hash.get(WID); // 쓰레드리스트에서 키 값으로 사용자 찾기
                                SThread.output.write(ID + " => " + WID + " : " + message + "\r\n");
                                SThread.output.flush();
                            } else {
                                output.write("[서버] 대상 사용자가 없습니다.\r\n");
                                output.flush();
                            }
                            break;
                        }
                        case REQ_LOGOUT: { // 로그아웃
                            String ID = st.nextToken();
                            cs.hash.remove(ID);
                            cs.list.remove(this);
                            display.append("[로그아웃] " + ID + " 접속 종료\n");
                            broadcast(ID + "님이 로그아웃하였습니다.", ID);
                            sock.close();
                            return;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    // 최종 닫기 블럭
                    if (clientID != null) { // 클라이언트가 아직 존재하면
                        cs.hash.remove(clientID);
                        cs.list.remove(this);
                        broadcast(clientID + "님 연결 종료.", clientID);
                    }
                    sock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 네트워크 내의 쓰레드를 순회하며 출력을 보내는 사용자 함수
         * @param message -> 사용자 메시지
         * @param exceptID -> 브로드캐스트 제외 ID
         * @throws IOException
         */
        private void broadcast(String message, String exceptID) throws IOException {
            for (ServerThread t : cs.list) {
                if (t.clientID != null && !t.clientID.equals(exceptID)) { // 네트워크내에 존재하는 아이디 거나 제외 아이디가 아니면 메시지 전송
                    t.output.write(message + "\r\n");
                    t.output.flush();
                }
            }
        }
    }
}
/*
* ReadLine 예외 -> 백그라운드 쓰레드를 통해서 관리 */