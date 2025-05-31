package example13;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class hw_13_2_C extends JFrame implements ActionListener, KeyListener {
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

    MulticastSocket multicastSocket;
    InetAddress group;
    int multicastPort;

    // 프로토콜 코드 상수로 지정
    private static final String SEPARATOR = "|"; // 구분자
    private static final int REQ_LOGON = 1001; // 로그온
    private static final int RES_LOGON_OK = 1002;
    private static final int RES_LOGON_FAIL = 1003;
    private static final int REQ_SENDWORDS = 1021; // 메시지전송
    private static final int REQ_LOGOUT = 1022;

    // GUI 생성자
    public hw_13_2_C() {
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
        addWindowListener(new hw_13_2_C.WinListener());

        setSize(400, 300);
        setVisible(true);
    }


   // 클라이언트 접속 메서드
   public void runClient() {
       new Thread(() -> {
           try {
               client = new Socket(InetAddress.getLocalHost(), 5000);
               mlbl.setText("연결된 서버 이름 : " + client.getInetAddress().getHostName());
               input = new BufferedReader(new InputStreamReader(client.getInputStream()));
               output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
               clientdata = new StringBuffer(2048);

               mlbl.setText("접속 완료. 사용할 아이디를 입력하세요.");

               while ((serverdata = input.readLine()) != null) {
                   if (serverdata.startsWith(RES_LOGON_OK + SEPARATOR)) { // 성공 코드 받았을 경우
                       String[] parts = serverdata.split("\\|");
                       String multicastIP = parts[1]; // IP 저장
                       multicastPort = Integer.parseInt(parts[2]); // port 번호 저장

                       group = InetAddress.getByName(multicastIP);
                       if (!group.isMulticastAddress()) throw new IOException("멀티캐스트 주소 아님"); // 멀티캐스트 오류 검사

                       multicastSocket = new MulticastSocket(multicastPort);
                       multicastSocket.joinGroup(group);
                       display.append("멀티캐스트 채팅 그룹 주소는" + group.getHostAddress() + "입니다.");
                       // 멀티캐스트 통신을 받기위한 쓰레드 초기 설정
                       new Thread(() -> {
                           try {
                               while (true) {
                                   byte[] buf = new byte[65536];
                                   DatagramPacket packet = new DatagramPacket(buf, buf.length);
                                   multicastSocket.receive(packet);
                                   String msg = new String(packet.getData(), 0, packet.getLength(), "UTF8");
                                   display.append(msg + "\n");
                               }
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }).start(); // UDP 쓰레드 run
                   } else if (serverdata.startsWith(RES_LOGON_FAIL + SEPARATOR)) { // 로그인 실패 코드
                       SwingUtilities.invokeLater(() -> {
                           mlbl.setText("중복된 아이디 입니다.");
                           ltext.setVisible(true);
                           ltext.setEditable(true);
                           ltext.setText("");
                           plabel.removeAll();
                           plabel.add(loglbl, BorderLayout.WEST);
                           plabel.add(ltext, BorderLayout.CENTER);
                           plabel.revalidate();
                           plabel.repaint(); // 패널 초기 상태로 스위칭
                       });
                   } else {
                       display.append(serverdata + "\n"); // 서버 메시지 수신
                   }
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }).start(); // TCP 쓰레드 run
   }

    //입력 처리
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if(source == ltext){
            ID = ltext.getText();
            mlbl.setText("멀티캐스트 채팅 서버에 가입 요청 합니다!");
            try {
                clientdata.setLength(0);
                clientdata.append(REQ_LOGON).append(SEPARATOR).append(ID);
                output.write(clientdata + "\r\n");
                output.flush();
                mlbl.setText(ID + "(으)로 로그인 하였습니다.");

                //로그아웃 버튼 전환
                plabel.removeAll();
                logoutBtn = new JButton("로그아웃");
                logoutBtn.addActionListener(this);
                plabel.add(logoutBtn, BorderLayout.CENTER); // 버튼 추가
                plabel.revalidate(); // 레이아웃 재계산
                plabel.repaint(); // 화면 갱신
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(source == logoutBtn){ // 로그아웃 버튼 눌릴 때
            logoutAndRestart();
        }
    }
    // 로그아웃 하고 다시 시작
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

            if (group != null && group.isMulticastAddress() && multicastSocket != null && !multicastSocket.isClosed()) {
                multicastSocket.leaveGroup(group);
                multicastSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            display.append("[시스템] 서버 연결 종료\n");
        }

        // 상태 초기화
        ID = null;
        ltext.setEditable(true);
        ltext.setText("");
        plabel.remove(logoutBtn);
        display.setText("");
        mlbl.setText("채팅 상태를 보여줍니다.");
        // 로그인 패널 재배치
        plabel.removeAll();
        plabel.add(loglbl, BorderLayout.WEST);
        plabel.add(ltext, BorderLayout.CENTER);
        plabel.revalidate();
        plabel.repaint();

        runClient(); // 클라이언트 다시 실행

    }

    //사용자 입력을 받는 메서드
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
            String message = wtext.getText();
            if (ID == null || ID.trim().isEmpty()) {
                mlbl.setText("로그인 후 이용하세요!!!");
                wtext.setText("");
            } else {
                try {
                    clientdata.setLength(0);
                    clientdata.append(REQ_SENDWORDS).append(SEPARATOR).append(ID).append(SEPARATOR).append(message);
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
        hw_13_2_C c = new hw_13_2_C();
        c.runClient();
    }

    // x 닫기
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

