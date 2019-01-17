package client.controllers;

import java.util.ArrayList;
import java.util.Arrays;

import client.ClientConsole;
import client.MyData;
import common.Librarian;
import common.Member;
import common.MemberCard;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class LibrarianController {
	private ReaderController rc;
	private Librarian librarian;
	public LibrarianController(ReaderController rc, Librarian librarian) {
		this.rc=rc;
		this.librarian=librarian;		
	}
	@FXML
	 private TextField searchIDField;
	@FXML
    private AnchorPane searchPane;
	@FXML
    private AnchorPane page;
    @FXML
    void entered(MouseEvent e) {
    	rc.mouseEntered(e);
    }
    @FXML
    void exited(MouseEvent e) {
    	rc.mouseExited(e);
    }
    @FXML
    void replacePage(MouseEvent e) {
    	rc.setBottom(e);
    }
    @FXML
    void searchMember(MouseEvent event) {
    	try {
    	MyData data = new MyData("search_member");
    	data.add("id", Integer.valueOf(searchIDField.getText()));
    	data.add("searcherID", librarian.getID());
    	rc.getCC().send(data);
    	MyData result = (MyData) rc.getCC().getFromServer();
    	if (result.getData().containsKey("member")) {
        	System.out.println("RESULT:"+((Member)result.getData("member")).getUserName());
    		Member member = (Member)result.getData("member");
    		if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Is this the member you were looking for?", 
    				member.getMemberCard().getFirstName() +" "+member.getMemberCard().getLastName()+" ("+member.getID()+") was found! Click OK to work on member.").get()==ButtonType.OK) {
    			rc.setBottom(event,"memberManagement",member);
    		}
    	} else
    		ClientConsole.newAlert(AlertType.INFORMATION, null, "No results.", "The Database doesn't contain such member with that ID.");
    	} catch (NumberFormatException e) {
    		ClientConsole.newAlert(AlertType.ERROR, null, "ID Field", "9 digits only in ID field!");
    	}
    }
    
    protected class MemberManagement {
    	private Member member;
    	public MemberManagement(Member member) {
    		this.member=member;
    	}
    	@FXML
    	void initialize() {
    			statusBox.getItems().addAll(Member.Status.values());
    			statusBox.getSelectionModel().select(member.getStatus());
    			usernameField.setText(member.getUserName());
    			idField.setText(String.valueOf(member.getID()));
    			firstnameField.setText(member.getMemberCard().getFirstName());
    			lastnameField.setText(member.getMemberCard().getLastName());
    			passwordField.setText(member.getPassword());
    			emailField.setText(member.getMemberCard().getEmailAddress());
    			phoneField.setText(member.getMemberCard().getPhoneNumber());
    	}
    	@FXML
        private AnchorPane pane;
    	@FXML
        private TextField usernameField;
        @FXML
        private TextField idField;
        @FXML
        private ChoiceBox<Member.Status> statusBox;
        @FXML
        private TextField firstnameField;
        @FXML
        private TextField lastnameField;
        @FXML
        private PasswordField passwordField;
        @FXML
        private TextField emailField;
        @FXML
        private TextField phoneField;

        @FXML
        void entered(MouseEvent event) {
        	rc.mouseEntered(event);
        }

        @FXML
        void exited(MouseEvent event) {
        	rc.mouseExited(event);
        }

        @FXML
        void saveMemberInfo(MouseEvent event) {
        	if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna save these changes?", "Once changed, the old information would be lost.").get() == ButtonType.OK) {
	    		MyData data = new MyData("saveInfo");
	    		data.add("admin", librarian.getID()); // TODO: write in logs
	    		data.add("id", Integer.parseInt(idField.getText()));
	    		data.add("username", usernameField.getText());
	    		data.add("password", passwordField.getText());
	    		data.add("firstName", firstnameField.getText());
	    		data.add("lastName", lastnameField.getText());
	    		data.add("email", emailField.getText());
	    		data.add("phone", phoneField.getText());
	    		data.add("status", statusBox.getSelectionModel().getSelectedItem().toString());
	    		rc.getCC().send(data);
	    		switch (rc.getCC().getFromServer().getAction()) {
	    		case "success":
	    			ClientConsole.newAlert(AlertType.INFORMATION, "", "Success", "Your information was successfuly saved.");
	    			member.setMemberCard((MemberCard)rc.getCC().getFromServer().getData("member_card"));
	    			break;
	    		case "fail":
	    		default:
	    			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "Something went wrong, your information was not saved.");
	    			break;
	    		}
	    	}
        }
    }
    
	protected class CreateUser {
		@FXML
	    private PasswordField passwordField;
		 @FXML
		    private TextField usernameField;
		 @FXML
		 private TextField idField;

		 @FXML
		 private TextField lastnameField;
		    @FXML
		    private TextField emailField;

		    @FXML
		    private TextField firstnameField;

		    @FXML
		    private TextField phoneField;

	    @FXML
	    void entered(MouseEvent e) {
	    	rc.mouseEntered(e);
	    }
	    @FXML
	    void exited(MouseEvent e) {
	    	rc.mouseExited(e);
	    }

	    @FXML
	    void submit(MouseEvent event) {
	    	//TODO: verify all fields are legit
	    	try {
	    	MyData data = new MyData("createUser");
	    	data.add("username", usernameField.getText());
	    	data.add("password", passwordField.getText());
	    	data.add("id", Integer.parseInt(idField.getText()));
	    	data.add("firstname", firstnameField.getText());
	    	data.add("lastname", lastnameField.getText());
	    	data.add("email", emailField.getText());
	    	data.add("phone", phoneField.getText());
	    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Verify", "Are you sure you want to create this user ("+ usernameField.getText() +")").get()==ButtonType.OK) {
	    		rc.getCC().send(data);
	    		MyData rcv = rc.getCC().getFromServer();
		    	switch (rcv.getAction()) {
		    	case "success":
		    		ClientConsole.newAlert(AlertType.INFORMATION, null, "Success!", "user was successfuly created!");
		    		break;
		    	case "fail":
		    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", (String)rcv.getData("reason"));
		    		break;
		    	}
	    	}
	    	} catch (NumberFormatException e) {
	    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "ID must be written in numbers");
	    		idField.clear();
	    	}
	    }

	}
}
