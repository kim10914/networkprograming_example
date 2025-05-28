package example5;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;

public class GetHostInfor_5_7 extends Frame implements ActionListener {
	TextField hostname;
	Button getinfor;
	TextArea display;
	public static void main(String args[]) {
		GetHostInfor_5_7 host = new GetHostInfor_5_7("InetAddress 클래스");
		host.setVisible(true);
	}
	public GetHostInfor_5_7(String str) {
		super(str);
		addWindowListener(new WinListener());
		setLayout(new BorderLayout());
		Panel inputpanel = new Panel();
		inputpanel.setLayout(new BorderLayout());
		inputpanel.add("North",new Label("호스트 이름 : "));
		hostname = new TextField("",30);
		getinfor = new Button("호스트 정보 얻기");
		inputpanel.add("Center",hostname);
		inputpanel.add("South",getinfor);
		getinfor.addActionListener(this);
		add("North",inputpanel);
		Panel outputpanel = new Panel();
		display = new TextArea("",24,40);
		display.setEditable(false);
		outputpanel.add("North", new Label("인터넷 주소"));
		outputpanel.add("Center",display);
		add("Center",outputpanel);
		setSize(270,200);
	}
	public void actionPerformed(ActionEvent e) {
		String name = hostname.getText();
		try {
			InetAddress inet = InetAddress.getByName(name);
			String ip = inet.getHostName()+"\n";
			display.append(ip);
		}catch(UnknownHostException ue) {
			String ip = name+": 해당 호스트가 없습니다.\n";
			display.append(ip);
		}
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}
