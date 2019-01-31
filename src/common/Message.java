package common;

import java.io.Serializable;
import java.sql.Date;

public class Message implements Serializable {
	private final int msgID;
	private int from;
	private int to;
	private Date date;
	private String content;
	
	public Message(int msgID , int from , int to , String content) {
		this.msgID=msgID;
		this.from=from;
		this.to=to;
		this.content=content;
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

	public Date getDate() {
		return date;
	}

	public int getMsgID() {
		return msgID;
	}
 }
