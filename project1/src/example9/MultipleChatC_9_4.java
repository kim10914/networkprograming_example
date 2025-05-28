package example9;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class MultipleChatC_9_4 extends Frame implements ActionListener {
    TextArea display;
    TextField text;
    Label lword;
    BufferedWriter output;
    BufferedReader input;
    Socket client;
    String clientdata = "";
    String serverdata = "";

    public MultipleChatC_9_4() {
        super("클라이언트");

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
        setSize(300, 150);
        setVisible(true);
    }

    // 클라이언트 실행 메서드
    public void runClient() {
        try {
            client = new Socket(InetAddress.getLocalHost(), 5000);
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            while (true) {
                serverdata = input.readLine(); // 서버로부터 데이터 수신
                display.append("\r\n" + serverdata); // 수신한 메시지 출력
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 메시지 전송 메서드
    public void actionPerformed(ActionEvent ae) {
        clientdata = text.getText();
        try {
            display.append("\r\n나의 대화말 : " + clientdata);
            output.write(clientdata + "\r\n");
            output.flush();
            text.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        MultipleChatC_9_4 c = new MultipleChatC_9_4();
        c.runClient();
    }

    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}

