package server;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ServerInfo extends JFrame{
	JTextField database,ip,username,password,port;
	JButton start,cancel;
	Server server;

	ServerInfo(Server server){
		super("Start Server");
		this.server=server;
		database=new JTextField();database.setFont(new Font("",2,20));
		database.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Database Name"));
		ip=new JTextField();ip.setFont(new Font("",2,20));ip.setText("127.0.0.1");
		ip.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "SQL Server IP"));
		username=new JTextField();username.setFont(new Font("",2,20));
		username.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "SQL Username"));
		password=new JTextField();password.setFont(new Font("",2,20));
		password.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "SQL Password"));
		port=new JTextField();port.setFont(new Font("",2,20));port.setText("3306");
		port.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "SQL Port"));

		this.setLayout(new GridLayout(6,1));
		this.add(ip);
		this.add(port);
		this.add(database);
		this.add(username);
		this.add(password);
		JPanel p=new JPanel();
		start=new JButton("START");start.addActionListener(e->{
			startServer();
		});
		cancel=new JButton("CANCEL");cancel.addActionListener(e->{
			this.dispose();		
		});
		p.add(start);p.add(cancel);
		this.add(p);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		this.setResizable(false);
		this.setSize(300,400);
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)ss.getWidth()/2-150,(int) ss.getHeight()/2-250);		
		this.setVisible(true);
	}
	private void startServer() {

		server.setFields(ip.getText(),port.getText() ,database.getText(),username.getText(), password.getText());
		server.start();
		this.dispose();
	}
//	public static void main(String args []) {
//		new ServerInfo();
//	}
}
