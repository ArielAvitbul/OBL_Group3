package common;

import java.sql.Date;

import com.itextpdf.text.Document;

public class Book {

	private final int bookID;
	private final String bookName;
	private final String[] authorsNames;
	private float editionNumber;
	private Date printDate;
	private final String[] topic;
	private final String shortDescription;
	private int numberOfCopies;
	private Date purchaseDate;
	private String shellLocation;
	private Document tableOfContent;
	private boolean isPopular;
	private int currentNumberOfCopies;
	
	public Book(int bookID, String bookName, String[] authorsNames, float editionNumber, Date printDate, String[] topic,
			String shortDescription, int numberOfCopies, Date purchaseDate, String shellLocation, boolean isPopular,
			int currentNumberOfCopies) 
	{
		super();
		this.bookID = bookID;
		this.bookName = bookName;
		this.authorsNames = authorsNames;
		this.editionNumber = editionNumber;
		this.printDate = printDate;
		this.topic = topic;
		this.shortDescription = shortDescription;
		this.numberOfCopies = numberOfCopies;
		this.purchaseDate = purchaseDate;
		this.shellLocation = shellLocation;
		this.isPopular = isPopular;
		this.currentNumberOfCopies = currentNumberOfCopies;
	}
	
	public int getBookID() 
	{
		return bookID;
	}

	public String getBookName() 
	{
		return bookName;
	}

	public String[] getAuthorsNames() 
	{
		return authorsNames;
	}

	public float getEditionNumber() 
	{
		return editionNumber;
	}

	public void setEditionNumber(float editionNumber) 
	{
		this.editionNumber = editionNumber;
	}

	public Date getPrintDate() 
	{
		return printDate;
	}

	public void setPrintDate(Date printDate) 
	{
		this.printDate = printDate;
	}

	public String[] getTopic() 
	{
		return topic;
	}
	
	public String getShortDescription() 
	{
		return shortDescription;
	}

	public int getNumberOfCopies() 
	{
		return numberOfCopies;
	}

	public void setNumberOfCopies(int numberOfCopies) 
	{
		this.numberOfCopies = numberOfCopies;
	}

	public Date getPurchaseDate() 
	{
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) 
	{
		this.purchaseDate = purchaseDate;
	}

	public String getShellLocation() 
	{
		return shellLocation;
	}

	public void setShellLocation(String shellLocation) 
	{
		this.shellLocation = shellLocation;
	}

	public boolean isPopular() 
	{
		return isPopular;
	}

	public void setPopular(boolean isPopular) 
	{
		this.isPopular = isPopular;
	}

	public int getCurrentNumberOfCopies() 
	{
		return currentNumberOfCopies;
	}

	public void setCurrentNumberOfCopies(int currentNumberOfCopies) 
	{
		this.currentNumberOfCopies = currentNumberOfCopies;
	}

}
