package common;

import java.io.Serializable;
import java.sql.Date;

//import common.Member.Status;

public class Violation implements Serializable	{

	public enum Type
	{
		LATE_RETURN,
		BOOK_IS_LOST,
		DAMAGED_BOOK,
		OTHER
	}
	private final int MemberID;
	private Date violationDate;
	private String description;
	private Type violationType;
	
	public Violation(int memberID, Date violationDate, String description, int violationType) 
	{
		super();
		MemberID = memberID;
		this.violationDate = violationDate;
		this.description = description;
		setViolationType(violationType);
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

	public Type getViolationType() 
	{
		return violationType;
	}

	public void setViolationType(int enumNumericValue) 
	{
		switch(enumNumericValue) {
		case 0:
			this.violationType = Type.LATE_RETURN;
			break;
		case 1:
			this.violationType = Type.BOOK_IS_LOST;
			break;
		case 2:		
			this.violationType = Type.DAMAGED_BOOK;
			break; 
		default://anyway default is other
			this.violationType = Type.OTHER;
		}
	}
}
