package common;

public class CopyInBorrow {

	private Book borroBook;
	private Borrow newBorrow;
	private final int copyNumber;
	
	public CopyInBorrow(Book borroBook, Borrow newBorrow, int copyNumber) {
		this.borroBook = borroBook;
		this.newBorrow = newBorrow;
		this.copyNumber = copyNumber;
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
