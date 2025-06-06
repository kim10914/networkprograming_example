//// 입력된 고객의 수를 표시함. 종료 버튼을 출력 버튼으로 수정 - 출력은 계좌번호나 이름 TextField에 입력된 계좌에 해당하는 정보를 해당 텍스트 필드에 출력
//// 윈도우 종료는 x 버튼으로 실행
//// RanddomAccessFile 객체 생성 시 파일 이름이 아닌 File 객체 사용
//package example3;
//
//import java.io.*; // 입출력 클래스
//
//import java.awt.*; // GUI 클래스
//import java.awt.event.*; // 이벤트 클래스
//
//public class hw_3_2 extends Frame implements ActionListener{ // 프레임 상속, 액션 리스너 구현
//	private TextField accountField, nameField, balanceField; // 텍스트 입력 필드 클래스 선언
//	private Button enter, search; // 버튼 클래스 선언
//	private RandomAccessFile file ; // 임의 접근 클래스 선언
//	private Record data; // 사용자 정의 클래스 선언
//	private Label count; // 고객 수 저장 하기 위한 클래스 선언
//	static int customerCount = 0; // 고객수 카운트
//
//	// 생성자
//	public hw_3_2() {
//		super("파일쓰기"); // Frame 생성자
//		data = new Record(); // Record 인스턴스 생성
//		try {
//			File f = new File("hw_3_2.txt"); // 파일 인스턴스 생성
//			file = new RandomAccessFile("hw_3_2.txt", "rw"); // 파일 읽고 쓰기 모드로 열기(없으면 생성)
//		}catch(IOException e ) { // 입출력 예외
//			System.err.println(e.toString()); // 오류 출력
//			System.exit(1); // 비정상 종료
//		}
//		setSize(300,150); // 사이즈
//		setLayout(new GridLayout(5,2)); // 5행 2열 배치
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
//		add(new Label("입력된 고객 수"));
//		count = new Label("0"); // Label의 초기값
//		add(count);
//
//		enter = new Button("입력"); // enter 버튼 생성(이름 : 입력)
//		enter.addActionListener(this); // 액션리스너 등록 엔터키 처리(this는 done을 가리킴)
//		add(enter); // 인스턴스 배치
//
//		search = new Button("출력");
//		search.addActionListener(this);
//		add(search);
//
//		addWindowListener(new WinListener()); // x 이벤트를 처리하기 위해 WindowListener 등록
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
//				d = Double.parseDouble(balanceField.getText()); // 잔고 문자열을 가져와서 실수로 d에 저장
//
//				if(accountNo > 0 && accountNo <= 100) { // 게좌번호가 0보다 크고 100보다 작을 경우
//					data.setAccount(accountNo); // Record 객체 계좌 번호를 설정
//					data.setName(nameField.getText()); // 이름 문자열을 가져와서 Record 객체 이름을 설정 (문자열이라서 변환 안해도 괜찮음)
//					data.setBalance(d); // Record 객체 잔고 설정
//
//					file.seek((long)(accountNo-1)*Record.size()); // 레코드의 바이트 크기(42바이트)만큼 이동해서 계좌번호에 해당하는 파일 위치로 이동
//					data.write(file); // 저장된 내용을 파일에 기록
//					customerCount++; // 입력된 고객 수 증가
//					count.setText(String.valueOf(customerCount)); // 입력된 고객의 수를 문자열로 변환하여 count로 세팅
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
//		if(e.getSource() == enter) { // 받은 이벤트가 enter일 경우
//			addRecord(); // addRecord 메서드 실행
//		} else if (e.getSource() == search) { // 받은 이벤트가 search일 경우
//			searchRecord(); // searchRecord 실행
//		}
//	}
//
//	public static void main(String args[]) { // 실행 코드
//		new hw_3_2();
//	}
//	class WinListener extends WindowAdapter{ // x버튼 로직 추가
//		public void windowClosing(WindowEvent we) {
//			try { // 오류 검증 코드 파일을 닫지 못했을 겨우 오류 출력
//				file.close();
//			}catch(IOException e) {
//				System.err.println("파일 닫기 오류 : " + e.toString());
//			}
//			System.exit(0);
//		}
//	}
//	// 검색 메서드 구현
//	public void searchRecord() {
//		try {
//			int searchAccount = Integer.parseInt(accountField.getText()); // 문자열을 읽어 정수에 저장
//
//			if(searchAccount <= 0 || searchAccount > 100) { // 0보다 작고 100 보다 클 경우
//				System.err.println("1~100 사이로 입력");
//				return; // 메서드 종료
//			}
//			file.seek((searchAccount -1) * Record.size()); // 계좌 번호는 1부터 시작하니까 0부터 시작할 수 있도록 인덱스를 변환하고 찾고 싶은 계좌 위치로 이동
//			data.read(file); // read 메서드를 이용하여 한 레코드를 읽음
//
//			if(data.getAccount() == 0) { // 입력된 번호의 레코드가 비어있을 경우
//				System.err.println("계좌 찾기 실패"); // 오류 출력
//			}else {
//				accountField.setText(String.valueOf(data.getAccount())); // 계좌를 가져와서 문자열 변환 후 출력
//				nameField.setText(data.getName()); // 이름을 가져와서 출력
//				balanceField.setText(String.valueOf(data.getBalance())); // 잔고를 가져와서 문자열 변환 후 출력
//			}
//		}catch(NumberFormatException e) { // 숫자 입력이 아닐 경우
//			System.err.println("계좌번호는 숫자만 입력가능"); // 오류 출력
//		} catch(IOException e) { // 입출력 오류
//			System.err.println("검색 오류" + e.toString()); // 오류 출력
//		}
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
