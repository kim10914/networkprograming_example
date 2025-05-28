/*HttpURLConnectionExample와 MyHttpServer 예제 수정
- 클라이언트는 서버에 ID/PWD를 전송하여 인증을 득한 후 원하는 파일을 전송 받을 수 있음
- POST 메소드 이용*/
package example12;
import java.io.*;
import java.net.*;
import java.util.*;

public class hw_12_3 {
    public static void main(String[] args) {
        try {
            //사용자 입력 받음
            Scanner scanner = new Scanner(System.in);
            System.out.print("서버 URL 입력 : ");
            String inputUrl = scanner.nextLine();
            // ID, PWD 받음
            System.out.print("ID 입력: ");
            String id = scanner.nextLine();
            System.out.print("PWD 입력: ");
            String pwd = scanner.nextLine();
            //URL 객체 생성 및 연결
            URL url = new URL(inputUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //POST 방식 요청 준비
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // 보낼 수 있음으로 설정
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // 일반적인 HTML 폼 전송 방식
            // 인증 정보 전송
            String postData = "id=" + URLEncoder.encode(id, "UTF-8") + "&pwd=" + URLEncoder.encode(pwd, "UTF-8");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes());
                os.flush();
            }
            // 응답 코드, 메시지 받아옴
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();

            System.out.println("응답 코드: " + responseCode);
            System.out.println("응답 메시지: " + responseMessage);
            // 200 ok? -> 파일 다운로드 
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String contentType = conn.getContentType(); // 컨텐트 타입
                System.out.println("Content-Type: " + contentType);

                String fileName = "downloaded_file"; // 파일 이름
                String disposition = conn.getHeaderField("Content-Disposition"); // 응답 헤더
                if (disposition != null && disposition.contains("filename=")) { // 응답 헤더가 있으면 그 값으로 fileName 없으면 downloaded_file
                    fileName = disposition.split("filename=")[1].replace("\"", "").trim();
                }
                // 디렉터리 생성 -> download
                File downloadDir = new File("download");
                if (!downloadDir.exists()) downloadDir.mkdir(); // 생성
                //파일 저장(인풋 스트림을 통해 파일을 읽어 butter에 넣으면서 반복적으로 저장)
                File outFile = new File(downloadDir, fileName);
                try (InputStream in = conn.getInputStream(); OutputStream out = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    System.out.println("파일 다운로드 완료: " + outFile.getAbsolutePath());
                }
            } else {
                System.out.println("파일 요청 실패 또는 인증 실패: " + responseCode);
            }

            conn.disconnect();

        } catch (IOException e) {
            System.out.println("오류 발생: " + e);
        }
    }
}
