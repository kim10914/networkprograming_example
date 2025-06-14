package NIO_Project;

import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatServer extends JFrame {
    private JTextArea display;   // 로그 출력 영역
    private JLabel info;         // 상단 상태 표시
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private Map<String, SocketChannel> userMap = new HashMap<>();
    private Map<SocketChannel, String> reverseMap = new HashMap<>();
    private Gson gson = new Gson();
    private final String LOG_FILE = "chat_log.txt";
    // 서버 GUI
    public ChatServer() {
        super("서버");
        info = new JLabel("서버가 열렸습니다." );
        add(info, BorderLayout.NORTH);

        display = new JTextArea("", 0, 0);
        display.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(
                display,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        add(scrollPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setSize(300, 250);
        setVisible(true);
    }
    // 서버 실행
    public void startServer() {
        try {
            selector = Selector.open(); // 다중 채널의 이벤트를 감지하는 셀렉터를 연다.
            serverChannel = ServerSocketChannel.open();// 서버 소켓 생성
            serverChannel.configureBlocking(false); // 클라언트의 요청을 논 블로킹으로 수신할 수 있도록 설정
            serverChannel.bind(new InetSocketAddress(5000)); // 5000번 포트에 바인딩
            serverChannel.register(selector, SelectionKey.OP_ACCEPT); // 이 채널을 selector에 등록하고 접속수락 이벤트 감지

            log("서버가 포트 5000에서 시작되었습니다.");
            info.setText("서버 실행 중...");
            // 사용자 이벤트 처리
            while (true) {
                selector.select(); // selector가 이벤트가 발생할 때 까지 대기한다.
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator(); // 발생한 이벤트
                // 이벤트를 순회하며 처리
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove(); // 처리한 이벤트는 삭제

                    if (key.isAcceptable()) handleAccept(); // 연결요청
                    else if (key.isReadable()) handleRead(key); // 데이터 수신
                }
            }
        } catch (IOException e) {
            log("서버 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // 클라이언트 접속 수락
    private void handleAccept() throws IOException {
        SocketChannel client = serverChannel.accept(); // 요청 수락 후 소켓채널 객체 생성
        client.configureBlocking(false); // 논블로킹 설정 -> 읽기 작업 블로킹 x
        client.register(selector, SelectionKey.OP_READ); // selector 등록, 읽기 감지 이벤트 등록
        log("새 클라이언트 연결됨: " + client.getRemoteAddress()); // 로그 표시
    }
    // 클라이언트 메시지 수신
    private void handleRead(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel(); // 이벤트 발생 채널
        ByteBuffer buffer = ByteBuffer.allocate(2048); // 데이터를 담을 버퍼
        
        try {
            int read = client.read(buffer); // 버퍼를 통해 데이터를 읽음
            if (read == -1) { // 연결 종료 시 로그아웃 처리
                handleLogout(client);
                return;
            }
            
            buffer.flip(); // 읽기 모드
            String json = StandardCharsets.UTF_8.decode(buffer).toString().trim(); // UTF8로 디코딩 -> JSON 형태로 변환
            Message msg = gson.fromJson(json, Message.class); // JSON 문자열을 message 객체로 파싱

            switch (msg.getType()) { // 메시지 타입에 따라 분기 처리
                case MessageType.LOGON:
                    handleLogon(msg, client);
                    break;
                case MessageType.LOGOUT:
                    handleLogout(client);
                    break;
                case MessageType.CHAT: // 일반 채팅
                    broadcast(msg);
                    break;
                case MessageType.WISPERSEND: // 귓속말
                    whisper(msg);
                    break;
            }

            logMessage(msg); // 메시지를 파일로 저장

        } catch (IOException e) {
            handleLogout(client);
        }
    }
    // 로그온 처리 메서드
    private void handleLogon(Message msg, SocketChannel client) throws IOException {
        String id = msg.getSender(); // 클라이언트
        // 중복 처리 -> 분기에 따른 메시지 객체 전송
        if (userMap.containsKey(id)) { // 중복
            Message fail = new Message(MessageType.LOGON_FAIL, "server", null, "중복된 ID입니다.", null);
            sendMessage(client, fail);
        } else { // 중복 x
            userMap.put(id, client);
            reverseMap.put(client, id);

            Message success = new Message(MessageType.LOGON_SUCCESS, "server", null, id, new ArrayList<>(userMap.keySet()));
            sendMessage(client, success);

            Message joinMsg = new Message(MessageType.USER_JOIN, "server", null, id + "님이 입장했습니다.", null);
            broadcastExcept(joinMsg, id);
        }
    }
    // 로그아웃 처리 메서드
    private void handleLogout(SocketChannel client) {
        try {
            String id = reverseMap.get(client); // 해당 클라이언트
            if (id != null) {
                userMap.remove(id);
                reverseMap.remove(client); // 사용자 삭제
                client.close(); // 소켓 닫기
                log(id + " 로그아웃");

                Message leave = new Message(MessageType.USER_LEAVE, "server", null, id + "님이 퇴장했습니다.", null);
                broadcast(leave); // 메시지 전송
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 브로드 캐스트
    private void broadcast(Message msg) {
        userMap.values().forEach(channel -> sendMessage(channel, msg)); // 전 사용자에게 메시지 전송
    }
    // 사용자 입장, 퇴장
    private void broadcastExcept(Message msg, String excludeID) {
        userMap.forEach((id, ch) -> { //해당 사용자를 제외한 모든 사용자에게 메시지 전송
            if (!id.equals(excludeID)) sendMessage(ch, msg);
        });
    }
    // 귓속말
    private void whisper(Message msg) {
        SocketChannel receiver = userMap.get(msg.getReceiver());
        SocketChannel sender = userMap.get(msg.getSender());

        if (receiver != null) {
            // sender, receiver 둘다 보냄
            sendMessage(receiver, msg);
            sendMessage(sender, msg);
        } else {
            // 대상 x
            msg.setReceiver("NONE");
            sendMessage(sender, msg);
        }
    }
    // 메시지 전송
    private void sendMessage(SocketChannel ch, Message msg) {
        try {
            String json = gson.toJson(msg); // json 문자열 객체 생성
            ByteBuffer buffer = ByteBuffer.wrap((json + "\n").getBytes(StandardCharsets.UTF_8)); // UTF8 방식으로 인코딩
            ch.write(buffer); // 해당 클라이언트에 데이터 전송
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 로그 저장(파일)
    private void logMessage(Message msg) {
        if (MessageType.CHAT.equals(msg.getType()) || MessageType.WISPERSEND.equals(msg.getType())) {
            String logEntry = "[" + msg.getSender() + "] " + msg.getContent(); // 메시지
            log(logEntry); // GUI에 로그 출력
            try (FileWriter fw = new FileWriter(LOG_FILE, true); // 파일에 작성
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(logEntry);
                bw.newLine();
            } catch (IOException e) {
                log("로그 저장 실패: " + e.getMessage());
            }
        }
    }
    // GUI에 작성
    private void log(String msg) {
        System.out.println(msg);
        display.append(msg + "\n");
        display.setCaretPosition(display.getDocument().getLength());
    }

    public static void main(String[] args) {
        new ChatServer().startServer();
    }
}
