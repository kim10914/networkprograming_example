package example4;

import java.io.*;
//import java.text.SimpleDateFormat;
//import java.util.Date;

public class hw_4_2 {
	public static void main(String args[]) {
		File f1,f2;
		f1 = new File(args[0]);
		f2 = new File(args[1]);
		
		try {
			BufferedReader br1 = new BufferedReader(new FileReader(f1)); 
			BufferedReader br2 = new BufferedReader(new FileReader(f2)); //파일 객체 읽어서 버퍼를 활용해 한줄씩 저장
			
			boolean same = true; // 불리안을 통해 같은지 검사할 거임
			String text1, text2; // 각 줄을 저장하는 문자열 변수
			
			while(true) { // 각 문장을 읽어 한줄씩 비교하는 반복문
				text1 = br1.readLine(); 
				text2 = br2.readLine();
				
				if(text1 == null && text2 == null)
					break;
				if(text1 == null || text2 == null || !text1.equals(text2)) { // 하나라도 null이거나 텍스트가 같지 않으면 
					same = false; // 비교는 틀림
					break; // 반복 종료
				}
			}
			br1.close();
			br2.close(); // 버퍼 닫기
			if(same) { // 같음 반환 시 
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				System.out.println("두 파일의 내용이 같습니다.");
				System.out.println("f1의 마지막 수정 날짜는 " + f1.lastModified());
				System.out.println("f2의 마지막 수정 날짜는 " + f2.lastModified()); 
//				System.out.println("f1의 마지막 수정 날짜는 " + sdf.format(new Date(f1.lastModified())));
//				System.out.println("f2의 마지막 수정 날짜는 " + sdf.format(new Date(f2.lastModified()))); // 사람이 보기 쉬운 시간으로 출력
			}else {
				try {
					File new_file = new File("hw_4_1_3.txt");
					BufferedReader brF1 = new BufferedReader(new FileReader(f1)); // 파일 읽고 버퍼에 저장
					BufferedReader brF2 = new BufferedReader(new FileReader(f2));
					BufferedWriter bw = new BufferedWriter(new FileWriter(new_file)); // 뉴파일을 쓰기모드로 열어서 버퍼를 이용해 씀
					
					String append_text;
					
					while ((append_text = brF1.readLine()) != null ) { //한줄씩 읽어서
						bw.write(append_text + "\n"); // 출력
						bw.flush(); // 버퍼 비우기(난 왜 flush를 하지 않으면 출력이 없는가...)
					}
					while ((append_text = brF2.readLine()) != null) {
						bw.write(append_text + "\n");
						bw.flush();
					}
					System.out.println("파일 내용 병합 완료"); 
					brF1.close();
					brF2.close();
					bw.close(); // 작업 끝나고 닫기
				}catch(IOException e) {
					System.err.println(e.toString());
				}
			}
			
		}catch(IOException e) {
			System.err.println(e.toString());
		}		
	}
}
