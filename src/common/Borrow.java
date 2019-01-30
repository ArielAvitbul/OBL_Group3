package common;

import java.io.Serializable;
import java.sql.Timestamp;

import javafx.beans.property.SimpleObjectProperty;

public class Borrow implements Serializable {


	private int borrowID;
	private final int bookID;
	private final int memberID;
	private final Timestamp borrowDate;
	private Timestamp returnDate;
	private Timestamp actualReturnDate;
	
	public Borrow(int borrowID,int bookID, int memberID, Timestamp borrowDate, Timestamp returnDate, Timestamp actualReturnDate) {
		super();
		this.borrowID = borrowID;
		this.bookID=bookID;
		this.memberID = memberID;
		this.borrowDate = borrowDate;
		this.returnDate = returnDate;
		this.actualReturnDate = actualReturnDate;
	}

	public Borrow(int bookID, int memberID, Timestamp date, Timestamp fromPicker) {
		this.bookID=bookID;
		this.memberID = memberID;
		this.borrowDate = date;
		this.returnDate = fromPicker;
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

	public Timestamp getBorrowDate() 
	{
		return borrowDate;
	}

	public Timestamp getReturnDate() 
	{
		return returnDate;
	}

	public void setReturnDate(Timestamp returnDate) 
	{
		this.returnDate = returnDate;
	}

	public Timestamp getActualReturnDate() 
	{
		return actualReturnDate;
	}

	public void setActualReturnDate(Timestamp actualReturnDate) 
	{
		this.actualReturnDate = actualReturnDate;
	}

	public SimpleObjectProperty borrowDate() {
		return new SimpleObjectProperty(borrowDate);
	}
	
	public SimpleObjectProperty actualReturnDate() {
		return new SimpleObjectProperty(actualReturnDate);
	}
	@Override
	public boolean equals(Object obj) {
		return this.getBorrowID() == ((Borrow)obj).getBorrowID() ? true : false;
	}
}