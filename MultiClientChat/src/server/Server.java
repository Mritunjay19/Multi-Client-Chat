package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
public class Server extends Thread{
	Socket s;
	Connection sqlconnection;
	HashMap<String,Socket> connectedusers;
	boolean f=true;
	ServerGUI g;
	String ip,sqldatabase,sqlusername,sqlpassword,port;
	Server(){

	}
	void setFields(String ip,String port,String sqldatabase,String sqlusername,String sqlpassword) {
		this.ip=ip;
		this.port=port;
		this.sqlusername=sqlusername;
		this.sqldatabase=sqldatabase;
		this.sqlpassword=sqlpassword;
	}
	public void run() {
		if(!establishSQLConnection(ip,sqldatabase,sqlusername,sqlpassword)) {
			JOptionPane.showMessageDialog(null, "SQL Connection ERROR");return;
		}	
		startAcceptingConnections();
	}

	 void startAcceptingConnections() {
		try {
			ServerSocket ss=new ServerSocket(5000);
			g=new ServerGUI(ss);g.addAllUsers(sqlconnection);g.start();
			connectedusers=new HashMap<String,Socket>();
			s=null;
		while(f) {
				s=null;
				s=ss.accept();
				System.out.println("Client Socket Connected");
				DataOutputStream out=new DataOutputStream(s.getOutputStream());
				if(checkClient()) {
					out.writeUTF("TRUE");
				}
				else out.writeUTF("FALSE");
		}
		}
		catch(Exception e) {
			closeServer();
			System.out.println(e.getStackTrace());
		}
	}


	private void closeServer() {
		Iterator it=connectedusers.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			try {
				((Socket)e.getValue()).close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}


	boolean establishSQLConnection(String ip, String sqldatabase, String sqlusername, String sqlpassword) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			sqlconnection=DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+sqldatabase,sqlusername,sqlpassword);
			createTable();
			System.out.println("SQL Connection Established");
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
		
	}
	void createTable() {
		try {
			Statement st=sqlconnection.createStatement();
			st.executeUpdate("create table users(username varchar(20) primary key not null,password varchar(20) not null);");
			st.close();
		} catch (SQLException e) {
		}
	}
	
	private boolean checkClient() {
		try {
			DataInputStream in=new DataInputStream(s.getInputStream());
			JSONObject obj=(JSONObject)(new JSONParser().parse(in.readUTF()));
			String uname=(String) obj.get("username"),pass=(String) obj.get("password"),type=(String)obj.get("type");
			Statement st=sqlconnection.createStatement();
			if(connectedusers.containsKey(uname))return false;
			if(type.equals("1")) {
				ResultSet rs=st.executeQuery("select password from users where username = \""+uname+"\"");
				if(rs.next())
				{	
					if(pass.equalsIgnoreCase(rs.getString(1))) {
						st.close();
						System.out.println("Client Connection Success");
						connectedusers.put(uname,s);
						ClientHandler t=new ClientHandler(uname,s,sqlconnection,connectedusers,g);
						g.add1(uname);
						t.start();
						return true;
					}
				}
				st.close();
				System.out.println("Client Connection Failure");
				return false;
			}
			else {
				ResultSet rs=st.executeQuery("select * from users where username =\""+uname+"\"");
				if(!rs.next()) {
					System.out.println("new user registered "+uname);
					st.executeUpdate("insert into users values("+"\""+uname+"\",\""+pass+"\")");
					st.executeUpdate("create table "+uname+"(srno int AUTO_INCREMENT primary key,sender varchar(20),message varchar(100))");
					st.close();
					sendNewUserMessage(uname);
					connectedusers.put(uname,s);
					ClientHandler t=new ClientHandler(uname,s,sqlconnection,connectedusers,g);
					g.add1(uname);g.add2(uname);
					t.start();
					return true;
				}else {
					System.out.println("Client Connection Failure");
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	private void sendNewUserMessage(String uname) {
		try {
			Iterator it=connectedusers.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry e=(Map.Entry)it.next();
				DataOutputStream out=new DataOutputStream(((Socket)e.getValue()).getOutputStream());
				JSONObject obj=new JSONObject();
				obj.put("type","2");
				obj.put("friend",uname);
				out.writeUTF(obj.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

