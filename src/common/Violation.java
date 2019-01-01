package common;

import java.sql.Date;

public class Violation {

	private enum type
	{
		LATE_RETURN,
		BOOK_IS_LOST,
		DAMAGED_BOOK
	}
	private final int MemberID;
	private Date violationDate;
	private String description;
	private type violationType;
	
	public Violation(int memberID, Date violationDate, String description, type violationType) 
	{
		super();
		MemberID = memberID;
		this.violationDate = violationDate;
		this.description = description;
		this.violationType = violationType;
	}
	
	public int getMemberID() 
	{
		return MemberID;
	}
	
	public Date getViolationDate() 
	{
		return violationDate;
	}

	public void setViolationDate(Date violationDate) 
	{
		this.violationDate = violationDate;
	}

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}

	public type getViolationType() 
	{
		return violationType;
	}

	public void setViolationType(type violationType) 
	{
		this.violationType = violationType;
	}
}
