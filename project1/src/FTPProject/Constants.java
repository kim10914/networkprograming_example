package FTPProject;

public class Constants {
    // 패킷 타입 정의
    public static final int RRQ = 1; // 읽기 요청 패킷
    public static final int WRQ = 2; // 쓰기요청 패킷
    public static final int DATA = 3; // 실제 데이터가 담긴 패킷
    public static final int ACK = 4; // ACK 패킷
    public static final int NACK = 5; // NACK(재전송) 패킷
    public static final int ERROR = 6; // 에러 패킷 -> 파일이 없거나 저장 실패, 인증 실패 등
    public static final int AUTH = 7; // 인증 패킷
    public static final int BLOCK_SIZE = 512; // 블록 사이즈는 512byte로 고정
}
