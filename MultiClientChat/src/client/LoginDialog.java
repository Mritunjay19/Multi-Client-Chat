package client;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.json.simple.JSONObject;

class LoginDialog extends JFrame  {
	JTextField username,password;
	JButton login,signup;
	JPanel p;
	public LoginDialog() {
		super("Login");
		this.setResizable(false);
		p=new JPanel();
		this.setLayout(new GridLayout(3,1));
		username=new JTextField(10);
		username.setFont(new Font("",2,20));username.setDocument(new JTextLimit(20));
		username.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "User Name"));
		
		password=new JTextField(10);password.setDocument(new JTextLimit(20));
		password.setFont(new Font("",2,20));
		
		password.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Password"));
		
		login=new JButton("Login");
		login.addActionListener(e->{
			loginClicked();
		});
		signup=new JButton("Signup");
		signup.addActionListener(e->{
			signupClicked();
		});
		p.add(login);p.add(signup);
		
		this.add(username);this.add(password);this.add(p);
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)ss.getWidth()/2-150,(int) ss.getHeight()/2-100);
		this.setSize(300, 200);		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((JFrame)e.getSource()).dispose();
			}
		});
		this.setVisible(true);
	}
	private void loginClicked() {
		JSONObject obj=new JSONObject();
		obj.put("type","1");
		sendJSON(obj,1);
	}
	private void signupClicked() {
		JSONObject obj=new JSONObject();
		obj.put("type","2");
		sendJSON(obj,2);
	}
	private void sendJSON(JSONObject obj,int type) {
		String uname=username.getText().toLowerCase(),pass=password.getText().toLowerCase();
		obj.put("username",uname);
		obj.put("password",pass);
		try{
			Socket s=new Socket("127.0.0.1",5000);
			System.out.println("Connected");
			DataOutputStream out=new DataOutputStream(s.getOutputStream());
			DataInputStream in=new DataInputStream(s.getInputStream());
			out.writeUTF(obj.toString());
			String connectionstatus=in.readUTF();
			if(connectionstatus.equals("TRUE")) {
				System.out.println("Connected to server");
				GUIHandler g=new GUIHandler(s,uname);
				new ChatReciever(s,g,uname).start();
				this.dispose();
			}
			else {
				System.out.println("Connection Denied");
				if(type==1)
					JOptionPane.showMessageDialog(null, "Incorrect Username or Password OR User already connected");
				else JOptionPane.showMessageDialog(null, "User Name already taken");
			}
			}catch(Exception e){
				JOptionPane.showMessageDialog(null,"Server Connection ERROR");
				System.out.println(e.getMessage());
			}
	}
	public static void main(String args[]) {
		new LoginDialog();
	}

}
