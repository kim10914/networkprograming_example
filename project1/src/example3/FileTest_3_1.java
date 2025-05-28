package example3;

import java.awt.*; // java GUI 클래스
import java.awt.event.*; // 이벤트 클래스
import java.io.*; //입출력 클래스

public class FileTest_3_1 extends Frame implements ActionListener { // Frame 상속 받고 액션 리스너 구현
	private TextField enter; // 파일명 이나 디렉토리 경로를 입력받는 필드
	private TextArea output; // 결과를 다중행으로 보여주는 출력창
	//생성자
	public FileTest_3_1() { 
		super("File 클래스 테스트"); // Frame 생성자 호출
		enter = new TextField("파일 및 디렉토리명을 입력하세요"); // 텍스트 필드 생성 문자열은 초기값
		enter.addActionListener(this); // 엔터 키 입력 이벤트를 처리할 리스너 등록
		output = new TextArea(); // 출력을 위한 텍스트 에리어 정의
		add(enter, BorderLayout.NORTH); // GUI 컴포넌트를 위쪽에 추가
		add(output, BorderLayout.CENTER); // 중앙 배치
		addWindowListener(new WinListener()); // x 이벤트를 처리하기 위해 WindowListener 등록
		setSize(400, 400);
		setVisible(true);
	}
	// 이벤트 처리 메서드
	public void actionPerformed(ActionEvent e) { // 발생한 액션 이벤트 객체를 매개 변수로 받는 메서드
		File name = new File(e.getActionCommand()); // 자바의 파일 또는 디렉토리를 추상적으로 표현하는 클래스에 텍스트 필드에서 입력된 문자열을 저장
		if(name.exists()) { // 파일 또는 디렉토리가 실제로 존재하는지 확인하는 메서드를 사용하여 검사
			output.setText(name.getName() + "이 존재한다.\n" + // output 창에 setText 설정
				(name.isFile() ? "파일이다.\n" : "파일이 아니다.\n") + // 파일인지 검사하는 메서드(삼항 연산자)
				(name.isDirectory() ? "디렉토리이다.\n" : "디렉토리가 아니다.\n") + // 폴더인지 여부 검사
				(name.isAbsolute() ? "절대경로이다.\n" : "절대경로가 아니다.\n") + // 사용된 경로가 절대 경로 인지 여부 검사
				"마지막 수정날짜은 : " + name.lastModified() +  // 마지막 수정된 날짜
				"\n파일의 길이는 : " + name.length() +  // 파일의 바이트 크기
				"\n파일의 경로는 : " + name.getPath() + // 생성 시 입력한 경로 그대로 반환
				"\n절대경로는 : " + name.getAbsolutePath() + // 절대 경로 반환
				"\n상위 디렉토리는 : " + name.getParent() ); // 상위 폴더 경로 반환
			if(name.isFile()) { // 파일인지 검사
				try {
					RandomAccessFile r = new RandomAccessFile(name,"r"); // 읽기모드(r)로 name 파일 객체를 정의(임의접근)
					StringBuffer buf = new StringBuffer(); // 문자열을 저장할 수 있는 클래스 정의
					String text; // 한줄 씩 읽은 문자열을 임시로 담음
					output.append("\n\n"); // 줄바꿈 2번 출력
					while((text = r.readLine()) != null) // 한줄씩 읽는데 NULL 만날 때 까지 반복 하면서 text에 저장
						buf.append( text + "\n" ); // text에 저장된 내용을 buf에 추가하고 줄바꿈
					output.append(buf.toString()); // buf에 저장된 문자열을 출력
				} catch(IOException e2) { // 입출력 관련 예외 처리(없음)
				}
			}
			else if(name.isDirectory()) { // 폴더인지 검사
				String directory[] = name.list(); // 디텍토리 내의 파일,폴더 이름을 문자열로 저장하여 배열에 저장
				output.append("\n\n디텍토리의 내용은 : \n"); // 문자열 출력
				for(int i = 0; i < directory.length; i++) // 폴더 내 항목 수 만큼 반복
					output.append(directory[i] + "\n"); // n번째 항목 출력
			}
		}
		else {
			output.setText(e.getActionCommand() + " 은 존재하지 않는다.\n"); // 아무것도 없음
		}
	}
	public static void main(String args[]) {
		FileTest_3_1 f = new FileTest_3_1();
	}
	class WinListener extends WindowAdapter{ // x버튼 로직 추가
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}

}
