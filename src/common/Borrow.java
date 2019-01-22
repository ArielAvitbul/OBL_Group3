package common;

import java.io.Serializable;
import java.sql.Date;

import javafx.beans.property.SimpleObjectProperty;

public class Borrow implements Serializable {


	private final int borrowID;
	private final int bookID;
	private final int memberID;
	private final Date borrowDate;
	private Date returnDate;
	private Date actualReturnDate;
	
	public Borrow(int borrowID,int bookID, int memberID, Date borrowDate, Date returnDate, Date actualReturnDate) {
		super();
		this.borrowID = borrowID;
		this.bookID=bookID;
		this.memberID = memberID;
		this.borrowDate = borrowDate;
		this.returnDate = returnDate;
		this.actualReturnDate = actualReturnDate;
	}

	public int getBorrowID() 
	{
		return borrowID;
	}

	public int getBookID() {
		return bookID;
	}

	public int getMemberID() 
	{
		return memberID;
	}

	public Date getBorrowDate() 
	{
		return borrowDate;
	}

	public Date getReturnDate() 
	{
		return returnDate;
	}

	public void setReturnDate(Date returnDate) 
	{
		this.returnDate = returnDate;
	}

	public Date getActualReturnDate() 
	{
		return actualReturnDate;
	}

	public void setActualReturnDate(Date actualReturnDate) 
	{
		this.actualReturnDate = actualReturnDate;
	}

	public SimpleObjectProperty borrowDate() {
		return new SimpleObjectProperty(borrowDate);
	}
	
	public SimpleObjectProperty actualReturnDate() {
		return new SimpleObjectProperty(actualReturnDate);
	}
}