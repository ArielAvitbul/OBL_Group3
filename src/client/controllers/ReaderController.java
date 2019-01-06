package client.controllers;

import java.io.IOException;
import java.util.HashMap;

import client.ClientConsole;
import client.MyButton;
import client.MyData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class ReaderController {
	private ClientConsole cc;
	private HashMap<String,MyButton> buttons;
	private HashMap<String,Object> controllers;
	public ReaderController(ClientConsole cc) {
		this.cc = cc;
	}
	@FXML
    void initialize() {
		buttons = new HashMap<>(); // holds all the dynamic buttons
		controllers = new HashMap<>(); // holds all the dynamic controllers
		buttons.put("search", new MyButton("client/images/buttons/searchBook.jpg", 402, 192, e->setBottom("search")));
		topPane.getChildren().add(buttons.get("search"));
    }
	@FXML
    private AnchorPane topPane;
    @FXML
    private TextField loginPassField;

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

    @FXML
    private void submitLogin (ActionEvent event)
    {
    	if(isValidLoginFields())
    	{
    		MyData login = new MyData ("login");
    		login.add("id", loginIdField.getText());
    		login.add("password", loginPassField.getText());
    		try {
    				cc.send(login);
    			}
    		catch (InterruptedException e)
    			{
    				ClientConsole.newAlert(AlertType.ERROR, null,"OBL System Error","Login denied!");
    			}
    	}
    	else {
    		ClientConsole.newAlert(AlertType.INFORMATION, null, "Empty fields", "One or more of your fields were empty");
    	}
    }

	private boolean isValidLoginFields() {
		return !(loginIdField.getText().isEmpty() && loginPassField.getText().isEmpty());
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
