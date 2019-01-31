package common;

import java.io.Serializable;
import java.sql.Date;
public class Message implements Serializable {
	private final int msgID;
	private int from;
	private int to;
	private String action="None";
	Member regarding;
	private Date date;
	private String content;
	private boolean handled=false;
	private boolean read;
	
	public Message(int msgID , int from , int to , String content, boolean read) {
		this.msgID=msgID;
		this.from=from;
		this.to=to;
		this.content=content;
		this.read=read;
		this.date = new Date(System.currentTimeMillis());
	}
	
	public Message(int msgID , int from , int to , String content, String action, Member regarding, boolean handled, boolean read) {
		this.msgID=msgID;
		this.from=from;
		this.to=to;
		this.content=content;
		this.action=action;
		this.regarding=regarding;
		this.handled=handled;
		this.read=read;
		this.date = new Date(System.currentTimeMillis());
	}
	
	public int getFrom() {
		return from;
	}
	
	public int getTo() {
		return to;
	}

	public String getContent() {
		return content;
	}
	public Member getRegarding() {
		return regarding;
	}

	public void setRegarding(Member regarding) {
		this.regarding = regarding;
	}
	public Date getDate() {
		return date;
	}

	public int getMsgID() {
		return msgID;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean wasHandled() {
		return handled;
	}

	public void setHandled(boolean handled) {
		this.handled = handled;
	}

	public boolean wasRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}
 }
