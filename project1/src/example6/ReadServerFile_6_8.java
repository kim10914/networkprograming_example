package example6;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class ReadServerFile_6_8 extends JFrame implements ActionListener{
	private TextField enter;
	private TextArea contents, contents2;
	public ReadServerFile_6_8() {
		super("호스트 파일 읽기");
		setLayout( new BorderLayout());
		enter = new TextField("URL을 입력하세요");
		enter.addActionListener(this);
		add(enter, BorderLayout.NORTH);
		
		contents = new TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		add(contents, BorderLayout.CENTER);
		
		contents2 = new TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		add(contents2,BorderLayout.SOUTH);
		
		addWindowListener(new WinListener());
		setSize(350,500);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		URL url;
		InputStream is;
		BufferedReader input;
		String line;
		StringBuffer buffer = new StringBuffer();
		String location = e.getActionCommand();
		try {
			url = new URL(location);
			
			buffer.append("원격 호스트의 프로토콜은 : " + url.getProtocol()).append('\n');
			buffer.append("원격 호스트의 호스트 이름는 : " +url.getHost()).append('\n');
			buffer.append("원격 호스트의 포트 번호는 : " + url.getPort()).append('\n');
			buffer.append("원격 호스트의 파일이름는 : " + url.getFile()).append('\n');
			buffer.append("원격 호스트의 path는" + url.getPath()).append('\n');
			buffer.append("원격 호스트의 ref는" + url.getRef()).append('\n');
			buffer.append("원격 호스트의 해쉬코드는 : "+ url.hashCode()).append('\n');
			contents.setText(buffer.toString());
			URLConnection urlcon = url.openConnection();
			String contentType = urlcon.getContentType();
			Object o = url.getContent();
			StringBuffer buffer2 = new StringBuffer();
			//비디오일때
			if(contentType.equals("video/mp4")){
				contents2.setText("파일을 읽는 중입니다...");
				contents2.setText("이 파일은 비디오 입니다\n");
			}
			//오디오일때
			else if(contentType.equals("audio/mpeg")){
				contents2.setText("파일을 읽는 중입니다...");
				contents2.setText("이 파일은 오디오 입니다\n");
			//텍스트일때	
			}else if(o.getClass().getName().contains("InputStream")){
				is = url.openStream();
				input = new BufferedReader(new InputStreamReader(is));
				contents2.setText("파일을 읽는 중입니다...");
				while((line = input.readLine())!=null) {
						buffer2.append(line).append('\n');
						contents2.setText(buffer2.toString());
				}
				input.close();
			//이미지일때
			}else if(o.getClass().getName().contains("Image")){
				contents2.setText("파일을 읽는 중입니다...");
				contents2.setText("이 파일은 이미지 파일 입니다\n");
			}
		}catch(MalformedURLException mal) {
			contents.setText("URL 형식이 잘못되었습니다.");
		}catch(IOException io) {
			contents.setText(io.toString());
		}catch(Exception ex) {
			contents.setText("호스트 컴퓨터의 파일만을 열 수 있습니다.");
		}
	}
	public static void main(String args[]) {
		ReadServerFile_6_8 read = new ReadServerFile_6_8();
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}
