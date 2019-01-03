package client.controllers;

import client.ClientConsole;
import client.MyData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SearchController {
	private ClientConsole cc;
	public SearchController(ClientConsole cc) {
		this.cc=cc;
	}

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