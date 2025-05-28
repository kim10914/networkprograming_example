// 서버 구현
package example7;

import java.io.*;
import java.net.*; // ServerSocket, Socket등 네트워크 통신에 필요한 클래스 호출

public class hw_7_1_server {
    private static final int PORT = 3000; // 포트 번호 지정
    private static final String File_name = "hw_7_1_server.txt"; // 파일 이름 지정

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT)){ // 포트 3000으로 새로운 서버 생성(소켓 안닫아도 된다)
            System.out.println("서버가 열렸습니다. 포트 " + PORT);

            while(true){
                Socket clientSocket = serverSocket.accept(); // 사용자 요청 기다리기
                System.out.println("클라이언트 연결 완료" + clientSocket.getInetAddress()); // 클라이언트 주소 출력

                new Client_thread(clientSocket,File_name).start(); // 클라이언트 쓰레드 생성(start는 쓰레드 실행)
            }
        }catch (IOException e){
            System.out.println(e);
        }
    }
}
// 멀티 쓰레드 구현
class Client_thread extends Thread{ // 쓰레드 상속
    private Socket socket; // 클라이언트와의 통신용 소켓
    private RandomAccessFile file; // 파일 객체 랜덤 엑세스
    private ObjectInputStream input; // 클라이언트로 객체 받음
    private ObjectOutputStream output; // 클라이언로 객체를 보냄

    public Client_thread(Socket socket, String file_name){ // 소켓이랑 파일 이름 받는 생성자
        this.socket = socket; // 소켓을 받는다.
        try{
            file = new RandomAccessFile(file_name,"rw"); // 읽고 쓰기 모드로 파일 접근
        }catch(IOException e){
            System.err.println(e);
        }
    }
    public void run(){ //run 오버라이드(run 메서드는 start메서드를 사용할 때 자동으로 실행된다.)
        try{
            input = new ObjectInputStream(socket.getInputStream()); // 직렬화 사용해야 하니까 Object사용
            output = new ObjectOutputStream(socket.getOutputStream());

            while(true){
                String command = (String) input.readObject(); // 클라이언트의 요청을 계속 받음

                if(command.equals("insert")){ // 입력
                    Record rec = (Record) input.readObject(); // Record 객체 수신
                    file.seek((rec.getAccount()-1)*Record.size()); // 입력할 위치 계산(계좌번호 -1)*42byte
                    file.writeInt(rec.getAccount()); // 게좌 저장
                    writeString(file, rec.getName(),15); // 고정길이 30byte 이름 저장
                    file.writeDouble(rec.getBalance()); // 잔고 저장
                    output.writeObject("입력되었습니다.");
                }else if(command.equals("select")){ // 조회
                    int account = input.readInt(); // 계좌 읽기
                    file.seek((account - 1) * Record.size()); // 계좌에 따른 위치 지정
                    int acc = file.readInt(); // 계좌 읽기
                    String name = readString(file, 15); // 이름 읽기
                    double balance = file.readDouble(); // 잔고 읽기
                    output.writeObject(new Record(acc, name.trim(), balance)); // 클라이언트에게 응답 객체 전달(역직렬화)
                } else if (command.equals("delete")) { // 삭제
                    int account = input.readInt(); // 계좌 입력
                    file.seek((account - 1) * Record.size()); // 위치 찾고
                    file.writeInt(0);
                    writeString(file, "", 15);
                    file.writeDouble(0.0); // 입력값을 공백으로 하고 삭제로 친다.
                    output.writeObject("삭제 성공");
                }
            }
        }catch (Exception e){
            System.out.println("예외로 인한 연결종료");
        } finally {
            try{
                socket.close();
                file.close();
            }catch(IOException e){
                System.err.println(e);
            }
        }
    }
    private void writeString(RandomAccessFile file, String str, int length) throws IOException{ // 파일, 문자열, 길이 받는 고정 문자열 쓰기 메서드
        StringBuffer buf = new StringBuffer(str); // setLegth를 사용하기 위해 사용(공백 채움도 있음)
        buf.setLength(length); // 30 byte로 고정
        file.writeChars(buf.toString()); // 문자 하나씩 기록
    }
    private String readString(RandomAccessFile file, int length) throws IOException{ //고정 문자열 읽는 메서드
        char [] chars = new char[length]; // 30byte 문자열 읽는 문자 배열 선언
        for(int i = 0; i < length; i++){ // 문자열 길이 만큼 반복
            chars[i] = file.readChar(); // 각 문자를 읽음
        }
        return new String(chars); // 문자열 반환
    }
}
// 직렬화 작업 : 자바 객체를 byte 형태로 변환하여 파일에 저장하거나 네트워크로 전송할 수 있게 만드는 기술(객체 전달하려면 필수)
// TCP 통신은 byte로 통신 가능함(위험성 : 신뢰할 수 없는 스트림을 역 질렬화 하면 원격 코드실행, 서비스 거부 등의 공격에 취약할 수 있음)
// 이런 문제의 해결은 Client에게 데이터를 받기만 하고 서버 내부에서 객체화 시키는 방법을 사용하면 되지않을까...?
class Record implements Serializable{ // 직렬화 구현
    private static final long serialVersionUID = 1L;
    // 직렬화 식별자(데이터의 버전 번호 고정 -> 호완성 문제 해결) 이름 값은 long type 1버전(long 타입 사용이유는 자바 직렬화 시스템에 명세가 되어있어서 그럼)

    private int account; // 계좌(4byte)
    private String name; // 이름(30byte)
    private double balance; // 잔고(8byte)

    // 생성자
    public Record(){}

    public Record(int account, String name, double balance){
        this.account = account;
        this.name = name;
        this.balance = balance;
    }

    public void setAccount(int a) { account = a;} // 입력 계좌번호 설정 메서드
    public int getAccount() { return account;}  // getAccount 호출 시 account 값 반환

    public void setName(String f) { name = f; } // 입력 이름 설정 메서드
    public String getName() {return name;} // getName 호출 시 Name 값 반환

    public void setBalance(double b) {balance = b;} // 입력된 실수 값을 잔고로 설정
    public double getBalance() {return balance;} // getBalance 호출 시 balance 값 반환

    public static int size() {return 42;} // 한 레코드의 사이즈를 42byte로 반환
}
// 인텔리 제이의 경고는 일단 무시하자