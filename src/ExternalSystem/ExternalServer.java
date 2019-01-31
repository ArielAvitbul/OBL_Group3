package ExternalSystem;

import java.io.IOException;
import java.util.Optional;

import common.ChatClient;
import common.CommonIF;
import common.MyData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
public class ExternalServer extends Application implements CommonIF 
{
  //Class variables *************************************************
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
  @FXML
  private TextField ipField;
  @FXML
  private TextField portField;
  @FXML
  void connect(MouseEvent event) {
      try {
		client= new ChatClient(ipField.getText(), Integer.parseInt(portField.getText()), this);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("graduationForm.fxml"));
		loader.setController(this.new GraduationForm());
		Stage stage = (Stage)ipField.getScene().getWindow();
		stage.setScene(new Scene(loader.load()));
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
  }
  public MyData getFromServer() {
	  return fromServer;
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client GUI.
   */
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("externalSystem.fxml"));
			AnchorPane root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Graduation System");
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
						Platform.exit();
				        System.exit(0);
				}
			});
		}
		catch(IOException exception) 
	    {
	      System.out.println("Error: Can't setup connection! Terminating client.");
	      exception.printStackTrace();
	      System.exit(1);
	    }
	}
	public static Optional <ButtonType> newAlert(AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title == null ? "OBL - Group 3" : title);
		alert.setHeaderText(header);
		alert.setContentText(content);
			return alert.showAndWait();
	}
	public static void main(String[] args)
	{
		launch(args);
	}
}
//End of ConsoleChat class
