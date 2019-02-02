package common;

import java.sql.Timestamp;
/**
 * This class represents an Extension for a certain borrow in the system
 * @author Good Guy
 *
 */
public class Extension {

	private final int memberID;
	private final int borrowID;
	private Timestamp newReturnDate;
	
	public Extension(int memberID, Borrow newBorrow,Timestamp newReturnDate) {
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

	public Timestamp getNewReturnDate() {
		return newReturnDate;
	}

	public void setNewReturnDate(Timestamp newReturnDate,Borrow newBorrow) {
		this.newReturnDate = newReturnDate;
		newBorrow.setReturnDate(newReturnDate);
	}
}
