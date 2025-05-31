package example13;

import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class MulticastChat_13_2 implements Runnable, WindowListener, ActionListener {

    protected InetAddress group; // 그룹 주소
    protected int port; // 포트 번호
    protected JFrame frame; // 프레임
    protected JTextField input; // 입력창
    protected JTextArea output; // 출력창
    protected Thread listener; // 수신 쓰레드
    protected MulticastSocket socket; // 소켓
    protected DatagramPacket outgoing, incoming;
    protected String loginID;

    // IP, port를 받는 생성자
    public MulticastChat_13_2(InetAddress group, int port) {
        this.group = group;
        this.port = port;
        initSWING(); // GUI 메서드 호출
    }
    // GUI 생성 메서드
    protected void initSWING() {
        frame = new JFrame("멀티캐스트 채팅 [호스트: " + group.getHostAddress() + ":" + port + "]");
        frame.addWindowListener(this);

        output = new JTextArea();
        output.setEditable(false);

        input = new JTextField();
        input.addActionListener(this);

        frame.setLayout(new BorderLayout());
        frame.add(output, BorderLayout.CENTER);
        frame.add(input, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }
    // 쓰레드 생성 및 동기화 진행 메서드(사용자 접속)
    public synchronized void start() throws IOException {
        if (listener == null) {
            initNet();
            listener = new Thread(this); // run() 실행됨
            listener.start();
        }
    }
    // 사용자 초기상태 생성 메서드
    protected void initNet() throws IOException {
        socket = new MulticastSocket(port);
        socket.setTimeToLive(1);
        socket.joinGroup(group);
        outgoing = new DatagramPacket(new byte[1], 1, group, port);
        incoming = new DatagramPacket(new byte[65505], 65505);
    }
    // 쓰레드 종료 및 자원을 회수하는 메서드
    public synchronized void stop() throws IOException {
        frame.setVisible(false);
        if (listener != null) {
            listener.interrupt();
            listener = null;
        }
        try {
            socket.leaveGroup(group);
        } finally {
            socket.close();
            System.exit(0);
        }
    }
    // 창이 열렸을 때 입력창에 입력 포커스를 요청하는 메서드
    public void windowOpened(WindowEvent we) {
        input.requestFocus();
    }
    // x 닫기 구현
    public void windowClosing(WindowEvent we) {
        try {
            stop(); // stop 메서드 호출
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void windowClosed(WindowEvent we) {}
    public void windowIconified(WindowEvent we) {}
    public void windowDeiconified(WindowEvent we) {}
    public void windowActivated(WindowEvent we) {}
    public void windowDeactivated(WindowEvent we) {} // 불필요한 메서드 정의
    //사용자 입력을 보내는 메서드
    public void actionPerformed(ActionEvent ae) {
        try {
            String message = ae.getActionCommand();
            String fullMessage = loginID + ": " + message;

            byte[] utf = fullMessage.getBytes("UTF8");
            outgoing.setData(utf);
            outgoing.setLength(utf.length);
            socket.send(outgoing);
            input.setText("");
        } catch (IOException e) {
            System.out.println(e);
            handleIOException(e);
        }
    }
    //오류가 발생했을 때 종료하는 메서드
    protected synchronized void handleIOException(IOException e) {
        try {
            stop();
        } catch (IOException ie) {
            System.out.println(ie);
        }
    }
    // UX 개선 사용자 종료 여부 물어보기
//    protected synchronized void handleIOException(IOException e) {
//        // 1. 오류 메시지 사용자에게 표시
//        JOptionPane.showMessageDialog(frame,
//                "네트워크 오류가 발생했습니다:\n" + e.getMessage(),
//                "연결 오류",
//                JOptionPane.ERROR_MESSAGE);
//
//        // 2. 사용자에게 종료 여부를 물어봄
//        int result = JOptionPane.showConfirmDialog(frame,
//                "채팅을 종료하시겠습니까?",
//                "종료 확인",
//                JOptionPane.YES_NO_OPTION);
//
//        // 3. 사용자가 종료를 선택한 경우만 stop() 호출
//        if (result == JOptionPane.YES_OPTION) {
//            try {
//                stop();
//            } catch (IOException ie) {
//                System.out.println(ie);
//            }
//        }
//    }
    public void run() {
        try {
            while (!Thread.interrupted()) {
                incoming.setLength(incoming.getData().length);
                socket.receive(incoming);
                String message = new String(incoming.getData(), 0, incoming.getLength(), "UTF8");
                output.append(message + "\n");
            }
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1 || args[0].indexOf(":") == -1) {
            throw new IllegalArgumentException("잘못된 멀티캐스트 주소입니다.");
        }

        int idx = args[0].indexOf(":");
        InetAddress group = InetAddress.getByName(args[0].substring(0, idx));
        int port = Integer.parseInt(args[0].substring(idx + 1));
        //로그인 창 -> 이거 어디서 남?
        String loginID = JOptionPane.showInputDialog(null, "로그인 ID를 입력하세요", "로그인", JOptionPane.PLAIN_MESSAGE); // 사용자에게 로그인 요구하는 창을 띄움
        if (loginID == null || loginID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID가 필요합니다. 프로그램을 종료합니다."); // 사용자에게 로그인 메시지를 보여주고
            return; // 종료
        }

        // 로그인 통신 시도 (여기선 서버 없이 무조건 성공한다고 가정)
        // 서버가 있다면 Socket을 사용하여 요청/응답 처리 필요

        // 로그인 성공 후 멀티캐스트 채팅 시작
        MulticastChat_13_2 chat = new MulticastChat_13_2(group, port); // 멀티캐스트 챗 생성
        chat.start(); // 쓰레드 스타트
        chat.loginID = loginID;
        chat.output.append("[로그인 성공] " + loginID + "님 환영합니다.\n");
    }
}
