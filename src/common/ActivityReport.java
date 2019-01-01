package common;

import java.sql.Date;

public class ActivityReport extends Report {

	private int frozenMembers;
	private int activeMembers;
	private int totalCopiesInBorrow;
	private int numberOfLateMembers;
	
	public ActivityReport(String reportID, Date dateIssued, int frozenMembers, int activeMembers,
			int totalCopiesInBorrow, int numberOfLateMembers) 
	{
		super(reportID, dateIssued);
		this.frozenMembers = frozenMembers;
		this.activeMembers = activeMembers;
		this.totalCopiesInBorrow = totalCopiesInBorrow;
		this.numberOfLateMembers = numberOfLateMembers;
	}

	public int getFrozenMembers() 
	{
		return frozenMembers;
	}

	public void setFrozenMembers(int frozenMembers) 
	{
		this.frozenMembers = frozenMembers;
	}

	public int getActiveMembers() 
	{
		return activeMembers;
	}

	public void setActiveMembers(int activeMembers) 
	{
		this.activeMembers = activeMembers;
	}

	public int getTotalCopiesInBorrow() 
	{
		return totalCopiesInBorrow;
	}

	public void setTotalCopiesInBorrow(int totalCopiesInBorrow) 
	{
		this.totalCopiesInBorrow = totalCopiesInBorrow;
	}

	public int getNumberOfLateMembers() 
	{
		return numberOfLateMembers;
	}

	public void setNumberOfLateMembers(int numberOfLateMembers) 
	{
		this.numberOfLateMembers = numberOfLateMembers;
	}
	
}
