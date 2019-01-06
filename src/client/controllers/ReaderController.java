package client.controllers;

import java.io.IOException;
import java.util.HashMap;

import client.ClientConsole;
import client.MyButton;
import client.MyData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class ReaderController {
	private ClientConsole cc;
	private HashMap<String,MyButton> buttons;
	private HashMap<String,Object> controllers;
	public Label welcomeMsg;
	public ReaderController(ClientConsole cc) {
		this.cc = cc;
	}
	@FXML
    void initialize() {
		buttons = new HashMap<>();
		controllers = new HashMap<>();
		buttons.put("search", new MyButton(topPane,"images/buttons/searchBook.jpg", 402, 192, e->setBottom("search")));
		buttons.put("login",new MyButton(topPane,"images/buttons/login.jpg", 700, 144, e->submitLogin(new ActionEvent())));
    }
	@FXML
    private AnchorPane topPane;
    @FXML
    private ImageView topImage;
    @FXML
    private PasswordField passField;
    @FXML
    private Button btnLogin;
    @FXML
    private TextField loginIdField;

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
    private void submitLogin (ActionEvent event)
    {
    	if(isValidLoginFields())
    	{
    		MyData login = new MyData ("login");
    		login.add("id", loginIdField.getText());
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
    			setTopPaneAfterLogin(); // clears the login area
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
	
	
	/* This method handles pane changing to a logged in member view
	 * input: none
	 * output: none
	 */
	private void setTopPaneAfterLogin() {
		MyButton toRemove = buttons.get("login");
		ImageView imgToRemove = toRemove.getImage();
		topPane.getChildren().remove(toRemove);
		topPane.getChildren().remove(imgToRemove);
		passField.setVisible(false);
		btnLogin.setVisible(false);
		buttons.remove("login");
		loginIdField.setVisible(false);
		String newImageURL = "client/images/afterLogin.jpeg";
		topImage.setImage(new Image(newImageURL));
		buttons.put("logout",new MyButton(topPane, null, 704,105, e->submitLogout(new ActionEvent())));
		buttons.get("logout").setPrefSize(106, 32);
		welcomeMsg = new Label("Welcome, "+cc.getFromServer().getData("MemberLoggedIn"));
		topPane.getChildren().add(welcomeMsg);
		welcomeMsg.setPrefSize(102, 32);
		welcomeMsg.setLayoutX(705);
		welcomeMsg.setLayoutY(61);
		welcomeMsg.setVisible(true);
	}
	
	
	/* This method handles a user logout
	 * input: none
	 * output: none
	 */
		private void submitLogout(ActionEvent actionEvent) {
			cc.getFromServer().getData().remove("MemberLoggedIn");
			String newImageURL = "client/images/beforeLogin.jpeg";
			topImage.setImage(new Image(newImageURL));
			topPane.getChildren().remove(welcomeMsg);
			buttons.get("logout").setVisible(false);
			passField.setVisible(true);
			passField.clear();
			loginIdField.setVisible(true);
			loginIdField.clear();
			btnLogin.setVisible(true);	
			buttons.put("login",new MyButton(topPane,"images/buttons/login.jpg", 700, 144, e->submitLogin(new ActionEvent())));
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
