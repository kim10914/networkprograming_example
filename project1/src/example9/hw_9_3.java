package example9;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

public class hw_9_3 extends Frame {
    TextArea display;
    Label info;
    String clientdata = "";
    String serverdata = "";
    List<ServerThread> list;
    public ServerThread sThread;

    public hw_9_3() {
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
            server = new ServerSocket(5000, 100);
            try {
                while (true) {
                    sock = server.accept();
                    sThread = new ServerThread(this, sock, display, info, serverdata);
                    sThread.start();
                    info.setText(sock.getInetAddress().getHostName() + " 서버는 클라이언트");
                }
            } catch (IOException ioe) {
                server.close();
                ioe.printStackTrace();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String args[]) {
        hw_9_3 s = new hw_9_3();
        s.runServer();
    }

    class WinListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
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
        hw_9_3 cs;

        public ServerThread(hw_9_3 e, Socket s, TextArea ta, Label l, String data) {
            sock = s;
            display = ta;
            info = l;
            serverdata = data;
            cs = e;

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
            cs.list.add(this);
            String clientdata;

            try {
                while ((clientdata = input.readLine()) != null) {
                    display.append(clientdata + "\r\n");

                    int cnt = cs.list.size();
                    for (int i = 0; i < cnt; i++) {
                        ServerThread sThread = (ServerThread) cs.list.get(i);
                        sThread.output.write(clientdata + "\r\n");
                        sThread.output.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            cs.list.remove(this);
            try {
                sock.close();
            } catch (IOException ea) {
                ea.printStackTrace();
            }
        }
    }
}


