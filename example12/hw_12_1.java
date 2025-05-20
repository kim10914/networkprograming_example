package example12;

import java.io.*;
import java.net.URI;
import java.net.*;


public class hw_12_1 {
	public static void main(String[] args) throws Exception{
        String URL = "https://naver.com";
        URL urlObj = new URL(URL);
        try {
        	
        }catch(IOException e) {
        	System.out.println(e);
        }
		/*HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.example.com"))
                .build();

        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        HttpHeaders headers = response.headers();
        headers.map().forEach((name, values) -> {
            System.out.println(name + ": " + values);
            }); */
    }
}
