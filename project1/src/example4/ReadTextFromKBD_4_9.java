package example4;

import java.io.*;

public class ReadTextFromKBD_4_9 {
	public static void main(String args[]) throws IOException {
		String text; // 텍스트를 저장할 문자열
		InputStreamReader isr; // 바이트 단위로 데이터를 읽어 문자 단위로 데이터를 변환
		BufferedReader br; // 버퍼를 이용하여 한번에 한줄씩 읽음
		FileOutputStream fos; // 바이트 단위로 데이터를 출력하는 클래스 
		OutputStreamWriter osw; // OutputStream을 이용하여 파일에 인코딩 방식을 적용하고 데이터 출력 
		BufferedWriter bw; // 버퍼를 이용해 한줄 씩 출력
		isr = new InputStreamReader(System.in); // 키보드 입력을 받음
		br = new BufferedReader(isr); // 입력 받은 데이터를 버퍼에 저장
		fos = new FileOutputStream("example_4_9.txt"); // 바이트 단위 출력할 파일을 지정 
		osw = new OutputStreamWriter(fos,"KSC5601"); // 인코딩 방식과 작성할 OutputStream 객체 지정 
		// osw = new OutputStreamWriter("example_4_9.txt","KSC5601"); // 오류 발생 -> OutputStream 객체를 넣어야함
		bw = new BufferedWriter(osw); // osw에 저장된 데이터들을 한줄씩 저장해서 출력
		while((text=br.readLine()) != null) { // 읽은 문자가 ctrl+z가 입력 될 때 까지 반복
			System.out.println(text); // 텍스트 출력
			bw.write(text+"\r\n"); // 파일에 내용 작성하고 커서를 앞으로 보내고 newLine
		}
		bw.flush(); // 버퍼 비우기
		bw.close(); // 버퍼 닫기(내부에 연결된 스트림들은 자동으로 닫힘)
	}
}
