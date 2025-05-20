package example12;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class MyHTTPServer_12_2 {

    public static void main(String[] args) throws Exception {
        System.out.println("MyHTTPServer Started");
        HttpServer server = HttpServer.create(new InetSocketAddress(5000), 0); // 포트 5000
        server.createContext("/", new DetailHandler()); // 모든 경로에 대해 핸들러 설정
        server.setExecutor(Executors.newFixedThreadPool(10)); // 멀티스레드 처리
        server.start(); // 서버 시작
    }

    public static String getResponse() {
        StringBuilder responseBuffer = new StringBuilder();
        responseBuffer
                .append("<html><h1>HTTPServer Home Page....</h1><br>")
                .append("<b>Welcome to the new and improved web</b><br>")
                .append("</html>");
        return responseBuffer.toString();
    }

    static class DetailHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 요청 헤더 출력
            System.out.println("\nRequest Headers:");
            Headers requestHeaders = exchange.getRequestHeaders();
            Set<String> keySet = requestHeaders.keySet();
            for (String key : keySet) {
                List<String> values = requestHeaders.get(key);
                String header = key + " = " + values.toString();
                System.out.println(header);
            }

            // 요청 방식 확인
            String requestMethod = exchange.getRequestMethod();

            if (requestMethod.equalsIgnoreCase("POST")) {
                // POST 요청 본문 처리
                System.out.println("Request Body:");
                InputStream in = exchange.getRequestBody();
                if (in != null) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = br.readLine()) != null) {
                            response.append(inputLine);
                        }
                        br.close();
                        System.out.println(response.toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("Request body is empty");
                }
            }

            // 응답 헤더 구성
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/html");
            responseHeaders.set("Server", "MyHTTPServer/1.0");
            responseHeaders.set("Set-cookie", "userID=Cookie Monster");

            // 응답 본문 작성
            String responseMessage = getResponse();
            byte[] responseBytes = responseMessage.getBytes();

            // 응답 헤더 전송 (200 OK, 콘텐츠 길이 포함)
            exchange.sendResponseHeaders(200, responseBytes.length);

            // 응답 헤더 출력 (콘솔용)
            System.out.println("Response Headers:");
            Set<String> responseHeadersKeySet = responseHeaders.keySet();
            for (String key : responseHeadersKeySet) {
                List<String> values = responseHeaders.get(key);
                String header = key + " = " + values.toString();
                System.out.println(header);
            }

            // 응답 본문 전송
            try (OutputStream responseBody = exchange.getResponseBody()) {
                responseBody.write(responseBytes);
            }
        }
    }
}
