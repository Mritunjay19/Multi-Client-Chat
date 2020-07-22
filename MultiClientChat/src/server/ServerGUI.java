package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

class ServerGUI extends Thread{
	JFrame f;
	JTabbedPane p;
	JPanel connected,all;
	JList<String> l1,l2;
	DefaultListModel<String> m1,m2;
	ServerSocket ss;
	ServerGUI(ServerSocket ss){
		this.ss=ss;
		f=new JFrame("Server");
		p=new JTabbedPane();
		connected=new JPanel();
		connected.setLayout(new BorderLayout());
		all=new JPanel();
		all.setLayout(new BorderLayout());
		l1=new JList<String>();
		l2=new JList<String>();
		m1=new DefaultListModel<String>();m2=new DefaultListModel<String>();
		l1.setModel(m1);l2.setModel(m2);
		connected.add(l1);all.add(l2);
		p.add("Connected Users", connected);
		p.add("All Users",all);
		f.add(p);
		f.setSize(300,400);
		f.setLocation(400, 100);
		f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		Dimension sss = Toolkit.getDefaultToolkit().getScreenSize();
		f.setLocation((int)sss.getWidth()/2-150,(int) sss.getHeight()/2-200);		
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int r=JOptionPane.showConfirmDialog(f, "Do you want to close Server ?");
				if(r==0) {
					try {
						ss.close();
						f.dispose();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
	public void run() {
		f.setVisible(true);
	}

	void add1(String user) {
		m1.addElement(user);
	}
	void add2(String user) {
		m2.addElement(user);
	}
	void remove1(String user) {
		m1.removeElement(user);
	}
	void remove2(String user) {
		m2.removeElement(user);
	}
	void addAllUsers(Connection sqlconnection) {
		try {
			Statement st=sqlconnection.createStatement();
			ResultSet rs=st.executeQuery("select username from users");
			while(rs.next()) {
				add2(rs.getString(1));
			}
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
