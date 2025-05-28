package example9;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class ChatWhisperC_9_8 extends Frame implements ActionListener, KeyListener {
    TextArea display; // 메시지 표시창
    TextField wtext, ltext; // 대화 입력, 로그인 입력
    Label mlbl, wlbl, loglbl; // 안내, 대화, 로그온 레이블
    BufferedWriter output; // 출력
    BufferedReader input; // 입력
    Socket client; // 소켓
    StringBuffer clientdata; // 클라이언트 데이터
    String serverdata; // 서버 데이터
    String ID; // 아이디

    // 코드
    private static final String SEPARATOR = "|"; // 구분자
    private static final int REQ_LOGON = 1001; // 로그온
    private static final int REQ_SENDWORDS = 1021; // 메시지 전송
    private static final int REQ_WISPERSEND = 1022; // 귓말 전송

    /**
     * GUI 생성자
     */
    public ChatWhisperC_9_8() {
        super("클라이언트");
        // 상단 레이블
        mlbl = new Label("채팅 상태를 보여줍니다.");
        add(mlbl, BorderLayout.NORTH);
        // 메시지 표현 창 중단
        display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        display.setEditable(false);
        add(display, BorderLayout.CENTER);
        // 패널 생성
        Panel ptotal = new Panel(new BorderLayout()); // 하단
        Panel pword = new Panel(new BorderLayout()); // 하단 상단
        // 대화 입력 패널
        wlbl = new Label("대화말:");
        wtext = new TextField(30);
        wtext.addKeyListener(this);
        pword.add(wlbl, BorderLayout.WEST);
        pword.add(wtext, BorderLayout.EAST);
        pword.add(wtext, BorderLayout.CENTER);
        // 로그온 패널
        Panel plabel = new Panel(new BorderLayout()); // 하단 하단
        loglbl = new Label("로그온:");
        ltext = new TextField(30);
        ltext.addActionListener(this);
        plabel.add(loglbl, BorderLayout.WEST);
        plabel.add(ltext, BorderLayout.EAST);
        // 하단 패널 배치
        ptotal.add(plabel, BorderLayout.SOUTH);
        ptotal.add(pword, BorderLayout.NORTH);
        add(ptotal, BorderLayout.SOUTH);

        addWindowListener(new WinListener());
        setSize(300, 250);
        setVisible(true);
    }

    /**
     * 클라이언트 실행 메서드
     */
    public void runClient() {
        try {
            client = new Socket(InetAddress.getLocalHost(), 5000); // 로컬호스트, 5000번 접속
            mlbl.setText("연결된 서버이름 : " + client.getInetAddress().getHostName());

            input = new BufferedReader(new InputStreamReader(client.getInputStream())); // 읽기 스트림
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream())); // 출력 스트림
            clientdata = new StringBuffer(2048); // 2KB 사용자 데이터
            mlbl.setText("접속 완료 사용할 아이디를 입력하세요.");
            // 서버 데이터 읽는 블럭
            while (true) {
                serverdata = input.readLine(); // 스림으로 읽은 서버 데이터
                display.append(serverdata + "\r\n"); // 메시지 창 출력
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 로그인 처리 메서드 
     * @param ae the event to be processed
     */
    public void actionPerformed(ActionEvent ae) {
        // 아이디 입력 없으면 입력 받아서 서버에 넘겨주는 블럭
        if (ID == null) { 
            ID = ltext.getText(); 
            mlbl.setText(ID + "으로 로그인 하였습니다.");
            try {
                clientdata.setLength(0);
                clientdata.append(REQ_LOGON);
                clientdata.append(SEPARATOR);
                clientdata.append(ID); // 1001|ID 형식
                output.write(clientdata.toString() + "\r\n"); // 전송
                output.flush();
                ltext.setVisible(false); // 가리기
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 메인
     * @param args 
     */
    public static void main(String args[]) {
        ChatWhisperC_9_8 c = new ChatWhisperC_9_8();
        c.runClient(); // 클라이언트 서버 구동
    }

    /**
     * 윈도 x키 구현
     */
    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    /**
     * 메시지 전송 메서드(키 눌렸을 경우)
     * @param ke the event to be processed
     */
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
            String message = wtext.getText(); // 사용자 메시지 저장
            StringTokenizer st = new StringTokenizer(message, " "); // 공백을 기준으로 토큰 생성
            // 메시지 처리 블럭
            if (ID == null) { // 로그인 검사
                mlbl.setText("다시 로그인 하세요!!!");
                wtext.setText("");
            } else {
                try {
                    if (st.nextToken().equals("/w")) { // /w userID MSG 형식으로 받았을 경우
                        message = message.substring(3); // "/w" 제거
                        String WID = st.nextToken();    // 귓속말 대상자
                        String Wmessage = st.nextToken();
                        // 토큰을 하나로 이어주는 부분
                        while (st.hasMoreTokens()) { // 남은 토큰이 없을 때 까지 반복
                            Wmessage += " " + st.nextToken(); // 만약 공백으로 작성된 메시지가 누락되는 것을 막음
                        }

                        clientdata.setLength(0);
                        clientdata.append(REQ_WISPERSEND);
                        clientdata.append(SEPARATOR);
                        clientdata.append(ID);
                        clientdata.append(SEPARATOR);
                        clientdata.append(WID);
                        clientdata.append(SEPARATOR);
                        clientdata.append(Wmessage); // 1022|sender|recipient|MSG 형식
                        output.write(clientdata + "\r\n"); // 전송
                        output.flush();
                    } else { // 귓말이 아닐 경우
                        clientdata.setLength(0);
                        clientdata.append(REQ_SENDWORDS);
                        clientdata.append(SEPARATOR);
                        clientdata.append(ID);
                        clientdata.append(SEPARATOR);
                        clientdata.append(message); // 1021|sneder|MSG 형식
                        output.write(clientdata + "\r\n"); // 전송 
                        output.flush();
                    }
                    wtext.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /* 3개 구현해서 선언만 함 */
    public void keyReleased(KeyEvent ke) {}
    public void keyTyped(KeyEvent ke) {}
}

