package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class ChatReciever extends Thread{
	Socket s;
	DataInputStream in;
	GUIHandler g;
	String username;
	Boolean exited;
	ChatReciever(Socket s,GUIHandler g,String username){
		this.s=s;
		this.g=g;
		exited=false;
		this.username=username;
		try {
			in=new DataInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run() {
		g.setChatReciever(this);
		try {
			while(true) {
				JSONObject obj=(JSONObject)(new JSONParser().parse(in.readUTF()));
				String type=(String)obj.get("type");
				if(type.contentEquals("1")) {
						System.out.println(obj);
						String friend=(String)obj.get("from"),message=(String)obj.get("message");
						((MyPanel)g.map.get(friend)).addFriendMessage(message);
				}
				else if(type.contentEquals("2")) {
					JOptionPane.showMessageDialog(g.f, obj.get("friend")+" has created Account");
					g.addFriend((String)obj.get("friend"));
				}
				else if(type.contentEquals("3")) {
					String friend=(String)obj.get("friend");
					if(friend.equals(username)) {
						g.disposeFrame();
					}
					else {
						g.removeFriend(friend);
					}
				}
				else {
					JSONArray a=(JSONArray)obj.get("friends");
					Iterator it=a.iterator();
					while(it.hasNext()) {
						g.addFriend((String)it.next());
					}
					g.start();
				}
			}
		} catch (Exception e) {
			System.out.println("exited-> "+exited);
			if(!exited) {JOptionPane.showMessageDialog(g.f,"Server Connection ERROR\nPlease Try Again..");g.disposeFrame();}
			e.printStackTrace();
		}		
	}
}
