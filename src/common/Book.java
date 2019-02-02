package common;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.beans.property.SimpleStringProperty;

public class Book implements Serializable{

	private int bookID;
	private String bookName;
	private String authorsNames;
	private float editionNumber;
	private Date printDate;
	private String topics;
	private String shortDescription;
	private int numberOfCopies;
	private Date purchaseDate;
	private String shelfLocation;
	private boolean isPopular;
	private int currentNumberOfCopies;
	
	public Book(int bookID, String bookName, String authorsNames, float editionNumber, Date printDate, String topics,
			String shortDescription, int numberOfCopies, Date purchaseDate, String shelfLocation, boolean isPopular,
			int currentNumberOfCopies) {
		this.bookID = bookID;
		this.bookName = bookName;
		this.authorsNames = authorsNames;
		this.editionNumber = editionNumber;
		this.printDate = printDate;
		this.topics = topics;
		this.shortDescription = shortDescription;
		this.numberOfCopies = numberOfCopies;
		this.purchaseDate = purchaseDate;
		this.shelfLocation = shelfLocation;
		this.isPopular = isPopular;
		this.currentNumberOfCopies = currentNumberOfCopies;
	}
	public Book(int bookID, String bookName) {
		this.bookID = bookID;
		this.bookName = bookName;
	}
	public String getAvlible() 
	{
		if(currentNumberOfCopies>0)
		return "Yes";
		else return "No";
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

	public SimpleStringProperty topics() {
		return new SimpleStringProperty(topics.toString());
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

	public String getTopics() 
	{
		return topics;
	}
	public void setTopics(ArrayList<String> topics) {
		this.topics=topics.toString();
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

	public String getShelfLocation() 
	{
		return shelfLocation;
	}

	public void getShelfLocation(String shelfLocation) 
	{
		this.shelfLocation = shelfLocation;
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
