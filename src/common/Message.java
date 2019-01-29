package common;

import java.io.Serializable;
import java.sql.Date;

public class Message implements Serializable {
	private int from;
	private int to;
	private Date date;
	private String content;
	
	public Message(int from , int to , String content) {
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
 }
