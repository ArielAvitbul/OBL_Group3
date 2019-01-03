package application;

import java.io.IOException;
import java.util.Optional;

import application.controllers.ReaderController;
import common.ChatClient;
import common.CommonIF;
import common.Student;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole extends Application implements CommonIF 
{
  //Class variables *************************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
	private Student student;
  ChatClient client;
  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
 * @throws InterruptedException 
   */
  public synchronized Student getStudent() throws Exception{
	  return student;
  }
  public void forgetStudent() {
	  this.student = null;
  }
  public void send(Object o) throws InterruptedException 
  {
	  
        client.handleMessageFromClientUI(o);
  }

  /**
   * This method overrides the method in the CommonIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  @Override
  public void handle(Object message)
  {
	  MyData data = (MyData) message;
	  switch (data.getAction()) {
	  case "login approved":
		  ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Good job", "login approved");
	  }
	  		
		  System.out.println("Client received: "+ data.getAction() +": "+ data.getData());
		  // TODO: work on client's response to server messages.
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client GUI.
   */
	@Override
	public void start(Stage primaryStage) {// bottom size 900 460
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
		    client= new ChatClient(getParameters().getRaw().get(0), Integer.parseInt(getParameters().getRaw().get(1)), this);
			loader.setController(new ReaderController(this));
			BorderPane root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch(IOException exception) 
	    {
	      System.out.println("Error: Can't setup connection! Terminating client.");
	      exception.printStackTrace();
	      System.exit(1);
	    } catch (IndexOutOfBoundsException e) {
			newAlert(AlertType.ERROR, null, "No IP/Port", "Please specify IP & Port in this order");
			System.exit(1);
		}
		
	}
	public void showBottom(BorderPane bp, String fxml, Object controller) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml+".fxml"));
		loader.setController(controller);
		AnchorPane root = loader.load();
		bp.setCenter(root);
	}
	public static Optional <ButtonType> newAlert(AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title == null ? "OBL - Group 3" : title);
		alert.setHeaderText(header);
		alert.setContentText(content);
			return alert.showAndWait();
	}
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
		public void stop() throws Exception {
			super.stop();
			client.closeConnection();
		}
}
//End of ConsoleChat class
