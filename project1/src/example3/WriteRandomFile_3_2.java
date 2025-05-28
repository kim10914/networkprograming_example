//package example3;
//
//import java.io.*; // 입출력 클래스
//import java.awt.*; // GUI 클래스
//import java.awt.event.*; // 이벤트 클래스
//
//public class WriteRandomFile_3_2 extends Frame implements ActionListener{ // 프레임 상속, 액션 리스너 구현
//	private TextField accountField, nameField, balanceField; // 텍스트 입력 필드 클래스 선언
//	private Button enter, done; // 버튼 클래스 선언
//	private RandomAccessFile output; // 임의 접근 클래스 선언
//	private Record data; // 사용자 정의 클래스 선언
//	// 생성자
//	public WriteRandomFile_3_2() {
//		super("파일쓰기"); // Frame 생성자
//		data = new Record(); // Record 인스턴스 생성
//		try {
//			output = new RandomAccessFile("customer.txt", "rw"); //customer.txt  읽고 쓰기 모드로 열기(없으면 생성)
//		}catch(IOException e ) { // 입출력 예외
//			System.err.println(e.toString()); // 오류 출력
//			System.exit(1); // 비정상 종료
//		}
//		setSize(300,150); // 사이즈
//		setLayout(new GridLayout(4,2)); // 4행 2열 배치
//
//		add(new Label("계좌번호")); // 레이블 이름 계좌번호 배치
//		accountField = new TextField(); // accountField 인스턴스 생성
//		add(accountField); // accountField 인스턴스 배치
//
//		add(new Label("이름"));
//		nameField = new TextField();
//		add(nameField);
//
//		add(new Label("잔고"));
//		balanceField = new TextField();
//		add(balanceField);
//
//		enter = new Button("입력"); // enter 버튼 생성(이름 : 입력)
//		enter.addActionListener(this); // 액션리스너 등록 엔터키 처리(this는 done을 가리킴)
//		add(enter); // 인스턴스 배치
//
//		done = new Button("종료");
//		done.addActionListener(this);
//		add(done);
//
//		setVisible(true); // GUI 화면 출력
//	}
//	// 사용자 입력 값을 저장하는 메서드
//	public void addRecord() {
//		int accountNo = 0; // 계좌번호
//		Double d; // 잔고
//		if( ! accountField.getText().equals("")) { // 계좌번호가 비어 있지 않으면
//			try {
//				accountNo = Integer.parseInt(accountField.getText()); // 계좌번호 문자열 가져와서 int로 저장
//				if(accountNo > 0 && accountNo <= 100) { // 게좌번호가 0보다 크고 100보다 작을 경우
//					data.setAccount(accountNo); // Record 객체 계좌 번호를 설정
//					data.setName(nameField.getText()); // 이름 문자열을 가져와서 Record 객체 이름을 설정
//					d = new Double(balanceField.getText()); // 잔고 문자열을 가져와서 동적 할당 하여 double로 변환하고 d에 저장
////					d = Double.parseDouble(balanceField.getText()); // 이거 안쓰는 이유가 있나요? - 이거 사용하면 된다.
//					data.setBalance(d.doubleValue()); // 실수로 저장된 d를 Record 객체 잔고로 설정
//					output.seek((long)(accountNo-1)*Record.size()); // 레코드의 바이트 크기(42바이트)만큼 이동해서 계좌번호에 해당하는 파일 위치로 이동
//					data.write(output); // 저장된 내용을 파일에 기록
//				}
//				accountField.setText("");
//				nameField.setText("");
//				balanceField.setText(""); // 입력란을 공백 처리
//			}catch(NumberFormatException nfe) { // 계좌 번호(accountNo) 잔고(d)가 숫자가 아닐 경우(숫자 입력 예외)
//				System.err.println("숫자를 입력하세요"); // 오류 메시지 출력
//			}catch(IOException io) { //입출력 예외 발생(파일 저장 중 문제가 생긴경우)
//				System.err.println(io.toString()); // 오류 출력
//				System.exit(1); // 프로그램 종료
//			}
//		}
//	}
//	// 이벤트 처리 메서드
//	public void actionPerformed(ActionEvent e) { // 이벤트 객체를 받아 처리하는 메서드
//		addRecord(); // addRecord 실행
//		if(e.getSource() == done) { // 받은 이벤트가 done일 경우
//			try {
//				output.close(); // 파일 닫기
//			}catch(IOException io) { // 입출력 오류
//				System.err.println("파일 닫기 에러\n" + io.toString()); // 파일 닫기 출력 및 오류 문자열 출력
//			}
//			System.exit(0); // 프로그램 정상 종료
//		}
//	}
//	public static void main(String args[]) { // 실행 코드
//		new WriteRandomFile_3_2();
//	}
//}
//// 사용자 정의 class
////class Record{
////	private int account; // 계좌(4byte)
////	private String name; // 이름(30byte)
////	private double balance; // 잔고(8byte)
////	// 파일 읽기 메서드
////	public void read(RandomAccessFile file) throws IOException{ // 임의 접근한 파일을 parameter로 받는 메서드
////		account = file.readInt(); // 파일에서 읽은 정수형 데이터를 저장
////		char namearray[] = new char[15]; // 30byte의 char 문자열을 선언
////		for(int i = 0; i < namearray.length; i++) // 문자 길이 만큼 반복
////			namearray[i] = file.readChar(); // readChar를 통해 파일에서 읽고 배열 index에 저장
////		name = new String(namearray); // char 배열을 string으로 변환해서 name에 저장
////		balance = file.readDouble(); // 파일에서 읽은 실수를 balance에 저장
////	}
////	// 파일 쓰기 메서드
////	public void write(RandomAccessFile file) throws IOException{ // 임의 접근한 파일을 parameter로 받는 메서드
////		StringBuffer buf; // 문자열 버퍼 선언
////		file.writeInt(account); // 계좌번호를 정수형으로 파일에 작성
////		if(name != null ) // name이 공백이 아니면
////			buf = new StringBuffer(name); // 이름을 버퍼에 저장
////		else
////			buf = new StringBuffer(15); // 이름을 공백으로 버퍼에 저장
////		buf.setLength(15); // 버퍼는 15(30byte)로 고정
////		file.writeChars(buf.toString()); // 버퍼 내용을 문자열로 받아 char로 파일에 작성
////		file.writeDouble(balance); // 잔고를 실수형으로 파일에 작성
////	}
////
////	public void setAccount(int a) { account = a;} // 입력 계좌번호 설정 메서드
////	public int getAccount() { return account;}  // getAccount 호출 시 account 값 반환
////	public void setName(String f) { name = f; } // 입력 이름 설정 메서드
////	public String getName() {return name;} // getName 호출 시 Name 값 반환
////	public void setBalance(double b) {balance = b;} // 입력된 실수 값을 잔고로 설정
////	public double getBalance() {return balance;} // getBalance 호출 시 balance 값 반환
////	public static int size() {return 42;} // 한 레코드의 사이즈를 42byte로 반환
////}
