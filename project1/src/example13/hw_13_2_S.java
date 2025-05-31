/*예제 13.2를 참조하여 예제 9.5와 9.6을 멀티캐스트 채팅 서버와 클라이언트로 수정하시오.
- 클라이언트는 프로그램 실행 시 로그인 아이디를 입력 받아 서버에 로그인 요청을 함
- 클라이언트로부터 로그온 요청 메시지를 수신하면 서버는 중복 아이디가 아닌 경우 멀티캐스트 그룹 주소를 회신함
- 클라이언트는 로그온 성공 시 서버가 보내온 멀티캐스트 그룹 주소로 멀티캐스트 그룹에 가입함
- 로그온 사용자들만 채팅에 참가할 수 있음
- 로그 아웃 시 클라이언트는 그룹에서 탈퇴하고 초기화면으로 돌아가며, 창을 닫을 때도 로그아웃 처리
- 필요한 메시지를 정의하여 구현함*/
package example13;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.event.*;

public class hw_13_2_S extends JFrame {
    JTextArea display; // 사용자 텍스트 창
    JLabel info; // 정보 나타내는 라벨
    List<hw_13_2_S.ServerThread> list; // 사용자 쓰레드 리스트 컬랙션으로 관리

    Set<String> login_users = Collections.synchronizedSet(new HashSet<>());
    private final int port = 3000; // UDP 포트
    private final String ip = "230.0.0.1";
    private final int TCP_PORT = 5000; // TCP 포트

    private MulticastSocket multicastSocket;
    private InetAddress group;

    //GUI 생성자
    public hw_13_2_S() {
        super("서버");
        // 상단 정보 출력 창
        info = new JLabel();
        add(info, BorderLayout.NORTH);

        // 중앙 메시지 출력 창
        display = new JTextArea("", 0, 0);
        display.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(display,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// 수직 스크롤 표시, 수평 스크롤 숨김
        add(scrollPane, BorderLayout.CENTER);

        addWindowListener(new hw_13_2_S.WinListener());
        setSize(300, 250);
        setVisible(true);
        //UDP 서버 및 패킷 정의
        try {
            group = InetAddress.getByName(ip);
            multicastSocket = new MulticastSocket();
            multicastSocket.setTimeToLive(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //서버 실행 메서드
    public void runServer() {
        try (ServerSocket server = new ServerSocket(TCP_PORT)){
            while (true) {
                Socket sock = server.accept();
                new ServerThread(sock , group).start();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    //메인
    public static void main(String args[]) {
        hw_13_2_S s = new hw_13_2_S();
        s.runServer();
    }

    // 윈도우 x로 닫기
    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    //사용자 쓰레드 구현
    class ServerThread extends Thread {
        Socket sock; // 소켓
        String loginID; // 사용자 ID
        BufferedWriter output; // 쓰기
        BufferedReader input; // 읽기

        // 서버간 메시지 프로토콜을 정의한 상수
        private static final String SEPARATOR = "|"; // |를 통해 분리
        private static final int REQ_LOGON = 1001; // 로그온 코드
        private static final int RES_LOGON_OK = 1002;
        private static final int RES_LOGON_FAIL = 1003; // 로그온 실패
        private static final int REQ_SENDWORDS = 1021; // 메시지 전송 코드
        private static final int REQ_LOGOUT = 1022; // 로그아웃 코드

        //서버 쓰레드 생성자
        public ServerThread(Socket s, InetAddress group ) {
            this.sock = s;
            try {
                input = new BufferedReader(new InputStreamReader(sock.getInputStream())); // 소켓 바이트 스트림 읽기
                output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())); // 소켓 바이트 스트림 출력
                info.setText("멀티캐스트 채팅 그룹 주소 : " + group.getHostAddress());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        // 메시지 전송 메서드
        private void sendMulticastNotice(String message) throws IOException {
            byte[] data = message.getBytes("UTF8");
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
            multicastSocket.send(packet);
        }
        
        //쓰레드 run 메서드
        public void run() {
            try {
                String clientdata;
                // 사용자 입력을 받아 처리하는 블럭
                while ((clientdata = input.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR); // 구분자 단위로 문자열 나눠 토큰으로 저장(해당 문자열, 구분자) -> 명령을 판단하기 위해 사용
                    int command = Integer.parseInt(st.nextToken()); // 코드 저장

                    switch (command) { // command 처리
                        case REQ_LOGON: { // 로그온
                            String ID = st.nextToken();
                            synchronized (login_users) {
                                if (login_users.contains(ID)) {
                                    output.write(RES_LOGON_FAIL + SEPARATOR + "\r\n"); // 오류 코드 전송
                                    output.flush();
                                    return; // 중복시 쓰레드 진행 x
                                } else {
                                    login_users.add(ID);
                                    this.loginID = ID;
                                    display.append("클라이언트가 " + ID + "(으)로 로그인 하였습니다.\n");
                                    output.write(RES_LOGON_OK + "|" + ip + "|" + port + "\r\n"); // 로그인 성공 코드 전송
                                    output.flush();

                                    sendMulticastNotice("[시스템] " + ID + "님이 입장했습니다.");
                                }
                            }
                            break;
                        }
                        case REQ_LOGOUT: { // 로그아웃
                            String ID = st.nextToken();
                            login_users.remove(ID);
                            display.append("로그아웃 : " + ID + "\n");

                            sendMulticastNotice("[시스템] " + ID + "님이 퇴장했습니다.");
                            break;
                        }
                        case REQ_SENDWORDS: { // 메시지 전송
                            String ID = st.nextToken();
                            String message = st.nextToken();
                            String fullMessage = ID + " : " + message;
                            display.append(fullMessage + "\n"); // 서버 콘솔에도 출력
                            sendMulticastNotice(fullMessage);   // 멀티캐스트로 전송
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    sock.close(); // 소켓 닫기
                } catch (IOException ea) {
                    ea.printStackTrace();
                }
            }
        }
    }
}
