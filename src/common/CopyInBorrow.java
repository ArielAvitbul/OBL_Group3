package common;

import java.io.Serializable;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;
/**
 * This class is an association class between a book and borrow
 * @author Good Guy
 *
 */
public class CopyInBorrow implements Serializable {

	private Book borroBook;
	private Borrow newBorrow;
	private Date returnDate;
	private String bookAuthor;
	private final int copyNumber;
	public CopyInBorrow(Book borroBook, Borrow newBorrow, int copyNumber) {
		this.borroBook = borroBook;
		bookAuthor = new String(borroBook.getAuthorsNames());
		this.newBorrow = newBorrow;
		returnDate = newBorrow.getReturnDate();
		this.copyNumber = copyNumber;
	}
	public Date getBorrowDate() 
	{
		return newBorrow.getBorrowDate();
	}

	public SimpleStringProperty borroBook() {
		return new SimpleStringProperty(borroBook.getBookName());
	}
	
	public String getBorroBookName() {
		return borroBook.getBookName();
	}
	
	public Date getReturnDate() {
		return returnDate;
	}
	
	public String getBookAuthor() {
		return bookAuthor;
	}
	public Book getBorroBook() 
	{
		return borroBook;
	}

	public void setBorroBook(Book borroBook) 
	{
		this.borroBook = borroBook;
	}

	public Borrow getNewBorrow() 
	{
		return newBorrow;
	}

	public void setNewBorrow(Borrow newBorrow) 
	{
		this.newBorrow = newBorrow;
	}

	public int getCopyNumber() 
	{
		return copyNumber;
	}

}