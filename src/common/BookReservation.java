package common;

import java.sql.Date;

public class BookReservation {

	private int memberID;
	private Date orderDate;
	private int bookID;
	
	public BookReservation(int memberID, Date orderDate, int bookID) 
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

	public Date getOrderDate() 
	{
		return orderDate;
	}

	public void setOrderDate(Date orderDate) 
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
