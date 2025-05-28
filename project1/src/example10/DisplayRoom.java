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
   private JButton dr_btClear; // ��ȭ�� â ȭ�� �����
   private JButton dr_btLogout; // �α׾ƿ� ���� ��ư

   public JTextArea dr_taContents; // ��ȭ�� ���� ����Ʈâ
   public JList<String> dr_lstMember; // ��ȭ�� ������
   public Vector<String> rMember = new Vector<String>();

   public JTextField dr_tfInput; // ��ȭ�� �Է��ʵ�

   public static ClientThread dr_thread;

   public DisplayRoom(ClientThread client, String title){
      super(title);

      // ��ȭ�濡�� ����ϴ� ������Ʈ�� ��ġ�Ѵ�.
      Panel northpanel = new Panel();
      dr_btClear = new JButton("ȭ�������"); 
      dr_btClear.addActionListener(this);
      northpanel.add(dr_btClear);
   
      dr_btLogout = new JButton("�α׾ƿ�");
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

      dr_thread = client; // ClientThread Ŭ������ �����Ѵ�.

      addWindowListener(new WinListener());

   }

   class WinListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we){
         System.exit(0); // �α׾ƿ� ��ƾ���� �ٲ۴�.
      }
   }

   // ȭ�������, �α׾ƿ� �̺�Ʈ�� ó���Ѵ�.
   public void actionPerformed(ActionEvent ae){
      Button b = (Button)ae.getSource();
      if(b.getLabel().equals("ȭ�������")){

      // ȭ������� ó�� ��ƾ

      }else if(b.getLabel().equals("�α׾ƿ�")){

      // �α׾ƿ� ó�� ��ƾ
      }
   }

   // �Է��ʵ忡 �Է��� ��ȭ���� ������ �����Ѵ�.
   public void keyPressed(KeyEvent ke){
      if(ke.getKeyChar() == KeyEvent.VK_ENTER){
         String words = dr_tfInput.getText(); // ��ȭ���� ���Ѵ�.
         dr_thread.requestSendWords(words); // ��ȭ���� ������ ����ڿ� �����Ѵ�.
      }
   }
   
   public void keyReleased(KeyEvent ke){}
   public void keyTyped(KeyEvent ke){}
}