package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import client.ClientConsole;
import common.Book;
import common.BookReservation;
import common.Borrow;
import common.CopyInBorrow;
import common.History;
import common.Librarian;
import common.Manager;
import common.Member;
import common.MemberCard;
import common.Message;
import common.MyData;
import common.MyFile;
import common.SendMail;
import common.Violation;
import javafx.scene.control.Alert.AlertType;

public class ServerController {
	public ServerController(MyDB db) {
		this.db=db;
	}
	final MyDB db;
	
	public static long getDifferenceDays(java.util.Date d2,java.util.Date d1) {
	    long diff = d2.getTime() - d1.getTime();
	    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
	
	public ArrayList<Book> getAllBooks() {
		ArrayList<Book> books = new ArrayList<>();
		try {
			ResultSet rs = db.select("SELECT * from books WHERE deleted = '0'");
		while (rs.next()) {//TODO: add table of content PDF.
			Book book = new Book(rs.getInt("bookID"), rs.getString("bookName"), rs.getString("authorsNames"), rs.getFloat("editionNumber"), rs.getDate("printDate"), rs.getString("topics"), rs.getString("shortDescription"), rs.getInt("numberOfCopies"), rs.getDate("purchaseDate"), rs.getString("shelfLocation"), rs.getBoolean("isPopular"),rs.getInt("currentNumberOfCopies"));
			books.add(book);
		}
		} catch (SQLException e) {
			System.out.println("Books FAILED to load! "+ e.getMessage());
		}
		return books;
	}
	public Book getBook(int bookID) throws SQLException {
		ResultSet rs = db.select("SELECT * from books WHERE deleted = '0' AND bookid="+ bookID);		if (db.hasResults(rs))
			return new Book(rs.getInt("bookID"), rs.getString("bookName"), rs.getString("authorsNames"), rs.getFloat("editionNumber"), rs.getDate("printDate"), rs.getString("topics"), rs.getString("shortDescription"), rs.getInt("numberOfCopies"), rs.getDate("purchaseDate"), rs.getString("shelfLocation"), rs.getBoolean("isPopular"),rs.getInt("currentNumberOfCopies"));
		return null;
	}
	/**
	 * This method handles the login process.
	 * checks in data base if details are valid.
	 * @author Good Guy
	 * @author Ariel
	 * @return MyData instance with the result.
	 * @param data - MyData instance with login details.
	 * @see MyData
	 * 
	 */
	public MyData login (MyData data) throws SQLException {
		ResultSet memberMatch = db.select("SELECT * FROM members WHERE id ='"+ data.getData("id")+"' AND password ='"+data.getData("password")+"'");
		MyData ret = new MyData("login_failed");
		if(!db.hasResults(memberMatch))
			ret.add("reason", "ID or password incorrect");
		else if (memberMatch.getBoolean("loggedin"))
			ret.add("reason", "Already logged in!");
		else if(memberMatch.getString("status").equals("LOCK"))
			ret.add("reason", "Your user is lock!");
		else {
			ret.setAction("login_approved");
			setLoggedIn(true,memberMatch.getInt("id"));
			ret.add("MemberLoggedIn",createMember(memberMatch));
			}
		return ret;
	}
	/**
	 * This method writes "loggedin" status according to the members status.
	 * @param value - true/false to write to data base
	 * @param id - id of the member
	 * @throws SQLException
	 * @author Ariel
	 */
	public void setLoggedIn(boolean value, int id) throws SQLException {
		PreparedStatement ps = db.update("UPDATE members SET loggedin =? WHERE id =?");
		ps.setBoolean(1, value);
		ps.setInt(2, id);
		ps.executeUpdate();
	}
	/**
	 * This method updates the members ip in the data base
	 * @param ip - ip to update
	 * @param id - member's id
	 * @throws SQLException
	 * @author Ariel
	 */
	public void updateIP(String ip,int id) throws SQLException {
		PreparedStatement ps = db.update("UPDATE members SET ip=? WHERE id =?");
		ps.setString(1, ip);
		ps.setInt(2, id);
		ps.executeUpdate();
	}
	/** this method searches for a member in the database, by given ID
	 * @return key: member is HashMap in case a result was found!
	 * 			or an empty HashMap in case a results as not found!
	 * @throws SQLException 
	 * @author Ariel
	 * */
	public MyData searchMember(int id) throws SQLException {
		MyData ret = new MyData("result");
		ResultSet rs = db.select("SELECT * FROM members where id='"+id+"'");
		if (rs.next()) // such member exists
			ret.add("member", createMember(rs));
		return ret;
	}
	/** this method inserts a new user to the database
	 * @param data - MyData instance with details to write
	 * @throws SQLException 
	 * @returns MyData instance with the result
	 * @author Ariel
	 * */
	public MyData createUser(MyData data) {
		MyData ret = new MyData("fail");
		try {
		 db.insertWithExecute("INSERT INTO members (`id`, `username`, `password`) "
				 + "VALUES ('"+data.getData("id")+"', '"+data.getData("username")+"', '"+data.getData("password")+"')");
		 db.insertWithExecute("INSERT INTO member_cards (`userID`, `firstName`, `lastName`, `phoneNumber`, `emailAddress`) "
		 		+ "VALUES ('"+data.getData("id")+"', '"+data.getData("firstname")+"', '"+data.getData("lastname")+"', '"+data.getData("phone")+"', '"+data.getData("email")+"')");
		} catch (SQLException e) {
			if (e instanceof MySQLIntegrityConstraintViolationException)
				ret.add("reason", "That user already exists!");
			else
				ret.add("reason", "Could not add a new user to the database");
			e.printStackTrace();
			return ret;
		}
		ret.setAction("success");
		return ret;
	}
	/**this method creates Member instance for the member that has logged in
	 * @param rs - ResultSet (member details) and MyDB instance
	 * @return Member instance
	 * @author Ariel
	 */
	public Member createMember(ResultSet rs) throws SQLException {
			Member toReturn=null;
			ResultSet localRS;
			if (db.hasResults(localRS = db.select("SELECT * FROM managers where id = "+ rs.getInt("id")))) 
				toReturn = new Manager(rs.getInt("id"), rs.getString("username"), rs.getString("password"), localRS.getInt("workerNum"),1);
			else if (db.hasResults(localRS = db.select("SELECT * FROM librarians where id = "+ rs.getInt("id"))))
				toReturn = new Librarian(rs.getInt("id"), rs.getString("username"), rs.getString("password"), localRS.getInt("workerNum"),localRS.getInt("permissionLevel"));
			else // member
				toReturn = new Member(rs.getInt("id"),rs.getString("username"),rs.getString("password"),Member.Status.valueOf(rs.getString("status")));
			toReturn.setMemberCard(getMemberCard(rs.getInt("id")));
			return toReturn;
	}
	/**
	 * This method creates a MemberCard from the data base by a given member id
	 * @param id - member's id
	 * @return MemberCard instance
	 * @throws SQLException
	 * @author Ariel
	 */
	private MemberCard getMemberCard(int id) throws SQLException {
		ResultSet rs = db.select("SELECT * from member_cards WHERE userID = "+ id);
		if (db.hasResults(rs))
		return new MemberCard(rs.getString("firstName"), rs.getString("lastName"), rs.getString("phoneNumber"), rs.getString("emailAddress"), getMemberBorrows(id), getMemberViolations(id), getMemberReservations(id),rs.getInt("lateReturns"));
		return null;
	}
		/** function to get all of the specified member borrows
		 * @param memberID -  member's id (unique)
		 * @return array of borrows.
		 * @author Good Guy
		 */
	public ArrayList<Borrow> getMemberBorrows(int memberID) throws SQLException {
		ArrayList<Borrow> memberBorrows = new ArrayList<Borrow>();
		ResultSet rs = db.select("SELECT * FROM borrows WHERE memberID ="+memberID);
		while(rs.next())
			memberBorrows.add(new Borrow(rs.getInt("borrowID"), rs.getInt("bookID"), rs.getInt("memberID"), rs.getTimestamp("borrowDate"), rs.getTimestamp("returnDate"), rs.getTimestamp("actualReturnDate")));
		return memberBorrows;
	}
	/** function to get all of the specified member violations
	 * @param memberID (unique) , MyDB instance.
	 * @return array of violations.
	 */
	public ArrayList<Violation> getMemberViolations(int memberID) throws SQLException {
		ArrayList<Violation> memberViolations = new ArrayList<Violation>();
		String MyQuery = "SELECT * FROM violations WHERE memberID ="+memberID;
		ResultSet rs = db.select(MyQuery);
		while(rs.next()) {
			memberViolations.add(new Violation(rs.getInt(1),rs.getDate(2),rs.getString(3),rs.getInt(4)));
		}
		return memberViolations;
	}
	/** function to get all of the specified member violations
	 * @param memberID - member's id (unique) , MyDB instance.
	 * @return array of reservations
	 * @author Good Guy.
	 */
	public ArrayList<BookReservation> getMemberReservations(int memberID) throws SQLException {
		ArrayList<BookReservation> memberReservations = new ArrayList<BookReservation>();
		ResultSet rs = db.select("SELECT * FROM book_reservations WHERE memberID ="+memberID);
		while(rs.next()) {
			memberReservations.add(new BookReservation(memberID, rs.getTimestamp("orderDate"),rs.getInt("bookID")));
		}
		return memberReservations;
	}
	/**
	 * This method saves the member's info in the data base
	 * @param userid -  member's id
	 * @param firstName
	 * @param lastName
	 * @param password
	 * @param email
	 * @param phone
	 * @return MyData Instance with the result
	 * @throws SQLException
	 * @author Ariel
	 */
	public MyData saveInfo(int userid, String firstName,String lastName, String password, String email, String phone) throws SQLException {
		MyData result = new MyData("fail");
		if (db.updateWithExecute("UPDATE member_cards set firstName='"+firstName+"',lastName='"+lastName+"',emailAddress='"+ email +"', phoneNumber='"+phone+"' WHERE userID='"+userid+"'")==1
				&& db.updateWithExecute("UPDATE members set password='"+password+"' WHERE id='"+userid+"'")==1) {//user found
			result.setAction("success");
			result.add("member_card", getMemberCard(userid)); // return the new updated member card!
		}else // member was not found, keep the fail action, add a message
			result.add("message", "Member was not found in the database.");
		return result;
	}
	/**
	 * This method saves info including status update (Admins only!)
	 * @param userid
	 * @param username
	 * @param status
	 * @return MyData instance with the results
	 * @throws SQLException
	 * @author Ariel
	 */
	public MyData saveInfoAdmin(int userid, String username, String status) throws SQLException {
		MyData result = new MyData("fail");
		if (db.updateWithExecute("UPDATE members set username='"+username+"',status='"+status+"' WHERE id='"+userid+"'")==1) {
			result.setAction("success");
			result.add("updatedMember", searchMember(userid));
		}else
			result.add("message", "Member was not found in the database.");
		return result;
	}
	/**
	 * This method handles order of a book (writes order to data base);
	 * @param userid - member's id
	 * @param book - book to order
	 * @return MyData instance with the results
	 * @throws SQLException
	 * @author Ariel
	 */
	public MyData orderBook(int userid, Book book) throws SQLException {
		if (book==null)
			return new MyData("fail","message","The book doesn't exists.");
			if (book.getCurrentNumberOfCopies()==0) { // can order
				ResultSet rs1 = db.select("SELECT COUNT(bookID) FROM book_reservations WHERE bookID='" + book.getBookID()+"'");
				rs1.next();
				int numberOfOrders= rs1.getInt(1);				
				if (numberOfOrders < book.getNumberOfCopies())
				{
				Timestamp today = new Timestamp(System.currentTimeMillis());
				db.insertWithExecute("INSERT INTO book_reservations (memberID, orderDate, bookID) VALUES ('"+userid+"', '"+today+"', '"+book.getBookID()+"')");
				return new MyData("success","reservation",new BookReservation(userid, today ,book.getBookID()));
				}
				else return new MyData("fail_Order","message","Cant Order, all of the the copies already ordered.");

			} else
				return new MyData("fail","message","There are still available copies of that book in the library.");
	}
	/**
	 * Author Feldman
	 * methos return the table of contents of book.
	 * input data - book.
	 * output - data - pdf file - table of contents
	 * @param data
	 * @return data
	 * @throws SQLException
	 */
public MyData getTableOfContents(MyData data) throws SQLException {
		
		Book b = (Book) data.getData("book");
		  MyFile msg= new MyFile(b);
		  msg.setWriteToPath("./src/client/TableOfContents");
		  try{
			      File newFile = new File ("./src/server/TableOfContents/"+ b.getBookID()+".pdf");
			      		      
			      byte [] mybytearray  = new byte [(int)newFile.length()];
			      FileInputStream fis = new FileInputStream(newFile);
			      BufferedInputStream bis = new BufferedInputStream(fis);			  
			      
			      msg.initArray(mybytearray.length);
			      bis.read(msg.getMybytearray(),0,mybytearray.length);
			      data.add("getFile", msg);
				  data.setAction("getFile");
				  bis.close();
			      return data;
			    }
			catch (Exception e) {
				System.out.println("Error send (Files)msg) to Client");
			}
		return data;
		
	}
public MyData getOrderBooks(MyData data) throws SQLException {
	ArrayList<Book> returnBookList = new ArrayList<>();
	int flag =0;

	String MyQuery = "select bookID from book_reservations where memberID = '"+data.getData("ID")+"'and arrivedDate is not null";
		ResultSet rs = db.select(MyQuery);
	while (rs.next()) 
	{

		Book book = getBook(rs.getInt("BookID"));
		returnBookList.add(book);
		flag=1;
	}
	rs.close();
	if (flag ==1)
	{
		data.add("returnBooklist", returnBookList);
		data.setAction("listOfReturnBooks");
		return data;
	}
	else 
	{
		data.setAction("unfind_borrows_Book");
		data.add("reason", "There is no books in borrow!");
		return data;
	}


}

/**
 * Author Feldman
 * method for delete book.
 * input : data - book.
 * @param data - message (success - if delete,  fail - if there is still borrowed copies)
 * @return
 * @throws SQLException
 */
public MyData deleteBook(MyData data) throws SQLException {
	Book book =  (Book) data.getData("book");
ResultSet rs1 = db.select("SELECT * FROM copy_in_borrow where BookID ='" + book.getBookID()+"'");
if(!db.hasResults(rs1)) {
	String query1 = "UPDATE books SET deleted = '1' WHERE bookID= '"+book.getBookID()+"'";
	db.updateWithExecute(query1);
	data.setAction("succeed");
	data.add("succeed", "Return book is succeed");
	return data;
}
else {
data.setAction("book_in_borrow");
data.add("book_in_borrow", "One or more of the copies is still borrow.");
return data;
}
}
/**
 * Author Feldman
 * method for returning the closed date the book will return - it start when reader choose book that is not
 * Available.
 * input : book.
 * @param data
 * @return data - the closed return book.
 * @throws SQLException
 */
public MyData getClosedReturnBook(MyData data) throws SQLException {
	Book book =  (Book) data.getData("book");
	ResultSet rs1 = db.select("SELECT * FROM borrows where bookID ='" + book.getBookID()+"' and actualReturnDate is null order by returnDate limit 1");
	if(db.hasResults(rs1)) {
	Date returnDate = rs1.getDate("returnDate");
	data.setAction("succeed");
	data.add("returnDate", returnDate);
	return data;

	}
else {
	data.setAction("fail");
	data.add("fail", "The book is not borrow.");
	return data;
	}
}

public Borrow getBorrow(int borrowID) throws SQLException {
	ResultSet rs = db.select("SELECT * from borrows WHERE borrowID="+ borrowID);
	rs.next();
	if (db.hasResults(rs))
		return new Borrow(rs.getInt("borrowID"), rs.getInt("bookID"),rs.getInt("memberID"), rs.getTimestamp("borrowDate"), rs.getTimestamp("returnDate"), rs.getTimestamp("actualReturnDate"));
	return null;
}

	/**
	 * This method compares between to strings not minding cases.
	 * @param str1 - string to compare
	 * @param str2 - string to compare
	 * @return True - strings match , False - Otherwise
	 * @author Ariel
	 */
	public boolean compareStrings(String str1, String str2) {
		try {
			return str1.toLowerCase().matches(".*"+str2.toLowerCase()+".*");
		} catch (Exception e) {
			return true; // if one of the strings is empty, then there's nothing to check and it will get here, so just return true.
		}
	}
	
	public MyData searchBook(ArrayList<String> genres, String bookName, String authorsName , ArrayList<String>freeTxt) throws SQLException {
		MyData ret = new MyData("result");
		ArrayList<Book> result = new ArrayList<>();
		String h;
		for (Book b : getAllBooks()) {
			if (compareStrings(b.getBookName(),bookName) &&	compareStrings(b.getAuthorsNames(),authorsName)) {
				boolean flag=true;
				for (String g : genres) {
					if (!b.getTopics().contains(g)) {
						flag=false;
					}
				}
				if(!resultByFreeText(b, freeTxt)) {
					flag = false;
				}
				if (flag)
				result.add(b);
			}
		}
		ret.add("search_results", result);
		return ret;
	}
	
	public boolean resultByFreeText(Book b , ArrayList<String> freeTxt) {
		boolean result = false;
		for(String s : freeTxt) {
			if(b.getAuthorsNames().toLowerCase().contains(s.toLowerCase()))
				result = true;
			if(b.getBookName().toLowerCase().contains(s.toLowerCase()))
				result = true;
			if(b.getTopics().toLowerCase().contains(s.toLowerCase()))
				result = true;
			if(b.getShortDescription().toLowerCase().contains(s.toLowerCase()))
				result=true;
		}
		return result;
	}
	
	/**
	 * This method retrieves all copies in borrow by borrow id
	 * @param myBorrows - ArrayList of borrows.
	 * @return ArrayList of copies in borrow.
	 * @throws SQLException
	 * @author Good Guy
	 */
	public ArrayList<CopyInBorrow> getCopiesInBorrow(ArrayList<Borrow> myBorrows) throws SQLException
	{
		ArrayList<CopyInBorrow> toReturn = new ArrayList<CopyInBorrow>();
		for(int i = 0; i< myBorrows.size(); i++) {
		ResultSet rs = db.select("SELECT * FROM copy_in_borrow WHERE borrowID="+myBorrows.get(i).getBorrowID());
		if(db.hasResults(rs)) {
				toReturn.add(new CopyInBorrow(getBook(rs.getInt("BookID")), myBorrows.get(i), rs.getInt("copyNumber")));
			}
		}
		return toReturn;
	}
	
	/**
	 * This method handles the extension update of a certain copy.
	 * @param copyInBorrow - copy to extend
	 * @param requester - requester of the service (user/librarian)
	 * @param fromPicker - new return date picked
	 * @return  MyData instance with the results.
	 * @throws SQLException
	 * @author Good Guy
	 */
	public MyData updateExtension(CopyInBorrow copyInBorrow , String requester , Timestamp fromPicker) throws SQLException {
		MyData toReturn = null;
		switch (requester) {
			case "user":
				if(checkReservations(copyInBorrow)) {
					toReturn = new MyData("ExtensionFailed");
					toReturn.add("updatedBorrow", copyInBorrow);
					toReturn.add("reason", "This book has been reserved!");
				}
				else {
					toReturn = updateBorrows(copyInBorrow , "user" , null);
					if(updateExtensions((CopyInBorrow)toReturn.getData("updatedBorrow"))==0) {
						toReturn = new MyData("ExtensionFailed");
						toReturn.add("updatedBorrow", copyInBorrow);
						toReturn.add("reason", "Can't update borrow!");
					}
					else {
						MemberCard borrower = getMemberCard(copyInBorrow.getNewBorrow().getMemberID());
						writeMsg(0,2, "Book extension", "The Borrow of "+borrower.getFirstName()+" "+borrower.getLastName()+"\nWith the book "+copyInBorrow.getBorroBookName()+"\nHas been extended in a week!");
					}break;
				}
				break;
			case "employee":
				if(checkReservations(copyInBorrow)) {
					toReturn = new MyData("hasReservations");
					toReturn.add("msgToPrint", "This book has reservations for it.\nAre you sure you want to extend?");
				}
				else {
					toReturn = updateBorrows(copyInBorrow , "employee" , fromPicker);
					if(updateExtensions((CopyInBorrow)toReturn.getData("updatedBorrow"))==0) {
						toReturn = new MyData("ExtensionFailed");
						toReturn.add("updatedBorrow", copyInBorrow);
						toReturn.add("reason", "Can't update borrow!");
					}
				}
				break;
			case "employeeAfterConfirmation":
				toReturn = updateBorrows(copyInBorrow , "employee" , fromPicker);
				if(updateExtensions((CopyInBorrow)toReturn.getData("updatedBorrow"))==0) {
					toReturn = new MyData("ExtensionFailed");
					toReturn.add("updatedBorrow", copyInBorrow);
					toReturn.add("reason", "Can't update borrow!");
				}
				break;
		}
		return toReturn;
	}
	
	/**
	 * This method checks if there are active orders regarding a certain copy
	 * @param copyInBorrow - copy to check
	 * @return True - order/s exists , False - otherwise
	 * @throws SQLException
	 * @author Good Guy
	 */
	private boolean checkReservations(CopyInBorrow copyInBorrow) throws SQLException {
		ResultSet rs = db.select("SELECT * FROM book_reservations WHERE bookID="+copyInBorrow.getBorroBook().getBookID()+" AND arrivedDate is null");
		return db.hasResults(rs);
	}
	
	/**
	 * This method extends a certain copy in borrow
	 * @param copyInBorrow - copy to extend
	 * @return result of the update execute
	 * @throws SQLException
	 * @author Good Guy
	 */
	private int updateExtensions(CopyInBorrow copyInBorrow) throws SQLException {
		String insertQ = "INSERT INTO extensions(memberID,borrowID,newReturnDate) "
				+ "VALUES(?,?,?)";
		PreparedStatement ps = db.update(insertQ);
		ps.setInt(1, copyInBorrow.getNewBorrow().getMemberID());
		ps.setInt(2, copyInBorrow.getNewBorrow().getBorrowID());
		ps.setTimestamp(3, copyInBorrow.getNewBorrow().getReturnDate());
		return ps.executeUpdate();
		
	}
	
	 /**
	  * 
	  * @param copyInBorrow - copy in borrow to update
	  * @param requester - service requester (user / librarian)
	  * @param fromPicker - new copy in borrow return date
	  * @return MyData instance with the results
	  * @throws SQLException
	  * @author Good Guy
	  */
	private MyData updateBorrows(CopyInBorrow copyInBorrow , String requester , Timestamp fromPicker) throws SQLException {
		MyData toReturn;
		int daysToExtend;
		Calendar c = Calendar.getInstance();
		c.setTime(copyInBorrow.getNewBorrow().getReturnDate());
		if(requester.equals("user")) 
			daysToExtend = (int)getDifferenceDays(copyInBorrow.getNewBorrow().getReturnDate(), copyInBorrow.getNewBorrow().getBorrowDate());
		else
			daysToExtend = (int)getDifferenceDays(fromPicker, copyInBorrow.getNewBorrow().getReturnDate());
		c.add(Calendar.DAY_OF_MONTH, daysToExtend);
		copyInBorrow.getNewBorrow().setReturnDate(new Timestamp(c.getTimeInMillis()));
		String query = "UPDATE borrows SET returnDate=? WHERE borrowID=?";
		PreparedStatement ps = db.update(query);
		ps.setTimestamp(1,copyInBorrow.getNewBorrow().getReturnDate());
		ps.setInt(2,copyInBorrow.getNewBorrow().getBorrowID());
		if(ps.executeUpdate()==1) {
			toReturn = new MyData("ExtensionSucceed");
			toReturn.add("updatedBorrow", copyInBorrow);
		}
		else {
			toReturn = new MyData("ExtensionFailed");
			toReturn.add("updatedBorrow", copyInBorrow);
		}
		return toReturn;
	}
	/**
	 * 
	 * @param id - the id of the member
	 * @return MyData- the parameters at the table or message that failed to load.
	 * @throws SQLException 
	 */

	public MyData history(int id) throws SQLException{
		MyData ret = new MyData("result");
		ArrayList<History> myHistory = new ArrayList<>();
		try {
			ResultSet rs= db.select("SELECT violations.description, violations.violationDate FROM violations WHERE violations.memberID="+ id);
			while (rs.next())
				myHistory.add(new History( "Violation" ,rs.getString("description"), rs.getTimestamp("violationDate")));
			ResultSet rs1= db.select("SELECT books.bookName, Borrows.borrowDate FROM books JOIN borrows WHERE borrows.bookID=books.bookID AND borrows.memberID="+ id);
			while (rs1.next())
				myHistory.add(new History( "Borrow" ,rs1.getString("bookName"), rs1.getTimestamp("borrowDate")));
			ResultSet rs2= db.select("SELECT books.bookName, book_reservations.orderDate FROM books JOIN book_reservations WHERE books.bookID=book_reservations.bookID AND book_reservations.memberID="+ id);
			while (rs2.next())
				myHistory.add(new History( "order" ,rs2.getString("bookName"), rs2.getTimestamp("orderDate")));
			ResultSet rs3= db.select("SELECT books.bookName, Borrows.actualReturnDate FROM books JOIN borrows WHERE borrows.bookID=books.bookID AND borrows.memberID="+ id);
			while (rs3.next())
				if(rs3.getDate("actualReturnDate")!=null)
					myHistory.add(new History( "Return" ,rs3.getString("bookName"), rs3.getTimestamp("actualReturnDate")));
			ResultSet rs4= db.select("SELECT books.bookName, extensions.newReturnDate FROM books JOIN borrows JOIN extensions WHERE borrows.bookID=books.bookID AND borrows.borrowID=extensions.borrowID AND borrows.memberID="+ id);
			while (rs4.next()) {
				myHistory.add(new History( "Extension" ,rs4.getString("bookName"), rs4.getTimestamp("newReturnDate")));
			}
			}
		catch (SQLException e) {
			System.out.println("History FAILED to load! "+ e.getMessage());
		}
		ret.add("list", myHistory);
			return ret;
	}
	/**
	 * 
	 * @param data - the book we want to add
	 * @return MyData - success / failed
	 * @throws IOException
	 * @throws SQLException
	 */
	public MyData addNewBook(MyData data) throws IOException, SQLException {
		MyData ret=new MyData("fail");
		Date toServer = Date.valueOf((LocalDate)data.getData("printDate"));
		try {
			PreparedStatement ps = db.update("INSERT INTO books (`bookName`, `authorsNames` , `editionNumber` , `printDate` , `shortDescription`, `numberOfCopies`"
					+ " , `shelfLocation` , `isPopular` , `topics` , `currentNumberOfCopies` , `purchaseDate`) "
					+ "VALUES ('"+data.getData("bookName")+"' , '"+data.getData("authorsNames")+
					"' , '"+data.getData("editionNumber")+"' , '"+toServer+"' , '"+data.getData("shortDescription")+"' , '"+data.getData("numberOfCopies")+
					"' , '"+data.getData("shelfLocation")+"' , ? , '"+data.getData("topics")+"' , '"+data.getData("currentNumberOfCopies")+
					"' , '"+data.getData("purchaseDate")+"')");
			ps.setBoolean(1, (boolean)data.getData("isPopular"));
			ps.executeUpdate();
		}
		catch (SQLException e) {
		if(e instanceof MySQLIntegrityConstraintViolationException)
			ret.add("reason", "That bookID already exist");
		else
			ret.add("reason", "Could not add a new book to the database");
		e.printStackTrace();
		return ret;
		}
		ResultSet rs = db.select("SELECT LAST_INSERT_ID()");
		rs.next();
		int bookid = rs.getInt(1);
		MyFile mf = (MyFile) data.getData("getFile");
    	File newFile = new File(mf.getWriteToPath()+"/"+bookid+".pdf");
		  if (!newFile.exists()) 
			newFile.createNewFile();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile));
		bos.write(mf.getMybytearray(), 0, mf.getSize());
		bos.close();
		ret.setAction("success");
		return ret;
	}
	
	public MyData updateBook(MyData data) throws SQLException, IOException  {
		MyData bookToUpdate;

			String query= "UPDATE books SET shelfLocation=?, numberOfCopies=?,currentNumberOfCopies=?,editionNumber=?,isPopular=?, topics='"+data.getData("genres")+"' WHERE bookID="+data.getData("bookID");
			PreparedStatement ps=db.update(query);
			ps.setString(1, (String) data.getData("shelfLocation"));
			Integer num = (Integer)data.getData("numberOfCopies");
			if(num.intValue()>=0)
			ps.setInt(2, (Integer)data.getData("numberOfCopies"));
			else {
				bookToUpdate=new MyData("number_of_copies_less_than_zero");
				bookToUpdate.add("copies_faild", data);
				return bookToUpdate;
			}
			ps.setInt(3, (Integer)data.getData("currentNumberOfCopies"));
			ps.setFloat(4, (Float)data.getData("editionNumber"));
			ps.setBoolean(5, (Boolean)data.getData("isPopular"));
			//guyguy

			if((Boolean) data.getData("FileChose"))
			{
			MyFile mf = (MyFile) data.getData("getFile");
	    	File newFile = new File(mf.getWriteToPath()+"/"+data.getData("bookID")+".pdf");
			  if (!newFile.exists()) 
				newFile.createNewFile();
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile));
			bos.write(mf.getMybytearray(), 0, mf.getSize());
			bos.close();
			}
		//guyguy
			if(ps.executeUpdate()==1) {
				bookToUpdate=new MyData("success");
				bookToUpdate.add("updatedBook", data);
				return bookToUpdate;
			}
		bookToUpdate=new MyData("failed");
		bookToUpdate.add("reason", new String("there was an error"));
		return bookToUpdate;
	}
	
	public MyData getActivityReports() throws SQLException {
		MyData data = new MyData("result");
		ResultSet rs = db.select("SELECT time from activity_reports");
		if (db.hasResults(rs)) {
		while (rs.next())
			data.add(String.valueOf(rs.getRow()), rs.getTimestamp("time"));
		} else data.setAction("Failure");
		return data;
	}
	public MyData report(MyData data) throws SQLException {
		ResultSet rs;
		float avg=0;
		ArrayList<Float> sum = new ArrayList<>();
		switch (data.getAction()) {
		case "Activity Report":
			if (data.getData().containsKey("ts")) {
				PreparedStatement ps = db.update("SELECT * from activity_reports where time=?");
				ps.setTimestamp(1, Timestamp.valueOf((String)data.getData("ts")));
				rs = ps.executeQuery();
				if (rs.next()) {
					data.add("active", rs.getInt("active"));
					data.add("locked", rs.getInt("locked"));
					data.add("frozen", rs.getInt("frozen"));
					data.add("totalBorrowedCopies", rs.getInt("totalBorrowedCopies"));
					data.add("lateReturners", rs.getInt("lateReturners"));
					data.add("ts",  data.getData("ts"));
				} else data.setAction("Failure");
			} else {
			rs = db.select("SELECT COUNT(id) as active, (SELECT count(id) from members where `status`='LOCK') as locked, (SELECT count(id) from members where `status`='FREEZE') as frozen, (SELECT count(*) from copy_in_borrow) as totalBorrowedCopies, (SELECT distinct count(memberID) from borrows where actualReturnDate > returnDate) as lateReturners from members where `status`='ACTIVE'");
			PreparedStatement ps = db.update("INSERT INTO `activity_reports` (`active`, `locked`, `frozen`, `totalBorrowedCopies`, `lateReturners`, `time`) VALUES (?, ?, ?, ?, ?, ?)");
			if (rs.next()) {
			data.add("active", rs.getInt("active"));
			ps.setInt(1, rs.getInt("active"));
			data.add("locked", rs.getInt("locked"));
			ps.setInt(2, rs.getInt("locked"));
			data.add("frozen", rs.getInt("frozen"));
			ps.setInt(3, rs.getInt("frozen"));
			data.add("totalBorrowedCopies", rs.getInt("totalBorrowedCopies"));
			ps.setInt(4, rs.getInt("totalBorrowedCopies"));
			data.add("lateReturners", rs.getInt("lateReturners"));
			ps.setInt(5, rs.getInt("lateReturners"));
			Timestamp time = new Timestamp(System.currentTimeMillis());
			time.setNanos(0);
			ps.setTimestamp(6, time);
			data.add("ts", time.toString());
			ps.executeUpdate();
			} else data.setAction("Failure");
			}
			break;
		 case "Borrow Report":
			 HashMap<Boolean,ArrayList<Float>> borrows = new HashMap<>(); // key: isPopular, value: borrows
			 rs = db.select("select isPopular, cast(ifnull((day(returnDate)-day(borrowDate))+((hour(returnDate)-hour(borrowDate)))/24+((minute(returnDate)-minute(borrowDate)))/(24*60),0) as decimal(10,2)) as borrowDuration from borrows right join books on books.bookID = borrows.bookID order by borrowDuration");
			 borrows.put(false,new ArrayList<>());
			 borrows.put(true,new ArrayList<>());
			 if (db.hasResults(rs)) {
					rs.beforeFirst();
			 while (rs.next()) {
				 float borrowDuration = rs.getFloat("borrowDuration");
				 borrows.get(rs.getBoolean("isPopular")).add(borrowDuration);
				 sum.add(borrowDuration);
			 }
			 Collections.sort(sum);
			 data.add("maxVal", sum.get(sum.size()-1));
			 data.add("median", sum.get(sum.size()/2));
			 for (Float f : sum)
				 avg+=f;
			 data.add("average", avg/sum.size());
			 data.add("borrows", borrows);
			 } else data.setAction("Failure");
			 break;
		 case "Late Return Report":
				HashMap<Integer,MyData> result = new HashMap<>();
				rs = db.select("select books.bookID,bookName, cast(if(actualReturnDate is null, (day(CURRENT_TIMESTAMP)-day(returnDate))+((hour(CURRENT_TIMESTAMP)-hour(returnDate)))/24+((minute(CURRENT_TIMESTAMP)-minute(returnDate)))/(24*60), (day(actualReturnDate)-day(returnDate))+((hour(actualReturnDate)-hour(returnDate)))/24+((minute(actualReturnDate)-minute(returnDate)))/(24*60)) as decimal(10,2)) as duration from borrows join books where if(actualReturnDate is null,current_timestamp>returnDate,actualReturnDate>returnDate) and books.bookID=borrows.bookID");
				if (db.hasResults(rs)) {
					rs.beforeFirst();
				while (rs.next()) {
					if (!result.containsKey(rs.getInt("bookID"))) { // init
						result.put(rs.getInt("bookID"), new MyData(rs.getString("bookName")));
						result.get(rs.getInt("bookID")).add("durations", new ArrayList<Float>());
					}
					float duration = rs.getFloat("duration");
					((ArrayList<Float>)result.get(rs.getInt("bookID")).getData("durations")).add(duration);
					sum.add(duration);
				 }
				Collections.sort(sum);
				data.add("maxVal", sum.get(sum.size()-1));//
				data.add("median", sum.get(sum.size()/2));
				 for (Float f : sum)
					 avg+=f;
				data.add("average", avg/sum.size());
				data.add("result", result);
				} else data.setAction("Failure");
				break;
		}
		return data;
	}
	
	/**
	 * This method writes a new tuple that represents a borrow to the data base
	 * @param bookAndBorrow - MyData instance holding the borrow and the book
	 * @return MyData instance with the results.
	 * @throws SQLException
	 * @author Good Guy
	 */
	public MyData writeNewBorrow(MyData bookAndBorrow) throws SQLException {
		MyData toReturn;
		int toUpdate;
		String query = "INSERT INTO borrows(memberID,bookID,borrowDate,returnDate) "
				+ "VALUES(?,?,?,?)";
		PreparedStatement ps = db.update(query);
		ps.setInt(1, ((Borrow)bookAndBorrow.getData("theBorrow")).getMemberID());
		ps.setInt(2,((Book)bookAndBorrow.getData("theCopy")).getBookID());
		Timestamp borrowDate = ((Borrow)bookAndBorrow.getData("theBorrow")).getBorrowDate();
		ps.setTimestamp(3,((Borrow)bookAndBorrow.getData("theBorrow")).getBorrowDate());
		ps.setTimestamp(4, ((Borrow)bookAndBorrow.getData("theBorrow")).getReturnDate());
		if(ps.executeUpdate()==1) {
			ResultSet rs = db.select("SELECT LAST_INSERT_ID()");
			rs.next();
			query = "INSERT INTO copy_in_borrow(copyNumber,bookID,borrowID) "
					+ "VALUES(?,?,?)";
			ps = db.update(query);
			toUpdate = ((Book)bookAndBorrow.getData("theCopy")).getCurrentNumberOfCopies();
			ps.setInt(1, toUpdate);
			ps.setInt(2, ((Book)bookAndBorrow.getData("theCopy")).getBookID());		
			ps.setInt(3, rs.getInt(1));
			if(ps.executeUpdate() == 1) {
				 query = "UPDATE books SET currentNumberOfCopies=? WHERE bookID="+((Book)bookAndBorrow.getData("theCopy")).getBookID();
				 ps = db.update(query);
				 toUpdate = ((Book)bookAndBorrow.getData("theCopy")).getCurrentNumberOfCopies();
				 ps.setInt(1, --toUpdate);
				 if (ps.executeUpdate() == 1) {
						toReturn = new MyData("borrowSuccess");
						((Book)bookAndBorrow.getData("theCopy")).setCurrentNumberOfCopies(toUpdate);
						toReturn.add("UpdatedBookAndBorrow", bookAndBorrow);
						toReturn.add("updatedMemberCard", getMemberCard((int)bookAndBorrow.getData("id")));
						return toReturn;
				 }
			}
		}
			toReturn = new MyData("borrowFailed");
			toReturn.add("reason", "Cannot write borrow in the system!");
			return toReturn;
	}
	/**
	 * author Feldman
	 * method return list of books that the user is borrow and not return it yet.
	 * @param data
	 * @return ArraList of books.
	 * @throws SQLException
	 */
	public MyData getReturnBooks(MyData data) throws SQLException {
		ArrayList<CopyInBorrow> returnBookList = new ArrayList<>();
		int flag =0;
		String MyQuery = "SELECT copy_in_borrow.borrowID, copy_in_borrow.copyNumber, copy_in_borrow.BookID "
				+ "FROM oblg3.copy_in_borrow "
				+ "INNER JOIN oblg3.borrows ON copy_in_borrow.borrowID=borrows.borrowID "
				+ "AND borrows.memberID='"
				+ data.getData("ID")
				+"';";
			ResultSet rs = db.select(MyQuery);
			while (rs.next()) 
			{
				Book book = getBook(rs.getInt("BookID"));
				int num = rs.getInt("borrowID");
				Borrow borrow =  getBorrow(num);
				returnBookList.add(new CopyInBorrow(book, borrow, rs.getInt("copyNumber")));
				flag=1;
			}
			rs.close();
			
			if (flag ==1)
			{
				data.add("returnBooklist", returnBookList);
				data.setAction("listOfReturnBooks");
				return data;
			}
			else 
			{
				data.setAction("unfind_borrows_Book");
				data.add("reason", "There is no books in borrow!");
				return data;
			}
		}

	/**
	 * Author Feldman
	 * method for return book, it check if the user Freeze and have less then 2 lates 
	 * it change him to Active.
	 * if user return copy that there is order for the book, its send him mail to come take it.
	 * the actual return date is saved in the database.
	 * @param data 
	 * @return message (success,fail)
	 * @throws SQLException
	 */
		public MyData returnCopy(MyData data) throws SQLException {
		CopyInBorrow copy =  (CopyInBorrow) data.getData("copy");
			java.util.Date today = new java.util.Date();
			Timestamp sqlDate = new Timestamp(today.getTime());
			java.util.Date returnDate = new java.util.Date(copy.getNewBorrow().getReturnDate().getTime());
			if(today.after(returnDate))
			{
				int lates;
				String query = "SELECT lateReturns FROM member_cards WHERE userID="+copy.getNewBorrow().getMemberID();
				ResultSet rs1 = db.select(query);
				if(db.hasResults(rs1)) {
					lates = rs1.getInt("lateReturns");
					if (lates <3)
					{
						String query1 = "UPDATE members SET status = 'ACTIVE' WHERE id= '"+copy.getNewBorrow().getMemberID()+"'";
						db.updateWithExecute(query1);
					}
				}
			
			}
		
			String query = "UPDATE borrows SET actualReturnDate = ? WHERE borrowID= '"+copy.getNewBorrow().getBorrowID()+"'";
			PreparedStatement stmt = db.update(query);
			stmt.setTimestamp(1, sqlDate);
			stmt.executeUpdate();
			String deleteQuery = "DELETE FROM copy_in_borrow WHERE borrowID= ?";
			PreparedStatement stmt1 = db.update(deleteQuery);
			stmt1.setInt(1, copy.getNewBorrow().getBorrowID());
			stmt1.executeUpdate();
			
			String reservationQuery = "SELECT * FROM book_reservations WHERE bookID='"+copy.getBorroBook().getBookID()+"' and arrivedDate is null order by orderDate limit 1";
			ResultSet rs1 = db.select(reservationQuery);
			if(db.hasResults(rs1)) {
				String query1 = "UPDATE book_reservations SET arrivedDate = ? WHERE bookID= '"+copy.getBorroBook().getBookID()+"' and arrivedDate is null order by orderDate limit 1";
				PreparedStatement stmt11 = db.update(query1);
				stmt11.setTimestamp(1, sqlDate);
				stmt11.executeUpdate();

				String memberID = rs1.getString("memberID");
				String emailQuery = "SELECT emailAddress, firstName FROM member_cards WHERE userID='"+memberID+"'";
				ResultSet rs2 = db.select(emailQuery);
				if(db.hasResults(rs2)) {
					String memberMail = rs2.getString("emailAddress");
					String userName = rs2.getString("firstName");
					String msg = "Hello "+userName+"\n\n Your Reservasion is resdy for "+copy.getBorroBook().getBookName()+"\n please come to take it";
					new SendMail(memberMail,"Reservesion is ready",msg);
				}
			}
			else {
				String addQuery = "UPDATE books SET currentNumberOfCopies = currentNumberOfCopies + ? WHERE bookID= '"+copy.getBorroBook().getBookID()+"'";
				PreparedStatement stmt3 = db.update(addQuery);
				stmt3.setInt(1, 1);
				stmt3.executeUpdate();
			}
			data.setAction("succeed");
			data.add("succeed", "Return book is succeed");
			return data;
		}
		/** 
		 * @author Ariel
		 * @param studentid - Student's ID
		 * @return the MyData's data key "message" will contain the message received from server.
		 * @return the action will be Success/Fail
		 * @throws SQLException
		 */
		protected MyData notifyGraduation(int studentid) throws SQLException {
			MyData data = new MyData();
			int status;
			ResultSet rs = db.select("SELECT * FROM members WHERE id ="+ studentid);
			if (!db.hasResults(rs)) { // no such student in DB
				data.setAction("Fail");
				 data.add("message", "Student was not found");
				 return data;
			}
			if (rs.getBoolean("graduated")) { // if true : student already known as graduated.
				data.setAction("Fail");
				 data.add("message", "OBL is was already updated regarding this student's graduation.");
				 return data;
			}
			 // check is student has copies in borrow
			status = db.select("SELECT * FROM borrows join copy_in_borrow WHERE borrows.borrowID=copy_in_borrow.BookID AND memberID="+studentid).next() ? 1 : 2;
			 PreparedStatement ps = db.update("UPDATE members SET graduated=?,status=? where id=?");
			 ps.setBoolean(1, true);
			 ps.setString(2, Member.Status.values()[status].toString());
			 ps.setInt(3, studentid);
			 if (ps.executeUpdate()==1) {
				 data.setAction("Success");
				 data.add("message", "Successfuly updated the system.");
			 } else {
				 data.setAction("Fail");
				 data.add("message", "Failed to update the system");
			 }
			 return data;
		  }
		public String getUserName(int userid) throws SQLException {
			if (userid==0)
				return "System";
			ResultSet rs = db.select("SELECT username from members where id="+userid);
			if (rs.first())
				return rs.getString("username");
			else
				return "Unknown";
		}
		/**
		 * get relevant messages
		 * @author Ariel
		 * @param userID - userID requesting this method
		 * @param rank - Member/Librarian/Manager
		 * @return
		 * @throws SQLException
		 */
		public MyData getMessages(int userID,String rank) throws SQLException {
			MyData toReturn;
			ArrayList<Message> theMessages = new ArrayList<Message>();
			String inboxAddition="";
			if (rank.equals("Manager"))
				inboxAddition+=" OR reciever=1"; // 1 : manager inbox
			if (rank.equals("Librarian"))
				inboxAddition+=" OR reciever=2"; // 2 : librarian inbox
			ResultSet rs = db.select("SELECT * FROM messages WHERE reciever="+userID+""+inboxAddition+" order by wasRead");
			if(!db.hasResults(rs))
				toReturn = new MyData("noMessages");
			else {
				do {
					if (rs.getString("action")==null) // check if message has action
						theMessages.add(new Message(rs.getInt("msgID"),getUserName(rs.getInt("sender")), rs.getInt("reciever"), rs.getString("subject"),rs.getString("content"),rs.getBoolean("wasRead")));
					else
						theMessages.add(new Message(rs.getInt("msgID"),getUserName(rs.getInt("sender")), rs.getInt("reciever"), rs.getString("subject"), rs.getString("content"),rs.getString("action"),(Member)searchMember(rs.getInt("regarding")).getData("member"),rs.getBoolean("handled"),rs.getBoolean("wasRead")));
				}while(rs.next());
				toReturn = new MyData("messages");
				toReturn.add("messages", theMessages);
			}
			return toReturn;
		}
		/**
		 * 
		 * @param data - the violation we want to add to the table
		 * @return MyData- success / failed
		 */
		public MyData addViolation(MyData data) {
			MyData ret=new MyData("fail");
			try {
				db.insertWithExecute("INSERT INTO violations (`memberID`, `ViolationDate` , `description` , `violationType`) "
						+ "VALUES ('"+data.getData("id")+"' , '"+data.getData("violationDate")+
						"' , '"+data.getData("violation")+"' , '"+data.getData("violationType")+"')");
			}
			catch (SQLException e) {
				e.printStackTrace();
				return ret;
				}
			ret.setAction("success");
			return ret;
		}
		
		/**
		 * This method removes a given message from the data base
		 * @param data - MyData instance contains the message to delete
		 * @return - MyData instance with the results
		 * @throws SQLException
		 * @author Good Guy
		 */
		public MyData removeMsg(MyData data) throws SQLException {
			Message toDelete = (Message)data.getData("toDelete");
			String deleteQuery = "DELETE FROM messages WHERE msgID=?";
			PreparedStatement ps = db.update(deleteQuery);
			ps.setInt(1, toDelete.getMsgID());
			if (ps.executeUpdate() == 1) 
				return new MyData("removed");
			return new MyData("failed");
		}

		public MyData orderBorrowRequest(MyData bookAndBorrow) throws SQLException {
			MyData data = writeNewBorrow(bookAndBorrow);
			String deleteQuery = "DELETE FROM book_reservations WHERE bookID= ? AND memberID = ?";
			PreparedStatement stmt1 = db.update(deleteQuery);
			stmt1.setInt(1, (int)((Book) bookAndBorrow.getData("theCopy")).getBookID());
			stmt1.setInt(2, ((int)bookAndBorrow.getData("ID")));
			stmt1.executeUpdate();
			String query = "UPDATE books SET currentNumberOfCopies = currentNumberOfCopies +? WHERE bookID= '"+(int)((Book) bookAndBorrow.getData("theCopy")).getBookID()+"'";
			PreparedStatement stmt = db.update(query);
			stmt.setInt(1, 1);
			stmt.executeUpdate();

			return data;


		}
		/**
		 * Updates 'wasRead' value in DB
		 * @author Ariel
		 * @param value
		 * @param msgID
		 * @return result
		 * @throws SQLException
		 */
		public MyData msgRead(boolean value, int msgID) throws SQLException {
			MyData data = new MyData("Success");
			PreparedStatement ps = db.update("UPDATE messages SET wasRead =? WHERE msgID=?");
			ps.setBoolean(1, value);
			ps.setInt(2, msgID);
			if (ps.executeUpdate()!=1)
				data.setAction("Failure");
			return data;
		}
		/**
		 * Handles actions from messages
		 * @author Ariel
		 * @param dfs - data recieved from server
		 * @return MyData type result
		 * @throws SQLException
		 */
		public MyData msgAction(MyData dfs) throws SQLException {
			MyData data = new MyData("Success");
			switch (dfs.getAction()) {
				case "3Late":
					PreparedStatement ps = db.update("UPDATE members SET status =? WHERE id =?");
					ps.setString(1, ((Member.Status)dfs.getData("newStatus")).toString());
					ps.setInt(2, (Integer)dfs.getData("memberID"));
					if (ps.executeUpdate()!=1)
						data.setAction("Failure");
					else {
						ps = db.update("UPDATE member_cards SET lateReturns = 0 WHERE id =?"); // since it was handled by a manager, reset lateReturns
						ps.setInt(1, (Integer)dfs.getData("memberID"));
						if (ps.executeUpdate()!=1)
							data.setAction("Failure");
					}
					break;
			}
			if (!data.getAction().equals("Failure")) {
				PreparedStatement ps = db.update("UPDATE messages SET handled =1 WHERE msgID =?");
				ps.setInt(1, (Integer)dfs.getData("msgID"));
				if (ps.executeUpdate()!=1)
					data.setAction("Failure");
			}
			return data;
		}
		
		/**
		 *  Writes a message from System
		 *  if from is 0, that means it's a System Message
		 * @author Ariel
		 * @param to - 1 : Managers, 2 : Librarians
		 * @param toUpdate - relevant copy in borrow
		 * @throws SQLException
		 */
		protected void writeMsg(int from, int to, String subject, String content) throws SQLException {
			db.updateWithExecute("INSERT INTO messages(sender,reciever,subject,content) VALUES("+from+","+to+",'"+subject+"','"+content+"')");
		}
		protected void writeMsg(int from, int to, String subject, String content,String action, Integer regarding) throws SQLException {
			db.updateWithExecute("INSERT INTO messages(sender,reciever,subject,content,action,regarding) VALUES("+from+","+to+",'"+subject+"','"+content+"','"+action+"',"+regarding+")");
	}
}
