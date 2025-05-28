// GUI를 수정 로컬 호스트의 정보를 TextArea에 출력
// 호스트 이름, 호스트 주소(여러 개라면 목록), 주소의 클래스 CanonicalHostName, 해시 코드, 로컬소트의 루프백 주소를 출력할 수 있도록 작성
package example4;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;


public class hw_4_3 extends JFrame implements ActionListener {
	TextField hostname;
	Button getinfor, getinfor2;
	TextArea display, display2;
	
	static String ipClass(byte[] ip) { // IP주소를 바이트 배열로 받는 메서드 (예 - 192.168.0.1 -> -64,-88,0,1 -> 오버 플로...)
		int ipByte = 0xff&ip[0]; // IP주소의 첫 옥텟을 받아 부호 없는 바이트로 처리함
		return(ipByte<128)?"A":(ipByte<192)?"B":(ipByte<224)?"C":(ipByte<240)?"D":"E"; // 각 바이트 별 클래스  판별
	}
	
	public static void main(String args[]) {
		hw_4_3 host = new hw_4_3("InetAddress 클래스");
		host.setVisible(true);
	}
	public hw_4_3(String str) {
		super(str);
		addWindowListener(new WinListener());
		setLayout(new BorderLayout());
		
		// 로컬 호스트(상단)
		JPanel topPanel = new JPanel(new BorderLayout());
		
		getinfor = new Button("로컬 호스트 정보 얻기");
		getinfor.addActionListener(this);
		topPanel.add(getinfor,BorderLayout.NORTH);
		
		display = new TextArea(6,40);
		display.setEditable(false);
		topPanel.add(display,BorderLayout.CENTER);
		
		add(topPanel,BorderLayout.NORTH);
		
		// 원격 호스트 입력(중단)
		JPanel midPanel = new JPanel(new BorderLayout());
		
		midPanel.add(new Label("호스트 이름"),BorderLayout.NORTH);
		hostname = new TextField("",30);
		midPanel.add(hostname, BorderLayout.CENTER);
		
		add(midPanel,BorderLayout.CENTER);
		
		// 원격 호스트 정보, 라벨(하단)
		JPanel bottomdPanel = new JPanel(new BorderLayout());
		
		getinfor2 = new Button("원격호스트 정보 얻기");
		getinfor2.addActionListener(this);
		bottomdPanel.add(getinfor2,BorderLayout.NORTH);
		
		bottomdPanel.add(new Label("원격 호스트 정보"), BorderLayout.CENTER);
		
		display2 = new TextArea(8,40);
		display2.setEditable(false);
		bottomdPanel.add(display2,BorderLayout.SOUTH);
		add(bottomdPanel,BorderLayout.SOUTH);
		
		setSize(300,400); //
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == getinfor) { // 로컬  호스트 정보를 입력 받았을 경우
			display.setText("");
			try {
				InetAddress local = InetAddress.getLocalHost(); // 호스트를 받아 InetAddress 객체로 처리
				display.append("로컬 호스트 정보\n");
				display.append("호스트 이름 : " + local.getHostName() + "\n");
				display.append("호스트 주소: " + local.getHostAddress() + "\n");
                display.append("클래스: " + ipClass(local.getAddress()) + "\n");
                display.append("Canonical Host Name: " + local.getCanonicalHostName() + "\n"); // 표준 호스트 이름
                display.append("해시 코드: " + local.hashCode() + "\n");
                display.append("루프백 주소: " + InetAddress.getLoopbackAddress() + "\n"); // 각 정보 GUI에 출력
			} catch(UnknownHostException ue) {
				display.append("로컬 호스트를 가져 올 수 없습니다.\n");
			}
		} else if(e.getSource() == getinfor2) {
			String name = hostname.getText();
			display2.setText("");
			try {
				InetAddress[] hosts = InetAddress.getAllByName(name); // 원격 호스트는 객체 배열로 받음
				display2.append("원격 호스트 정보\n");
				display2.append("호스트 이름 : " + name + "\n");
				for(InetAddress host : hosts) { // 범위 기반 for 루프
					display2.append("호스트 주소: " + host.getHostAddress() + "\n");
                    display2.append("클래스: " + ipClass(host.getAddress()) + "\n");
                    display2.append("Canonical Host Name: " + host.getCanonicalHostName() + "\n");
                    display2.append("해시 코드: " + host.hashCode() + "\n"); // 각 정보 출력
				}
				
			}catch(UnknownHostException ue) {
				display.append("원격 호스트" + name +"를 찾을 수 없습니다.\n");
			}
		}
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}
