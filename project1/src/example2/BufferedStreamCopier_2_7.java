///입력스트림의 데이터를 출력 스트림으로 복사하는 코드
package example2;

import java.io.*;

public class BufferedStreamCopier_2_7 { 
    public static void main(String args[]) {
        try {
            copy(System.in, System.out); // 표준 입력을 출력으로 복사
        } catch (IOException e) { // IO 예외 발생시 
            System.err.println(e); // 에러 메시지 출력
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException { //입력 스트림과 출력 스트림을 argument로 받는 메서드 오류시 예외 발생 시 호출한 곳으로 예외를 전달
        synchronized(out) {  // 출력 스트림을 여러 스레드가 동시에 사용하는 충돌을 막음
            BufferedInputStream bin = new BufferedInputStream(in); // 버퍼 입력 스트림 선언
            BufferedOutputStream bout = new BufferedOutputStream(out); // 버퍼 출력 스트림 선언
            while (true) {
                int data = bin.read(); // 한 바이트씩 읽음
                if (data == -1) break; // EOF(파일의 끝)면 종료
                bout.write(data); // 읽은 데이터를 출력 스트림(콘솔)에 씀
            }
            bout.flush(); // 남아있는 버퍼 내용 출력(버퍼 비우기)
        }
    }
}
