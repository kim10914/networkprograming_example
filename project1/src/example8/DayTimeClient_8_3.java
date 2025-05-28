package example8;

import java.io.*;
import java.net.*;

public class DayTimeClient_8_3 {
	public static void main(String args[]) {
		Socket theSocket;
		String host;
		InputStream is;
		BufferedReader reader;
		if(args.length>0) {
			host=args[0];
		}else {
			host="localhost";
		}
		try {
			theSocket = new Socket(host,13);
			is = theSocket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is));
			String theTime = reader.readLine();
			System.out.println("호스트의 시간은 "+ theTime +"이다.");
		}catch(UnknownHostException e) {
			System.err.println(args[0]+" 호스트 찾을 수 없습니다.");
		}catch(IOException e) {
			System.err.println(e);
		}
	}
}
