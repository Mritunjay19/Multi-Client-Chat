package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.json.simple.JSONObject;

class ChatSender  {
	Socket s;
	String username;
	DataOutputStream out;
//	GUIHandler g;
	ChatSender(Socket s,String username){
		this.s=s;
//		this.g=g;
		this.username=username;
		try {
			out=new DataOutputStream(s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void send1(String to,String message) {
		try {
//			Scanner in=new Scanner(System.in);
			JSONObject obj=new JSONObject();
			obj.put("type","1");
			obj.put("from",username);
			obj.put("to",to);
			obj.put("message",message);
			out.writeUTF(obj.toString());
			System.out.println("JSON Object sended");
		
		}catch(Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
	public void send2() {
		try {
			JSONObject obj=new JSONObject();
			obj.put("type","2");
			out.writeUTF(obj.toString());
			System.out.println("JSON Object sended");
		}catch(Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
	public void send3() {
		try {
			JSONObject obj=new JSONObject();
			obj.put("type","3");
			out.writeUTF(obj.toString());
			System.out.println("JSON Object sended");
		}catch(Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
}
