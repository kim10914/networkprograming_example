/*URL에 의해 지정된 웹 페이지의 전체 HTTP 헤더 정보와 웹페이지의 내용을 HttpURLConnection
클래스의 메소드 활용하여 콘솔창에 출력하는 프로그램을 작성하시오(응답 라인의 응답코드와
응답구문 포함). 단, 10일 이내 수정된 웹페이지만 가져오며, 해당 웹페이지가 없는 경우 ‘요청된
웹페이지가 10일 이내에 수정되지 않음’을 출력함*/
package example12;

import java.io.*;
import java.net.*;
import java.util.*;

public class hw_12_1 {
    public static void main(String[] args) {
        try {
            URL url = new URL("https://www.naver.com/"); // 테스트용 정적 웹페이지
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            long tenDaysAgo = System.currentTimeMillis() - (10L * 24 * 60 * 60 * 1000); // 현재시간 - 10일(10일 전 시간을 저장)
            conn.setIfModifiedSince(tenDaysAgo); // 10일 안에 수정 되었으면
            conn.setRequestMethod("GET"); // GET 요청

            // 연결 수행
            int responseCode = conn.getResponseCode(); // 200?
            String responseMessage = conn.getResponseMessage(); // ok?

            System.out.println("응답 코드: " + responseCode);
            System.out.println("응답 메시지: " + responseMessage);
            // 응답 메시지 200일 경우 해더 정보 출력 하는 부분
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("\n[HTTP 헤더 정보]");
                Map<String, List<String>> headers = conn.getHeaderFields(); // 서버의 응답 헤더를 키-값 형태로 저장
                // 서버로 부터 받은 전체 HTTP 응답 헤더를 출력
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    String key = entry.getKey(); // 헤더 이름과 값의 목록
                    List<String> values = entry.getValue(); // 헤더 이름에 대한 값 목록
                    System.out.println((key != null ? key : "[Status-Line]") + ": " + String.join(", ", values));
                }

                System.out.println("\n[웹 페이지 내용]");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                reader.close();
            } else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) { // 10일 이내에 수정되지 않음
                System.out.println("요청된 웹페이지가 10일 이내에 수정되지 않음");
            } else {
                System.out.println("요청 실패: " + responseCode + " " + responseMessage); // 요청도 실패한 경우
            }

            conn.disconnect();

        } catch (IOException e) {
            System.out.println("오류 발생: " + e);
        }
    }
}
