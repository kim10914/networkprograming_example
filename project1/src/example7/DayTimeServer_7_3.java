package example7;

import java.io.*;
import java.net.*;
import java.util.Date;

public class DayTimeServer_7_3 {
	public final static int daytimeport=13;
	public static void main(String args[]) {
		ServerSocket theServer;
		Socket theSocket = null;
		BufferedWriter writer;
		try {
			theServer = new ServerSocket(daytimeport);
			while(true) {
				try {
					theSocket = theServer.accept();
					OutputStream os = theSocket.getOutputStream();
					writer = new BufferedWriter(new OutputStreamWriter(os));
					Date now = new Date();
					writer.write(now.toString()+"\r\n");
					writer.flush();
					theSocket.close();
				}catch(IOException e) {
					System.out.println(e);
				}finally {
					try {
						if(theSocket != null) theSocket.close();
					}catch(IOException e) {
						System.out.println(e);
					}
				}
			}
		}catch(IOException e) {
			System.out.println(e);
		}
			
	}
}
