package SimpleChat;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class DisplayRoom extends JFrame implements ActionListener, KeyListener
{
   private JButton dr_btClear; // 대화말 창 화면 지우기
   private JButton dr_btLogout; // 로그아웃 실행 버튼

   public JTextArea dr_taContents; // 대화말 내용 리스트창
   public JList<String> dr_lstMember; // 대화방 참가자
   public Vector<String> rMember = new Vector<String>();

   public JTextField dr_tfInput; // 대화말 입력필드

   public static ClientThread dr_thread;

   public DisplayRoom(ClientThread client, String title){
      super(title);

      // 대화방에서 사용하는 컴포넌트를 배치한다.
      Panel northpanel = new Panel();
      dr_btClear = new JButton("화면지우기"); 
      dr_btClear.addActionListener(this);
      northpanel.add(dr_btClear);
   
      dr_btLogout = new JButton("로그아웃");
      dr_btLogout.addActionListener(this);
      northpanel.add(dr_btLogout);

      Panel centerpanel = new Panel();
      dr_taContents = new JTextArea(10, 27);
      dr_taContents.setEditable(false);
      centerpanel.add(dr_taContents);
     
      dr_lstMember = new JList<String>();
      JScrollPane sp = new JScrollPane(dr_lstMember);
      sp.setPreferredSize(new Dimension(100,180));
      centerpanel.add(sp);

      Panel southpanel = new Panel();
      dr_tfInput = new JTextField(41);
      dr_tfInput.addKeyListener(this);
      southpanel.add(dr_tfInput);

      add("North", northpanel);
      add("Center", centerpanel);
      add("South", southpanel);

      dr_thread = client; // ClientThread 클래스와 연결한다.

      addWindowListener(new WinListener());

   }

   class WinListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we){
         System.exit(0); // 로그아웃 루틴으로 바꾼다.
      }
   }

   // 화면지우기, 로그아웃 이벤트를 처리한다.
   public void actionPerformed(ActionEvent ae){
      Button b = (Button)ae.getSource();
      if(b.getLabel().equals("화면지우기")){

      // 화면지우기 처리 루틴

      }else if(b.getLabel().equals("로그아웃")){

      // 로그아웃 처리 루틴
      }
   }

   // 입력필드에 입력한 대화말을 서버에 전송한다.
   public void keyPressed(KeyEvent ke){
      if(ke.getKeyChar() == KeyEvent.VK_ENTER){
         String words = dr_tfInput.getText(); // 대화말을 구한다.
         dr_thread.requestSendWords(words); // 대화말을 참여한 사용자에 전송한다.
      }
   }
   
   public void keyReleased(KeyEvent ke){}
   public void keyTyped(KeyEvent ke){}
}