package server;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import client.MyData;
import common.Student;
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
	private ReaderServerController readerCont;
  
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
		db.update("UPDATE users set loggedin=0").executeUpdate();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	readerCont = new ReaderServerController();
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
	  		    case "search_book":
	  				client.sendToClient(readerCont.search(db,data));
	  				break;
	  		    case "login":
	  		    	System.out.println((Integer)data.getData("id")+" had logged in (IP:"+ client.getInetAddress().toString()+")");
	  		    	readerCont.updateIP(db, client.getInetAddress().toString(), (Integer)data.getData("id"));
	  		    	client.sendToClient(readerCont.login(db, data));
	  		    	break;
	  		    case "client_stopped":
	  		    	readerCont.setLoggedIn(db, false, (String)data.getData("ip"));
	  		    	break;
	  		    case "logout":
	  		    	readerCont.setLoggedIn(db, false, (Integer)data.getData("id"));
	  		    	System.out.println(data.getData("id") +" has logged out");
	  		    	client.sendToClient(new MyData("successful_logout"));
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
