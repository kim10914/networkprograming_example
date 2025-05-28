package example12;

import java.net.*;
import java.io.*;

public class SaveBinaryData_12_6 {
    public static void main(String[] args) {
        URL u;
        URLConnection uc;

        for (int i = 0; i < args.length; i++) {
            try {
                u = new URL(args[i]);
                uc = u.openConnection(); // URLConnection 객체 생성

                String ct = uc.getContentType(); // 컨텐츠 유형 반환
                int cl = uc.getContentLength();  // 컨텐츠 길이 반환

                if (ct.startsWith("text/") || cl == -1) {
                    System.out.println("이진 데이터 파일이 아닙니다.");
                    return; // 텍스트 파일이면 프로그램 종료
                }

                InputStream is = uc.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[cl]; // 이진 데이터 크기만큼 할당
                int bytesread = 0;
                int offset = 0;

                while (offset < cl) {
                    bytesread = bis.read(buffer, offset, buffer.length - offset);
                    if (bytesread == -1) break;
                    offset += bytesread;
                }

                bis.close();

                if (offset != cl) {
                    System.out.println("데이터를 정상적으로 읽지 않았습니다.");
                }

                String filename = u.getFile(); // URL에서 파일 이름 읽음
                filename = filename.substring(filename.lastIndexOf('/') + 1); // 경로 제외한 파일명만 추출

                FileOutputStream fout = new FileOutputStream(filename);
                fout.write(buffer); // 데이터 저장
                fout.flush();
                fout.close();
            } catch (MalformedURLException e) {
                System.out.println("입력된 URL은 잘못된 URL입니다.");
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}

