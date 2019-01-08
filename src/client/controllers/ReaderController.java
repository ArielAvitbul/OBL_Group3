package client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import client.ClientConsole;
import client.MyData;
import client.MyImage;
import common.Member;
import common.MemberCard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class ReaderController {
	private ClientConsole cc;
	private HashMap<String,Object> controllers;
	public Label welcomeMsg;
	public ReaderController(ClientConsole cc) {
		this.cc = cc;
	}
	@FXML
    void initialize() {
		controllers = new HashMap<>();
    }
	@FXML
    private ImageView searchBook;
	@FXML
    private HBox MenuBox;
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
    /* This method removes objects from a pane */
	private void removeFrom(Pane pane, ArrayList<String> names) {
		ArrayList<Node> list = new ArrayList<>();
		for (Node n : pane.getChildren())
			if (names.contains(n.getId()))
				list.add(n);
		pane.getChildren().removeAll(list);
	}
    /* This method removes an object from a pane */
	private boolean removeFrom(Pane pane, String name) {
		for (Node n : pane.getChildren()) {
			if (name.equals(n.getId())) {
				pane.getChildren().remove(n);
				return true;
			}
		}
		return false;
	}
    void addTo(Pane pane, Node button, boolean enteredexit) {
    	pane.getChildren().add(button);
    	if (enteredexit) {
    	button.setOnMouseEntered(e-> mouseEntered(e));
    	button.setOnMouseExited(e->mouseExited(e));
    	}
    }
    @FXML
    protected void mouseEntered(MouseEvent ev) {
    	//ImageView button = ((ImageView)ev.getSource());
    	//	button.setImage(new Image("client/images/buttons/"+button.getId()+"Pressed.jpg"));
    	ColorAdjust effect = new ColorAdjust();
    	effect.setBrightness(-0.1);
     ((ImageView)ev.getSource()).setEffect(effect);
    }
    @FXML
    protected void mouseExited(MouseEvent ev) {
    	((ImageView)ev.getSource()).setEffect(null);
    }
    @FXML
    void setBottom(MouseEvent ev) { // button name must be equal to the fxml name
    	String fxml = ((ImageView)ev.getSource()).getId();
    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("client/fxmls/"+fxml+".fxml"));
    	if (!controllers.containsKey(fxml)) {
    		switch (fxml) {
    		case "searchBook":
    			controllers.put(fxml, new SearchController());
    			break;
    		case "viewProfile":
    			controllers.put(fxml, new ViewProfileController());
    			break;
    			default: // unrecognized fxml
    				ClientConsole.newAlert(AlertType.ERROR, null, "Unrecognized FXML", "Hey, make sure you wrote the write fxml name and handled it correctly.");
    				//System.exit(1);
    		}
    	}
    	loader.setController(controllers.get(fxml));
		try {
			((BorderPane)topPane.getScene().getRoot()).setCenter(loader.load());
		} catch (IOException e) {e.printStackTrace();}
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
    			addTo(topPane,new MyImage("logout","client/images/buttons/logout.png", loginButton.getLayoutX(),loginButton.getLayoutY(), e->submitLogout(e)),true);
    			addTo(topPane,welcomeMsg = new Label("Welcome, "+cc.getFromServer().getData("MemberLoggedIn")),true);
    			welcomeMsg.setId("welcomeMsg");
    			welcomeMsg.setLayoutX(loginPicture.getLayoutX());
    			welcomeMsg.setLayoutY(loginPicture.getLayoutY());
    			removeFrom(topPane,new ArrayList<>(Arrays.asList("loginButton","loginIdField","passField","loginPicture")));
    			addTo(MenuBox, new MyImage("viewProfile","client/images/buttons/viewProfile.jpg",e1->setBottom(e1)),true);
    		} else if (result.equals("login_failed"))
    			ClientConsole.newAlert(AlertType.INFORMATION, null, "Login failed!", (String)cc.getFromServer().getData("reason"));
    	}
    	else
    		ClientConsole.newAlert(AlertType.INFORMATION, null, "Empty fields", "One or more of your fields were empty");
    }
    
		private void submitLogout(MouseEvent event) {
			removeFrom(topPane, new ArrayList<>(Arrays.asList("welcomeMsg","logout")));
			addTo(topPane,passField,false);
			passField.clear();
			addTo(topPane,loginIdField,false);
			loginIdField.clear();
			addTo(topPane,loginPicture,false);
			addTo(topPane,loginButton,false); // no need for boolean value to be true; it remembers.
			removeFrom(MenuBox,"viewProfile");
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
	
	private class ViewProfileController {
		@FXML
		void initialize() {
			Member member = ((Member) cc.getReader());
			idField.setText(String.valueOf(member.getId()));
	//		nameField.setText(member.getMemberCard().getFirstName());
		}
		@FXML
	    private TextField idField;

	    @FXML
	    private TextField statusField;

	    @FXML
	    private TextField nameField;

	    @FXML
	    private TextField emailField;

	    @FXML
	    private ImageView saveButton;

	    @FXML
	    private TextField phoneField;
	    @FXML
	    private AnchorPane pane;
	    @FXML
	    void entered(MouseEvent e) {
	    	mouseEntered(e);
	    }

	    @FXML
	    void exited(MouseEvent e) {
	    	mouseExited(e);
	    }

	    @FXML
	    void save(MouseEvent event) {

	    }

	    @FXML
	    void history(MouseEvent event) {

	    }

	}
}
