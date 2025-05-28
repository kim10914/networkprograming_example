package example9;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class hw_9_4 extends Frame implements ActionListener {
    TextArea display;
    TextField text;
    BufferedWriter output;
    BufferedReader input;
    String clientdata = "";
    String serverdata = "";

    public hw_9_4() {
        super("클라이언트");

        Panel p = new Panel();
        p.setLayout(new BorderLayout());
        text = new TextField();
        text.addActionListener(this);
        p.add(text, BorderLayout.CENTER);

        display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        display.setEditable(false);

        add(display, BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);
        addWindowListener(new WinListener());

        setSize(300, 250);
        setVisible(true);

        try {
            Socket client = new Socket(InetAddress.getLocalHost(), 5000);
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            while ((serverdata = input.readLine()) != null) {
                display.append("\r\n[받기] " + serverdata);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            clientdata = text.getText();
            output.write(clientdata + "\r\n");
            output.flush();
            text.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        hw_9_4 c = new hw_9_4();
        c.runClient();
    }

    public void runClient() {
        // 이미 생성자에서 통신을 처리하므로 이 메서드는 필요 없을 수도 있습니다.
    }

    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}
