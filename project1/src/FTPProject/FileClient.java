package FTPProject;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class FileClient {
    private static final int SERVER_PORT = 21; // 서버 연결 포트
    private static final int TIMEOUT = 3000; // DatagramSocket의 타임아웃
    private static final int MAX_RETRIES = 5; // TimeOut 발생 시 최대 5회까지 재전송
    private static final String serverIp = "127.0.0.1"; // IP
    // 메인 : 로그인(인증) -> 읽기/쓰기 패킷 전송
    public static void main(String[] args) {
        String filename;

        try (DatagramSocket socket = new DatagramSocket()) { // UDP 통신 소켓 생성
            socket.setSoTimeout(TIMEOUT); // TimeOut 설정
            InetAddress serverAddress = InetAddress.getByName(serverIp); // 서버 주소객체 생성

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("ID: ");
            String id = br.readLine();
            System.out.print("PW: ");
            String pw = br.readLine();
            System.out.print("mode: ");
            String mode = br.readLine();
            System.out.print("filename: ");
            filename = br.readLine(); // ID, PW, mode, file 입력

            byte[] authPacket = PacketUtil.createAuthPacket(id, pw); // ID, PW 패킷을 메서드호출로 생성(id:pw)
            DatagramPacket auth = new DatagramPacket(authPacket, authPacket.length, serverAddress, SERVER_PORT); // UDP 패킷으로 매핑
            socket.send(auth); // UDP 패킷을 전송

            byte[] buf = new byte[1024]; // 서버 응답을 받는 버퍼
            DatagramPacket authResp = new DatagramPacket(buf, buf.length); // 서버의 응답을 받는 패킷을 정의
            try {
                socket.receive(authResp); // 패킷을 리시브한다.
                int type = PacketUtil.getPacketType(buf); // 패킷의 타입을 받아서 저장
                if (type == Constants.ACK) { // ACK일 경우
                    System.out.println("사용자 인증 성공");
                } else if (type == Constants.ERROR) { // ERROR 수신시
                    String msg = PacketUtil.getErrorMessage(buf, authResp.getLength()); // ERROR 메시지
                    System.out.println("사용자 인증 실패: " + msg);
                    return;
                }
            } catch (SocketTimeoutException e) { // 이 경우 서버의 인증응답이 없음
                System.out.println("인증 응답 없음");
                return;
            }

            if (mode.equals("read")) { // read일 경우
                sendReadRequest(socket, serverAddress, filename); // RRQ요청
            } else if (mode.equals("write")) { // write일 경우
                sendWriteRequest(socket, serverAddress, filename); // WRQ요청
            } else {
                System.out.println("잘못된 모드. read 또는 write 선택");
            }

        } catch (IOException e) { // 소켓 생성, 전송, 수신 중 발생할 수 있는 I/O예외 처리
            e.printStackTrace();
        }
    }
    // WRQ요청 메서드
    private static void sendWriteRequest(DatagramSocket socket, InetAddress serverAddress, String filename) throws IOException {
        File file = new File(filename); // 업로드 파일
        if (!file.exists()) { // 파일 없음?
            System.out.println("전송할 파일이 존재하지 않습니다.");
            return;
        }

        byte[] wrqPacket = PacketUtil.createWRQPacket(filename); // WRQ 패킷 생성 메서드
        DatagramPacket sendPacket = new DatagramPacket(wrqPacket, wrqPacket.length, serverAddress, SERVER_PORT); // UDP 패킷으로 매핑
        socket.send(sendPacket); // 패킷 전송
        System.out.println("WRQ 전송 완료: " + filename);

        byte[] buffer = new byte[1024]; // 서버의 응답을 받는 버퍼
        DatagramPacket ackPacket = new DatagramPacket(buffer, buffer.length); // 서버의 응답을 받는 UDP 패킷 생성
        try {
            socket.receive(ackPacket); // 응답을 받습니다.
        } catch (SocketTimeoutException e) { // 응답이 없는 경우
            System.out.println("서버 응답 없음");
            return;
        }

        int ackType = PacketUtil.getPacketType(buffer); // 수신 패킷 타입 확인
        int ackBlock = PacketUtil.getBlockNumber(buffer); // 수신 패킷의 블록번호 확인
        // 서버에게 ACK(0)를 받았는지 확인 -> ACK(0)의 의미
        if (ackType != Constants.ACK || ackBlock != 0) { // 수신한 패킷이 ACK, 블록번호는 첫번째 여야함
            System.out.println("오류 : ACK(0)가 아님");
            return;
        }

        try (FileInputStream fis = new FileInputStream(filename)) { // FileInputStream을 통해 DATA 패킷전송
            byte[] fileBuffer = new byte[Constants.BLOCK_SIZE]; // 파일을 블록단위(512byte)로 읽음
            int blockNumber = 1; // 블록의 시작은 1부터

            while (true) { // 블록 전송 반복문
                int bytesRead = fis.read(fileBuffer); // 512byte씩 읽어가며, Data패킷 생성
                if (bytesRead == -1) break; // 파일의 마지막을 읽으면 break

                byte[] dataPacket = PacketUtil.createDataPacket(blockNumber, fileBuffer, bytesRead); // 데이터를 전송하는 패킷 생성

                boolean ackReceived = false; // ACK 받았는지
                int retries = 0; // TimeOut 카운트

                while (!ackReceived && retries < MAX_RETRIES) { // 서버가 받거나 TimeOut 카운트 까지 반복
                    DatagramPacket data = new DatagramPacket(dataPacket, dataPacket.length, serverAddress, SERVER_PORT); // Data패킷을 UDP 패킷으로 매핑
                    socket.send(data); // 전송
                    System.out.println("DATA 전송: block " + blockNumber);

                    try {
                        socket.receive(ackPacket); // ACK 받기
                        int type = PacketUtil.getPacketType(ackPacket.getData()); // 타입 받고
                        int ackNum = PacketUtil.getBlockNumber(ackPacket.getData()); // 해당 패킷의 숫자 받음

                        if (type == Constants.ACK && ackNum == blockNumber) { // ACK가 아니거나 숫자가 일치하면
                            System.out.println("ACK 수신: block " + blockNumber);
                            ackReceived = true;
                        }
                    } catch (SocketTimeoutException e) { // 응답이 이상함
                        retries++;
                        System.out.println("재전송 시도: block " + blockNumber); // 오류 카운트 올리고 다시 반복
                    }
                }

                if (!ackReceived) { // 완전 전송 실패
                    System.out.println("전송 실패: block " + blockNumber);
                    return;
                }

                if (bytesRead < Constants.BLOCK_SIZE) { // 마지막 블록일 경우(512보다 작음)
                    System.out.println("전송 완료");
                    break; //종료
                }

                blockNumber++;
            }
        }
    }
    // RRQ 패킷 전송
    private static void sendReadRequest(DatagramSocket socket, InetAddress serverAddress, String filename) throws IOException {
        byte[] rrqPacket = PacketUtil.createRRQPacket(filename); // RRQ 패킷 생성
        DatagramPacket sendPacket = new DatagramPacket(rrqPacket, rrqPacket.length, serverAddress, SERVER_PORT); // RRQ 패킷 UDP 패킷으로 매핑
        socket.send(sendPacket);
        System.out.println("RRQ 전송 완료: " + filename);

        try (FileOutputStream fos = new FileOutputStream("received_" + filename)) { // 파일을 received_file로 생성
            int expectedBlock = 1; // 블록 숫자

            while (true) {
                byte[] buffer = new byte[1024]; // 패킷 받는 버퍼
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length); // 받는 UDP 패킷 정의

                try {
                    socket.receive(receivePacket); // 패킷을 받음
                } catch (SocketTimeoutException e) { // TimeOut 발생 시
                    System.out.println("타임아웃 발생. 종료");
                    break;
                }

                int packetType = PacketUtil.getPacketType(buffer); // 타입을 저장

                if (packetType == Constants.DATA) { // DATA임?
                    int blockNumber = PacketUtil.getBlockNumber(buffer); // 숫자 확인
                    byte[] data = PacketUtil.getDataContent(buffer, receivePacket.getLength()); // Data를 읽을 바이트 배열

                    if (blockNumber == expectedBlock) { // 블록 숫자가 같으면 저장
                        fos.write(data); // 데이터 작성
                        System.out.println("DATA 수신: block " + blockNumber);

                        byte[] ackPacket = PacketUtil.createAckPacket(blockNumber); // 응답 패킷 생성
                        DatagramPacket ack = new DatagramPacket(ackPacket, ackPacket.length, serverAddress, SERVER_PORT);
                        socket.send(ack); // 받았음~

                        if (data.length < Constants.BLOCK_SIZE){
                            System.out.println("마지막 블럭을 읽었습니다.");
                            break; // 데이터 길이가 512보다 작으면 종료
                        }
                        expectedBlock++;
                    }else { // 수신한 블록 번호가 다르면
                        System.out.println("블록 불일치: 현재 = " + expectedBlock + ", 수신 = " + blockNumber);
                        byte[] nackPacket = PacketUtil.createNackPacket(expectedBlock); // NACK 블럭생성
                        DatagramPacket nack = new DatagramPacket(nackPacket, nackPacket.length, serverAddress, SERVER_PORT); // 매핑
                        socket.send(nack); // 전송
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("파일 저장 실패: " + e.getMessage());
        }
    }
}
