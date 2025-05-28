package example9;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class hw_9_2 extends Frame implements ActionListener {
    TextArea display;
    TextField text;
    Label lword;
    BufferedWriter output;
    BufferedReader input;
    Socket client;
    String clientdata = "";
    String serverdata = "";

    public hw_9_2() {
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
        setSize(300, 200);
        setVisible(true);
    }

    // 클라이언트 실행 메서드
    public void runClient() {
        try {
            client = new Socket(InetAddress.getLocalHost(), 5000);
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            while (true) {
                serverdata = input.readLine();
                if (serverdata.equals("quit")) {
                    display.append("\n서버와의 접속이 중단되었습니다.");
                    output.flush();
                    break;
                } else {
                    display.append("\n서버 메시지 : " + serverdata);
                    output.flush();
                }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        clientdata = text.getText();
        try {
            display.append("\n클라이언트 : " + clientdata);
            output.write(clientdata + "\r\n");
            output.flush();
            text.setText("");
            if (clientdata.equals("quit")) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        hw_9_2 c = new hw_9_2();
        c.runClient();
    }

    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}

