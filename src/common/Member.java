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

	public Member() {
		
	}
	public Member(int id, String username, String password) {
		this.id=id;
		this.userName=username;
		this.password=password;
	}
	
	private void setMemberCard(MemberCard card) {
		this.myMemberCard=card;
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
