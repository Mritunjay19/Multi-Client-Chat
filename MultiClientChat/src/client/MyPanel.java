package client;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

public class MyPanel extends JPanel {
	String to;
	JTextArea chats;
	
	MyPanel(String to){
		super();
		this.to=to;
		this.setLayout(new BorderLayout());
		chats=new JTextArea();
		chats.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), to,TitledBorder.LEFT,TitledBorder.ABOVE_TOP,new Font("",3,15)));
		chats.setEditable(false);
		this.add(chats);
	}
	void addMyMessage(String message) {
		chats.append("\nMe:- "+message+"\n");
	}
	void addFriendMessage(String message) {
		chats.append("\n"+to+":- "+message+"\n");
	}
}
