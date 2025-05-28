package example2;

import java.io.*;
import java.awt.*; // 그래필 구성 객체 호출
import java.awt.event.*; // 이벤트 처리 객체 호출

// 
public class ReadSeqFile_2_6 extends Frame implements ActionListener{ //Frame 클래스, 액션 리스너 사용 선언
	private TextField account, name, balance; // Text 입력 필드 클래스 선언
	private Button next, done; // 이벤트를 실행할 버튼 클래스 선언
	private DataInputStream input; // 파일을 읽기 위한 DataInputStream 클래스 선언
	
	// 생성자(GUI 구현)
	public ReadSeqFile_2_6() {
		super("고객파일을 읽음"); //Frame 클래스 생성자 호출 제목 : 고객파일을 읽음
		try {
			input = new DataInputStream(new FileInputStream("client.txt")); //파일을 바이트 단위로 읽는 객체를 다양한 자료형으로 읽을 수 있도록 DataInputStream(이진 파일을 문자열로 읽어옴)으로 래핑 
		}catch (IOException e) { // IO 예외(파일이 없거나, 읽을 수 없는 경우)
			 System.err.println(e.toString()); // 오류 출력
			 System.exit(1); // 비정상 종료
		}
		setSize(250,130); // 가로 250, 세로 130 픽셀의 GUI
		setLayout(new GridLayout(4,2)); // 그리드 레이아웃 4행 2열 생성
		
		add(new Label("계좌번호")); // 라벨 프래임 생성
		account = new TextField(20); // account 텍스트 필드 20지정
		account.setEditable(false); // 사용자 입력이 불가능 하도록 설정
		add(account); // account의 설정된 필드를 프레임에 추가
		
		add(new Label("이름")); 
		name = new TextField(20);
		name.setEditable(false);
		add(name);
		
		add(new Label("잔고"));
		balance = new TextField(20);
		balance.setEditable(false);  
		add(balance);
		
		next = new Button("출력"); // next는 출력 이란 버튼임
		next.addActionListener(this); // 이벤트 발생시 this의 acationPerformed가 호출
		add(next); // 버튼을 프레임에 추가
		
		done = new Button("종료");
		done.addActionListener(this);
		add(done);
		setVisible(true); // 화면에 GUI를 실제로 띄움
	}
	//
	public void actionPerformed(ActionEvent e) { //이벤트 처리 메서드
		if(e.getSource() == next) // 받은 인수가 출력(next)인 경우 
			readRecord(); // readRecord()메서드 실행
		else 
			closeFile(); // 아니면 파일을 닫는다.
	}
	public void readRecord() {
		int accountNo; // 계좌 번호
		double d; // 잔고
		String namedata; // 이름
		
		try {
			accountNo = input.readInt(); // 파일에서 정수를 받아 저장
			namedata = input.readUTF(); // 파일에서 UTF문자를 받아 저장
			d = input.readDouble(); // 파일에서 실수를 받아 저장
			
			account.setText(String.valueOf(accountNo)); // accountNo를 문자열로 변환해서 account 텍스트 필드에 출력
			name.setText(namedata); // namedata를 받아 필드에 출력
			balance.setText(String.valueOf(d)); // d를 문자열로 변환해서 balance 텍스트 필드에 출력
		}catch(EOFException eof) { // 파일의 끝(더 출력할 파일이 없다면)
			closeFile(); // 파일 닫기
		}catch(IOException io) { // 입출력 예외(파일 읽어오기 실패 등) 
			System.err.println(io.toString()); // 오류 출력
			System.exit(1); // 비정상 종료
		}
	}
	private void closeFile() { // 파일 닫기 메서드
		try {
			input.close(); // 입력 스트림(input)을 닫는다.
			System.exit(0); // 정상 종료
		}catch(IOException io) { // 입출력 예외(파일에 접근이 어려운 경우 등)
			System.err.println(io.toString()); // 에러 메시지를 표준 에러 스트림에 출력
			System.exit(1); // 비정상 종료
		}
	}
	public static void main(String args[]) {
		new ReadSeqFile_2_6(); // ReadSeqFile_2_6객체 생성(실행)
	}
}
