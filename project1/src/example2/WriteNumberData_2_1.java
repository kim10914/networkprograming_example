package example2; // 파일 소속

import java.io.*; // 자바 입출력 클래스 사용

public class WriteNumberData_2_1 {
	static FileOutputStream fout; // 파일을 바이트 단위로 출력하는 스트림 클래스를 선언
	static DataOutputStream dos;  // 원시 데이터를 읽어서 이진 데이터로 저장하는 클래스 선언
	public static void main(String args[]) {
		try {
			fout = new FileOutputStream("numberdata.txt"); //numberdata.txt 파일 생성(덮어쓰기가능)
			dos = new DataOutputStream(fout); //fout 파일을 감싸서 이진 데이터 저장이 가능하도록 래핑
//			dos.writeUTF("true");
//			dos.writeUTF("989.27");
			dos.writeBoolean(true); // dos의 불리언 값을 1로 저장
			dos.writeDouble(989.27); // dos의 실수 값을 989.27로 저장(부동 소수점 형식)
			for(int i = 1; i<=500; i++) {
				dos.writeInt(i) ; // 1~500을 이진 데이터로 저장
//				dos.writeUTF(String.valueOf(i));
			}
		}catch(IOException e) { //입출력 예외가 발생 했을 경우 
			System.err.println(e); //콘솔에 오류 출력
		}finally { //무조건 실행
			try {
				if(dos!= null) dos.close(); //dos에 입력 값이 있으면 dos.close
			}catch(IOException e) {} // 파일을 닫지 못한경우 계속 대기
		}
	}
}

//오류 : 파일 깨짐 -> dataInputStream을 사용하기 write UTF 사용하기
