package client.controllers;

import java.io.IOException;

import client.ClientConsole;
import client.MyButton;
import client.MyData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class ReaderController {
	private MyButton searchButton;
	private ClientConsole cc;
	private SearchController searchController;
	public ReaderController(ClientConsole cc) {
		this.cc = cc;
	}
	@FXML
    void initialize() {
		searchController = new SearchController(cc);
		searchButton = new MyButton("images/buttons/searchBook.jpg", 402, 192, e->menu_search());
		topPane.getChildren().add(searchButton.getImage());
		topPane.getChildren().add(searchButton);
    }
	@FXML
    private AnchorPane topPane;
    @FXML
    private TextField loginPassField;

    @FXML
    private Button btnLogin;

    @FXML
    private TextField loginIdField;

    void menu_search() {
    	try {
			cc.setBottom((BorderPane)topPane.getScene().getRoot(), "search", searchController);
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
}
