package application.controllers;

import java.io.IOException;

import application.ClientConsole;
import application.MyData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class ReaderController {
	private ClientConsole cc;
	private SearchController searchController;
	public ReaderController(ClientConsole cc) {
		this.cc = cc;
	}
	@FXML
    void initialize() {
		searchController = new SearchController(cc);
    }
    @FXML
    private TextField loginPassField;

    @FXML
    private Button btnLogin;

    @FXML
    private Button search_book_button;

    @FXML
    private TextField loginIdField;
    @FXML
    void menu_search(ActionEvent event) {
    	BorderPane bp = (BorderPane) ((Node)event.getSource()).getScene().getRoot();
    	try {
			cc.showBottom(bp, "search", searchController);
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
        			Alert alert = new Alert(AlertType.ERROR);
        			alert.setTitle("OBL System Error");
        			alert.setHeaderText("Login denied!");
        			alert.setContentText("One or more of your fields were empty!");
        			alert.showAndWait();
    			}
    	}
    	else {
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("OBL System Information");
    		alert.setHeaderText("Login denied!");
    		alert.setContentText("One or more of your fields were empty!");
    		alert.showAndWait();
    	}
    }

	private boolean isValidLoginFields() {
		return !(loginIdField.getText().isEmpty() && loginPassField.getText().isEmpty());
	}
}
