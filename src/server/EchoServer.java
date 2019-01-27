package server;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import client.MyData;
import common.Borrow;
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
   */
  public EchoServer(int port) 
  {
    super(port);
	db = new MyDB();
	try {
		db.updateWithExecute("UPDATE members set loggedin=0");
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	serverCont = new ServerController(db);
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
	  		  case "newBorrowRequest":
	  		    	client.sendToClient(serverCont.writeNewBorrow(data));
	  		    	break;
	  		  case "addNewBook":
	  		    	client.sendToClient(serverCont.addNewBook(data));
	  		    	break;
	  		case "updateBook":
  		    	MyData save=serverCont.updateBook(data);
  		    	client.sendToClient(save);
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
	  		    	MyData extensionResult = serverCont.updateExtension((Borrow)data.getData("TheBorrow"));
	  		    	extensionResult.add("UpdatedBorrow", data.getData("TheBorrow"));
	  		    	client.sendToClient(extensionResult);
	  		    	break;
	  		  case "getCopiesInBorrow":
	  		    	MyData copiesInBorrow = new MyData("copiesInBorrow");
	  		    	copiesInBorrow.add("copies", serverCont.getCopiesInBorrow((ArrayList<Borrow>)data.getData("borrows")));
	  		    	client.sendToClient(copiesInBorrow);
	  		    	break;
		  		case "copyToReturn":
	  				client.sendToClient(serverCont.returnCopy(data));

	  				break;
	  		    case "search_member":
	  		    	client.sendToClient(serverCont.searchMember((Integer)data.getData("id")));
	  		    	break;
	  		    case "createUser":
	  		    	client.sendToClient(serverCont.createUser(data));
	  		    	break;
	  		  case "searchBook":
	  				client.sendToClient(serverCont.searchBook((ArrayList<String>)data.getData("genres"),(String)data.getData("bookName"),(String)data.getData("authorsName")));
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
	  			  MyData normalSave = serverCont.saveInfo((Integer)data.getData("id"),(String)data.getData("firstName"),(String)data.getData("lastName"),(String)data.getData("password"),(String)data.getData("email"),(String)data.getData("phone"));
	  			  if (normalSave.getAction().equals("success") && data.getData().containsKey("admin")) { // Member Management saveInfo
	  				// TODO: add change log
	  				  client.sendToClient(serverCont.saveInfoAdmin((Integer)data.getData("id"), (String)data.getData("username"), (String)data.getData("status")));
	  		    	} else // Member Area saveInfo
	  		    		client.sendToClient(normalSave);
	  		    	break;
	  		    case "orderBook":
	  		    	client.sendToClient(serverCont.orderBook(((Integer)data.getData("id")), (Integer)data.getData("bookID")));
	  		    	break;
	  		    case "getBooks":
	  		    	MyData books = new MyData("getBooks");
	  		    	books.add("books", serverCont.getAllBooks());
	  		    	client.sendToClient(books); // TODO: returns all the books.. replace this with search result!
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
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    
    try {
    	EchoServer sv = new EchoServer(Integer.parseInt(args[0]));
      sv.listen(); //Start listening for connections
    } 
    catch (IOException e) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    } catch (IndexOutOfBoundsException e) {
		System.out.println("Enter port.");
		System.exit(1);
	}
  }
}
//End of EchoServer class
