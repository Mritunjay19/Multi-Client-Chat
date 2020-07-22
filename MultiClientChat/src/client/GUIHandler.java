package client;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class GUIHandler extends Thread implements ListSelectionListener , ActionListener {
	JFrame f;
	JButton send;
	JTextArea message;
	JList<String> list;
	HashMap <String , MyPanel > map;
	Socket s;
	String username ,currentfriend="";
	JPanel chatpanel,p;
	DefaultListModel<String> friends; 
	ChatSender chatsender;
	JMenuItem logout,deleteaccount;
	JMenu menu;
	JMenuBar mb;
	ChatReciever chatreciever;
	
	 GUIHandler(Socket s, String uname) {
		chatsender=new ChatSender(s,uname);
		f=new JFrame(uname+" -Chats");
		send=new JButton("Send");
		send.addActionListener(this);
		chatpanel=new JPanel();
		chatpanel.setLayout(new BorderLayout());
		message=new JTextArea();message.setBorder(BorderFactory.createEtchedBorder());message.setDocument(new JTextLimit(100));
		this.s=s;
		this.username=uname;
		f.setLayout(new BorderLayout());
		list=new JList<String>();
		list.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Friends",TitledBorder.LEFT,TitledBorder.ABOVE_TOP,new Font("",3,20)));
		friends=new DefaultListModel<String>();
		list.setModel(friends);
		list.addListSelectionListener(this);
		list.setPreferredSize(new Dimension(100,400));
		f.add(new JScrollPane(list),BorderLayout.WEST);
		p=new JPanel();
		p.setLayout(new BorderLayout());
		p.add(message);p.add(send,BorderLayout.EAST);
		chatpanel.add(p,BorderLayout.SOUTH);
		f.add(chatpanel);
		f.setSize(400,400);
		f.setLocation(200,150);
		map=new HashMap<String,MyPanel>();
		mb=new JMenuBar();menu=new JMenu("Logout");
		logout=new JMenuItem("Log Out");logout.addActionListener(this);logout.setHorizontalAlignment(SwingConstants.RIGHT);
		deleteaccount=new JMenuItem("Delete Account");deleteaccount.addActionListener(this);deleteaccount.setHorizontalAlignment(SwingConstants.RIGHT);
		menu.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		menu.add(logout);menu.add(deleteaccount);
		mb.add(Box.createHorizontalGlue());mb.add(menu);
		f.setJMenuBar(mb);
		f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				confirmLogout();
			}
		});
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		f.setLocation((int)ss.getWidth()/2-200,(int) ss.getHeight()/2-200);
	}
	 
	
	public void valueChanged(ListSelectionEvent e) {
		chatpanel.removeAll();
		chatpanel.repaint();
		chatpanel.revalidate();
		chatpanel.add(p,BorderLayout.SOUTH);
		if(list.isSelectionEmpty())return;
		chatpanel.add(map.get(list.getSelectedValue()));
		currentfriend=(String)list.getSelectedValue();
	}
	public void run() {
		f.setVisible(true);
	}

	void addFriend(String friend) {
		System.out.println(friend+" added");
		
		map.put(friend, new MyPanel(friend));
		friends.addElement(friend);
	}
	 
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==(Object)send) {
			if(currentfriend.equals("")) {
				JOptionPane.showMessageDialog(null, "No Friend Selected");
			}
			else {
				map.get(currentfriend).addMyMessage(message.getText());
				chatsender.send1(currentfriend, message.getText());
				message.setText("");
			}
		}
		else if((JMenuItem)e.getSource()==logout) {
			confirmLogout();
		}
		else {
			int r=JOptionPane.showConfirmDialog(f, "Do you want to Delete Account ?", "Delete Account", JOptionPane.YES_NO_OPTION);
			if(r==0) {
				chatsender.send3();
			}
		}
	}


	private void confirmLogout() {
		int r=JOptionPane.showConfirmDialog(f, "Do you want to LogOut ?", "Log out", JOptionPane.YES_NO_OPTION);
		if(r==0) {
			chatsender.send2();
			disposeFrame();
		}
	}


	 void disposeFrame() {
		
		try {
			chatreciever.exited=true;
			f.dispose();
			s.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
	}
	
	void removeFriend(String friend) {
		System.out.println(friend+" removed");
		checkIfFriendSelected(friend);
		JOptionPane.showMessageDialog(f, friend+" has deleted his account !! ");
		map.remove(friend);
		friends.removeElement(friend);
	}

	private void checkIfFriendSelected(String friend) {
		try {
			String s=list.getSelectedValue();
			if(s.equals(friend)){
				list.clearSelection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	void setChatReciever(ChatReciever cr) {
		chatreciever=cr;
	}


	}
