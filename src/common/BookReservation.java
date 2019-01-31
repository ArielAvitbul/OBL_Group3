package common;

import java.io.Serializable;
import java.sql.Timestamp;


public class BookReservation implements Serializable{

	private int memberID;
	private Timestamp orderDate;
	private int bookID;
	
	public BookReservation(int memberID, Timestamp orderDate, int bookID) 
	{
		super();
		this.memberID = memberID;
		this.orderDate = orderDate;
		this.bookID = bookID;
	}

	public int getMemberID() 
	{
		return memberID;
	}

	public void setMemberID(int memberID) 
	{
		this.memberID = memberID;
	}

	public Timestamp getOrderDate() 
	{
		return orderDate;
	}

	public void setOrderDate(Timestamp orderDate) 
	{
		this.orderDate = orderDate;
	}

	public int getBookID() 
	{
		return bookID;
	}

	public void setBookID(int bookID) 
	{
		this.bookID = bookID;
	}
}
