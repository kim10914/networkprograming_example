// 콘솔 입력을 읽어 동적으로 크기를 조장하여 저장한 후 출력
// arraycopy() 메서드를 별도의 함수로 작성하여 사용하기
package example1;
import java.io.*;

public class homework1_1 {
	//전역 변수 선언
	static int size = 0; // 현재 읽은 데이터 수
	static int bufferSize = 80; // 입력을 저장할 버퍼의 초기 크기(이후 동적 증가)
	static byte buffer[] = new byte[bufferSize]; //bufferSize크기의 바이트 배열
	public static void main(String args[]) {
		try { //입출력 작업을 수행할 경우 예외가 발생할 가능성이 높기 때문에 try-catch블록사용 - 왜 입출력시 예외가 많이 발생하나요?
			int dataRead; 
			while((dataRead = System.in.read(buffer, size, bufferSize-size)) >= 0){ //키보드 입력을 buffer에 저장하고 입력된 데이터의 바이트 만큼 읽음 EOF 명령 전까지 수행
				size += dataRead; // size에 dataRead를 더함
				if(size == bufferSize) //버퍼 크기가 가득 찬 경우 increaseBufferSize()메서드 수행
					increaseBufferSize();				
			}
			System.out.write(buffer,0,size); // 입력된 내용을 buffer[0]j부터 size 바이트 만큼 데이터를 출력
		}catch(IOException e) { // 예외 처리 
			System.err.println("스트림으로 부터 데이터를 읽을 수 없습니다.");//오류 출력 스트림
		}
	}
	static void increaseBufferSize() {
		bufferSize += 80; // 버퍼 사이즈 증가
		byte[] newBuffer = new byte[bufferSize]; // bufferSize를 키워서 새로운 버퍼 생성
		customArrayCopy(buffer, 0, newBuffer, 0, size); //문자열 복사를 새로운 배열에 해줌
		buffer = newBuffer; // buffer 교체
	}
	static void customArrayCopy(byte[] first, int firststart, byte[] sec, int secstart, int length) {
	    for (int i = 0; i < length; i++) {
	        sec[secstart + i] = first[firststart + i]; // 요소를 한 개씩 복사
	    }
	}

}
