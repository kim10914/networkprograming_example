package example2;

import java.io.*; // 자바 입출력 클래스 사용

//파일을 쓰고 읽는 프로그램
public class hw_2_1 {
	static FileOutputStream fout; // 파일을 바이트 단위로 출력하는 스트림 클래스를 선언
	static DataOutputStream dos;  // 원시 데이터를 읽어서 이진 데이터로 저장하는 클래스 선언
	static FileInputStream fin; // 바이트 단위로 파일을 읽기 위한 스트림 객체 선언
	static DataInputStream dis; // fin을 래핑 하여 원시 데이터를 읽기 위한 입력 스트림
	public static void main(String args[]) {
		// 파일을 쓰는 블록
		try {
			fout = new FileOutputStream("hw_2_1.txt"); //numberdata.txt 파일 생성(덮어쓰기가능)
			dos = new DataOutputStream(fout); //fout 파일을 감싸서 이진 데이터 저장이 가능하도록 래핑
			dos.writeBoolean(true); // dos의 불리언 값을 1로 저장
			dos.writeDouble(989.27); // dos의 실수 값을 989.27로 저장(부동 소수점 형식)
			for(int i = 1; i<=500; i++) {
				dos.writeInt(i) ; // 1~500을 이진 데이터로 저장
			}
		}catch(IOException e) { //입출력 예외가 발생 했을 경우 
			System.err.println(e); //콘솔에 오류 출력
		}
		// 파일을 읽는 블록
		boolean bdata; // readBoolean()으로 읽은 값 저장
		double ddata;  // readDouble()로 읽은 값 처장
		int number; // readInt()로 읽은 숫자 저장
		try {
			fin = new FileInputStream("hw_2_1.txt"); // 파일을 읽는다(없을경우 예외처리)
			dis = new DataInputStream(fin); // 파일 래핑을 통해 원시 데이터를 읽도록 만든다.
			bdata = dis.readBoolean(); // dis를 통해 읽은 Boolean 값을 저장(1byte 읽음)
			System.out.println(bdata); // 읽은 값을 콘솔에 출력
			ddata = dis.readDouble(); // dis를 통해 Double 값을 저장(8 byte 읽음)
			System.out.println(ddata); // 저장된 값을 콘솔에 출력
			while(true) { // 무한 루프(EOF 만나면 예외처리로 넘어감)
				number = dis.readInt(); //dis를 통해 int 값을 저장(4byte로 읽음)
				System.out.print(number+" "); // 저장된 값을 콘솔에 출력하고 한칸 뛰기
			}
		}catch(EOFException e) {
			System.out.println("데이터를 모두 읽었습니다.");
		}catch(IOException e) { // IO 예외 처리 
			System.err.println(e); // 콘솔 출력
		}
		finally { //무조건 실행
			try {
				if(dos!= null) dos.close(); // dos에 입력 값이 있으면 dos.close(자원 반납)
				if(dis!= null) dis.close(); // 입력 스트림 닫기
			}catch(IOException e) {} // 파일을 닫지 못한경우 계속 대기
		}
	}
}
