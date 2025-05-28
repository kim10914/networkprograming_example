package SimpleChat;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;

public class ChatClient extends JFrame implements ActionListener
{
   
   private JTextField cc_tfLogon; // �α׿� �Է� �ؽ�Ʈ �ʵ�
   private JButton cc_btLogon; // �α׿� ���� ��ư
   private JButton cc_btEnter; // ��ȭ�� ���� �� ���� ��ư
   private JButton cc_btLogout; // �α׾ƿ� ��ư

   public JTextField cc_tfStatus; // �α׿� ���� �ȳ�
   public JTextField cc_tfDate; // �����ð�
   public JList<String> cc_lstMember = new JList<String>(); // ��ȭ�� ������
   public Vector<String> lMember = new Vector<String>();


   public static ClientThread cc_thread;

   public String msg_logon="";

   public ChatClient(String str){
      super(str);

      // �α׿�, ��ȭ�� ���� �� ���� ��ư�� �����Ѵ�.
      JPanel bt_JPanel = new JPanel();
      cc_btLogon = new JButton("�α׿½���");
      cc_btLogon.addActionListener(this);
      bt_JPanel.add(cc_btLogon);
      
      cc_tfLogon = new JTextField(10);
      bt_JPanel.add(cc_tfLogon);
      
      cc_btEnter = new JButton("��ȭ������");
      cc_btEnter.addActionListener(this);
      bt_JPanel.add(cc_btEnter);
      
      cc_btLogout = new JButton("�α׾ƿ�");
      cc_btLogout.addActionListener(this);
      bt_JPanel.add(cc_btLogout);
      add("Center", bt_JPanel);

      // 4���� JPanel ��ü�� ����Ͽ� ��ȭ�� ������ ����Ѵ�.
      JPanel roomPanel = new JPanel(); // 3���� �г��� ���� �гΰ�ü
      roomPanel.setLayout(new BorderLayout());

      JPanel northPanel = new JPanel();
      cc_tfStatus = new JTextField("�ϴ��� �ؽ�Ʈ �ʵ忡  ID�� �Է��Ͻʽÿ�,",43); 
      													// ��ȭ���� �������� �˸�
      cc_tfStatus.setEditable(false);
      northPanel.add(cc_tfStatus);
      
      JPanel centerPanel = new JPanel();
      centerPanel.add(new JLabel("�α׿� �ð� : "),BorderLayout.WEST);
      cc_tfDate = new JTextField("�α׿� �ð��� ǥ�õ˴ϴ�.");
      cc_tfDate.setEditable(false);
      centerPanel.add(cc_tfDate);

      JPanel southPanel = new JPanel();
      southPanel.add(new JLabel("�α׿� �����"));
      cc_lstMember = new JList<String>();
      JScrollPane sp = new JScrollPane(cc_lstMember);
      sp.setPreferredSize(new Dimension(100,100));
      southPanel.add(sp);

      roomPanel.add("North", northPanel);
      roomPanel.add("Center", centerPanel);
      roomPanel.add("South", southPanel);
      add("North", roomPanel);

      // �α׿� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�

      addWindowListener(new WinListener());
   }

   class WinListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we){

         System.exit(0); // ���߿� �α׾ƿ���ƾ���� ����
      }
   }

   // �α׿�, ��ȭ�� ���� �� ���� ��ư ���� �̺�Ʈ�� ó���Ѵ�.
   public void actionPerformed(ActionEvent ae){
      //JButton b = (JButton)ae.getSource();
	   String cmd = ae.getActionCommand();
      if(cmd.equals("�α׿½���")){

         // �α׿� ó�� ��ƾ
         msg_logon = cc_tfLogon.getText(); // �α׿� ID�� �д´�.
         if(!msg_logon.equals("")){
            cc_thread.requestLogon(msg_logon); // ClientThread�� �޼ҵ带 ȣ��
         }else{
         	JOptionPane.showMessageDialog(null, "�α׿� id�� �Է��ϼ���.", "�α׿�", 
        			JOptionPane.WARNING_MESSAGE); 
         }
      }else if(cmd.equals("��ȭ������")){

         // ��ȭ�� ���� �� ���� ó�� ��ƾ
         msg_logon = cc_tfLogon.getText(); // �α׿� ID�� �д´�.
         if(!msg_logon.equals("")){
            cc_thread.requestEnterRoom(msg_logon); // ClientThread�� �޼ҵ带 ȣ��
         }else{
      		  JOptionPane.showMessageDialog(null, "�α׿��� ���� �Ͻʽÿ�.", "�α׿�", 
            			JOptionPane.WARNING_MESSAGE); 
         }

      }else if(cmd.equals("�α׾ƿ�")){

      // �α׾ƿ� ó�� ��ƾ
      // To Do

      }
   }

   public static void main(String args[]){
      ChatClient client = new ChatClient("��ȭ�� ���� �� ����");
      client.pack();
      client.setVisible(true);

      // ������ �����ϰ� ������ ����� �����带 ȣ���Ѵ�.
      
      // ������ Ŭ���̾�Ʈ�� �ٸ� �ý������� ����ϴ� ���
      // ���� : java ChatClient [ȣ��Ʈ�̸��� ��Ʈ��ȣ�� �ʿ��ϴ�.]
      // To Do
            
       cc_thread = new ClientThread(client); // ���� ȣ��Ʈ�� ������
       cc_thread.start(); // Ŭ���̾�Ʈ�� �����带 �����Ѵ�.
      
   }
}
