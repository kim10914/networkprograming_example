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
public class ChatWhisperS_9_7 extends JFrame {
    JTextArea display; // 텍스틑 쓰는 곳
    JLabel info; // 정보 출력 
    List<ServerThread> list; // 쓰레드를 저장하는 배열
    Hashtable<String, ServerThread> hash; // 해시 테이블을 사용 ID - 쓰레드 형식으로 컬럼 저장
    /*내부적으로 해시 함수를 사용해 데이터를 저장하고 검색함 중복키를 허용하지 않음*/

    /**
     * GUI 생성자
     */
    public ChatWhisperS_9_7() {
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
        ChatWhisperS_9_7 s = new ChatWhisperS_9_7();
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
        ChatWhisperS_9_7 cs; // 파일 객체
        String clientdata; // 사용자 정보

        // 코드 상수 정의
        private static final String SEPARATOR = "|"; // 구분자
        private static final int REQ_LOGON = 1001; // 로그온
        private static final int REQ_SENDWORDS = 1021; // 메시지 보내기
        private static final int REQ_WISPERSEND = 1022; // 귓말 보내기

        /**
         * 생성자
         * @param c
         * @param s
         * @param ta
         * @param l
         */
        public ServerThread(ChatWhisperS_9_7 c, Socket s, JTextArea ta, JLabel l) {
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
                cs.list.add(this); // 현 쓰레드 리스트에 넣기
                // 사용자 입력 읽어 서 처리하는 블럭
                while ((clientdata = input.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR); // 1001|ID|MSG 형식으로 토큰 구성
                    int command = Integer.parseInt(st.nextToken()); // 첫 토큰 저장
                    int cnt = cs.list.size(); // 사용자 수

                    switch (command) {
                        case REQ_LOGON: { // 로그온
                            String ID = st.nextToken();
                            display.append("클라이언트가 " + ID + "으로 로그인 하였습니다.\n");
                            cs.hash.put(ID, this);
                            break;
                        }
                        case REQ_SENDWORDS: { // 메시지 보내기
                            String ID = st.nextToken();
                            String message = st.nextToken();
                            display.append(ID + " : " + message + "\n");
                            for (int i = 0; i < cnt; i++) { // 모든 클라이언트에 메시지를 보냄
                                ServerThread SThread = cs.list.get(i);
                                SThread.output.write(ID + " : " + message + "\r\n");
                                SThread.output.flush();
                            }
                            break;
                        }
                        case REQ_WISPERSEND: { // 귓말 보내기
                            String ID = st.nextToken();
                            String WID = st.nextToken(); // 귓말 대상 아이디
                            String message = st.nextToken();

                            display.append(ID + " => " + WID + " : " + message + "\n");
                            ServerThread SThread = cs.hash.get(WID); // ID를 키값으로 해당 쓰레드를 찾아 메시지를 보냄
                            if (SThread != null) {
                                SThread.output.write(ID + " => " + WID + " : " + message + "\r\n");
                                SThread.output.flush();
                            }
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            cs.list.remove(this);
            try {
                sock.close();
            } catch (IOException ea) {
                ea.printStackTrace();
            }
        }
    }
}
