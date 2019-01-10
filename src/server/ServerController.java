package server;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import client.MyData;
import common.Book;
import common.BookReservation;
import common.Borrow;
import common.Librarian;
import common.Manager;
import common.Member;
import common.MemberCard;
import common.Violation;

public class ServerController {
	public ServerController(MyDB db) {
		this.db=db;
	}
	final MyDB db;
	public ArrayList<Book> getAllBooks() {
		ArrayList<Book> books = new ArrayList<>();
		try {
		ResultSet rs = db.select("SELECT * from book");
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
		ResultSet rs = db.select("SELECT * from book WHERE bookid="+ bookID);
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
		String MyQuery = "SELECT *"
							+ "FROM users "
							+ "WHERE id = '"+ data.getData("id")+"' "
							+ "AND password = '"+data.getData("password")+"';";
		ResultSet memberMatch = db.select(MyQuery);
		MyData ret = new MyData("login_failed");
		if(!db.hasResults(memberMatch))
			data.add("reason", "ID or password incorrect");
		else if (memberMatch.getBoolean("loggedin"))
			data.add("reason", "Already logged in!");
		else {
			data.setAction("login_approved");
			setLoggedIn(true,memberMatch.getInt("id"));
			data.add("MemberLoggedIn",createMember(memberMatch));
			}
		return data;
	}
	
	public void setLoggedIn(boolean value, int id) throws SQLException { // TODO change name to id...
		PreparedStatement ps = db.update("UPDATE users SET loggedin =? WHERE id =?");
		ps.setBoolean(1, value);
		ps.setInt(2, id);
		ps.executeUpdate();
	}
	public void updateIP(String ip,int id) throws SQLException {
		PreparedStatement ps = db.update("UPDATE users SET ip=? WHERE id =?");
		ps.setString(1, ip);
		ps.setInt(2, id);
		ps.executeUpdate();
	}
	/*this method creates Member instance for the member that has logged in
	 * input: ResultSet (member details) and MyDB instance
	 * output: Member instance
	 */
	public Member createMember(ResultSet rs) throws SQLException {
			Member toReturn=null;
			ResultSet localRS;
			if (db.hasResults(localRS = db.select("SELECT * FROM Manager where id = "+ rs.getInt("id")))) 
				toReturn = new Manager(rs.getInt("id"), rs.getString("username"), rs.getString("password"), localRS.getInt("workerNum"),1);
			else if (db.hasResults(localRS = db.select("SELECT * FROM Librarian where id = "+ rs.getInt("id"))))
				toReturn = new Librarian(rs.getInt("id"), rs.getString("username"), rs.getString("password"), localRS.getInt("workerNum"),localRS.getInt("permissionLevel"));
			else // member
				toReturn = new Member(rs.getInt("id"),rs.getString("username"),rs.getString("password"));
			toReturn.setMemberCard(getMemberCard(rs.getInt("id")));
			return toReturn;
	}
	/* fetch membercard*/
	private MemberCard getMemberCard(int id) throws SQLException {
		ResultSet rs = db.select("SELECT * from member_card WHERE userID = "+ id);
		System.out.println(getMemberReservations(id));
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
		String MyQuery = "SELECT * FROM Borrow WHERE memberID ="+memberID;
		ResultSet rs = db.select(MyQuery);
		while(rs.next())
			memberBorrows.add(new Borrow(rs.getInt(1),rs.getInt(2),rs.getDate(3),rs.getDate(4),rs.getDate(5),rs.getBoolean(6)));
		return memberBorrows;
	}
	/* function to get all of the specified member violations
	 * input: memberID (unique) , MyDB instance.
	 * output: array of violations.
	 */
	public ArrayList<Violation> getMemberViolations(int memberID) throws SQLException {
		ArrayList<Violation> memberViolations = new ArrayList<Violation>();
		String MyQuery = "SELECT * FROM Violation WHERE memberID ="+memberID;
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
		ResultSet rs = db.select("SELECT * FROM book_reservation WHERE memberID ="+memberID);
		while(rs.next()) {
			memberReservations.add(new BookReservation(memberID, rs.getDate("orderDate"),rs.getInt("bookID")));
		}
		return memberReservations;
	}
	public MyData orderBook(int userid, int bookID) throws SQLException {
		Book book = getBook(bookID);
		if (book==null)
			return new MyData("fail","message","The book doesn't exists.");
			if (book.getCurrentNumberOfCopies()==0) { // can order
				Date date = new Date(System.currentTimeMillis());
				db.insert("INSERT INTO book_reservation (memberID, orderDate, bookID) VALUES ('"+userid+"', '"+date+"', '"+bookID+"')");
				return new MyData("success","reservation",new BookReservation(userid, date,bookID));
			} else
				return new MyData("fail","message","There are still available copies of that book in the library.");
	}
}
