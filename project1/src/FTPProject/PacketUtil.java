package FTPProject;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PacketUtil { // 유틸 클래스
    // RRQ 패킷 생성
    public static byte[] createRRQPacket(String filename) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // 바이트 배열을 순차 적으로 처리하는 객체 생성
        baos.write(Constants.RRQ);
        baos.writeBytes(filename.getBytes(StandardCharsets.UTF_8));
        return baos.toByteArray(); // 1,msg 형식으로 배열을 반환한다.
    }
    // WRQ 패킷 생성
    public static byte[] createWRQPacket(String filename) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Constants.WRQ);
        baos.writeBytes(filename.getBytes(StandardCharsets.UTF_8));
        return baos.toByteArray();
    }
    // Data 패킷 생성
    public static byte[] createDataPacket(int blockNumber, byte[] data, int length) {
        ByteBuffer buffer = ByteBuffer.allocate(1 + 4 + length); // 패킷 크기의 버퍼 생성 (type + block_number + data)
        buffer.put((byte) Constants.DATA); // 데이터 패킷을 전송하는 코드
        buffer.putInt(blockNumber); // 블록
        buffer.put(data, 0, length); // 0부터 데이터 쓰기(최대 512byte) -> 517btye의 패킷 생성
        return buffer.array(); // 3, block_number, msg 형식으로 리턴
    }
    // ACK 패킷 생성
    public static byte[] createAckPacket(int blockNumber) {
        ByteBuffer buffer = ByteBuffer.allocate(1 + 4);
        buffer.put((byte) Constants.ACK);
        buffer.putInt(blockNumber);
        return buffer.array();
    }
    // ERROR 패킷 생성
    public static byte[] createErrorPacket(String message) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Constants.ERROR);
        baos.writeBytes(message.getBytes(StandardCharsets.UTF_8));
        return baos.toByteArray();
    }
    // AUTH 패킷 생성
    public static byte[] createAuthPacket(String id, String pw) {
        String data = id + ":" + pw;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(Constants.AUTH);
        baos.writeBytes(data.getBytes(StandardCharsets.UTF_8));
        return baos.toByteArray();
    }
    // 패킷 타입 반환
    public static int getPacketType(byte[] data) {
        return data[0]; // 첫 부분만 읽어서 반환함
    }
    // 블록 넘버 가져옴
    public static int getBlockNumber(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data); // data를 읽는 버퍼
        buffer.get(); // 0번째 배열
        return buffer.getInt(); // number 반환
    }
    // 데이터 읽기
    public static byte[] getDataContent(byte[] data, int length) {
        return Arrays.copyOfRange(data, 5, length); // 5번째 부터 읽기
    }
    // 파일이름 가져오기
    public static String getFilename(byte[] data, int length) {
        return new String(data, 1, length - 1, StandardCharsets.UTF_8); // 1번째 부터 읽음
    }
    // ERROR 메시지 가져오기
    public static String getErrorMessage(byte[] data, int length) {
        return new String(data, 1, length - 1, StandardCharsets.UTF_8); // 1번째 부터 읽음
    }
    // ID : PW로 파싱
    public static String[] getAuthInfo(byte[] data, int length) {
        String payload = new String(data, 1, length - 1, StandardCharsets.UTF_8);
        return payload.split(":", 2);
    }
    //NACK 패킷 생성
    public static byte[] createNackPacket(int blockNumber) {
        ByteBuffer buffer = ByteBuffer.allocate(1 + 4);
        buffer.put((byte) Constants.NACK);
        buffer.putInt(blockNumber);
        return buffer.array();
    }
}
