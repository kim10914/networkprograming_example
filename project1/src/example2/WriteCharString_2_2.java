// 문자열을 파일에 저장하는 프로그램
package example2;
	
import java.io.*;

public class WriteCharString_2_2 {
	static DataOutputStream dos; //원시 데이터를 이진데이터로 저장하는 스트림 클래스(전역)
	public static void main(String args[]) {
		try {
			String data; 
			FileOutputStream fout = new FileOutputStream("chardata.txt"); //바이트단위 파일 객체 생성  
			dos = new DataOutputStream(fout); // fout 파일을 dos로 래핑
			dos.writeChar(65); // A저장
			dos.writeUTF("반갑습니다"); 
			dos.writeUTF("자바 채팅 프로그래밍 교재"); // 문자열 저장
		}catch(EOFException e) { // EOF 예외 처리 
			System.err.println(e); // 에러 메시지 콘솔 출력
		}catch(IOException e) { // IO 예외 처리
			System.err.println(e); // 에러 메시지 콘솔 출력
		}finally { //파일 스트림을 닫아 리소스 누수를 발생 방지
			try {
				if(dos != null) dos.close(); //예외 발생으로 dos가 초기화 되지 않았을 경우 dos 닫기
			}catch(IOException e) { //입출력 예외 발생 시 
				System.err.println(e); // 에러 메시지 콘솔 출력
			}
		}
	}
}
