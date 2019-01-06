package client.controllers;

import java.io.IOException;
import java.util.HashMap;

import client.ClientConsole;
import client.MyData;
import client.MyImage;
import common.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class ReaderController {
	private ClientConsole cc;
	private HashMap<String,MyImage> buttons;
	private HashMap<String,Object> controllers;
	public Label welcomeMsg;
	public ReaderController(ClientConsole cc) {
		this.cc = cc;
	}
	@FXML
    void initialize() {
		buttons = new HashMap<>();
		controllers = new HashMap<>();
		buttons.put("search", new MyImage("client/images/buttons/searchBook.jpg", 402, 192, e->setBottom("search")));
		topPane.getChildren().add(buttons.get("search"));
    }
	@FXML
    private AnchorPane topPane;
    @FXML
    private PasswordField passField;
    @FXML
    private Button btnLogin;
    @FXML
    private TextField loginIdField;
    @FXML
    private ImageView loginPicture;
    @FXML
    private ImageView loginButton;

    void setBottom(String fxml) {
    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("client/fxmls/"+fxml+".fxml"));
    	if (!controllers.containsKey(fxml)) {
    		switch (fxml) {
    		case "search":
    			controllers.put(fxml, new SearchController());
    			break;
    			default: // unrecognized fxml
    				ClientConsole.newAlert(AlertType.ERROR, null, "Unrecognized FXML", "Hey, make sure you wrote the write fxml name and handled it correctly.");
    				System.exit(1);
    		}
    	}
    	loader.setController(controllers.get(fxml));
		try {
			((BorderPane)topPane.getScene().getRoot()).setCenter(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /*This function handles a Reader login request and sends it to server if its valid
     * input:none
     * output: successful or unsuccessful login reaction 
     */
    @FXML
    private void submitLogin (MouseEvent event)
    {
    	if(isValidLoginFields())
    	{
    		MyData login = new MyData ("login");
    		login.add("name", loginIdField.getText());
    		login.add("password", passField.getText());
    		try {
    				cc.send(login);
    			}
    		catch (InterruptedException e)
    			{
    				ClientConsole.newAlert(AlertType.ERROR, null,"OBL System Error","Login denied!");
    			}
    		String result = cc.getFromServer().getAction();
    		if (result.equals("login_approved")) {
    			buttons.put("logout",new MyImage("client/images/buttons/logout.png", loginButton.getLayoutX(),loginButton.getLayoutY(), e->submitLogout(e)));
    			topPane.getChildren().add(buttons.get("logout"));
    			topPane.getChildren().add(welcomeMsg = new Label("Welcome, "+cc.getFromServer().getData("MemberLoggedIn")));
    			welcomeMsg.setLayoutX(loginPicture.getLayoutX());
    			welcomeMsg.setLayoutY(loginPicture.getLayoutY());
    			// removing login items
    			topPane.getChildren().remove(loginButton);
    			topPane.getChildren().remove(loginIdField);
    			topPane.getChildren().remove(passField);
    			topPane.getChildren().remove(loginPicture);
    		} else if (result.equals("login_failed")) {
    			ClientConsole.newAlert(AlertType.INFORMATION, null, "Login failed!", (String)cc.getFromServer().getData("reason"));
    		}
    	}
    	else {
    		ClientConsole.newAlert(AlertType.INFORMATION, null, "Empty fields", "One or more of your fields were empty");
    		}
    }
    
    
    /* This function checks if login fields are empty after clicking the login button
     * input: none
     * output: T/F
     */
	private boolean isValidLoginFields() {
		return !(loginIdField.getText().isEmpty() && passField.getText().isEmpty());
	}
	
	
	/* This method handles a user logout
	 * input: none
	 * output: none
	 */
		private void submitLogout(MouseEvent event) {
			topPane.getChildren().remove(welcomeMsg);
			topPane.getChildren().remove(buttons.get("logout"));
			topPane.getChildren().add(passField);
			passField.clear();
			topPane.getChildren().add(loginIdField);
			loginIdField.clear();
			topPane.getChildren().add(loginButton);
			topPane.getChildren().add(loginPicture);
			
			try {
				MyData data = new MyData("logout");
				Member member = (Member) cc.getReader();
				data.add("name", member.getUserName());
				cc.send(data);
				
			} catch (InterruptedException e) {e.printStackTrace();}
			cc.forgetReader();
   }
	private class SearchController {

		@FXML
	    private Button searchButton;

	    @FXML
	    private TextField authorField;

	    @FXML
	    private TextArea freetextField;

	    @FXML
	    private TextField nameField;

	    @FXML
	    private TextField genreField;

	    @FXML
	    void submitSearch(ActionEvent event) {
	    	MyData data = new MyData("search_book");
	    	if (!nameField.getText().isEmpty())
	    	data.add("name", nameField.getText());
	    	if (!authorField.getText().isEmpty())
	    	data.add("author", authorField.getText());
	    	if (!genreField.getText().isEmpty())
	    	data.add("genre", genreField.getText());
	    	if (!freetextField.getText().isEmpty())
	    	data.add("freetext", freetextField.getText());
	    	try {
	    	cc.send(data);
	    	}
			catch (InterruptedException e)
			{
				ClientConsole.newAlert(AlertType.ERROR, null, "Search failure", "Something went wrong..");
			}
	}
	}
}
