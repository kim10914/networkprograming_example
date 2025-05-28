// 문자열을 키보드로 입력 받아 파일에 저장 후 저장된 파일에서 한 줄씩 읽어 화면에 출력하는 클래스 작성
// File, InputStreamReader, OutputStreamWriter 클래스 사용하고 인코딩은 UTF-8
package example3;

import java.io.*;

public class hw_3_3 {
	public static void main(String[] args) throws IOException {
		// try안에 스트림 저장하면 알아서 try 빠져나오면서 close
		try {
			// 파일 쓰기
			File file = new File("hw_3_3.txt"); // 파일 객체 생성
			String text; // 한줄씩 읽을 때 쓸 문자열 변수
			InputStreamReader isr; // 바이트 스트림을 문자 스트림을 변환
			BufferedReader br; // 문자를 버퍼링을 통해 읽음
			FileOutputStream fos; // 바이트 스트림으로 파일에 쓰기위한 스트림
			OutputStreamWriter osw; // 바이트를 문자로 변환하여 출력함
			BufferedWriter bw; // 텍스트를 버퍼링해서 쓸 수 있음
			isr = new InputStreamReader(System.in); // 키보드로 입력 받아서 문자단;위로 변환
			br = new BufferedReader(isr); // 읽은 입력을 버퍼를 통해 읽음
			fos = new FileOutputStream(file); // 파일에 쓰기 위해 OutputStream으로 감싸기
//			fos = new FileOutputStream("hw_3_3.txt"); //이걸로 file 객체 생성 안하기 가능
			osw = new OutputStreamWriter(fos,"UTF-8"); // UTF-8 인코딩 버전으로 바이트를 받아 문자로 입력
			bw = new BufferedWriter(osw); // 버퍼를 이용하여 한줄씩 쓴다.
			while((text=br.readLine()) != null) { // 버퍼 리드 라인을 이용해 text에 저장
                    bw.write(text+"\r\n"); // 버퍼 라이터를 통해 text를 쓰고 줄 바꿈
			}
			bw.flush(); // 버퍼에 비우기
			bw.close(); // 파일 닫기
			
			// 파일 읽기
			FileInputStream fis = new FileInputStream(file); // 파일에서 바이트 단위로 읽기
//			FileInputStream fis  = new FileInputStream("hw_3_3.txt"); // 이걸로 file 객체 생성 안하기 가능
			InputStreamReader isr2 = new InputStreamReader(fis,"UTF-8"); // 바이트 문자를 UTF-8로 변환해서 읽기
			BufferedReader br2 = new BufferedReader(isr2); // 버퍼를 이용해서 한줄 씩 읽기

            while((text=br2.readLine()) != null) { // 버퍼를 통해 한줄씩 읽고 text에 문자열 저장
                System.out.println(text); // 출력
            }
		}catch(IOException e) { // 입출력 예외 처리
			System.err.println(e.toString());
		}
		
    }
}
