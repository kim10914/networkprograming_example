package example12;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class HttpURLConnectionExample_12_1 {

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception {
        HttpURLConnectionExample_12_1 http = new HttpURLConnectionExample_12_1();
        System.out.println("Sent HTTP POST request");
        http.sendPost();
    }

    // send client POST data
    private void sendPost() {
        try {
            URL url = new URL("http://localhost:5000"); // URL 설정
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true); // POST 방식
            connection.setRequestMethod("POST"); // POST 메서드 설정
            connection.setRequestProperty("User-Agent", USER_AGENT); // 헤더 설정

            // POST 데이터 전송
            OutputStream os = connection.getOutputStream();
            os.write("Ts=000005".getBytes()); // 바디 작성
            os.close();

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == 200) {
                String response = getResponse(connection);
                System.out.println("response: " + response);
            } else {
                System.out.println("Bad Response Code: " + responseCode);
            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // read POST response data
    private String getResponse(HttpURLConnection connection) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {

            // 응답 헤더 출력
            System.out.println("Request Headers");
            Map<String, List<String>> requestHeaders = connection.getHeaderFields();
            Set<String> keySet = requestHeaders.keySet();

            for (String key : keySet) {
                List<String> values = requestHeaders.get(key);
                String header = key + " = " + values.toString();
                System.out.println(header);
            }

            // 응답 바디 읽기
            System.out.println();
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine).append("\n");
            }

            return response.toString(); // 바디 반환

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
