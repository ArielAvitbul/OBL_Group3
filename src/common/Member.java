package common;

import java.net.InetAddress;

public class Member extends Reader
{
	
	private enum Status
	{
		FREEZE,
		LOCK,
		ACTIVE
	}
	
	private final int memberNumber;
	private final String userName;
	private String password;
	private Status userStatus;
	private MemberCard myMemberCard;
	
	public Member()
	{
		super();
	}
	
	public Member(int newMemberNumber, String newUserName, String newPassword, Status newUserStatus,
			String firstName, String lastName, String phoneNumber, String emailAddress,
			Borrow[] borrowHistory, Violation[] violationHostory, int lateReturns) 
	{
		super();
		this.memberNumber=newMemberNumber;
		this.userName=newUserName;
		this.password=newPassword;
		this.userStatus=newUserStatus;
		this.myMemberCard=new MemberCard(firstName, lastName, phoneNumber, emailAddress, 
				borrowHistory, violationHostory, lateReturns);
		// TODO Auto-generated constructor stub
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

	public void setUserStatus(Status userStatus) 
	{
		this.userStatus = userStatus;
	}
	
	

}
