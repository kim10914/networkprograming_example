package example9;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class OneToOneS_9_1 extends Frame implements ActionListener {
    TextArea display;
    TextField text;
    Label lword;
    Socket connection;
    BufferedWriter output;
    BufferedReader input;
    String clientdata = "";
    String serverdata = "";

    public OneToOneS_9_1() {
        super("서버");
        display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        display.setEditable(false);
        add(display, BorderLayout.CENTER);

        Panel pword = new Panel(new BorderLayout());
        lword = new Label("대화입력");
        text = new TextField(30); // 전송할 데이터를 입력하는 필드
        text.addActionListener(this); // 입력된 데이터를 송신하기 위한 이벤트 연결

        pword.add(lword, BorderLayout.WEST);
        pword.add(text, BorderLayout.EAST);
        add(pword, BorderLayout.SOUTH);

        addWindowListener(new WinListener());
        setSize(300, 200);
        setVisible(true);
    }

    // 서버 실행 메서드
    public void runServer() {
        ServerSocket server;
        try {
            server = new ServerSocket(5000, 100);
            connection = server.accept();

            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            input = new BufferedReader(isr); // 서버가 전송한 대화말을 수신

            OutputStream os = connection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            output = new BufferedWriter(osw); // 클라이언트에 대화말을 전송

            while (true) {
                clientdata = input.readLine();
                if (clientdata.equals("quit")) {
                    display.append("\n클라이언트와의 접속이 종료되었습니다.\n");
                    output.flush();
                    break;
                } else {
                    display.append("\n클라이언트 메시지 : " + clientdata);
                    output.flush();
                }
            }
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 서버 대화 전송
    public void actionPerformed(ActionEvent ae) {
        serverdata = text.getText();
        try {
            display.append("\n서버 : " + serverdata);
            output.write(serverdata + "\r\n");
            output.flush();
            text.setText("");
            if (serverdata.equals("quit")) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // main 메서드
    public static void main(String args[]) {
        OneToOneS_9_1 s = new OneToOneS_9_1();
        s.runServer();
    }

    // 윈도우 종료 이벤트 핸들러
    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}
