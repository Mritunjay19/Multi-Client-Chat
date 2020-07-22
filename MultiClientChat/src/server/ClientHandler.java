package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mysql.cj.xdevapi.JsonArray;


class ClientHandler extends Thread {
	Socket s;
	ServerGUI g;
	String username;
	Connection sqlconnection;
	HashMap<String,Socket> connectedusers;
	DataOutputStream out;
	DataInputStream in;
	ClientHandler(String username,Socket s,Connection sqlconnection,HashMap<String,Socket> connectedusers,ServerGUI g){
		this.connectedusers=connectedusers;
		this.g=g;
		this.username=username;
		this.s=s;
		this.sqlconnection=sqlconnection;
		try {
			in=new DataInputStream(s.getInputStream());
			out=new DataOutputStream(s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(s.isConnected())System.out.println("Socket is Connected");
		else System.out.println("Socket is Closed");
	}
	public void run() {
		System.out.println("ClientHandler Thread started for "+username);
		sendFriendsList();
		checkWaitingChats();
		try {
			while(true) {
				JSONObject obj=(JSONObject)(new JSONParser().parse(in.readUTF()));
				System.out.println("JSON object recieved from "+username);
				String type=(String)obj.get("type");
				System.out.println(obj.toString()); 
				if(type.contentEquals("1")) {
					String to=(String) obj.get("to");
					if(connectedusers.containsKey(to))
						forwardMessage(obj,to);
					else
						addToWaiting(obj,to);
				}
				else if(type.contentEquals("2")) {
					connectedusers.remove(username);
					g.remove1(username);
					s.close();
					in.close();
					out.close();
				}
				else {
					sendDeleteMessage();
					deleteUserRecord();
					connectedusers.remove(username);
					s.close();
					in.close();
					out.close();
				}
			}
		} catch (Exception e) {
			if(connectedusers.containsKey(username))connectedusers.remove(username);
			g.remove1(username);
			e.printStackTrace();
		}
	}
	private void deleteUserRecord() {
		try {
			g.remove1(username);
			g.remove2(username);
			Statement st=sqlconnection.createStatement();
			st.executeUpdate("drop table "+username);
			st.executeUpdate("delete from users where username=\""+username+"\"");
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	private void sendDeleteMessage() throws IOException {
		Iterator it=connectedusers.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry) it.next();
			DataOutputStream out=new DataOutputStream(((Socket)e.getValue()).getOutputStream());
			JSONObject obj=new JSONObject();
			obj.put("type", "3");
			obj.put("friend",username);
			out.writeUTF(obj.toString());
		}
	}
	private void sendFriendsList() {
		try {
			Statement st=sqlconnection.createStatement();
			ResultSet rs=st.executeQuery("select username from users where username != \""+username+"\"");
			JSONObject obj=new JSONObject();
			JSONArray friends=new JSONArray();
			while(rs.next()) {
				friends.add(rs.getString(1));
			}
			obj.put("friends",friends);
			obj.put("type","4");
			out.writeUTF(obj.toString());
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private void addToWaiting(JSONObject obj,String to) {
		try {
			Statement st=sqlconnection.createStatement();
			st.executeUpdate("insert into "+to+" (sender,message) values (\""+obj.get("from")+"\",\""+obj.get("message")+"\")");
			st.close();
			System.out.println("messsage added to "+to+" waiting chat list");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private void forwardMessage(JSONObject obj,String to) {
		try {
			DataOutputStream toout=new DataOutputStream(connectedusers.get(to).getOutputStream());
			toout.writeUTF(obj.toString());
			System.out.println("message forwarded to "+to);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void checkWaitingChats() {
		try {
			Statement st=sqlconnection.createStatement();
			ResultSet rs=st.executeQuery("Select * from "+username+" order by srno ASC");
			while(rs.next()) {
				JSONObject obj=new JSONObject();
				obj.put("type","1");
				obj.put("from", rs.getString(2));
				obj.put("message",rs.getString(3));
				out.writeUTF(obj.toString());
			}		
			st.executeUpdate("delete from "+username);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
//	void stopThread() {
//		
//	}
}
