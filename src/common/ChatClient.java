// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package common;

import java.io.IOException;

import ocsf.client.AbstractClient;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
	final private int delay=3; // how long will the client wait for a server response
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  CommonIF clientUI; 
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, CommonIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param o The message from the server.
   */
  public synchronized void handleMessageFromServer(Object o) 
  {
    clientUI.handle(o);
    notify();
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param o The message from the UI.    
   */
  public synchronized void handleMessageFromClientUI(Object o)  
  {
    try
    {
    	sendToServer(o);
    	wait(delay*1000); // if 3 seconds passed and wasn't awakened : nothing came back from the server
    }
    catch(Exception e)
    {
    	MyData error = new MyData("error");
    	error.add("msg", "Could not send "+ o+" to server.  Terminating client.");
      clientUI.handle(error);
      e.printStackTrace();
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
