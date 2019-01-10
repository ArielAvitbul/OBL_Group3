package common;

import java.io.Serializable;
import java.sql.Date;

public class Borrow implements Serializable {


	private final int borrowID;
	private final int memberID;
	private final Date borrowDate;
	private Date returnDate;
	private Date actualReturnDate;
	private boolean isLate;
	
	public Borrow(int borrowID, int memberID, Date borrowDate, Date returnDate, Date actualReturnDate, boolean isLate) {
		super();
		this.borrowID = borrowID;
		this.memberID = memberID;
		this.borrowDate = borrowDate;
		this.returnDate = returnDate;
		this.actualReturnDate = actualReturnDate;
		this.isLate = isLate;
	}

	public int getBorrowID() 
	{
		return borrowID;
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

	public boolean isLate() {
		return isLate;
	}

	public void setLate(boolean isLate) {
		this.isLate = isLate;
	}
}
