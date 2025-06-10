package FTPProject;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FileServer {
    private static final int PORT = 21; // 포트
    private static final int TIMEOUT = 3000; // TimeOut
    private static final int MAX_RETRIES = 5; // 최대 TimeOut 횟수
    private static final String serverIp = "127.0.0.1"; // IP

    // 사용자 정보 저장
    private static final Map<String, String> users = new HashMap<>();
    static {
        users.put("ksh", "1234");
    }
    private static final Set<InetSocketAddress> authenticatedClients = new HashSet<>(); // 클라이언트의 IP와 포트 번호를 저장(RRQ,WRQ 요청시 검증)
    // 메인 :
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) { // 서버 소캣 열기(port 바인딩)
            System.out.println("서버 시작");
            byte[] buffer = new byte[1024]; // 클라이언트에게 받은 패킷을 담을 버퍼

            while (true) { // 요청 수신
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length); // 받는 패킷
                socket.receive(requestPacket); // 패킷 받기

                int packetType = PacketUtil.getPacketType(requestPacket.getData()); // 타입 저장(RRQ, WRQ, AUTH 등)
                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort(); // 응답을 해야할 대상
                InetSocketAddress clientKey = new InetSocketAddress(clientAddress, clientPort); // 사용자 IP, Port로 인증 대상 생성

                if (packetType == Constants.AUTH) { // 가입 패킷일 경우
                    String[] credentials = PacketUtil.getAuthInfo(requestPacket.getData(), requestPacket.getLength());
                    String id = credentials[0];
                    String pw = credentials[1]; // ID, PW를 저장
                    //로그인 인증
                    if (users.containsKey(id) && users.get(id).equals(pw)) { // ID, PW가 일치한다면
                        authenticatedClients.add(clientKey); // IP, Port 저장
                        byte[] ack = PacketUtil.createAckPacket(0); // ack 생성
                        socket.send(new DatagramPacket(ack, ack.length, clientAddress, clientPort)); // 매핑 후 전송
                        System.out.println("인증 성공: " + id);
                    } else {
                        byte[] err = PacketUtil.createErrorPacket("인증 실패"); // ERROR 패킷 생성
                        socket.send(new DatagramPacket(err, err.length, clientAddress, clientPort)); // 매핑 후 전송
                        System.out.println("인증 실패: " + id);
                    }
                    continue;
                }
                // 인증되지 않은 사용자일 경우
                if (!authenticatedClients.contains(clientKey)) {
                    byte[] err = PacketUtil.createErrorPacket("인증되지 않은 사용자"); // ERROR 패킷 생성
                    socket.send(new DatagramPacket(err, err.length, clientAddress, clientPort)); // 매핑 후 전송
                    System.out.println("인증되지 않은 사용자의 접속");
                    continue;
                }
                // 인증된 사용자의 패킷을 받는다.
                if (packetType == Constants.RRQ) { // RRQ 처리
                    String filename = PacketUtil.getFilename(requestPacket.getData(), requestPacket.getLength());
                    System.out.println("RRQ 수신: " + filename);
                    handleReadRequest(socket, clientAddress, clientPort, filename); // RRQ 메서드 호출
                } else if (packetType == Constants.WRQ) { // WRQ 처리
                    String filename = PacketUtil.getFilename(requestPacket.getData(), requestPacket.getLength());
                    System.out.println("WRQ 수신: " + filename);
                    handleWriteRequest(socket, clientAddress, clientPort, filename); // WRQ 메서드 호출
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // RRQ 메서드
    private static void handleReadRequest(DatagramSocket socket, InetAddress clientAddress, int clientPort, String filename) {
        try (FileInputStream fis = new FileInputStream(filename)) { // 파일 읽기
            byte[] fileBuffer = new byte[Constants.BLOCK_SIZE]; // 읽은 데이터
            int blockNumber = 1; // 블록

            while (true) { // 데이터 전송
                int bytesRead = fis.read(fileBuffer); // 파일의 읽은 내용 저장
                if (bytesRead == -1) break; // EOF 읽으면 끝

                byte[] dataPacket = PacketUtil.createDataPacket(blockNumber, fileBuffer, bytesRead); // 패킷 생성
                boolean ackReceived = false; // ACK 받았는지 여부
                int retries = 0; // ACK 카운트

                while (!ackReceived && retries < MAX_RETRIES) { // ACK 받거나 MAX까지만 재전송
                    DatagramPacket sendPacket = new DatagramPacket(dataPacket, dataPacket.length, clientAddress, clientPort); // 해당 데이터 블록 매핑
                    socket.send(sendPacket); // 전송
                    System.out.println("DATA 전송: block " + blockNumber);

                    try {
                        socket.setSoTimeout(TIMEOUT); // 타임 아웃 설정
                        byte[] ackBuffer = new byte[1024]; // ACK 받는 버퍼
                        DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length); // ACK 패킷 생성
                        socket.receive(ackPacket); // ACK 받기

                        int ackType = PacketUtil.getPacketType(ackBuffer); // ACK 타입
                        int ackBlock = PacketUtil.getBlockNumber(ackBuffer); // 블럭

                        if (ackType == Constants.ACK && ackBlock == blockNumber) { // 해당 블럭을 받았는지 확인
                            System.out.println("ACK 수신: block " + blockNumber);
                            ackReceived = true;
                        } else if (ackType == Constants.NACK && ackBlock == blockNumber) { // NACK 이고, 블록 숫자도 맞으면?
                            System.out.println("NACK 수신: block " + blockNumber + ", 즉시 재전송");
                        } // ACK 아니면 다시 반복
                    } catch (SocketTimeoutException e) { // 타임 아웃이면?
                        retries++;
                        System.out.println("타임아웃. 재전송 시도: " + retries);
                    }
                }

                if (!ackReceived) { // 결국 전송을 실패했을 때
                    System.out.println("전송 실패: block " + blockNumber);
                    return;
                }

                if (bytesRead < Constants.BLOCK_SIZE) { // 마지막 블록
                    System.out.println("전송 완료: 마지막 블록 (" + bytesRead + " bytes)");
                    break;
                }

                blockNumber++;
            }

        } catch (FileNotFoundException e) { // 파일이 없을 때
            System.out.println("파일 없음 입력된 파일: " + filename);
            try {
                byte[] errorPacket = PacketUtil.createErrorPacket("해당 파일을 찾지 못했습니다." + filename);
                socket.send(new DatagramPacket(errorPacket, errorPacket.length, clientAddress, clientPort)); // ERROR 패킷 전송
            } catch (IOException ignored) {}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // WRQ 메서드
    private static void handleWriteRequest(DatagramSocket socket, InetAddress clientAddress, int clientPort, String filename) {
        File targetFile = new File("upload_" + filename); // upload_filename으로 작성
        if (targetFile.exists()) { // 해당 파일 있을 때
            System.out.println("중복된 파일 존재: " + targetFile.getName());
            try {
                byte[] err = PacketUtil.createErrorPacket("파일 이미 존재: " + filename);
                DatagramPacket error = new DatagramPacket(err, err.length, clientAddress, clientPort);
                socket.send(error);
            } catch (IOException ignored) {}
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(targetFile)) { // 파일 쓰기
            byte[] ack0 = PacketUtil.createAckPacket(0);
            DatagramPacket ackPacket = new DatagramPacket(ack0, ack0.length, clientAddress, clientPort);
            socket.send(ackPacket); // ACK(0)전송
            System.out.println("WRQ 수락 ACK(0) 전송");

            int expectedBlock = 1; // 블록

            while (true) { // 파일을 읽어서 작성
                byte[] buffer = new byte[1024]; // 데이터를 적을 버퍼
                DatagramPacket dataPacket = new DatagramPacket(buffer, buffer.length); // 수신 패킷

                socket.setSoTimeout(TIMEOUT); // 타임아웃 설정
                try {
                    socket.receive(dataPacket); // 패킷 받기
                } catch (SocketTimeoutException e) {
                    System.out.println("TimeOut 발생 종료...");
                    break;
                }

                int packetType = PacketUtil.getPacketType(buffer); // 패킷 타입
                if (packetType == Constants.DATA) { // 데이터임?
                    int blockNum = PacketUtil.getBlockNumber(buffer); // 블록
                    byte[] data = PacketUtil.getDataContent(buffer, dataPacket.getLength()); // 데이터 저장

                    if (blockNum == expectedBlock) {
                        fos.write(data); // 데이터 작성
                        System.out.println("DATA 수신: block " + blockNum);
                        
                        byte[] ack = PacketUtil.createAckPacket(blockNum); // ACK 생성 
                        DatagramPacket ackResponse = new DatagramPacket(ack, ack.length, clientAddress, clientPort); // 매핑
                        socket.send(ackResponse); // 전송
                        System.out.println("ACK 전송: block " + blockNum);

                        if (data.length < Constants.BLOCK_SIZE) { // 512보다 작으면(마지막 블록)
                            System.out.println("파일 저장 완료: upload_" + filename);
                            break;
                        }

                        expectedBlock++;
                    }
                }
            }

        } catch (IOException e) { //파일 저장 오류
            System.out.println("파일 저장 중 오류: " + e.getMessage());
            try {
                byte[] err = PacketUtil.createErrorPacket("서버에서 파일 저장 실패");
                DatagramPacket error = new DatagramPacket(err, err.length, clientAddress, clientPort);
                socket.send(error); // ERROR 보내기
            } catch (IOException ignored) {}
        }
    }
}
