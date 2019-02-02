package server;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import common.Book;
import common.Borrow;
import common.CopyInBorrow;
import common.Member;
import common.MyData;
import common.SendMail;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  private static final float MILLISECONDS_PER_DAY = 86400000;
//Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
	private MyDB db;
	private ServerController serverCont;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
 * @throws IOException 
 * @throws SQLException 
   */
  public EchoServer(String sqlUser, String sqlPass, int port) throws IOException, SQLException 
  {
    super(port);
	db = new MyDB(sqlUser,sqlPass);
	listen();
	initialize();
  }
  /**
   * 
   * @author Ariel
   * @throws SQLException
   */
  private void initialize() throws SQLException {
	  serverCont = new ServerController(db);
	  db.updateWithExecute("UPDATE members set loggedin=0");
	  Date now = Calendar. getInstance().getTime(), nextMidnight = new Date(now.getYear(),now.getMonth(),now.getDate()+1,0,0,0);
	  AutomatedActions(nextMidnight);
  }

  private void AutomatedActions(Date executeTime) throws SQLException {
	  //Automated action functions here

	  checkLateReturns();
	  CancleResevion();

	  //
	  Timer timer = new Timer();
	  TimerTask task = new TimerTask() {
		@Override
		public void run() {
			executeTime.setDate(executeTime.getDate()+1); // next midnight
			try {
				AutomatedActions(executeTime);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	timer.schedule(task, executeTime);
  }
  
  //Instance methods ************************************************
  /**
   * Author feldman
   * function for checking if users late in return books
   * and check if there is one day to return book - if there is its send  reminder Email to the user.
   * if user late in return he freeze until he return the book.
   * @throws SQLException
   */
  private void checkLateReturns() throws SQLException {
	  String MyQuery = "SELECT * FROM oblg3.copy_in_borrow INNER JOIN borrows ON copy_in_borrow.borrowID=borrows.borrowID";
		ResultSet rs = db.select(MyQuery);
		while (rs.next()) 
		{
			java.util.Date today = new java.util.Date();
			Timestamp returnDate = rs.getTimestamp("returnDate");
			int memberID = rs.getInt("memberID");
			boolean mailSent = rs.getBoolean("mailSent");
			int borrowID = rs.getInt("borrows.borrowID");
			Book book = serverCont.getBook(rs.getInt("bookID"));
			java.util.Date returnDateUTIL = new java.util.Date(returnDate.getTime());
			//int days = (int) daysBetween(returnDateUTIL,borrowDateUTIL);
			float days = daysBetween(today, returnDateUTIL);
			if (days>0)
			{
				String query1 = "UPDATE members SET status = 'FREEZE' WHERE id= '"+memberID+"'";
				db.updateWithExecute(query1);
				String lateQuery = "UPDATE member_cards SET lateReturns = lateReturns + ? WHERE userID= '"+memberID+"'";
				PreparedStatement stmt3 = db.update(lateQuery);
				stmt3.setInt(1, 1);
				stmt3.executeUpdate();

				String checkQuery = "SELECT * FROM violations WHERE lateOn='"+borrowID+"'";
				ResultSet rs4 = db.select(checkQuery);
				if(!db.hasResults(rs4)) {
				 	java.util.Date today1 = new java.util.Date();
				 	Timestamp sqlDate = new Timestamp(today1.getTime());
					String query = "INSERT INTO violations(memberID,ViolationDate,description,violationType,lateOn) "
							+ "VALUES(?,?,?,?,?)";
					PreparedStatement ps = db.update(query);
					ps.setInt(1, memberID);
					ps.setTimestamp(2,sqlDate);
					ps.setString(3, "late return");
					ps.setInt(4, 0);
					ps.setInt(5, borrowID);
					ps.executeUpdate();
					Boolean timeLate=	rs.getBoolean("3timesLate");
					String latesQuery = "SELECT lateReturns FROM member_cards WHERE userID='"+memberID+"'";
					ResultSet rs2 = db.select(latesQuery);
					if(db.hasResults(rs2)) {
						 int latesReturn = rs2.getInt("lateReturns");
							if(latesReturn==3 &&timeLate == false)
							{
								
									String messageQuery = "INSERT INTO messages(sender,subject,reciever,content) VALUES(?,?,?,?)";
									PreparedStatement ps1 = db.update(messageQuery);
									ps1.setInt(1, 0);
									ps1.setString(2, "3 Late in book returns");
									ps1.setInt(3,memberID);
									ps1.setString(4, "This issue was brought to the managers's attention.");
									ps1.executeUpdate();
								String latesReurnQuery = "UPDATE borrows SET 3timesLate = ? WHERE borrowID= '"+borrowID+"'";
								PreparedStatement stmt4 = db.update(latesReurnQuery);
								stmt4.setBoolean(1, true);
								stmt4.executeUpdate();
								serverCont.writeMsg(0, 1, "3rd Late Book Return",memberID +" was late to return books for the 3rd time.\nHe is now frozen and waiting on a manager's action.", "3Late", memberID);
							}
								serverCont.writeMsg(0, memberID, "Late Book Return","Your account has been frozen due to : Late Book Return.");
					}
				}

			}
			if(getHourDiff(returnDateUTIL,today)<=24 && !mailSent)
			{
				String emailQuery = "SELECT emailAddress, firstName FROM member_cards WHERE userID='"+memberID+"'";
				ResultSet rs2 = db.select(emailQuery);
				if(db.hasResults(rs2)) {
					String memberMail = rs2.getString("emailAddress");
					String userName = rs2.getString("firstName");
					String msg = "Hello "+userName+"\n\n This is a reminder regarding the book\n"+ book.getBookName() +"\nRemember to return it in time!\nReturn date :"+returnDateUTIL.toString()+".\n\nGood day.";
					new SendMail(memberMail,"Return Book Reminder",msg);
					serverCont.writeMsg(0,memberID, "Reminder", msg);
					String sendMailQuery = "UPDATE borrows SET mailSent = ? WHERE borrowID= "+borrowID;
					PreparedStatement stmt3 = db.update(sendMailQuery);
					stmt3.setBoolean(1, true);
					stmt3.executeUpdate();
			}

		}
		}
		rs.close();
}

  private long getHourDiff(Date d1, Date d2) {
	  return TimeUnit.HOURS.convert(d1.getTime()-d2.getTime(), TimeUnit.MILLISECONDS);
  }
  /**
   * @Author Feldman
   * function get two dates and return the difference between them.
   * @param d2 - Date
   * @param d1 - Date
   * @return the days between two dates (in days!)
   */
	public static long getDifferenceDays(java.util.Date d1,java.util.Date d2) {
	    long diff = d2.getTime() - d1.getTime();
	    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
	private float daysBetween(java.util.Date one, java.util.Date date) { 
		float difference = (one.getTime()-date.getTime())/MILLISECONDS_PER_DAY; 
		return difference;
	}
	
	/**
	 *@Author Feldman
	 * function for check if user order don't take the book he order in two days
	 * if he don't take the book the order is delete and the book is move to the next user in the queue of orders.
	 * when the book move to another user mail send to him.
	 * @throws SQLException
	 */
	 private void CancleResevion() throws SQLException
	 {
		 	java.util.Date today = new java.util.Date();
		 	Timestamp sqlDate = new Timestamp(today.getTime());
			String MyQuery = "SELECT * FROM book_reservations WHERE arrivedDate is not null ";
			ResultSet rs = db.select(MyQuery);
			while (rs.next()) 
			{
				int memberID = rs.getInt("memberID");
				int bookID = rs.getInt("bookID");
				Timestamp arrivedDate = rs.getTimestamp("arrivedDate");
				java.util.Date arrivedDateUTIL = new java.util.Date(arrivedDate.getTime());
				//int days = (int) daysBetween(today,arrivedDateUTIL);
				//int days = today.compareTo(arrivedDateUTIL);
				float days = daysBetween(today, arrivedDateUTIL);
				if(days>2)
				{
					String deleteQuery = "DELETE FROM book_reservations WHERE memberID= ? AND bookID = ?";
					PreparedStatement stmt1 = db.update(deleteQuery);
					stmt1.setInt(1, memberID);
					stmt1.setInt(2, bookID);
					stmt1.executeUpdate();
					ResultSet rs4 = db.select("SELECT memberID from book_reservations where bookID='"+bookID+"' and arrivedDate is null order by orderDate limit 1");
					if (db.hasResults(rs4))
						memberID=rs4.getInt("memberID");
					String query1 = "UPDATE book_reservations SET arrivedDate = ? WHERE bookID= '"+bookID+"' and arrivedDate is null order by orderDate limit 1";
					PreparedStatement stmt11 = db.update(query1);
					stmt11.setTimestamp(1, sqlDate);
					stmt11.executeUpdate();

					String emailQuery = "SELECT emailAddress, firstName FROM member_cards WHERE userID='"+memberID+"'";
					ResultSet rs2 = db.select(emailQuery);
					if(db.hasResults(rs2)) {
					String memberMail = rs2.getString("emailAddress");
					String userName = rs2.getString("firstName");
					String bookNameQuery = "SELECT bookName FROM books WHERE bookID='"+bookID+"'";
					ResultSet rs3 = db.select(bookNameQuery);
					if(db.hasResults(rs3)) {
					String bookName = rs3.getString("bookName");

					String msg = "Hello "+userName+"\n\n Your Reservasion is resdy for "+bookName+"\n please come to take it";
					new SendMail(memberMail,"Reservesion is ready",msg);
					}
					}
				}
			}
	 }

//Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * 
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object o, ConnectionToClient client)
  {
	  try 
		{
	  		MyData data = (MyData) o;
	  		    switch (data.getAction()) {
	  		    case "deleteMsg":
	  		    	client.sendToClient(serverCont.removeMsg(data));
	  		    	break;
		  		  case "notify_graduation":
		  		    	client.sendToClient(serverCont.notifyGraduation((Integer)data.getData("id")));
		  		    		break;
		  		case "getClosedReturn":
	  				client.sendToClient(serverCont.getClosedReturnBook(data));
	  				break;
		  		case "deleteBook":
	  				client.sendToClient(serverCont.deleteBook(data));
	  				break;
	  		  	case "newBorrowRequest":
  		    	client.sendToClient(serverCont.writeNewBorrow(data));
  		    	break;
		 		  case "BorrowOrderRequest":
		  		    	client.sendToClient(serverCont.orderBorrowRequest(data));
		  		    	break;
	  		  	case "addNewBook":
	  		    	client.sendToClient(serverCont.addNewBook(data));
	  		    	break;
	  		  	case "updateBook":
  		    	MyData save=serverCont.updateBook(data);
  		    	client.sendToClient(save);
	  			break;
	  			case "getActivityReports":
		  			client.sendToClient(serverCont.getActivityReportDates());
		  			break;
	  		    case "Activity Report":
	  		    case "Borrow Report":
	  		    case "Late Return Report":
	  		    	client.sendToClient(serverCont.report(data));
	  		    	break;
	  		  case "history":
	  		    	client.sendToClient(serverCont.history((Integer)data.getData("id")));
	  		    	break;
	  		  case "BorrowToExtend":
	  		    	client.sendToClient(serverCont.updateExtension((CopyInBorrow)data.getData("TheCopyInBorrow"),(String)data.getData("requester"),(Timestamp)data.getData("fromPicker")));
	  		    	break;
	  		  case "getCopiesInBorrow":
	  		    	MyData copiesInBorrow = new MyData("copiesInBorrow");
	  		    	copiesInBorrow.add("copies", serverCont.getCopiesInBorrow((ArrayList<Borrow>)data.getData("borrows")));
	  		    	client.sendToClient(copiesInBorrow);
	  		    	break;
		  		case "copyToReturn":
	  				client.sendToClient(serverCont.returnCopy(data));
	  				break;
		  		case "orderBooks":
	  				client.sendToClient(serverCont.getOrderBooks(data));
	  				break;
	  		    case "addViolation":
	  		    	client.sendToClient(serverCont.addViolation(data));
	  		    	break;
	  		    case "search_member":
	  		    	client.sendToClient(serverCont.searchMember((Integer)data.getData("id")));
	  		    	break;
	  		    case "createUser":
	  		    	client.sendToClient(serverCont.createUser(data));
	  		    	break;
	  		  case "searchBook":
	  				client.sendToClient(serverCont.searchBook((ArrayList<String>)data.getData("genres"),(String)data.getData("bookName"),(String)data.getData("authorsName"), (ArrayList<String>)data.getData("freeText")));
	  				break;
	  		case "tableOfContents":
  				client.sendToClient(serverCont.getTableOfContents(data));
  				break;
	  		case "returnBook":
  				client.sendToClient(serverCont.getReturnBooks(data));
  				break;
	  		    case "login":
	  		    	System.out.println((Integer)data.getData("id")+" had logged in (IP:"+ client.getInetAddress().toString()+")");
	  		    	serverCont.updateIP(client.getInetAddress().toString(), (Integer)data.getData("id"));
	  		    	client.sendToClient(serverCont.login(data));
	  		    	break;
	  		  case "saveInfo":
	  			  Member member = (Member)data.getData("member");
	  		    		client.sendToClient(serverCont.saveInfo(member.getID(),member.getUserName(),member.getMemberCard().getFirstName(),member.getMemberCard().getLastName(),member.getPassword(),member.getMemberCard().getEmailAddress(),member.getMemberCard().getPhoneNumber(),member.getStatus().toString()));
	  		    	break;
	  		    case "orderBook":
	  		    	client.sendToClient(serverCont.orderBook(((Integer)data.getData("id")), (Book)data.getData("book")));
	  		    	break;
	  		    case "getBooks":
	  		    	MyData books = new MyData("getBooks");
	  		    	books.add("books", serverCont.getAllBooks());
	  		    	client.sendToClient(books); // TODO: returns all the books.. replace this with search result!
	  		    	break;
	  		    case "msgRead":
	  		    	client.sendToClient(serverCont.msgRead(true,(Integer)data.getData("msgID")));
	  		    	break;
	  		    case "3Late":
	  		    	client.sendToClient(serverCont.msgAction(data));
	  		    	break;
	  		    case "getMessages":
	  		    	client.sendToClient(serverCont.getMessages((int)data.getData("member"),(String)data.getData("rank")));
	  		    	break;
	  		    case "client_stopped":
	  		    case "logout":
	  		    	serverCont.setLoggedIn(false, (Integer)data.getData("id"));
	  		    	System.out.println(data.getData("id") +" has logged out");
	  		    	client.sendToClient(data);
	  		    	break;
	  			default:
	  					client.sendToClient(o);
	  		    }
		} catch (SQLException e) {e.printStackTrace();}
		catch (IOException e) {
			System.out.println("Failed to send to client...");
			e.printStackTrace();
			}
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
}
