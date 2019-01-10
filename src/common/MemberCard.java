package common;

import java.io.Serializable;
import java.util.ArrayList;

public class MemberCard implements Serializable{
	
	private String firstName="";
	private String lastName="";
	private String phoneNumber="";
	private String emailAddress="";
	private ArrayList<Borrow> borrowHistory=null;
	private ArrayList<Violation> violationHostory=null;
	private ArrayList<BookReservation> reservationHistory=null;
	private int lateReturns=0;

	public MemberCard(String firstName, String lastName, String phoneNumber, String emailAddress,
			ArrayList<Borrow> borrowHistory, ArrayList<Violation> violationHostory, ArrayList<BookReservation> reservationHistory, int lateReturns) 
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
		this.borrowHistory = borrowHistory;
		this.violationHostory = violationHostory;
		this.lateReturns = lateReturns;
		this.reservationHistory=reservationHistory;
	}
	
	public ArrayList<BookReservation> getReservationHistory() {
		System.out.println(reservationHistory);
		return reservationHistory;
	}
	public boolean checkBookReserved(int bookid) {
		for (BookReservation br : reservationHistory)
			if (br.getBookID()==bookid)
				return true;
		return false;
	}
	public BookReservation addBookReservation(BookReservation reservation) {
		this.reservationHistory.add(reservation);
		return reservation;
	}
	public void setReservationHistory(ArrayList<BookReservation> reservationHistory) {
		this.reservationHistory = reservationHistory;
	}

	public MemberCard() {
		this.borrowHistory = new ArrayList<>();
		this.violationHostory = new ArrayList<>();
		this.reservationHistory = new ArrayList<>();
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
	
	public ArrayList<Borrow> getBorrowHistory() 
	{
		return borrowHistory;
	}
	
	public void setBorrowHistory(ArrayList<Borrow> borrowHistory) 
	{
		this.borrowHistory = borrowHistory;
	}
	
	public ArrayList<Violation> getViolationHostory() 
	{
		return violationHostory;
	}
	
	public void setViolationHostory(ArrayList<Violation> violationHostory) 
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
