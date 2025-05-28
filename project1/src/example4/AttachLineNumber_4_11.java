package example4;

import java.io.*; // 입출력 클래스

public class AttachLineNumber_4_11 { 
	public static void main(String args[]) {
		String buf; // 문자열을 저장
		FileInputStream fin = null; // 파일을 바이트 단위로 읽음 
		FileOutputStream fout = null; // 바이트 단위로 데이터를 읽어 파일에 출력
		if(args.length != 2) {
			System.out.println("소스파일 및 대상파일을 지정하십시오."); // 인수가 2개가 아닐경우
			System.exit(1); // 비정상 종료
		}
		try {
			fin = new FileInputStream(args[0]); // 읽을 파일 저장
			fout = new FileOutputStream(args[1]); // 쓸 파일 저장
		}catch(Exception e) { // 예외 발생 시 
			System.out.println(e); //출력
			System.exit(1);
		}
		BufferedReader read = new BufferedReader(new InputStreamReader(fin)); // 문자열을 읽을 때 버퍼를 이용해서 읽음
		PrintStream write = new PrintStream(fout); // 대상 파일에 줄바꿈과 자동 flush를 갖춰 출력
		int num = 1; //  처음 시작 번호
		while(true) { // 무한 반복
			try {
				buf=read.readLine(); // 버퍼를 이용해 한줄 읽음
				if(buf==null)break; // 읽을 내용이 더 없으면 끝
			}catch(IOException e) { // 입출력 오류
				System.out.println(e); // 오류 출력
				break;
			}
			buf = num + ":" + buf; // 1 : 문자열 형태로 저장
			write.println(buf); // 문자열 작성
			num++; // 번호 상승
		}
		try {
			fin.close(); // inputStream을 닫음
			fout.close(); // OutputStream을 닫음
		}catch(IOException e) { // 입출력 오류
			System.out.println(e); // 오류 출력
		}
	}
}
