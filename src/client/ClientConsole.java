package client;

import java.io.IOException;

import client.controllers.ReaderController;
import common.ChatClient;
import common.CommonIF;
import common.Member;
import common.MyData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;
 * @author OBL Group 3
 * @version July 2000
 */
public class ClientConsole extends Application implements CommonIF 
{
  //Class variables *************************************************
	@FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    protected class GraduationForm {
  	  @FXML
  	    private TextField idField;
  	  @FXML
  	    void notify(MouseEvent event) {
  		  MyData data = new MyData("notify_graduation");
  		  data.add("id", Integer.parseInt(idField.getText()));
  		  send(data);
  		  newAlert(AlertType.INFORMATION, null, fromServer.getAction(), (String)fromServer.getData("message"));
  	    }
    }
  /**
   * The instance of the client that created this ConsoleChat.
   */
  protected ChatClient client;
  protected MyData fromServer;
  private int userid=-1;
  @FXML
  void connect(MouseEvent event) {
      try {
		client= new ChatClient(ipField.getText(), Integer.parseInt(portField.getText()), this);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmls/gui.fxml"));
		loader.setController(new ReaderController(this));
		((Stage)ipField.getScene().getWindow()).close();
		Stage stage = new Stage();
		stage.setScene(new Scene(loader.load()));
		stage.setTitle("OBL G3");
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (userid!=-1) {
					MyData data = new MyData("client_stopped");
					data.add("id", userid);
					send(data);
					}
					try {
						client.closeConnection();
					} catch (IOException e) {e.printStackTrace();}
					Platform.exit();
			        System.exit(0);
			}
		});
      }
	    catch (IOException e) 
	    {
	    	newAlert(AlertType.ERROR, null, "Connection error", "Could not connect to server.");
	    }
  	catch (NumberFormatException e) {
  		newAlert(AlertType.ERROR, null, "Wrong format", "Numbers only for port.");
  		portField.clear();
  	}
  }
  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
 * @throws InterruptedException 
   */
  public void send(Object o)
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
	  fromServer = (MyData) message;
	  System.out.println("Client received: "+ fromServer.getAction() +": "+ fromServer.getData());
	  if (fromServer.getAction().equals("login_approved"))
			  this.userid = ((Member)fromServer.getData("MemberLoggedIn")).getID();
	  else if (fromServer.getAction().equals("successful_logout"))
		  this.userid = 0; // back to default, forget the user.
  }
  public MyData getFromServer() {
	  return fromServer;
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client GUI.
   * @throws IOException
   */
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmls/connectToServerGUI.fxml"));
		Scene scene = new Scene(loader.load());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("OBL G3");
	primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		@Override
		public void handle(WindowEvent event) {
				Platform.exit();
		        System.exit(0);
		}
	});
	}
	/**
	 * This method will create a new pop-up alert in javafx gui
	 * @param type - Alert Type
	 * @param title Alert title
	 * @param header Alert Header
	 * @param content Alert Content
	 * @return 
	 */
	public static ButtonType newAlert(AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title == null ? "OBL - Group 3" : title);
		alert.setHeaderText(header);
		alert.setContentText(content);
			return alert.showAndWait().get();
	}
	public static void main(String[] args)
	{
		launch(args);
	}

}