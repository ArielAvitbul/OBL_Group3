package server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import client.MyData;
import common.Book;
import common.BookReservation;
import common.Borrow;
import common.CopyInBorrow;
import common.History;
import common.Librarian;
import common.Manager;
import common.Member;
import common.MemberCard;
import common.MyFile;
import common.Violation;

public class ServerController {
	public ServerController(MyDB db) {
		this.db=db;
	}
	final MyDB db;
	public ArrayList<Book> getAllBooks() {
		ArrayList<Book> books = new ArrayList<>();
		try {
		ResultSet rs = db.select("SELECT * from books");
		while (rs.next()) {//TODO: add table of content PDF.
			Book book = new Book(rs.getInt("bookID"), rs.getString("bookName"), rs.getString("authorsNames"), rs.getFloat("editionNumber"), rs.getDate("printDate"), rs.getString("topic"), rs.getString("shortDescription"), rs.getInt("numberOfCopies"), rs.getDate("purchaseDate"), rs.getString("shellLocation"), rs.getBoolean("isPopular"),rs.getInt("currentNumberOfCopies"));
			books.add(book);
		}
		} catch (SQLException e) {
			System.out.println("Books FAILED to load! "+ e.getMessage());
		}
		return books;
	}
	public Book getBook(int bookID) throws SQLException {
		ResultSet rs = db.select("SELECT * from books WHERE bookid="+ bookID);
		if (db.hasResults(rs))
			return new Book(rs.getInt("bookID"), rs.getString("bookName"), rs.getString("authorsNames"), rs.getFloat("editionNumber"), rs.getDate("printDate"), rs.getString("topic"), rs.getString("shortDescription"), rs.getInt("numberOfCopies"), rs.getDate("purchaseDate"), rs.getString("shellLocation"), rs.getBoolean("isPopular"),rs.getInt("currentNumberOfCopies"));
		return null;
	}
	/*method for confirming login request from client
	 * input: MyDB instance and MyData instance
	 * output: MyData (Member instance) that has been logged in or empty MyData
	 * Guy Wrote This
	 */
	public MyData login (MyData data) throws SQLException {
		ResultSet memberMatch = db.select("SELECT * FROM members WHERE id ="+ data.getData("id")+" AND password ="+data.getData("password"));
		MyData ret = new MyData("login_failed");
		if(!db.hasResults(memberMatch))
			ret.add("reason", "ID or password incorrect");
		else if (memberMatch.getBoolean("loggedin"))
			ret.add("reason", "Already logged in!");
		else {
			ret.setAction("login_approved");
			setLoggedIn(true,memberMatch.getInt("id"));
			ret.add("MemberLoggedIn",createMember(memberMatch));
			}
		return ret;
	}
	
