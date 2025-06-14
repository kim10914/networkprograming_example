package NIO_Project;

import com.google.gson.Gson;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatClient extends JFrame implements ActionListener, KeyListener {
    JLabel mlbl, wlbl, loglbl;      // 상태, 대화, 로그온 레이블
    JTextArea display;              // 메시지 표시창
    JTextField wtext, ltext;        // 대화 입력, 로그인 입력창
    JButton logbtn; // 로그아웃 버튼

    SocketChannel client; // NIO 소켓
    Selector selector; // NIO 이벤트 감시
    BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>(); // 송신 메시지를 담는 큐
    Gson gson = new Gson(); // Gson 객체(json 객체를 파싱 및 매핑)

    String ID = null; // 사용자

    public ChatClient() {
        super("클라이언트");
        // 상단 레이블
        mlbl = new JLabel("접속 완료. 사용할 아이디를 입력하세요.");
        add(mlbl, BorderLayout.NORTH);

        //내용 표시(중단
        display = new JTextArea("", 0, 0);
        display.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(display, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // 하단 패널 구성
        Panel ptotal = new Panel(new BorderLayout());
        Panel pword = new Panel(new BorderLayout());
        // 메시지 입력 창
        wlbl = new JLabel("대화말:");
        wtext = new JTextField(30);
        wtext.addKeyListener(this);
        pword.add(wlbl, BorderLayout.WEST);
        pword.add(wtext, BorderLayout.CENTER);
        // 로그온 창
        Panel plabel = new Panel(new BorderLayout());
        loglbl = new JLabel("로그온:");
        ltext = new JTextField(30);
        ltext.addActionListener(this);
        plabel.add(loglbl, BorderLayout.WEST);
        plabel.add(ltext, BorderLayout.CENTER);
        // 버튼 추가
        logbtn = new JButton("on");
        logbtn.addActionListener(this);
        plabel.add(logbtn, BorderLayout.EAST);
        // 각 패널 배치
        ptotal.add(pword, BorderLayout.NORTH);
        ptotal.add(plabel, BorderLayout.SOUTH);
        add(ptotal, BorderLayout.SOUTH);
        // x 로 닫기
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                logout();
                System.exit(0);
            }
        });

        setSize(300, 250);
        setVisible(true);
    }
    // 클라이언트 실행
    public void runClient() {
        try {
            selector = Selector.open(); // 이벤트를 감시하는 NIO 객체 생성
            client = SocketChannel.open(); // 비동기 클라이언트 소켓 생성
            client.configureBlocking(false); // 논 블로킹 모드 설정
            client.connect(new InetSocketAddress("localhost", 5000)); // 서버 연결 시도
            client.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ); // selector는 연결완료, 읽기 가능 이벤트를 감지함

            new Thread(() -> listenServer()).start(); // 서버 수신 쓰레드
            new Thread(() -> sendLoop()).start(); // 메시지 전송 쓰레드

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 서버 수신 쓰레드 메서드
    private void listenServer() {
        try {
            while (true) { // 계속해서 서버의 메시지를 들음
                selector.select(); // 등록된 채널 중 이벤트가 발생한 채널 감지(블로킹 호출)
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator(); // 이벤트가 발생한 채널들의 key 집합을 반복 처리
                while (iter.hasNext()) { // 이벤트 키에 대해 하나씩 처리
                    SelectionKey key = iter.next(); // 다음 이벤트를 읽음
                    iter.remove(); // 진행 이벤트 삭제
                    // 연결을 완료시킴
                    if (key.isConnectable()) {
                        if (client.isConnectionPending()) { // 연결 대기 중 이라면
                            client.finishConnect(); // 연결 완료
                        }
                    }
                    // 서버에서 데이터를 받음
                    if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(2048); // 서버 데이터를 읽을 버퍼
                        int read = client.read(buffer); // 수신 데이터를 버퍼에 채움
                        if (read == -1) { // 연결 종료 시 수신 쓰레드 종료
                            return;
                        }
                        buffer.flip(); // 버퍼 읽기 모드 전환
                        String json = StandardCharsets.UTF_8.decode(buffer).toString().trim(); // UTF8문자열로 디코딩
                        Message msg = gson.fromJson(json, Message.class); // gson을 이용해 message 객체로 파싱및 매핑
                        receiveMessage(msg); // 수신 메시지를 receiveMessage 메서드로 넘겨준다.
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 송신 쓰레드 메서드
    private void sendLoop() {
        try {
            while (true) {
                String json = sendQueue.take(); // 문자열 하나 추출
                ByteBuffer buffer = ByteBuffer.wrap((json + "\n").getBytes(StandardCharsets.UTF_8)); // UTF8방식으로 인코딩
                client.write(buffer); // 서버에 전송
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 로그아웃
    private void logout() {
        if (ID != null) {
            // 메시지 객체 생성
            Message msg = new Message();
            msg.setType(MessageType.LOGOUT);
            msg.setSender(ID);
            sendQueue.offer(gson.toJson(msg));

            try {
                if (client != null && client.isOpen()) {
                    client.close(); // 소켓 닫기
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 초기화면
            ID = null;
            mlbl.setText("접속 완료. 사용할 아이디를 입력하세요.");
            ltext.setEnabled(true);
            ltext.setText("");
            logbtn.setLabel("ON");

            new Thread(() -> runClient()).start();
        }
    }
    // 로그온
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logbtn && "OFF".equals(logbtn.getLabel())) {
            logout(); // 로그아웃 로직 실행
        } else if (ID == null) { // 로그인 되지 않은 상태면
            ID = ltext.getText();
            if (ID.isEmpty()) return;

            Message msg = new Message();
            msg.setType(MessageType.LOGON);
            msg.setSender(ID);
            sendQueue.offer(gson.toJson(msg)); // 로그온 메시지 객체 추가
        }
    }
    // 서버로 부터 받은 메시지 처리
    public void receiveMessage(Message msg) {
        switch (msg.getType()) {
            case MessageType.LOGON_SUCCESS: // 로그인 성공
                ID = msg.getContent();
                mlbl.setText(ID + "으로 로그인하였습니다.");
                ltext.setEnabled(false);
                logbtn.setLabel("OFF");
                break;
            case MessageType.LOGON_FAIL: // 로그인 실패
                mlbl.setText("중복된 ID입니다. 다시 입력하세요.");
                ID = null;
                break;
            case MessageType.CHAT: // 일반 메시지
            case MessageType.USER_JOIN: // 사용자 입장
            case MessageType.USER_LEAVE: // 사용자 나감
                display.append(msg.getSender() + ": " + msg.getContent() + "\n");
                break;
            case MessageType.WISPERSEND: // 귓속말
                if (msg.getReceiver().equals("NONE")) {
                    mlbl.setText("귓속말 대상이 없습니다!");
                } else if (msg.getReceiver().equals(ID) || msg.getSender().equals(ID)) {
                    display.append("(귓속말) " + msg.getSender() + " → " + msg.getReceiver() + ": " + msg.getContent() + "\n");
                }
                break;
        }
    }
    // 메시지 처리
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
            String message = wtext.getText().trim();
            if (message.isEmpty() || ID == null) return;

            Message msg = new Message(); // 전송 메시지 객체 생성
            if (message.startsWith("/w ")) { // 귓속말 처리
                String[] parts = message.split(" ", 3);
                if (parts.length >= 3) {
                    msg.setType(MessageType.WISPERSEND);
                    msg.setSender(ID);
                    msg.setReceiver(parts[1]);
                    msg.setContent(parts[2]); // /w user message
                } else {
                    return;
                }
            } else {
                msg.setType(MessageType.CHAT);
                msg.setSender(ID);
                msg.setContent(message); // 일반채팅
            }

            sendQueue.offer(gson.toJson(msg)); // 메시지를 큐에 추가
            wtext.setText("");
        }
    }

    public void keyReleased(KeyEvent ke) {}
    public void keyTyped(KeyEvent ke) {}

    public static void main(String[] args) {
        ChatClient c = new ChatClient();
        c.runClient();
    }
}
