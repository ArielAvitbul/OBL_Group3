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
	
	private int memberNumber;
	private String userName;
	private String password;
	private Status userStatus;
	private MemberCard myMemberCard;
	
	public Member(int newMemberNumber, String newUserName, String newPassword, int newUserStatus,
			String firstName, String lastName, String phoneNumber, String emailAddress,
			Borrow[] borrowHistory, Violation[] violationHostory, int lateReturns) 
	{
		super();
		this.memberNumber=newMemberNumber;
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

	public int getMemberNumber() 
	{
		return memberNumber;
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

	public Status getUserStatus() 
	{
		return userStatus;
	}

	public void setUserStatus(int enumNumericValue) 
	{
		switch(enumNumericValue) {
			case 0:
				this.userStatus = Status.FREEZE;
				break;
			case 1:
				this.userStatus = Status.LOCK;
				break;
			case 2: // anyway default is ACTIVE
			default:
				this.userStatus = Status.ACTIVE;
				break;
				
		}
	}
	@Override
	public String toString() { // for testing
		return userName;
	}
}
