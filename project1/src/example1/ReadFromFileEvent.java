package example1;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

class ReadFromFileEvent extends Frame implements ActionListener{
	Label lfile;
	TextField tfile;
	TextArea tadata;
	String filename;
	public ReadFromFileEvent(String str) {
		super(str);
		setLayout(new FlowLayout());
		lfile = new Label("파일을 입력하시오");
		add(lfile);
		tfile = new TextField(20);
		tfile.addActionListener(this);
		add(tfile);
		tadata = new TextArea(3,35);
		add(tadata);
		addWindowListener(new WinListener());
	}
	public static void main(String args[]) {
		ReadFromFileEvent text = new ReadFromFileEvent("파일읽기");
		text.setSize(270,160);
		text.show();
	}
	public void actionPerformed(ActionEvent ae) {
		byte buffer[] = new byte[100];
		filename = tfile.getText();
		try {
			FileInputStream fin = new FileInputStream(filename);
			fin.read(buffer);
			String data = new String(buffer);
			tadata.setText(data+"\n");
			System.out.println(data);
		}catch(IOException e) {
			System.out.println(e.toString());
		}
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}
