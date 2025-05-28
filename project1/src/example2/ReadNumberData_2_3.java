package example2;
import java.io.*;

public class ReadNumberData_2_3 {
	static FileInputStream fin; // 바이트 단위로 파일을 읽기 위한 스트림 객체 선언
	static DataInputStream dis; // fin을 래핑 하여 원시 데이터를 읽기 위한 입력 스트림
	public static void main(String args[]) {
		boolean bdata; // readBoolean()으로 읽은 값 저장
		double ddata;  // readDouble()로 읽은 값 처장
		int number; // readInt()로 읽은 숫자 저장
		try {
			fin = new FileInputStream("numberdata.txt"); // 파일을 읽는다(없을경우 예외처리)
			dis = new DataInputStream(fin); // 파일 래핑을 통해 원시 데이터를 읽도록 만든다.
			bdata = dis.readBoolean(); // dis를 통해 읽은 Boolean 값을 저장(1byte 읽음)
			System.out.println(bdata); // 읽은 값을 콘솔에 출력
			ddata = dis.readDouble(); // dis를 통해 Double 값을 저장(8 byte 읽음)
			System.out.println(ddata); // 저장된 값을 콘솔에 출력
			while(true) { // 무한 루프(EOF 만나면 예외처리로 넘어감)
				number = dis.readInt(); //dis를 통해 int 값을 저장(4byte로 읽음)
				System.out.print(number+" "); // 저장된 값을 콘솔에 출력하고 한칸 뛰기
			}
		}catch(EOFException e) { // EOF 예외 처리
			System.out.println("데이터를 모두 읽었습니다."); // 콘솔 출력(읽을 데이터가 더 없으면 출력)
		}catch(IOException e) { // IO 예외 처리 
			System.err.println(e); // 콘솔 출력
		}
	}
}
