package example9;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

public class MultipleChatS_9_3 extends Frame {
    TextArea display;
    Label info;
    String clientdata = "";
    String serverdata = "";
    List<ServerThread> list; // 연결된 클라이언트 관리
    public MultipleChatS_9_3() {
        super("서버");
        info = new Label();
        add(info, BorderLayout.CENTER);

        display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        display.setEditable(false);
        add(display, BorderLayout.SOUTH);

        addWindowListener(new WinListener());
        setSize(300, 250);
        setVisible(true);
    }

    public void runServer() {
        ServerSocket server;
        Socket sock;
        try {
            list = new ArrayList<ServerThread>();
            server = new ServerSocket(5000, 100); // 서버 소켓 생성
            while (true) {
                sock = server.accept();
                ServerThread SThread = new ServerThread(this, sock, display, info, serverdata);
                SThread.start(); // 클라이언트별 스레드 실행
                info.setText(sock.getInetAddress().getHostName() + " 서버는 클라이언트와 연결됨");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String args[]) {
        MultipleChatS_9_3 s = new MultipleChatS_9_3();
        s.runServer();
    }

    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
    // 클라이언트별 스레드 클래스
    class ServerThread extends Thread {
        Socket sock;
        InputStream is;
        InputStreamReader isr;
        BufferedReader input;
        OutputStream os;
        OutputStreamWriter osw;
        BufferedWriter output;
        TextArea display;
        Label info;
        TextField text;
        String serverdata = "";
        MultipleChatS_9_3 cs;

        public ServerThread(MultipleChatS_9_3 cs, Socket s, TextArea ta, Label i, String data) {
            sock = s;
            display = ta;
            info = i;
            serverdata = data;
            this.cs = cs;
            try {
                is = sock.getInputStream();
                isr = new InputStreamReader(is);
                input = new BufferedReader(isr);

                os = sock.getOutputStream();
                osw = new OutputStreamWriter(os);
                output = new BufferedWriter(osw);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        public void run() {
            cs.list.add(this); // 연결된 클라이언트를 리스트에 추가
            String clientdata;
            try {
                while ((clientdata = input.readLine()) != null) {
                    display.append(clientdata + "\r\n");
                    int cnt = cs.list.size();
                    for (int i = 0; i < cnt; i++) {
                        ServerThread SThread = cs.list.get(i);
                        SThread.output.write(clientdata + "\r\n");
                        SThread.output.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            cs.list.remove(this); // 연결이 끊긴 클라이언트를 리스트에서 제거
            try {
                sock.close(); // 소켓 닫기
            } catch (IOException ea) {
                ea.printStackTrace();
            }
        }
    }
}



