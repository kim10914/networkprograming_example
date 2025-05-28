package example2;

import java.io.*;
import java.awt.*; // GUI 컴포넌트 호출 클래스
import java.awt.event.*; // 이벤트 처리를 위한 클래스

// 파일을 생성하고 GUI를 통해 문자열을 받아 저장하는 객체
public class CreateSeqFile_2_5 extends Frame implements ActionListener { // frame을 상속받아 윈도우 생성 및 ActionListener를 구현하여 이벤트 처리
	private TextField account, name, balance; // 입력 받는 필드
	private Button enter, done; // 버튼
	private DataOutputStream output; // 파일에 데이터를 저장할 출력 스트림
	//GUI 구성
	public CreateSeqFile_2_5() { //생성자
		super("고객파일을 생성"); // Frame 생성자를 호출함
		try { 
			output = new DataOutputStream(new FileOutputStream("client.txt")); // 바이트 단위 파일을 생성하고 DataOutputStream으로 래핑 
		}catch(IOException e) { // 입출력 예외 발생 시(파일이 없거나, 권한이 없거나, 디스크 오류 발생) 
			System.err.println(e.toString()); // 에러 메시지를 콘솔에 문자열로 출력
			System.exit(1); // 예외가 발생 했으므로 종료
		}
		setSize(250,130); // 가로 250, 세로 130 픽셀
		setLayout(new GridLayout(4,2)); //레이아웃을 4행 2열로 설정
		// 컴포넌트 추가
		add(new Label("계좌번호")); // 라벨 컴포넌트 생성 
		account = new TextField(20); // 입력창 생성
		add(account); // 입력창을 프레임에 추가
		
		add(new Label("이름")); 
        name = new TextField(20);
        add(name);
        
		
		add(new Label("잔고"));
		balance = new TextField(20);
		add(balance);
		
		enter = new Button("입력"); //버튼 객체 생성
		enter.addActionListener(this); //enter를 통해 액션리스너를 선언 버튼 클릭 시 actionPerformed()메서드 호출
		add(enter); // 버튼을 프레임에 추가
		
		done = new Button("종료");
		done.addActionListener(this);
		add(done); 
		setVisible(true); //GUI 창을 사용자에게 화면에 보이도록 설정
	}
	// 입력받은 정보를 파일에 적는 메서드
	public void addRecord() { 
		int accountNo = 0; //계좌번호
		String d; //잔고
		if(!account.getText().equals("")) { //account에 입력된 문자열을 검사, 문자열이 공백이 아닌 경우
			try {
				accountNo = Integer.parseInt(account.getText()); //account에 입력된 문자열을 가져와서 정수로 변환
				if(accountNo > 0) { // 계좌번호가 양수일 경우 
					output.writeInt(accountNo); // 파일에 정수형태로 계좌번호를 저장
					output.writeUTF(name.getText()); // name의 문자열을 받아 UTF 형식으로 파일에 저장 
					d=balance.getText();  // d에 잔고의 문자열을 저장
					output.writeDouble(Double.parseDouble(d)); // d를 실수로 전환하고 파일에 저장
				}
				account.setText("");
				name.setText("");
				balance.setText(""); //입력창을 모두 초기화
			}catch(NumberFormatException nfe) { // 계좌 번호(accountNo) 잔고(d)가 숫자가 아닐 경우(숫자 입력 예외) 
				System.err.println("정수를 입력해야 합니다."); // 오류 메시지 출력
			}catch(IOException io) { //입출력 예외 발생(파일 저장 중 문제가 생긴경우)
				System.err.println(io.toString()); // 오류 출력
				System.exit(1); // 프로그램 종료
			}
		}
	}
	// 버튼 
	public void actionPerformed(ActionEvent e) { // 버튼이 눌렸을 때 자동으로 호출되는 이벤트 처리 메서드 오버라이딩(눌린 버튼으로 정보 받음)
		addRecord(); // 입력 받은 정보 호출
		if(e.getSource() == done) { // 눌린 버튼이 종료(done)라면
			try { 
				output.close(); // 파일 닫기(자원 해제)
			}catch(IOException io) { // 입출력 예외(파일을 닫는 중 오류 발생)
				System.err.println(io.toString()); // 메시지를 출력 
			}
			System.exit(0); //프로그램 정상 종료(0은 정상종료, 1은 비정상 종료)
		}
	}
	public static void main(String args[]) {
		new CreateSeqFile_2_5(); //CreateSeqFile_2_5 객체 생성 GUI 프로그램(생성자) 실행
	}
}
