package example10;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class DisplayRoom extends JFrame implements ActionListener, KeyListener
{
   private JButton dr_btClear; // 대화말 창 화면 지우기
   private JButton dr_btLogout; // 로그아웃 실행 버튼
   private JButton dr_btWhisper; // 쪽지 보내기 버튼 추가

   public JTextArea dr_taContents; // 대화말 내용 리스트창
   public JTextField userID;
   public JList<String> dr_lstMember; // 대화방 참가자
   public Vector<String> rMember = new Vector<String>();

   public JTextField dr_tfInput; // 대화말 입력필드

   public static ClientThread dr_thread;

   public DisplayRoom(ClientThread client, String title){
      super(title);
      dr_thread = client; // ClientThread 클래스와 연결한다.

      // 대화방에서 사용하는 컴포넌트를 배치한다.
      JPanel northpanel = new JPanel();
      userID = new JTextField(15);
      userID.setEditable(false);
      northpanel.add(userID); // 상단에 접속 아이디 표시 추가

      // 화면 지우기 버튼
      dr_btClear = new JButton("화면지우기");
      dr_btClear.addActionListener(this);
      northpanel.add(dr_btClear);
      // 퇴실하기 버튼
      dr_btLogout = new JButton("퇴실하기");
      dr_btLogout.addActionListener(this);
      northpanel.add(dr_btLogout);
      // 메시지 표시창
      JPanel centerpanel = new JPanel();
      dr_taContents = new JTextArea(10, 27);
      dr_taContents.setEditable(false);
      centerpanel.add(dr_taContents);
      // 유저 표시창
      dr_lstMember = new JList<String>();
      JScrollPane sp = new JScrollPane(dr_lstMember);
      sp.setPreferredSize(new Dimension(100,180));
      centerpanel.add(sp);
      // 메시지 입력창
      JPanel southpanel = new JPanel();
      dr_tfInput = new JTextField(41);
      dr_tfInput.addKeyListener(this);
      southpanel.add(dr_tfInput);

      //쪽지 보내기 버튼 추가
      dr_btWhisper = new JButton("쪽지보내기");
      dr_btWhisper.addActionListener(this);
      southpanel.add(dr_btWhisper);

      add("North", northpanel);
      add("Center", centerpanel);
      add("South", southpanel);

      addWindowListener(new WinListener());

   }

   class WinListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we){
         dr_thread.requestQuitRoom();

         ChatClient main = new ChatClient("대화방 개설 및 입장");
         main.cc_thread = dr_thread;
         dr_thread.ct_client = main;

         main.pack();
         main.setVisible(true);
      }
   }

   public void updateUserIdLabel() {
      userID.setText("ID: " + dr_thread.userID);
   }

   // 화면지우기, 로그아웃 이벤트를 처리한다.
   public void actionPerformed(ActionEvent ae){
//      Button b = (Button)ae.getSource(); // 버튼이라서 오류뜸 ㅠㅠ
      Object src = ae.getSource();
      if(src == dr_btClear){
         dr_taContents.setText(""); // 화면지우기 처리 루틴
      }else if(src == dr_btLogout){// 퇴실하기
         dr_thread.requestQuitRoom(); // 로그아웃 처리 루틴 -> 퇴실하기 루틴

         ChatClient main = new ChatClient("대화방 개설 및 입장");
         main.cc_thread = dr_thread;
         dr_thread.ct_client = main;

         main.pack();
         main.setVisible(true);
         this.dispose(); // 현재 창 닫기
      }else if(src == dr_btWhisper){ // 쪽지 보내기 루틴
         String toId = dr_lstMember.getSelectedValue(); // 사용자 선택
         if (toId != null) {
            String msg = dr_tfInput.getText().trim(); // 메시지
//            String msg = JOptionPane.showInputDialog(this, "귓속말 내용 입력:"); // 입력창 새로 띄우기
            if (msg != null && !msg.trim().isEmpty()) {
               dr_thread.requestWhisper(toId, msg); // 귓말 요청 메서드 호출

               dr_taContents.append("[귓속말] 나 → " + toId + ": " + msg + "\n"); // 나한테도 보이기
               System.out.println("[귓속말] 나 → " + toId + ": " + msg + "\n");
               dr_tfInput.setText(""); // 입력 필드 초기화
            }
         } else {
            JOptionPane.showMessageDialog(this, "대상 사용자를 선택하세요.", "쪽지 보내기", JOptionPane.WARNING_MESSAGE);
         }
      }
   }

   // 입력필드에 입력한 대화말을 서버에 전송한다.
   public void keyPressed(KeyEvent ke){
      if(ke.getKeyChar() == KeyEvent.VK_ENTER){
         String words = dr_tfInput.getText(); // 대화말을 구한다.
         if (!words.trim().isEmpty()){ // 공백 검사
            dr_thread.requestSendWords(words); // 대화말을 참여한 사용자에 전송한다.
         }
      }
   }

   public void keyReleased(KeyEvent ke){}
   public void keyTyped(KeyEvent ke){}
}