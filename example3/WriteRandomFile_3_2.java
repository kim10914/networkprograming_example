package example3;

import java.io*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WriteRandomFile_3_2 extends Frame implements ActionListener{
	private TextField accountField, nameField, balanceField;
	private Button enter, done;
	private RandomAccessFile output;
	private Record data;
	public WriteRandomFile_3_2() {
		super("파일쓰기");
		data = new Record();
		try {
			output = new RandomAccessFile("customer.txt", "rw");
		}catch(IOException e ) {
			System.err.println(e.toString());
			System.exit(1);
		}
		setSize(300,150);
		setLayout(new GridLayout(4,2));
		
		add(new Label("계좌번호"));
		accountField = new TextField();
		add(accountField);
		
		add(new Label("이름"));
		nameField = new TextField();
		add(nameField);
		
		add(new Label("잔고"));
		balanceField = new TextField();
		add(balanceField);
		
		enter = new Button("입력");
		enter.addActionListener(this);
		add(enter);
		
		done = new Button("종료");
		done.addActionListener(this);
		add(done);
	}
	public void addRecord() {
		int accountNo = 0;
		Double d;
		if( ! accountField.getText().equals("")) {
			try {
				accountNo = Integer.parseInt(accountField.getText());
				if(accountNo > 0 && accountNo <= 100) {
					data.setAccount(accountNo);
					data.setName(nameField.getText());
					d = new Double(balanceField.getText());
					data.setBalance(d.doubleValue());
					output.seek((long)(accountNo-1)*Record.size());
					data.write(output);
				}
				accountField.setText("");
				nameField.setText("");
				balanceField.setText("");
			}catch(NumberFormatException nfe) { // 계좌 번호(accountNo) 잔고(d)가 숫자가 아닐 경우(숫자 입력 예외) 
				System.err.println("숫자를 입력하세요"); // 오류 메시지 출력
			}catch(IOException io) { //입출력 예외 발생(파일 저장 중 문제가 생긴경우)
				System.err.println(io.toString()); // 오류 출력
				System.exit(1); // 프로그램 종료
			}
		}
	}
	public void actionPerformed(ActionEvent e) {
		addRecord();
		if(e.getSource() == done) {
			try {
				output.close();
			}catch(IOException io) {
				System.err.println("파일 닫기 에러\n" + io.toString());
			}
			System.exit(0);
		}
	}
	public static void main(String args[]) {
		new WriteRandomFile_3_2();
	}
}

class Record{
	private int account;
	private String name;
	private double balance;
	
	public void read(RandomAccessFile file) throws IOException{
		account = file.readInt();
		char namearray[] = new char[15];
		for(int i = 0; i < namearray.length; i++)
			namearray[i] = file.readChar();
		name = new String(namearray);
		balance = file.readDouble();
	}
	
	public void write(RandomAccessFile file) throws IOException{
		StringBuffer buf;
		file.writeInt(account);
		if(name != null )
			buf = new StringBuffer(name);
		else
			buf = new StringBuffer(15);
		buf.setLength(15);
		file.writeChars(buf.toString());
		file.writeDouble(balance);
	}
	
	public void setAccount(int a) { account = a;}
	public int getAccount() { return account;}
	public void setName(String f) { name = f; }
	public String getName() {return name;}
	public void setBalance(double b) {balance = b;}
	public double getBalance() {return balance;}
	public static int size() {return 42;}
}
