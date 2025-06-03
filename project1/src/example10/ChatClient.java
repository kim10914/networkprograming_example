package example10;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ChatClient extends JFrame implements ActionListener
{
   public JTextField cc_tfLogon; // 로그온 입력 텍스트 필드
   public JButton cc_btLogon; // 로그온 실행 버튼
   public JButton cc_btEnter; // 대화방 개설 및 입장 버튼
   public JButton cc_btLogout; // 로그아웃 버튼

   public JTextField cc_tfStatus; // 로그온 개설 안내
   public JTextField cc_tfDate; // 개설시각
   public JList<String> cc_lstMember = new JList<String>(); // 대화방 참가자
   public Vector<String> lMember = new Vector<String>(); // 동기화가 보장된 동적배열

   public static ClientThread cc_thread;
   public String msg_logon = "";

   // GUI 클래스
   public ChatClient(String str){
      super(str);

      // 로그온, 대화방 개설 및 입장 버튼을 설정한다.
      JPanel bt_JPanel = new JPanel();
      cc_btLogon = new JButton("로그온실행");
      cc_btLogon.addActionListener(this);
      bt_JPanel.add(cc_btLogon);

      cc_tfLogon = new JTextField(10);
      bt_JPanel.add(cc_tfLogon);

      cc_btEnter = new JButton("대화방입장");
      cc_btEnter.addActionListener(this);
      bt_JPanel.add(cc_btEnter);

      cc_btLogout = new JButton("로그아웃");
      cc_btLogout.addActionListener(this);
      bt_JPanel.add(cc_btLogout);
      add("Center", bt_JPanel);

      // 4개의 JPanel 객체를 사용하여 대화방 정보를 출력한다.
      JPanel roomPanel = new JPanel(); // 3개의 패널을 담을 패널객체
      roomPanel.setLayout(new BorderLayout());

      JPanel northPanel = new JPanel();
      cc_tfStatus = new JTextField("하단의 텍스트 필드에  ID를 입력하십시오,",43);
      // 대화방의 개설상태 알림
      cc_tfStatus.setEditable(false);
      northPanel.add(cc_tfStatus);

      JPanel centerPanel = new JPanel();
      centerPanel.add(new JLabel("로그온 시각 : "),BorderLayout.WEST);
      cc_tfDate = new JTextField("로그온 시각이 표시됩니다.");
      cc_tfDate.setEditable(false);
      centerPanel.add(cc_tfDate);

      JPanel southPanel = new JPanel();
      southPanel.add(new JLabel("로그온 사용자"));
      cc_lstMember = new JList<String>();
      JScrollPane sp = new JScrollPane(cc_lstMember);
      sp.setPreferredSize(new Dimension(100,100));
      southPanel.add(sp);

      roomPanel.add("North", northPanel);
      roomPanel.add("Center", centerPanel);
      roomPanel.add("South", southPanel);
      add("North", roomPanel);

      cc_btEnter.setEnabled(false);
      cc_btLogout.setEnabled(false); // 입장, 로그아웃 비활성화

      cc_tfLogon.requestFocusInWindow(); // 로그온 텍스트 필드에 포커스를 맞추는 메소드 추가

      addWindowListener(new WinListener());
   }

   class WinListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we){
         if (cc_thread != null) {
            cc_thread.requestLogout(); // 로그아웃 하고 종료 추가
            System.exit(0);
         }
      }
   }

   // 로그온, 대화방 개설 및 입장 버튼 눌림 이벤트를 처리한다.
   public void actionPerformed(ActionEvent ae){
      //JButton b = (JButton)ae.getSource();
      Object src = ae.getSource();
      if(src == cc_btLogon){ // 로그온 버튼
         // 로그온 처리 루틴
         msg_logon = cc_tfLogon.getText(); // 로그온 ID를 읽는다.
         if(!msg_logon.equals("")){
            cc_thread.requestLogon(msg_logon); // ClientThread의 메소드를 호출
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            cc_tfDate.setText(timeStamp);
            cc_tfStatus.setText(msg_logon + "(으)로 로그온 로그온 중입니다.");
            cc_tfLogon.setEditable(false);
            cc_btLogon.setEnabled(false);
            cc_btEnter.setEnabled(true);
            cc_btLogout.setEnabled(true); // GUI 설정
            
         }else{
            JOptionPane.showMessageDialog(null, "로그온 id를 입력하세요.", "로그온",
                    JOptionPane.WARNING_MESSAGE);
            cc_tfLogon.setEditable(true);
            cc_btLogon.setEnabled(true);
            cc_btEnter.setEnabled(false);
            cc_btLogout.setEnabled(false); // GUI 설정
         }
      }else if(src == cc_btEnter){
         // 대화방 개설 및 입장 처리 루틴
         msg_logon = cc_tfLogon.getText(); // 로그온 ID를 읽는다.
         if(!msg_logon.equals("")){
            cc_thread.requestEnterRoom(msg_logon); // ClientThread의 메소드를 호출
         }else{
            JOptionPane.showMessageDialog(null, "로그온을 먼저 하십시오.", "로그온",
                    JOptionPane.WARNING_MESSAGE);
         }

      }else if(src == cc_btLogout){
         // 로그아웃 처리 루틴
         cc_thread.requestLogout();
         cc_tfLogon.setText("");
         cc_tfLogon.setEditable(true);
         cc_btLogon.setEnabled(true);
         cc_btEnter.setEnabled(false);
         cc_btLogout.setEnabled(false); // 버튼 초기화
         cc_tfStatus.setText("로그아웃이 성공했습니다.");
         cc_tfDate.setText("로그온 시각이 표시됩니다.");
         lMember.clear();
         cc_lstMember.setListData(new String[]{});
      }
   }

   public static void main(String args[]){
      ChatClient client = new ChatClient("대화방 개설 및 입장");
      client.pack();
      client.setVisible(true);

      // 소켓을 생성하고 서버와 통신할 스레드를 호출한다.

      // 서버와 클라이언트를 다른 시스템으로 사용하는 경우
      // 실행 : java ChatClient [호스트이름과 포트번호가 필요하다.]
      // To Do

      cc_thread = new ClientThread(client); // 로컬 호스트용 생성자
      cc_thread.start(); // 클라이언트의 스레드를 시작한다.

   }
}