package example4;

import java.io.*;

public class hw_4_1 {
	public static void main(String[] args) {
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
		PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fout)),true); // 바이트 스트림을 문자스트림을 변환하고, 버퍼를 이용하여 읽으며, 줄단위 출력을 함 , 자동 플러쉬
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
			pw.println(buf); // 문자열 작성
			num++; // 번호 상승
		}
		// 저장된 파일을 콘솔에 출력
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(args[1])); // 파을을 문자기반, 줄 단위로 읽음 
			while((buf = lnr.readLine()) != null) { // EOF 만나면 종료
			System.out.println(buf); // 문자열 콘솔 출력
			}
		}catch(IOException e) {
			System.err.println(e.toString());
		}
		try {
			fin.close(); // inputStream을 닫음
			pw.close(); // PrintWriter 닫기
		}catch(IOException e) { // 입출력 오류
			System.out.println(e); // 오류 출력
		}
	}
}
