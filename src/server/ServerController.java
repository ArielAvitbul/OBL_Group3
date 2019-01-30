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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

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

public class ServerController {
	public ServerController(MyDB db) {
		this.db=db;
	}
	final MyDB db;
	public ArrayList<Book> getAllBooks() {
		ArrayList<Book> books = new ArrayList<>();
		try {
			ResultSet rs = db.select("SELECT * from books WHERE deleted = '0'");
		while (rs.next()) {//TODO: add table of content PDF.
			Book book = new Book(rs.getInt("bookID"), rs.getString("bookName"), rs.getString("authorsNames"), rs.getFloat("editionNumber"), rs.getDate("printDate"), rs.getString("topics"), rs.getString("shortDescription"), rs.getInt("numberOfCopies"), rs.getDate("purchaseDate"), rs.getString("shellLocation"), rs.getBoolean("isPopular"),rs.getInt("currentNumberOfCopies"));
			books.add(book);
		}
		} catch (SQLException e) {
			System.out.println("Books FAILED to load! "+ e.getMessage());
		}
		return books;
	}
	public Book getBook(int bookID) throws SQLException {
		ResultSet rs = db.select("SELECT * from books WHERE deleted = '0' AND bookid="+ bookID);		if (db.hasResults(rs))
			return new Book(rs.getInt("bookID"), rs.getString("bookName"), rs.getString("authorsNames"), rs.getFloat("editionNumber"), rs.getDate("printDate"), rs.getString("topics"), rs.getString("shortDescription"), rs.getInt("numberOfCopies"), rs.getDate("purchaseDate"), rs.getString("shellLocation"), rs.getBoolean("isPopular"),rs.getInt("currentNumberOfCopies"));
		return null;
	}
	/*method for confirming login request from client
	 * input: MyDB instance and MyData instance
	 * output: MyData (Member instance) that has been logged in or empty MyData
	 * Guy Wrote This
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
	
	public void setLoggedIn(boolean value, int id) throws SQLException {
		PreparedStatement ps = db.update("UPDATE members SET loggedin =? WHERE id =?");
		ps.setBoolean(1, value);
		ps.setInt(2, id);
		ps.executeUpdate();
	}
	public void updateIP(String ip,int id) throws SQLException {
		PreparedStatement ps = db.update("UPDATE members SET ip=? WHERE id =?");
		ps.setString(1, ip);
		ps.setInt(2, id);
		ps.executeUpdate();
	}
	/** this method searches for a member in the database, by given ID
	 * returns: key: member is HashMap incase a result was found!
	 * 			or an empty HashMap incase a resultw as not found!
	 * @throws SQLException */
	public MyData searchMember(int id) throws SQLException {
		MyData ret = new MyData("result");
		ResultSet rs = db.select("SELECT * FROM members where id='"+id+"'");
		if (rs.next()) // such member exists
			ret.add("member", createMember(rs));
		return ret;
	}
	/** this method inserts a new user to the database
	 * @throws SQLException */
	public MyData createUser(MyData data) {
		MyData ret = new MyData("fail");
		try {
		 db.insert("INSERT INTO members (`id`, `username`, `password`) "
				 + "VALUES ('"+data.getData("id")+"', '"+data.getData("username")+"', '"+data.getData("password")+"')");
		 db.insert("INSERT INTO member_cards (`userID`, `firstName`, `lastName`, `phoneNumber`, `emailAddress`) "
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
	 * input: ResultSet (member details) and MyDB instance
	 * output: Member instance
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
	/* fetch membercard*/
	private MemberCard getMemberCard(int id) throws SQLException {
		ResultSet rs = db.select("SELECT * from member_cards WHERE userID = "+ id);
		if (db.hasResults(rs))
		return new MemberCard(rs.getString("firstName"), rs.getString("lastName"), rs.getString("phoneNumber"), rs.getString("emailAddress"), getMemberBorrows(id), getMemberViolations(id), getMemberReservations(id),rs.getInt("lateReturns"));
		return null;
		}
		/* function to get all of the specified member borrows
		 * input: memberID (unique) , MyDB instance.
		 * output: array of borrows.
		 */
	public ArrayList<Borrow> getMemberBorrows(int memberID) throws SQLException {
		ArrayList<Borrow> memberBorrows = new ArrayList<Borrow>();
		ResultSet rs = db.select("SELECT * FROM borrows WHERE memberID ="+memberID);
		while(rs.next())
			memberBorrows.add(new Borrow(rs.getInt("borrowID"), rs.getInt("bookID"), rs.getInt("memberID"), rs.getDate("borrowDate"), rs.getDate("returnDate"), rs.getDate("actualReturnDate")));
		return memberBorrows;
	}
	/* function to get all of the specified member violations
	 * input: memberID (unique) , MyDB instance.
	 * output: array of violations.
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
	/* function to get all of the specified member reservations
	 * input: memberID (unique) , MyDB instance.
	 * output: array of violations.
	 */
	public ArrayList<BookReservation> getMemberReservations(int memberID) throws SQLException {
		ArrayList<BookReservation> memberReservations = new ArrayList<BookReservation>();
		ResultSet rs = db.select("SELECT * FROM book_reservations WHERE memberID ="+memberID);
		while(rs.next()) {
			memberReservations.add(new BookReservation(memberID, rs.getDate("orderDate"),rs.getInt("bookID")));
		}
		return memberReservations;
	}
	
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
	public MyData saveInfoAdmin(int userid, String username, String status) throws SQLException {
		MyData result = new MyData("fail");
		if (db.updateWithExecute("UPDATE members set username='"+username+"',status='"+status+"' WHERE id='"+userid+"'")==1) {
			result.setAction("success");
			result.add("member_card", getMemberCard(userid)); // return the new updated member card!
		}else // member was not found, keep the fail action, add a message
			result.add("message", "Member was not found in the database.");
		return result;
	}
	public MyData orderBook(int userid, int bookID) throws SQLException {
		Book book = getBook(bookID);
		if (book==null)
			return new MyData("fail","message","The book doesn't exists.");
			if (book.getCurrentNumberOfCopies()==0) { // can order
				Date date = new Date(System.currentTimeMillis());
				db.insert("INSERT INTO book_reservations (memberID, orderDate, bookID) VALUES ('"+userid+"', '"+date+"', '"+bookID+"')");
				return new MyData("success","reservation",new BookReservation(userid, date,bookID));
			} else
				return new MyData("fail","message","There are still available copies of that book in the library.");
	}
	
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
public MyData getClosedReturnBook(MyData data) throws SQLException {
	Book book =  (Book) data.getData("book");
	ResultSet rs1 = db.select("SELECT * FROM borrows where bookID ='" + book.getBookID()+"' and actualReturnDate is null order by returnDate limit 1");
	if(db.hasResults(rs1)) {
	Date returnDate = rs1.getDate("returnDate");
	System.out.println(returnDate);
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
		return new Borrow(rs.getInt("borrowID"), rs.getInt("bookID"),rs.getInt("memberID"), rs.getDate("borrowDate"), rs.getDate("returnDate"), rs.getDate("actualReturnDate"));
	return null;
}

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
			if(b.getAuthorsNames().contains(s))
				result = true;
			if(b.getBookName().contains(s))
				result = true;
			if(b.getTopics().contains(s))
				result = true;
			if(b.getShortDescription().contains(s))
				result=true;
		}
		return result;
	}
	
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
	
	public MyData updateExtension(CopyInBorrow copyInBorrow) throws SQLException {
		MyData toReturn;
		if(checkReservations(copyInBorrow)) {
			toReturn = new MyData("ExtensionFailed");
			toReturn.add("updatedBorrow", copyInBorrow);
			toReturn.add("reason", "This book has been reserved!");
		}
		else {
		toReturn = updateBorrows(copyInBorrow);
		if(updateExtensions((CopyInBorrow)toReturn.getData("updatedBorrow"))==0) {
			toReturn = new MyData("ExtensionFailed");
			toReturn.add("updatedBorrow", copyInBorrow);
			toReturn.add("reason", "Can't update borrow!");
		}
		else {
		writeMsgToLibrerians(copyInBorrow);
			}
		}
		return toReturn;	
	}
	private boolean checkReservations(CopyInBorrow copyInBorrow) throws SQLException {
		ResultSet rs = db.select("SELECT * FROM book_reservations WHERE bookID="+copyInBorrow.getBorroBook().getBookID());
		return db.hasResults(rs);
	}
	private int updateExtensions(CopyInBorrow copyInBorrow) throws SQLException {
		String insertQ = "INSERT INTO extensions(memberID,borrowID,newReturnDate) "
				+ "VALUES(?,?,?)";
		PreparedStatement ps = db.update(insertQ);
		ps.setInt(1, copyInBorrow.getNewBorrow().getMemberID());
		ps.setInt(2, copyInBorrow.getNewBorrow().getBorrowID());
		ps.setDate(3, new Date(copyInBorrow.getNewBorrow().getReturnDate().getTime()));
		return ps.executeUpdate();
		
	}
	private MyData updateBorrows(CopyInBorrow copyInBorrow) throws SQLException {
		MyData toReturn;
		Calendar c = Calendar.getInstance();
		c.setTime(copyInBorrow.getNewBorrow().getReturnDate());
		c.add(Calendar.DAY_OF_MONTH, 7);
		copyInBorrow.getNewBorrow().setReturnDate(new java.sql.Date(c.getTimeInMillis()));
		System.out.println(copyInBorrow.getNewBorrow().getReturnDate());
		System.out.println(copyInBorrow.getNewBorrow().getBorrowID());
		String query = "UPDATE borrows SET returnDate=? WHERE borrowID=?";
		PreparedStatement ps = db.update(query);
		ps.setDate(1,copyInBorrow.getNewBorrow().getReturnDate());
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
	
	private void writeMsgToLibrerians(CopyInBorrow toUpdate) throws SQLException {
		ArrayList<Integer> librerians = new ArrayList<Integer>();
		MemberCard borrower = getMemberCard(toUpdate.getNewBorrow().getMemberID());
		String insertQ = "INSERT INTO messages(sender,reciever,content) "
				+ "VALUES(?,?,?)";
		String query = "SELECT id FROM librarians";
		ResultSet rs = db.select(query);
		while(rs.next()) {
			PreparedStatement ps = db.update(insertQ);
			ps.setInt(1, toUpdate.getNewBorrow().getMemberID());
			ps.setInt(2, rs.getInt("id"));
			ps.setString(3, "The Borrow of "+borrower.getFirstName()+" "+borrower.getLastName()+"\nWith the book "+toUpdate.getBorroBookName()+"\nHas been extended in a week!");
			ps.executeUpdate();
		}
	}
	public MyData history(int id) throws SQLException{
		MyData ret = new MyData("result");
		ArrayList<History> myHistory = new ArrayList<>();
		try {
			ResultSet rs= db.select("SELECT violations.description, violations.violationDate FROM violations WHERE violations.memberID="+ id);
			while (rs.next())
				myHistory.add(new History( "Violation" ,rs.getString("description"), rs.getDate("violationDate")));
			ResultSet rs1= db.select("SELECT books.bookName, Borrows.borrowDate FROM books JOIN borrows WHERE borrows.bookID=books.bookID AND borrows.memberID="+ id);
			while (rs1.next())
				myHistory.add(new History( "Borrow" ,rs1.getString("bookName"), rs1.getDate("borrowDate")));
			ResultSet rs2= db.select("SELECT books.bookName, book_reservations.orderDate FROM books JOIN book_reservations WHERE books.bookID=book_reservations.bookID AND book_reservations.memberID="+ id);
			while (rs2.next())
				myHistory.add(new History( "order" ,rs2.getString("bookName"), rs2.getDate("orderDate")));
			ResultSet rs3= db.select("SELECT books.bookName, Borrows.actualReturnDate FROM books JOIN borrows WHERE borrows.bookID=books.bookID AND borrows.memberID="+ id);
			while (rs3.next())
				if(rs3.getDate("actualReturnDate")!=null)
					myHistory.add(new History( "Return" ,rs3.getString("bookName"), rs3.getDate("actualReturnDate")));
			ResultSet rs4= db.select("SELECT books.bookName, extensions.newReturnDate FROM books JOIN borrows JOIN extensions WHERE borrows.bookID=books.bookID AND borrows.borrowID=extensions.borrowID AND borrows.memberID="+ id);
			while (rs4.next()) {
				myHistory.add(new History( "Extension" ,rs4.getString("bookName"), rs4.getDate("newReturnDate")));
			}
			}
		catch (SQLException e) {
			System.out.println("History FAILED to load! "+ e.getMessage());
		}
		ret.add("list", myHistory);
			return ret;
	}
	
	public MyData addNewBook(MyData data) throws IOException, SQLException {
		MyData ret=new MyData("fail");
		Date toServer = Date.valueOf((LocalDate)data.getData("printDate"));
		try {
			PreparedStatement ps = db.insert("INSERT INTO books (`bookName`, `authorsNames` , `editionNumber` , `printDate` , `shortDescription`, `numberOfCopies`"
					+ " , `shellLocation` , `isPopular` , `topics` , `currentNumberOfCopies` , `purchaseDate`,`deleted`) "
					+ "VALUES ('"+data.getData("bookName")+"' , '"+data.getData("authorsNames")+
					"' , '"+data.getData("editionNumber")+"' , '"+toServer+"' , '"+data.getData("shortDescription")+"' , '"+data.getData("numberOfCopies")+
					"' , '"+data.getData("shellLocation")+"' , ? , '"+data.getData("topics")+"' , '"+data.getData("currentNumberOfCopies")+
					"' , '"+data.getData("purchaseDate")+"', ?)");
			ps.setBoolean(1, (Boolean)data.getData("isPopular"));
			ps.setBoolean(2, false);
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

			String query= "UPDATE books SET shellLocation=?, numberOfCopies=?,currentNumberOfCopies=?,editionNumber=?,isPopular=?, topics='"+data.getData("genres")+"' WHERE bookID="+data.getData("bookID");
			PreparedStatement ps=db.update(query);
			ps.setString(1, (String) data.getData("shellLocation"));
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
	

	public MyData report(MyData data) throws SQLException {
		ResultSet rs;
		switch (data.getAction()) {
		case "Activity Report":
			rs = db.select("SELECT COUNT(id) as active, (SELECT count(id) from members where `status`='LOCK') as inactive, (SELECT count(id) from members where `status`='FREEZE') as frozen, (SELECT count(*) from copy_in_borrow) as totalCopiesInBorrow, (SELECT distinct count(memberID) from borrows where actualReturnDate > returnDate) as lateMembers from members where `status`='ACTIVE'");
			if (rs.next()) {
			data.add("active", rs.getInt("active"));
			data.add("inactive", rs.getInt("inactive"));
			data.add("frozen", rs.getInt("frozen"));
			data.add("totalCopiesInBorrow", rs.getInt("totalCopiesInBorrow"));
			data.add("lateMembers", rs.getInt("lateMembers"));
			}
			break;
		 case "Borrow Report": // TODO: maybe get only 1 view, by not using getBook
			 HashMap<Book,ArrayList<Integer>> books = new HashMap<>();
			 rs = db.select("select books.bookID, ifnull(returnDate-borrowDate,0) as borrowDuration from borrows right join books on books.bookID = borrows.bookID order by bookID");
			 while (rs.next()) {
				 Book book = getBook(rs.getInt("bookID"));
				 if (!books.containsKey(book))
				 books.put(book, new ArrayList<>());
				 books.get(book).add(rs.getInt("borrowDuration"));
			 }
			 data.add("books", books);
			 break;
		 case "Late Return Report":
				HashMap<String,Integer> result = new HashMap<>();
				rs= db.select("select books.bookName,count(books.bookName) as amount from books join copy_in_borrow WHERE deleted = '0' AND books.bookID=copy_in_borrow.BookID  group by books.bookName union select bookName,0 from books WHERE deleted = '0' AND bookID not in (select bookID from copy_in_borrow)");
				while (rs.next())
					result.put(rs.getString("bookName"), rs.getInt("amount"));
				data.add("result", result);
				break;
		}
		return data;
	}
	
	public MyData writeNewBorrow(MyData bookAndBorrow) throws SQLException {
		MyData toReturn;
		int toUpdate;
		String query = "INSERT INTO borrows(memberID,bookID,borrowDate,returnDate) "
				+ "VALUES(?,?,?,?)";
		PreparedStatement ps = db.update(query);
		ps.setInt(1, ((Borrow)bookAndBorrow.getData("theBorrow")).getMemberID());
		ps.setInt(2,((Book)bookAndBorrow.getData("theCopy")).getBookID());
		ps.setDate(3, ((Borrow)bookAndBorrow.getData("theBorrow")).getBorrowDate());
		ps.setDate(4, ((Borrow)bookAndBorrow.getData("theBorrow")).getReturnDate());
		if(ps.executeUpdate()==1) {
			ResultSet rs = db.select("SELECT LAST_INSERT_ID()");
			rs.next();
			System.out.println(rs.getInt(1));
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
						return toReturn;
				 }
			}
		}
			toReturn = new MyData("borrowFailed");
			toReturn.add("reason", "Cannot write borrow in the system!");
			return toReturn;
	}
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

		public MyData returnCopy(MyData data) throws SQLException {
		CopyInBorrow copy =  (CopyInBorrow) data.getData("copy");
			java.util.Date today = new java.util.Date();
			java.sql.Date sqlDate = new java.sql.Date(today.getTime());
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
			stmt.setDate(1, sqlDate);
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
				stmt11.setDate(1, sqlDate);
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
		/**
		 * @author Good Guy
		 * @param librarianID
		 * @return MyData instance representing the messages.
		 * @throws SQLException
		 */
		public MyData getMessages(int librarianID) throws SQLException {
			MyData toReturn;
			ArrayList<Message> theMessages = new ArrayList<Message>();
			//String query = "SELECT * FROM messages WHERE to="+librarianID;
			ResultSet rs = db.select("SELECT * FROM messages WHERE reciever="+librarianID);
			System.out.println(db.hasResults(rs));
			if(!db.hasResults(rs))
				toReturn = new MyData("noMessages");
			else {
				do {
					theMessages.add(new Message(rs.getInt("sender"), rs.getInt("reciever"), rs.getString("content")));
				}while(rs.next());
				toReturn = new MyData("messages");
				toReturn.add("messages", theMessages);
			}
			return toReturn;
		}
		public MyData addViolation(MyData data) {
			MyData ret=new MyData("fail");
			try {
				PreparedStatement ps = db.insert("INSERT INTO violations (`memberID`, `ViolationDate` , `description` , `violationType`) "
						+ "VALUES ('"+data.getData("id")+"' , '"+data.getData("violationDate")+
						"' , '"+data.getData("violation")+"' , '"+data.getData("violationType")+"')");
				ps.executeUpdate();
			}
			catch (SQLException e) {
				e.printStackTrace();
				return ret;
				}
			ret.setAction("success");
			return ret;
		}

}
