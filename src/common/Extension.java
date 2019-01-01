package common;

import java.sql.Date;

public class Extension {

	private final int memberID;
	private final int borrowID;
	private Date newReturnDate;
	
	public Extension(int memberID, Borrow newBorrow,Date newReturnDate) {
		super();
		this.memberID = memberID;
		this.borrowID = newBorrow.getBorrowID();
		this.newReturnDate=newReturnDate;
		newBorrow.setReturnDate(newReturnDate);
	}

	public int getMemberID() {
		return memberID;
	}

	public int getBorrowID() {
		return borrowID;
	}

	public Date getNewReturnDate() {
		return newReturnDate;
	}

	public void setNewReturnDate(Date newReturnDate,Borrow newBorrow) {
		this.newReturnDate = newReturnDate;
		newBorrow.setReturnDate(newReturnDate);
	}
}
