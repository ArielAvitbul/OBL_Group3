package application;

import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ReaderController {
	private ClientConsole cc;
	 
	public ReaderController(ClientConsole cc) {
		this.cc = cc;
	}
	
    @FXML
    private Button btnLogin;
    @FXML
    private TextField loginIdField;
    @FXML
    private TextField loginPassField;
    
    private void submitLogin (ActionEvent event) throws SQLException
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