	public void setLoggedIn(boolean value, int id) throws SQLException { // TODO change name to id...
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
		return new MemberCard(); // TODO: incase no member card exists in the db, perhaps remove this later
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
	
public MyData getTableOfContents(MyDB db, MyData data) throws SQLException {
		
		Book b = (Book) data.getData("book");
		 File myFile = new File("./src/server/TableOfContents/"+b.getBookID()+".pdf");
		  MyFile msg= new MyFile(b);
		  msg.setWriteToPath("./src/client/ClientTableOfContents");
		  String LocalfilePath="./src/server/TableOfContents/"+ b.getBookID()+".pdf";
		  try{
			      File newFile = new File (LocalfilePath);
			      		      
			      byte [] mybytearray  = new byte [(int)newFile.length()];
			      FileInputStream fis = new FileInputStream(newFile);
			      BufferedInputStream bis = new BufferedInputStream(fis);			  
			      
			      msg.initArray(mybytearray.length);
			      msg.setSize(mybytearray.length);
			      
			      bis.read(msg.getMybytearray(),0,mybytearray.length);
			      data.add("getFile", msg);
				  data.setAction("getFile");
				  bis.close();
			      return data;
			    }
			catch (Exception e) {
				System.out.println("Error send (Files)msg) to Server");
			}
		return data;
		
	}
public Borrow getBorrow(int borrowID) throws SQLException {
	ResultSet rs = db.select("SELECT * from borrows WHERE borrowID="+ borrowID);
	rs.next();
	int borrowdID = rs.getInt("borrowID");
	int memberID = rs.getInt("memberID");
	Date borrowDate = rs.getDate("borrowDate");
	Date returnDate = rs.getDate("returnDate");
	Date actualReturnDate = rs.getDate("actualReturnDate");
	boolean isLate=rs.getBoolean("isLate");
	if (db.hasResults(rs))
		return new Borrow(rs.getInt("borrowID"), rs.getInt("bookID"),rs.getInt("memberID"), rs.getDate("borrowDate"), rs.getDate("returnDate"), rs.getDate("actualReturnDate"));
	return null;
}
public MyData getReturnBooks(MyDB db, MyData data) throws SQLException {
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
	public MyData searchBook(MyDB db, MyData data) throws SQLException {
		ArrayList<Book> bookList = new ArrayList<>();
		
		int checkFlag = 0;
		int flag = 0;
		String MyQuery = "SELECT BookID "
				+ "FROM books "
				+ "WHERE ";
		if (!data.getData("bookName").equals(""))
		{
			MyQuery = MyQuery + "bookName = '"+ data.getData("bookName")+"'";
			checkFlag = 1;
		}
		if (!data.getData("authorName").equals(""))
		{
			if(checkFlag ==0)
			MyQuery = MyQuery + "authorsNames  = '"+ data.getData("authorName")+"'";
			else MyQuery = MyQuery + "AND authorsNames = '"+data.getData("authorName")+"'";
			checkFlag =1;
		}
		if ((boolean) data.getData("genreDrama"))
		{
			if (checkFlag == 0)
			MyQuery = MyQuery + "topic LIKE '%' 'Drama' '%'";
			else MyQuery = MyQuery + "AND topic LIKE '%' 'Drama' '%'";
			checkFlag =1;
		}
		if ((boolean) data.getData("genreThriller"))
		{
			if (checkFlag == 0)
			MyQuery = MyQuery + "topic LIKE '%' 'Thriller' '%'";
			else MyQuery = MyQuery + "AND topic LIKE '%' 'Thriller' '%'";
			checkFlag =1;
		}
		if ((boolean) data.getData("genreAdventure"))
		{
			if (checkFlag == 0)
			MyQuery = MyQuery + "topic LIKE '%' 'Adventure' '%'";
			else MyQuery = MyQuery + "AND topic LIKE '%' 'Adventure' '%'";
			checkFlag =1;
		}
		if ((boolean) data.getData("genreBoxSF"))
		{
			if (checkFlag == 0)
			MyQuery = MyQuery + "topic LIKE '%' 'SF' '%'";
			else MyQuery = MyQuery + "AND topic LIKE '%' 'SF' '%'";
			checkFlag =1;
		}
		if ((boolean) data.getData("genreKids"))
		{
			if (checkFlag == 0)
			MyQuery = MyQuery + "topic LIKE '%' 'Kids' '%'";
			else MyQuery = MyQuery + "AND topic LIKE '%' 'Kids' '%'";
			checkFlag =1;
		}
		if ((boolean) data.getData("genreTextBook"))
		{
			if (checkFlag == 0)
			MyQuery = MyQuery + "topic LIKE '%' 'TextBook' '%'";
			else MyQuery = MyQuery + "AND topic LIKE '%' 'TextBook' '%'";
			checkFlag =1;
		}
		MyQuery = MyQuery + ";";
		
		if (checkFlag == 0)
		{
		data.setAction("empty_fields");
		data.add("reason", "Your fields are empty");
		return data;
		}
	
			
		ResultSet rs = db.select(MyQuery);
		data.getData().clear();
		while (rs.next()) 
		{	
					bookList.add(getBook(rs.getInt("BookID")));		
					flag=1;
		}
		rs.close();
		
		
		if (flag ==1)
		{
			data.add("booklist", bookList);
			data.setAction("listOfBooks");
			return data;
		}
		else 
		{
			data.setAction("unfind_book");
			data.add("reason", "No book found!");
			return data;
		}
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
	
	public MyData updateExtension(Borrow toUpdate) throws SQLException {
		Calendar c = Calendar.getInstance();
		c.setTime(toUpdate.getReturnDate());
		c.add(Calendar.DAY_OF_MONTH, 7);
		toUpdate.setReturnDate((Date) c.getTime());
		String query = "UPDATE borrows SET returnDate="+toUpdate.getReturnDate()+" WHERE borrowID="+toUpdate.getBorrowID();
		db.updateWithExecute(query);
		MyData toReturn = new MyData("ExtensionSucceed");
		toReturn.add("updaedBorrow", toUpdate);
		return toReturn;
	}
	
	public MyData history(int id) throws SQLException{
		MyData ret = new MyData("result");
		ArrayList<History> myHistory = new ArrayList<>();
		try {
			ResultSet rs= db.select("SELECT books.bookName , borrows.borrowDate, borrows.actualReturnDate FROM books JOIN borrows WHERE books.bookID=borrows.bookID AND borrows.memberID="+ id);
			while (rs.next())
				myHistory.add(new History(rs.getString("bookName"), rs.getDate("borrowDate"), rs.getDate("actualReturnDate")));
		}
		catch (SQLException e) {
			System.out.println("History FAILED to load! "+ e.getMessage());
		}
		ret.add("list", myHistory);
			return ret;
	}
}
