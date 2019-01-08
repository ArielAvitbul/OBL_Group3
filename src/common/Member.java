package common;

import java.io.Serializable;

public class Member extends Reader implements Serializable
{
	
	private enum Status
	{
		FREEZE,
		LOCK,
		ACTIVE
	}
	
	private int id;
	private String userName="";
	private String password;
	private Status status=Status.ACTIVE;
	private MemberCard myMemberCard;
	
	public Member(int id, String newUserName, String newPassword, int newUserStatus,
			String firstName, String lastName, String phoneNumber, String emailAddress,
			Borrow[] borrowHistory, Violation[] violationHostory, int lateReturns) 
	{
		super();
		this.id=id;
		this.userName=newUserName;
		this.password=newPassword;
		setUserStatus(newUserStatus);
		this.myMemberCard=new MemberCard(firstName, lastName, phoneNumber, emailAddress, 
				borrowHistory, violationHostory, lateReturns);
		// TODO Auto-generated constructor stub
	}
	public Member() {
		
	}
	public Member(String username, String password) {
		this.userName=username;
		this.password=password;
	}
	public MemberCard getMemberCard() {
		return myMemberCard;
	}
	public int getId() 
	{
		return id;
	}
	
	public String getUserName() 
	{
		return userName;
	}
	
	public String getPassword() 
	{
		return password;
	}
	
	public void setPassword(String password) 
	{
		this.password = password;
	}

	public Status getStatus() 
	{
		return status;
	}

	public void setUserStatus(int enumNumericValue) 
	{
		switch(enumNumericValue) {
			case 0:
				this.status = Status.FREEZE;
				break;
			case 1:
				this.status = Status.LOCK;
				break;
			case 2: // anyway default is ACTIVE
			default:
				this.status = Status.ACTIVE;
				break;
				
		}
	}
	@Override
	public String toString() { // for testing
		return userName;
	}
}
