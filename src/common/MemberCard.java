package common;

public class MemberCard {
	
	private final String firstName;
	private final String lastName;
	private String phoneNumber;
	private String emailAddress;
	private Borrow[] borrowHistory;
	private Violation[] violationHostory;
	private int lateReturns;

	public MemberCard(String firstName, String lastName, String phoneNumber, String emailAddress,
			Borrow[] borrowHistory, Violation[] violationHostory, int lateReturns) 
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
		this.borrowHistory = borrowHistory;
		this.violationHostory = violationHostory;
		this.lateReturns = lateReturns;
	}
	
	public String getFirstName() 
	{
		return firstName;
	}
	
	public String getLastName() 
	{
		return lastName;
	}
	
	public String getPhoneNumber() 
	{
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) 
	{
		this.phoneNumber = phoneNumber;
	}
	
	public String getEmailAddress() 
	{
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) 
	{
		this.emailAddress = emailAddress;
	}
	
	public Borrow[] getBorrowHistory() 
	{
		return borrowHistory;
	}
	
	public void setBorrowHistory(Borrow[] borrowHistory) 
	{
		this.borrowHistory = borrowHistory;
	}
	
	public Violation[] getViolationHostory() 
	{
		return violationHostory;
	}
	
	public void setViolationHostory(Violation[] violationHostory) 
	{
		this.violationHostory = violationHostory;
	}
	
	public int getLateReturns() 
	{
		return lateReturns;
	}
	
	public void setLateReturns(int lateReturns) 
	{
		this.lateReturns = lateReturns;
	}
}
