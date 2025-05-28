package example9;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;

public class hw_9_8_2 extends JFrame implements ActionListener, KeyListener {
    JTextArea display; // 메시지 표시창
    JTextField wtext, ltext; // 대화 입력, 로그인 입력
    JLabel mlbl, wlbl, loglbl; // 안내, 대화, 로그온 레이블
    BufferedWriter output; // 출력
    BufferedReader input; // 입력
    Socket client; // 소켓
    StringBuffer clientdata; // 클라이언트 데이터
    String serverdata; // 서버 데이터
    String ID; // 아이디
    JButton logoutBtn;
    JPanel plabel; // 로그온 로그아웃 페널

    // 코드
    private static final String SEPARATOR = "|"; // 구분자
    private static final int REQ_LOGON = 1001; // 로그온
    private static final int REQ_LOGOUT = 1002; // 로그아웃
    private static final int REQ_SENDWORDS = 1021; // 메시지 전송
    private static final int REQ_WISPERSEND = 1022; // 귓말 전송

    /**
     * GUI 생성자
     */
    public hw_9_8_2() {
        super("클라이언트");
        // 상단 레이블
        mlbl = new JLabel("채팅 상태를 보여줍니다.");
        add(mlbl, BorderLayout.NORTH);
        // 메시지 표현 창 중단
        display = new JTextArea();
        display.setEditable(false); // 편집 불가 설정
        JScrollPane scrollPane = new JScrollPane(display,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// 수직 스크롤 표시, 수평 스크롤 숨김
        add(scrollPane, BorderLayout.CENTER);
        // 패널 생성
        JPanel ptotal = new JPanel(new BorderLayout()); // 하단
        
        // 대화 입력 패널
        JPanel pword = new JPanel(new BorderLayout()); // 하단 상단
        wlbl = new JLabel("대화말:");
        wtext = new JTextField(30);
        wtext.addKeyListener(this);
        pword.add(wlbl, BorderLayout.WEST);
        pword.add(wtext, BorderLayout.EAST);
        ptotal.add(pword, BorderLayout.CENTER);
        // 로그온 패널
        plabel = new JPanel(new BorderLayout()); // 하단 하단
        loglbl = new JLabel("로그온:");
        ltext = new JTextField(30);
        ltext.addActionListener(this);
        plabel.add(loglbl, BorderLayout.WEST);
        plabel.add(ltext, BorderLayout.EAST);
        ptotal.add(plabel, BorderLayout.SOUTH);
//        // 로그아웃 패널 -> 구현 실패... 그냥 아무것도 안보임...
//        logoutBtn = new JButton("로그아웃");
//        logoutBtn.addActionListener(this);
//        logoutBtn.setVisible(false);
//        plabel.add(logoutBtn, BorderLayout.EAST);
        // 하단 패널 배치
        add(ptotal, BorderLayout.SOUTH);

        addWindowListener(new WinListener());
        setSize(400, 300);
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
            // 서버 데이터를 읽는 블럭
            Thread receiveTread = new Thread(()->{ //run() 백그라운드 쓰레드 구현을 통해 readLine() 블럭 현상을 통한 예외를 방지
                try{
                    while ((serverdata = input.readLine()) != null) {
                        display.append(serverdata + "\r\n");
                    }
                } catch(IOException e){
                    display.append("[시스템] 서버 연결 종료\n");
                }
            });
            receiveTread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 로그인 처리 메서드 
     * @param ae the event to be processed
     */
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        // 아이디 입력 없으면 입력 받아서 서버에 넘겨주는 블럭
        if (source == ltext) {
            ID = ltext.getText(); 
            mlbl.setText(ID + "으로 로그인 하였습니다.");
            try {
                clientdata.setLength(0);
                clientdata.append(REQ_LOGON);
                clientdata.append(SEPARATOR);
                clientdata.append(ID); // 1001|ID 형식
                output.write(clientdata.toString() + "\r\n"); // 전송
                output.flush();

                //로그아웃 버튼으로 변환
                plabel.removeAll();
                logoutBtn = new JButton("로그아웃");
                logoutBtn.addActionListener(this);
                plabel.add(logoutBtn, BorderLayout.CENTER); // 버튼 추가
                plabel.revalidate(); // 레이아웃 재계산
                plabel.repaint(); // 화면 갱신

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(ae.getSource() == logoutBtn){
            logoutAndRestart();
        }
    }

    /**
     * 메인
     * @param args 
     */
    public static void main(String args[]) {
        hw_9_8_2 c = new hw_9_8_2();
        c.runClient(); // 클라이언트 서버 구동
    }

    /**
     * 윈도 x키 구현
     */
    class WinListener extends WindowAdapter {
        /**
         * x를 눌렀을 때 로그아웃 정보를 서버에 보내고 종료
         * @param e the event to be processed 
         */
        public void windowClosing(WindowEvent e) {
            try {
                if (client != null && !client.isClosed()) { // 소켓이 열려 있을 때
                    clientdata.setLength(0);
                    clientdata.append(REQ_LOGOUT);
                    clientdata.append(SEPARATOR);
                    clientdata.append(ID); // 1002|user
                    output.write(clientdata + "\r\n"); //전송
                    output.flush();

                    client.close(); // 클라이언트 소켓 닫기
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                System.exit(0);
            }
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
                        clientdata.append(Wmessage); // 1022|sender|receiver|MSG 형식
                        output.write(clientdata + "\n"); // 전송
                        output.flush();
                    } else { // 귓말이 아닐 경우
                        clientdata.setLength(0);
                        clientdata.append(REQ_SENDWORDS);
                        clientdata.append(SEPARATOR);
                        clientdata.append(ID);
                        clientdata.append(SEPARATOR);
                        clientdata.append(message); // 1021|sneder|MSG 형식
//                        display.append(ID + " : " + message + "\r\n");
                        output.write(clientdata + "\n"); // 전송
                        output.flush();
                    }
                    wtext.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /* 3개 구현해야해서 선언만 함 */
    public void keyReleased(KeyEvent ke) {}
    public void keyTyped(KeyEvent ke) {}

    /**
     * 로그아웃 하고 다시 시작하는 메서드
     */
    private void logoutAndRestart() {
        try {
            // 클라이언트가 완전히 닫히지 않은 경우 서버에 메시지를 보냄
            if (ID != null && client != null && !client.isClosed()) {
                clientdata.setLength(0);
                clientdata.append(REQ_LOGOUT).append(SEPARATOR).append(ID); // 1002|user 형식
                output.write(clientdata + "\r\n");
                output.flush();
            }
            if (client != null) client.close(); // 소켓이 아직 있으면 닫아줌
        } catch (IOException e) {
            e.printStackTrace();
            display.append("[시스템] 서버 연결 종료\n");
        }

            // 상태 초기화
            ID = null;
            ltext.setEditable(true);
            ltext.setText("");
            logoutBtn.setVisible(false);
            display.setText("");
            mlbl.setText("채팅 상태를 보여줍니다.");
            // 로그인 패널 재배치
            plabel.removeAll();
            plabel.add(loglbl, BorderLayout.WEST);
            plabel.add(ltext, BorderLayout.EAST);
            plabel.revalidate();
            plabel.repaint();
            
            runClient(); // 클라이언트 다시 실행


    }

}

