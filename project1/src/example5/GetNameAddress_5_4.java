package example5;

import java.io.*;
import java.net.*;

public class GetNameAddress_5_4 {
	public static void main(String args[]) {
		String hostname;
		BufferedReader br;
		br = new BufferedReader(new InputStreamReader(System.in)); //bufferedReader를 사용하는 이유는 readLine을 쓰기위해 
		System.out.println("호스트 이름 또는 IP 주소를 입력하세요");
		try {
			if((hostname = br.readLine()) != null) {
				InetAddress addr = InetAddress.getByName(hostname);
				System.out.println("호스트 이름은" +addr.getHostName());
				System.out.println("IP 주소는 "+addr.getHostAddress());
			}
			InetAddress laddr = InetAddress.getLocalHost();
			System.out.println("로컬 호스트 이름은 "+laddr.getHostName());
			System.out.println("로컬 IP 주소는 "+laddr.getHostAddress());
		}catch(UnknownHostException e) {
			System.out.println(e);
		}catch(IOException e) {
			System.out.println(e);
		}
	}
}
