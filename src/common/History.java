package common;

import java.io.Serializable;
import java.util.Date;

public class History implements Serializable {

	private String bookName;
	private final Date borrowDate;
	private Date actualReturnDate;
	
	public History(String bookName, Date borrowDate, Date actualReturnDate) {
		super();
		this.bookName = bookName;
		this.borrowDate = borrowDate;
		this.actualReturnDate = actualReturnDate;
	}
	
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public Date getBorrowDate() {
		return borrowDate;
	}
	public Date getActualReturnDate() {
		return actualReturnDate;
	}
	public void setActualReturnDate(Date actualReturnDate) {
		this.actualReturnDate = actualReturnDate;
	}
	
	public String toString() {
		return ("name= "+this.getBookName() + " date= "+ this.getBorrowDate());
	}
}