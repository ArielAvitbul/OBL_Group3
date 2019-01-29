package server;
import java.io.IOException;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
public class serverGUI extends Application
{
	private EchoServer sv=null;
	 @FXML
	    private TextField sqlUserField;

	    @FXML
	    private AnchorPane mainPane;

	    @FXML
	    private PasswordField sqlPassField;

	    @FXML
	    private TextField portField;
    @FXML
    void launchServer(MouseEvent event) throws Exception {
    	try {
	    	sv = new EchoServer(sqlUserField.getText(),sqlPassField.getText(),Integer.valueOf(portField.getText()));
	      sv.listen(); //Start listening for connections
  		mainPane.getChildren().clear();
  		Label label = new Label("Server connected.");
  		label.setLayoutX(50);
  		label.setLayoutY(mainPane.getHeight()/2);
  		Button b = new Button("Launch External System");
  		b.setOnMouseReleased(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				
			}
		});
  		mainPane.getChildren().add(label);
	    }
	    catch (IOException e) 
	    {
	    	newAlert(AlertType.ERROR, null, "Server error", "Could not listen for clients!");
	    }
    	catch (NumberFormatException e) {
    		newAlert(AlertType.ERROR, null, "Wrong format", "Numbers only.");
    		portField.clear();
    	}
    }
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client GUI.
 * @throws IOException 
   */
	@Override
	public void start(Stage primaryStage) throws IOException {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("serverGUI.fxml"));
			AnchorPane root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
						Platform.exit();
				        System.exit(0);
				}
			});
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
