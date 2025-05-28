package example2;

import java.io.*;
import java.io.DataInputStream;

public class ReadCharString_2_4 {
	static FileInputStream fin; // 파일을 읽을 객체
	static DataInputStream dis; // 래핑을 통해 문자열을 읽을 객체
	public static void main(String args[]) {
		char ch;  
		String sdata1, sdata2; // 읽어온 데이터를 저장할 변수
		try {
			fin = new FileInputStream("chardata.txt"); //chardata.txt를 읽어서 객체에 저장(없을 경우 IO 예외 발생)
			dis = new DataInputStream(fin); // 읽을 fin을 래핑하여 데이터를 읽을 수 있도록 해줌
			ch = dis.readChar(); // 첫 단 1byte를 읽어 char 형태로 ch 변수에 저장
			sdata1 = dis.readUTF(); // UTF-8 인코딩으로 저장된 문자열을 읽고 저장(내부저긍로 문자열 길이 + 바이트가 읽힘 -> 그래서 2번 읽음)
			sdata2 = dis.readUTF(); // 다음 UTF 문자열 저장
			System.out.println(ch); 
			System.out.println(sdata1);
			System.out.println(sdata2); // 저장된 데이터를 콘솔에 출력
//			while(true) { //예외처리 확인 코드
//				sdata2 = dis.readUTF();
//				System.out.println(sdata2); 	
//			}
		}catch(EOFException e) { // EOF 예외 발생시(하지만 위 코드는 정확하게 3개의 데이터를 읽기 때문에 EOF이 발생하지 않는다.)
			System.out.println(e); // 정상 종료
		}catch(IOException e) { // IO 예외 발생시 
			System.err.println(e); // 오류 메시지 출력
		}
	}
}

// 예외 처리시 e는 발생한 예외의 정보를 담고 있는 객체임
// EOF 예외 처리시 아무것도 출력이 없도록 설정함
