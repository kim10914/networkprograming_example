package example2;

import java.io.*;

public class hw_2_2 {
	static DataOutputStream dos; //원시 데이터를 이진데이터로 저장하는 스트림 클래스(전역)
	static FileInputStream fin; // 파일을 읽을 객체
	static DataInputStream dis; // 래핑을 통해 문자열을 읽을 객체
	public static void main(String args[]) {
		char ch;  
		String sdata1, sdata2; // 읽어온 데이터를 저장할 변수
		try {
			FileOutputStream fout = new FileOutputStream("hw_2_2.txt"); //바이트단위 파일 객체 생성  
			dos = new DataOutputStream(fout); // fout 파일을 dos로 래핑
			dos.writeChar(65); // A저장
			dos.writeUTF("반갑습니다"); 
			dos.writeUTF("자바 채팅 프로그래밍 교재"); // 문자열 저장
		}catch(EOFException e) { // EOF 예외 처리 
			System.err.println(e); // 에러 메시지 콘솔 출력
		}catch(IOException e) { // IO 예외 처리
			System.err.println(e); // 에러 메시지 콘솔 출력
		}
		try {
			fin = new FileInputStream("hw_2_2.txt"); //chardata.txt를 읽어서 객체에 저장(없을 경우 IO 예외 발생)
			dis = new DataInputStream(fin); // 읽을 fin을 래핑하여 데이터를 읽을 수 있도록 해줌
			ch = dis.readChar(); // 첫 단 1byte를 읽어 char 형태로 ch 변수에 저장
			sdata1 = dis.readUTF(); // UTF-8 인코딩으로 저장된 문자열을 읽고 저장(내부저긍로 문자열 길이 + 바이트가 읽힘 -> 그래서 2번 읽음)
			sdata2 = dis.readUTF(); // 다음 UTF 문자열 저장
			System.out.println(ch); 
			System.out.println(sdata1);
			System.out.println(sdata2);
		}catch(EOFException e) { // EOF 예외 발생시(하지만 위 코드는 정확하게 3개의 데이터를 읽기 때문에 EOF이 발생하지 않는다.)
			System.out.println(e); // 정상 종료
		}catch(IOException e) { // IO 예외 발생시 
			System.err.println(e); // 오류 메시지 출력
		}
		
		finally { //파일 스트림을 닫아 리소스 누수를 발생 방지
			try {
				if(dos != null) dos.close(); // output스트림 자원 반납
				if(dis != null) dis.close(); // input 스트림 자원 반납
			}catch(IOException e) { //입출력 예외 발생 시 
				System.err.println(e); // 에러 메시지 콘솔 출력
			}
		}
	}
}
