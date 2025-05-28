// 클라이언트 구현
package example7;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class hw_7_1_client extends JFrame implements ActionListener {
    private JTextField accountField, nameField, balanceField;
    private JButton insertBtn, selectBtn, deleteBtn;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String host; // 기본 호스트 로컬 호스트
    public hw_7_1_client(String host) {
        super("파일쓰기");

        setLayout(new GridLayout(5, 2)); // 그리드 레이아웃

        add(new JLabel("계좌번호:"));
        accountField = new JTextField();
        add(accountField);

        add(new JLabel("이름:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("잔고:"));
        balanceField = new JTextField();
        add(balanceField); // 각 입력창 생성

        insertBtn = new JButton("입력");
        selectBtn = new JButton("조회");
        deleteBtn = new JButton("삭제"); // 버튼 정의

        insertBtn.addActionListener(this);
        selectBtn.addActionListener(this);
        deleteBtn.addActionListener(this); // 이벤트 처리

        add(insertBtn);
        add(selectBtn);
        add(deleteBtn); // 각 요소 생성

        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Swing에서 창 닫기 구현(백그라운드 프로세스 종료도 함)
        setVisible(true);
        this.host = host; // main 호스트 받음
        connectToServer(); // 서버 연결 메서드
    }
    private void connectToServer() {
        try {
            socket = new Socket(host,3000); // host와 해당 호스트의 3000번 포트로 접속
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream()); // 스트림 데이터 역 직렬화
            // output -> input 순서로 열어야 deadlock현상(교착현상) 방지 가능
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "서버 연결 실패: " + e.getMessage()); // 현제 프레임 기준 GUI 팝업으로 에러메시지 출력
            System.exit(1);
        }
    }
    public void actionPerformed(ActionEvent e) {
        try {
            String command = e.getActionCommand(); // 사용자 입력 커맨드 처리

            if (command.equals("입력")) { // 입력
                int acc = Integer.parseInt(accountField.getText()); //계좌
                String name = nameField.getText(); // 이름
                double bal = Double.parseDouble(balanceField.getText()); // 잔고

                Record rec = new Record(acc, name, bal); // Record 객체 생성하고 전송
                output.writeObject("insert"); // insert 보내기
                output.writeObject(rec); // 직렬화 사용
                String response = (String) input.readObject(); // readObject로 객체 받아서 역직렬화 진해앟고 (String)으로 강제 캐스팅해서 변수에 저장
                JOptionPane.showMessageDialog(this, response); // 현 컴포넌트를 기준으로 응답 메시지를 팝업으로 띄움(입력되었습니다.) -> 형식상 2개의 argument를 요구함

                // 생성자 확인하세용(showMessageDialog)
//                public static void showMessageDialog(Component parentComponent,
//                        Object message) throws HeadlessException {
//                    showMessageDialog(parentComponent, message, UIManager.getString(
//                                    "OptionPane.messageDialogTitle", parentComponent),
//                            INFORMATION_MESSAGE);
//                }
                accountField.setText("");
                nameField.setText("");
				balanceField.setText(""); // 입력란을 공백 처리

            } else if (command.equals("조회")) {
                int acc = Integer.parseInt(accountField.getText()); // 계좌번호 저장
                output.writeObject("select");
                output.writeInt(acc);
                output.flush(); // ObjectOutputStream은 내부적으로 버퍼를 사용함
                Record rec = (Record) input.readObject(); // 객체 읽어서 저장함
                nameField.setText(rec.getName()); // 텍스트 필드 설정
                balanceField.setText(String.valueOf(rec.getBalance())); // 잔고 필드 설정

            } else if (command.equals("삭제")) {
                int acc = Integer.parseInt(accountField.getText()); // 계좌번호 저장
                output.writeObject("delete");
                output.writeInt(acc);
                output.flush(); // 버퍼 비워서 공백으로 값 설정(초기화)
                String response = (String) input.readObject();
                JOptionPane.showMessageDialog(this, response);
                accountField.setText("");
                nameField.setText("");
                balanceField.setText(""); // 입력란을 공백 처리

            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "오류 발생: " + ex.getMessage()); // 해당 처리 컴포넌트 오류 발생 팝업 출력
        }
    }

    public static void main(String[] args) {
        String host = (args.length > 0) ? args[0] : "localhost"; // 삼항 연산자 사용해서 참이면 입력 호스트 아니면 로컬호스트 연결
        new hw_7_1_client(host); // 생성자에 호스트 주기
    }
}
//Record 객체 생성은 서버에서 함