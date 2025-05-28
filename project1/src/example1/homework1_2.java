package example1;

import javax.swing.*; // GUI 사용
import java.awt.*; // 레이아웃 및 GUI관련 기능
import java.awt.event.ActionEvent; // 이벤트 처리
import java.awt.event.ActionListener; // 에벤트 듣기
import java.io.*;

//GUI 생성 코드 
public class homework1_2 extends JFrame {
    private JTextField inputFileField, outputFileField; // 입출력 파일 경로 입력 필드
    private JTextArea textArea; // 복사된 문자열 표현 구역
    private JButton copyButton; // 복사 버튼

    public homework1_2() {
    	//GUI창 설정
        setTitle("파일 복사 프로그램");
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫으면 프로그램종료
//        setLayout(new BorderLayout()); //없어도 되는 기능(외곽 불더주기)

        // 상단 입력 패널 (파일 입력 필드 + 확인 버튼)
        JPanel panel = new JPanel(new GridBagLayout()); //디폴트레이아웃을 grid를 통해 요소들 생성
        GridBagConstraints gbc = new GridBagConstraints(); //하나의 셀에 여러 요소를 넣기 위해 gridBag레이아 웃 사용
        gbc.insets = new Insets(5, 5, 5, 5); //여백 추가
        gbc.fill = GridBagConstraints.HORIZONTAL; //입력 필드 가로 크기 늘리기

        // 입력 파일 필드
        gbc.gridx = 0; gbc.gridy = 0; // 0행 0열
        panel.add(new JLabel("입력파일"), gbc);
        gbc.gridx = 1;
        inputFileField = new JTextField(12); //입력 필드
        panel.add(inputFileField, gbc); 

        // 출력 파일 필드
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("출력파일"), gbc);
        gbc.gridx = 1;
        outputFileField = new JTextField(12);
        panel.add(outputFileField, gbc);

        // 확인 버튼 (출력 파일 입력창 옆에 배치)
        copyButton = new JButton("확인"); // 버튼 초기화
        gbc.gridx = 2; // 출력 파일 입력창 오른쪽에 배치
        panel.add(copyButton, gbc);

        add(panel, BorderLayout.NORTH);

        // 파일 내용 출력 영역 (파일내용 제목 + JTextArea)
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel fileLabel = new JLabel("파일내용", JLabel.CENTER);
        contentPanel.add(fileLabel, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false); // 사용자의 textArea 수정을 막기위해
        contentPanel.add(new JScrollPane(textArea), BorderLayout.CENTER); // 스크롤 기능 추가
        add(contentPanel, BorderLayout.CENTER); // 내용 표시 영역을 중앙으로 조정

        // 버튼 이벤트 설정
        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyFile(); // 파일 복사 진행
            }
        });

        setVisible(true);
    }
    //파일 복사 메서드
    private void copyFile() {
        String inputFile = inputFileField.getText(); //경로 저장
        String outputFile = outputFileField.getText();

        if (inputFile.isEmpty() || outputFile.isEmpty()) {
            JOptionPane.showMessageDialog(this, "파일명을 입력하세요!", "오류", JOptionPane.ERROR_MESSAGE); // 오류처리
            return;
        }

        try (FileInputStream fis = new FileInputStream(inputFile); // 입력파일(바이트 단위 읽기)
             FileOutputStream fos = new FileOutputStream(outputFile); // 출력파일
             BufferedReader br = new BufferedReader(new FileReader(inputFile))) { //입력파일의 텍스트 저장

            // 파일 복사
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length); 
            }

            // 파일 내용을 읽어 TextArea에 표시
            textArea.setText(""); // 기존 내용 초기화
            String line;
            while ((line = br.readLine()) != null) {
                textArea.append(line + "\n"); // 한줄씩 읽고 enter
            }

            JOptionPane.showMessageDialog(this, "파일 복사가 완료되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE); //성공메시지

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "파일 처리 중 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE); //예외처리
        }
    }

    public static void main(String[] args) {
        new homework1_2();
    }
}
