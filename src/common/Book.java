package common;

import java.io.Serializable;
import java.sql.Date;

import com.itextpdf.text.Document;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Book implements Serializable{

	private int bookID;
	private String bookName;
	private String authorsNames;
	private float editionNumber;
	private Date printDate;
	private  String topic;
	private String shortDescription;
	private int numberOfCopies;
	private Date purchaseDate;
	private String shellLocation;
	private boolean isPopular;
	private int currentNumberOfCopies;
	
	public Book(int bookID, String bookName, String authorsNames, float editionNumber, Date printDate, String topic,
			String shortDescription, int numberOfCopies, Date purchaseDate, String shellLocation, boolean isPopular,
			int currentNumberOfCopies) 
	{
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
	public Book(int bookID, String bookName) {
		this.bookID = bookID;
		this.bookName = bookName;
	}

	public int getBookID() 
	{
		return bookID;
	}

	public String getBookName() 
	{
		return bookName;
	}
	
	public SimpleStringProperty bookName() {
		return new SimpleStringProperty(bookName);
	}

	public SimpleStringProperty topic() {
		return new SimpleStringProperty(topic);
	}
	
	public SimpleStringProperty authorsNames() {
		return new SimpleStringProperty(authorsNames);
	}
	
	public String getAuthorsNames() 
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

	public String getTopic() 
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
	
	@Override
	public String toString() {
		return this.bookName;
	}

}
