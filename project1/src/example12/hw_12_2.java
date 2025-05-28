/*URL을 입력으로 받아 해당 URL이 지정하는 자원의 content-type을 확인한 후 바이너리
파일인 경우 서버에서 다운로드 받아 URL에 포함된 것과 같은 파일 이름으로
‘download’라는 디렉토리에 저장하는 프로그램을 작성하시오*/
package example12;

import java.io.*;
import java.net.*;
import java.util.*;

public class hw_12_2 {
    public static void main(String[] args) {
        try {
            // 사용자로부터 URL 입력 받기
            Scanner scanner = new Scanner(System.in);
            System.out.print("다운로드할 URL 입력: ");
            String inputUrl = scanner.nextLine();
            //URL 연결 생성
            URL url = new URL(inputUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // 응답코드 확인
            int responseCode = conn.getResponseCode(); // 200
            String responseMessage = conn.getResponseMessage(); //ok

            System.out.println("응답 코드: " + responseCode);
            System.out.println("응답 메시지: " + responseMessage);

            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 ok?
                String contentType = conn.getContentType();
                System.out.println("Content-Type: " + contentType);

                // 바이너리 파일인지 판별 (text나 html이 아니면 바이너리로 간주)
                if (contentType != null && !contentType.startsWith("text") && !contentType.contains("html")) {
                    // 파일 이름 추출
                    String filePath = url.getPath();
                    String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
                    if (fileName.isEmpty()) fileName = "downloaded_file";

                    // 디렉토리 생성
                    File downloadDir = new File("download");
                    if (!downloadDir.exists()) downloadDir.mkdir();

                    // 파일 저장 경로 설정
                    File outFile = new File(downloadDir, fileName);
                    try (InputStream in = conn.getInputStream(); OutputStream out = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead); // 읽은 만큼만 저장
                        }
                        System.out.println("파일 다운로드 완료: " + outFile.getAbsolutePath());
                    }
                } else {
                    System.out.println("이 URL은 바이너리 파일이 아닙니다. (Content-Type: " + contentType + ")");
                }
            } else {
                System.out.println("요청 실패: " + responseCode + " " + responseMessage);
            }

            conn.disconnect(); // 연결 종료

        } catch (IOException e) {
            System.out.println("오류 발생: " + e);
        }
    }
}
