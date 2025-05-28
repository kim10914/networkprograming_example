package example9;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class hw_9_6 extends JFrame implements ActionListener, KeyListener {
    JTextArea display; // 메시지 출력창
    JTextField wtext, ltext; // 대화 입력창, 로그인 입력창
    JLabel mlbl, wlbl, loglbl;
    BufferedWriter output; // 쓰기
    BufferedReader input; // 읽기
    Socket client; // 클라이언트
    StringBuffer clientdata; // 사용자 입력 저장
    String serverdata; // 서버 데이터 저장
    String ID; // 아이디
    JButton logoutBtn; // 로그아웃 버튼
    JPanel plabel; // 로그온/로그아웃 패널
    
    // 프로토콜 코드 상수로 지정
    private static final String SEPARATOR = "|"; // 구분자
    private static final int REQ_LOGON = 1001; // 로그온
    private static final int REQ_LOGOUT = 1002; // 로그아웃 요청 코드
    private static final int REQ_SENDWORDS = 1021; // 메시지전송



    /**
     * GUI 생성자
     */
    public hw_9_6() {
        super("클라이언트");

        // 상단 상태 메시지
        mlbl = new JLabel("채팅 상태를 보여줍니다.");
        add(mlbl, BorderLayout.NORTH);

        // 중앙 메시지 출력창
        display = new JTextArea();
        display.setEditable(false); // 편집 불가 설정
        JScrollPane scrollPane = new JScrollPane(display,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// 수직 스크롤 표시, 수평 스크롤 숨김
        add(scrollPane, BorderLayout.CENTER);

        // 하단 입력창
        JPanel ptotal = new JPanel(new BorderLayout());

        // 대화 입력창(하단의 중단)
        JPanel pword = new JPanel(new BorderLayout()); // 하나의 볼더 레이아웃으로 더 쪼갬
        wlbl = new JLabel("대화말");
        wtext = new JTextField(30);
        wtext.addKeyListener(this);
        pword.add(wlbl, BorderLayout.WEST);
        pword.add(wtext, BorderLayout.CENTER);
        ptotal.add(pword, BorderLayout.CENTER);

        // 로그온 입력창(하단의 하단)
        plabel = new JPanel(new BorderLayout());
        loglbl = new JLabel("로그온");
        ltext = new JTextField(30);
        ltext.addActionListener(this);
        plabel.add(loglbl, BorderLayout.WEST);
        plabel.add(ltext, BorderLayout.CENTER);
        ptotal.add(plabel, BorderLayout.SOUTH);

        add(ptotal, BorderLayout.SOUTH);

        addWindowListener(new hw_9_6.WinListener());

        setSize(400, 300);
        setVisible(true);
    }


    /**
     * 클라이언트 실행 메서드
     */
    public void runClient() {
        try {
            client = new Socket(InetAddress.getLocalHost(), 5000); // 로컬 호스트, 5000번 접속
            mlbl.setText("연결된 서버 이름 : " + client.getInetAddress().getHostName()); // 라벨 제어
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream())); // 클라이언트 소켓 으로 입출력 스트림 지정
            clientdata = new StringBuffer(2048); // 2KB 문자열 버퍼

            mlbl.setText("접속 완료. 사용할 아이디를 입력하세요.");

            // 서버의 메시지를 수신하는 블럭
            while (true) {
                serverdata = input.readLine();
                display.append(serverdata + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 로그온 처리 이벤트 메서드
     * @param ae the event to be processed
     */
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if(source == ltext){ // 로그인 버튼 눌리면
            ID = ltext.getText();
            mlbl.setText(ID + "으로 로그인 하였습니다.");
            try {
                // 로그인 데이터 처리
                clientdata.setLength(0);
                clientdata.append(REQ_LOGON);
                clientdata.append(SEPARATOR);
                clientdata.append(ID); // 1001 | ID 형식으로 문자열 작성
                output.write(clientdata + "\r\n"); // 서버에 보내기 -> 클라이언트가 가공한 문자열을 받아서 서버는 처리만 해주면 된다.
                output.flush();

                //로그아웃 버튼 변환
                plabel.removeAll();
                logoutBtn = new JButton("로그아웃");
                logoutBtn.addActionListener(this);
                plabel.add(logoutBtn, BorderLayout.CENTER); // 버튼 추가
                plabel.revalidate(); // 레이아웃 재계산
                plabel.repaint(); // 화면 갱신

            }catch (IOException e) {
                e.printStackTrace();
            }
        } else if (source == logoutBtn) { // 로그아웃 버튼 눌릴 때
            try {
                // 로그아웃 데이터 처리
                clientdata.setLength(0);
                clientdata.append(REQ_LOGOUT);
                clientdata.append(SEPARATOR);
                clientdata.append(ID); // 1002|user
                output.write(clientdata + "\r\n"); //전송
                output.flush();

                mlbl.setText("로그아웃 하였습니다.");
                client.close();
                System.exit(0);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 사용자 메시지 처리 메서드 엔터키 누르면 채팅 메시지를 서버로 전송
     * @param ke the event to be processed
     */
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyChar() == KeyEvent.VK_ENTER) { // 사용자가 엔터 누른 경우
            String message = wtext.getText();
            if (ID == null) { // ID 입력을 받지 않은 경우
                mlbl.setText("다시 로그인 하세요!!!");
                wtext.setText("");
            } else {
                try {
                    clientdata.setLength(0);
                    clientdata.append(REQ_SENDWORDS);
                    clientdata.append(SEPARATOR);
                    clientdata.append(ID);
                    clientdata.append(SEPARATOR);
                    clientdata.append(message); // 1021|ID|MSG 형식으로 문자열 저장
                    output.write(clientdata.toString() + "\r\n");
                    output.flush();
                    wtext.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 키를 눌렀다가 뗐을 때 호출
     * @param ke the event to be processed
     */
    public void keyReleased(KeyEvent ke) { }

    /**
     * 문자 키를 입력했을 때 호출
     * @param ke the event to be processed
     */
    public void keyTyped(KeyEvent ke) { }
    // 이거 추가하는 이유는 KeyListener는 인터페이스 객체라서 모든 메서드를 반드시 구현해야함.

    /**
     * 메인
     * @param args
     */
    public static void main(String args[]) {
        hw_9_6 c = new hw_9_6();
        c.runClient();
    }

    /**
     * 윈도x로 닫기
     */
    class WinListener extends WindowAdapter {
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
}


