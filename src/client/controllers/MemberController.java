package client.controllers;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

import client.ClientConsole;
import client.MyImage;
import common.Book;
import common.BookReservation;
import common.Borrow;
import common.CopyInBorrow;
import common.History;
import common.Member;
import common.MemberCard;
import common.Message;
import common.MyData;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
/**
 * a controller for all member's actions
 * @author Ariel
 *
 */
public class MemberController {
	private Member member;
	protected ReaderController rc;
	/**
	 * Builder for MemberController
	 * @param rc - ReaderController link
	 * @param member - the member's instance after login
	 */
	public MemberController(ReaderController rc, Member member) {
		this.rc=rc;
		this.member = member;
	}
	/**
	 * Returns the instance of the member
	 * @return member instance
	 */
	public Member getMember() {
		return member;
	}
	private int checkFields() {
		if(!emailField.getText().contains("@")) {
			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "You enterred an incorrect email. please try again");
			return 0;
		}
		if(!emailField.getText().contains(".")) {
			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "You enterred an incorrect email. please try again");
			return 0;
		}
		if(!phoneField.getText().startsWith("05")) {
			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "You enterred an incorrect phone. please insert number that start at 05");
			return 0;
		}
		if((!phoneField.getText().contains("-"))&&(phoneField.getText().length()<10 || phoneField.getText().length()>10)) {
			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "You enterred an incorrect phone. please insert 10 digits");
			return 0;
		}
		if((phoneField.getText().contains("-"))&&(phoneField.getText().length()<11 || phoneField.getText().length()>11)) {
			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "You enterred an incorrect phone. please insert 10 digits");
			return 0;
		}
		if(firstnameField.getText().matches(".*\\d+.*")) {
			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "You enterred a first name with digits");
			return 0;
		}
		if(lastnameField.getText().matches(".*\\d+.*")) {
			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "You enterred a last name with digits");
			return 0;
		}
		return 1;
	}
		@FXML
		void initialize() {
			idField.setText(String.valueOf(member.getID()));
			usernameField.setText(member.getUserName());
			passwordField.setText(member.getPassword());
			firstnameField.setText(member.getMemberCard().getFirstName());
			lastnameField.setText(member.getMemberCard().getLastName());
			emailField.setText(member.getMemberCard().getEmailAddress());
			phoneField.setText(member.getMemberCard().getPhoneNumber());
			statusField.setText(String.valueOf(member.getStatus()));
		}
		@FXML
	    private TextField usernameField;
	    @FXML
	    private TextField idField;
	    @FXML
	    private TextField emailField;
	    @FXML
	    private TextField phoneField;
	    @FXML
	    private TextField statusField;
	    @FXML
	    private TextField lastnameField;
	    @FXML
	    private PasswordField passwordField;
	    @FXML
	    private TextField firstnameField;
	    @FXML
	    void entered(MouseEvent e) {
	    	rc.mouseEntered(e);
	    }

	    @FXML
	    void exited(MouseEvent e) {
	    	rc.mouseExited(e);
	    }

	    @FXML
	    void replacePage(MouseEvent event) {
	    	if(checkPossibility(event)==0)
	    		return;
	    	else
	    		rc.setBottom(event);
	    }
	    private int checkPossibility(MouseEvent event) {
	    	if (member.getStatus().equals(Member.Status.FREEZE) && (((ImageView)event.getSource()).getId().equals("orderBook"))) {
	    		ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "Your user is freeze. you can't order books");
	    		return 0;
	    	}
	    	if(member.getStatus().equals(Member.Status.FREEZE) &&(((ImageView)event.getSource()).getId().equals("extensionRequest"))) {
	    		ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "Your user is freeze. you can't extend any borrow");
	    		return 0;
	    	}
	    	return 1;

		}
	    /**
	     * This function handles the action after clicking 'Save' button
	     * @param event - MouseEvent
	     */
	    @FXML
	    void saveInfo(MouseEvent event) {
	    			MyData data = new MyData("saveInfo");
	    			data.add("id", Integer.parseInt(idField.getText()));
	    			if(firstnameField.getText().equals("")) {
		    			ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You deleted the first name. please insert now");
		    			return;
		    		}
	    			else
	    				data.add("firstName", firstnameField.getText());
	    			if(lastnameField.getText().equals("")) {
	    				ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You deleted the last name. please insert now");
		    			return;
	    			}
	    			else
	    				data.add("lastName", lastnameField.getText());
	    			if(passwordField.getText().equals("")) {
	    				ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You deleted the password. please insert now");
		    			return;
	    			}
	    			else
	    				data.add("password", passwordField.getText());
	    			if(emailField.getText().equals("")) {
	    				ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You deleted the email address. please insert now");
		    			return;
	    			}
	    			else
	    				data.add("email", emailField.getText());
	    			if(phoneField.getText().equals("")) {
	    				ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You deleted the phone number. please insert now");
		    			return;
	    			}
	    			else
	    				data.add("phone", phoneField.getText());
	    			if(checkFields()==1) {
	    				if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna save these changes?", "Once changed, the old information would be lost.") == ButtonType.OK) {
	    			if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna save these changes?", "Once changed, the old information would be lost.") == ButtonType.OK) {
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
	    }
	    /**
	     *
	     * @author sapir carmi
	     * class that responsible about the history of the user
	     * include: borrows, violations, returns, extensions.
	     */
	    protected class HistoryController {
    		@FXML
    		void initialize() {
    			MyData data = new MyData("history");
    			data.add("id", member.getID());
				rc.getCC().send(data);
			ArrayList<History> arrOb = (ArrayList<History>)rc.getCC().getFromServer().getData("list");
			BorrowTable.getItems().addAll(arrOb);
			colType.setCellValueFactory(new PropertyValueFactory<History,String>("type"));
			colName.setCellValueFactory(new PropertyValueFactory<History,String>("name"));
			colDate.setCellValueFactory(new PropertyValueFactory<History,Date>("actualDate"));
		}
        @FXML
    	private AnchorPane pane;
        @FXML
        private ImageView r;

        @FXML
        private TableColumn<History, String> colType;

        @FXML
        private TableColumn<History, String> colName;

        @FXML
        private TableColumn<History, Date> colDate;
        
		@FXML
		private TableView<History> BorrowTable;
	    @FXML
	    void entered(MouseEvent event) {
	    	rc.mouseEntered(event);
	    }

	    @FXML
	    void exited(MouseEvent event) {
	    	rc.mouseExited(event);
	    }

	    @FXML
	    void goBack(MouseEvent event) {
	    	rc.setBottom("memberArea");
	    }
    	}
	  /**
	   * The ExtensionRequestController class is the controller of the auto extension process's GUI
	   * @author Good Guy
	   *
	   */
	    protected class ExtensionRequestController {
	    	    @FXML
	    	    private AnchorPane ExtensionAnPane;

	    	    @FXML
	    	    private ImageView ExtensionImage;
	    	    
	    	    @FXML
	    	    private ImageView ExtensionHeader;

	    	    @FXML
	    	    private TableView<CopyInBorrow> ExtensionCurrBooks;

	    	    @FXML
	    	    private TableColumn<CopyInBorrow, String> BookNameCol;
	    	    
	    	    @FXML
	    	    private TableColumn<CopyInBorrow, Timestamp> RetDateCol;
	    	    
	    	    @FXML
	    	    private TableColumn<CopyInBorrow, String> BookAuthorCol;
	    	    @FXML
	    	    void entered(MouseEvent event) {
	    	    	rc.mouseEntered(event);
	    	    }

	    	    @FXML
	    	    void exited(MouseEvent event) {
	    	    	rc.mouseExited(event);
	    	    }
	    	    @FXML
	    	    void goBack(MouseEvent event) {
	    	    	rc.setBottom("memberArea");
	    	    }
	    	    @FXML
	    	    void initialize() {
	    	    	ArrayList<CopyInBorrow> copies = null;
	    	    	ArrayList<Borrow> currBorrows = new ArrayList<Borrow>();
	    	    	int i = 0;
	    	    	while(member.getMemberCard().getBorrowHistory().size()>i) {
	    	    		if(isExtendableBorrow(i,member))
	    	    			currBorrows.add(member.getMemberCard().getBorrowHistory().get(i));
	    	    			i++;
	    	    	}
	    	    	MyData data = new MyData("getCopiesInBorrow");
	    	    	data.add("borrows", currBorrows);
	    	    		rc.getCC().send(data);
	    	    		copies = (ArrayList<CopyInBorrow>) rc.getCC().getFromServer().getData("copies");

	    	    			ExtensionCurrBooks.getItems().addAll(copies);
	    	    			BookNameCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow,String>("borroBook"));  	    			
	    	    			BookAuthorCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow,String>("bookAuthor"));
	    	    			RetDateCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow,Timestamp>("returnDate"));
	    	    		
	    	    	}
	    	    
	    	    /**
	    	     * @author Good Guy
	    	     * @param i - index of a Borrow in ArrayList of borrows in the member's MemberCard.
	    	     * @return True - if this Borrow can be extended
	    	     *,False - Otherwise
	    	     * @see MemberCard
	    	     * @see Borrow
	    	     */
				protected boolean isExtendableBorrow(int i, Member member) {
					Timestamp returnDate =  new Timestamp(member.getMemberCard().getBorrowHistory().get(i).getReturnDate().getTime());
					if(ReaderController.getDifferenceDays(returnDate,new java.util.Date())>6)
						return false;
					return true;
				}	  
				/**
				 * This method handles the extension request submitted by the member
				 * @author Good Guy
				 * @param e - event which on this method was called
				 * 
				 */
				@FXML
				private void submitExtensionRequest(MouseEvent e) {
	    			CopyInBorrow selected = (CopyInBorrow)ExtensionCurrBooks.getSelectionModel().getSelectedItem();
	    			if(selected==null) {
	    				ClientConsole.newAlert(AlertType.INFORMATION,null, "No Book Selected!", "Select a book from the list");
	    				return;
	    			}
	    			if(selected.getBorroBook().isPopular()) {
	    				ClientConsole.newAlert(AlertType.INFORMATION,null, "Popular book!", "This book is popular therfore you cannot extend your borrow!");
	    				return;
	    				}
	    			else {
	    					MyData data = new MyData("BorrowToExtend");
	    					data.add("TheCopyInBorrow", selected);
	    					data.add("requester", "user");
	    					data.add("fromPicker", null);
	    						rc.getCC().send(data);	
	    						switch(rc.getCC().getFromServer().getAction()) {
	    						case "ExtensionSucceed":
	    							ClientConsole.newAlert(AlertType.INFORMATION, null ,"Your borrow has been extended!", "your return date has been updated by your previous borrow length!");
	    							break;
	    						case "ExtensionFailed":
	    							ClientConsole.newAlert(AlertType.ERROR, null ,"Extension Failed!", (String)rc.getCC().getFromServer().getData("reason"));
	    							break;
	    						}
	    				}
	    			}
				}
	    /**
	     * a controller for Order Book page
	     * @author Ariel
	     *
	     */
	    
	    public void orderBook(Book book) {
    	    	if(book!=null)
    	    	{
				if (!getMember().getMemberCard().checkBookReserved(book.getBookID())) {
    	    	MyData data = new MyData("orderBook");
    	    	data.add("id", getMember().getID());
    	    	data.add("book", book);
    	    	rc.getCC().send(data);
				switch (rc.getCC().getFromServer().getAction()) {
				case "success":
					getMember().getMemberCard().addBookReservation(((BookReservation)rc.getCC().getFromServer().getData("reservation")));
					ClientConsole.newAlert(AlertType.INFORMATION, null, "Success!", book.getBookName() +" was successfuly ordered.");
					break;
				case "fail_Order":
					ClientConsole.newAlert(AlertType.WARNING, null, "", (String)rc.getCC().getFromServer().getData("message"));
					break;
				case "fail":
					ClientConsole.newAlert(AlertType.WARNING, null, "", (String)rc.getCC().getFromServer().getData("message"));
					break;
					default:
						System.out.println("Something went wrong...");
				}
    	    	} else
    	    		ClientConsole.newAlert(AlertType.WARNING, null, "Reserved already", "You have already reserved that book.");
        	    }
   	    	 else
    	    		ClientConsole.newAlert(AlertType.WARNING, null, "Book is not choose", "Please choose book before order.");
	    }
    	
    	protected class Inbox {
        	private ArrayList<Message> myMessages;
    	    @FXML
    	    private AnchorPane ChooseBookPane;

    	    @FXML
    	    private TableView<Message> messagesTV;

    	    @FXML
    	    private TableColumn<Message, String> fromColumn;

    	    @FXML
    	    private TableColumn<Message, Date> dateColumn;
    	    @FXML
    	    private TableColumn<Message, String> subjectColumn;
    	    
    	    @FXML
    	    private ImageView deleteMsg;

    	    @FXML
    	    private TextFlow contentTF;
    	    @FXML
    	    void initialize() {
    	    	messagesTV.setRowFactory(new Callback<TableView<Message>, TableRow<Message>>() {
    	            @Override
    	            public TableRow<Message> call(TableView<Message> param) {
    	                return new TableRow<Message>() {
    	                    @Override
    	                    protected void updateItem(Message msg, boolean empty) {
    	                    	super.updateItem(msg, empty);
    	                    	if (msg==null || msg.wasRead())
    	                            setStyle("");
    	                    	else 
    	                    	   setStyle("-fx-font-weight: bold");
    	                    }
    	                };
    	            }
    	        });
    	    	MyData data = new MyData("getMessages");
	    		data.add("rank", member.getClass().getSimpleName()); // To determine wether to reveal general inboxes to user.
    	    	data.add("member", member.getID());
    	    	rc.getCC().send(data);
    	    	switch(rc.getCC().getFromServer().getAction()) {
    	    	case "messages":
    	    		myMessages = (ArrayList<Message>)rc.getCC().getFromServer().getData("messages");
    	    		messagesTV.getItems().addAll(myMessages);
    	    		fromColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("from"));
    	    		dateColumn.setCellValueFactory(new PropertyValueFactory<Message, Date>("date"));
    	    		subjectColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("subject"));
    	    		break;
    	    	case "noMessages":
    	    		messagesTV.setPlaceholder(new Label("No New Messages!"));
    	    		break;
    	    	}
    	    }

    	    @FXML
    	    void deleteMsg(MouseEvent event) {
    	    	Message toDelete = messagesTV.getSelectionModel().getSelectedItem();
    	    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Confirm", "Are you sure you want to delete this message ('"+toDelete.getSubject()+"')?")==ButtonType.OK) {
    	    	MyData data = new MyData("deleteMsg");
    	    	data.add("toDelete", toDelete);
    	    	rc.getCC().send(data);
    	    	switch(rc.getCC().getFromServer().getAction()) {
    	    	case "removed":
    	    		ClientConsole.newAlert(AlertType.INFORMATION, null ,"Message Removed From Inbox!", "Message has been deleted!");
    	    		contentTF.getChildren().clear();
    	    		messagesTV.getItems().clear();
    	    		initialize();
    	    		break;
    	    	case "failed":
    	    		ClientConsole.newAlert(AlertType.INFORMATION, null ,"Could Not Remove Message!", "Message has not been removed!");
    	    		contentTF.getChildren().clear();
    	    		messagesTV.getItems().clear();
    	    		initialize();
    	    		break;
    	    	}
    	    	}
    	    }
    	    @FXML
    	    void entered(MouseEvent event) {
    	    	rc.mouseEntered(event);
    	    }

    	    @FXML
    	    void exited(MouseEvent event) {
    	    	rc.mouseExited(event);
    	    }
    	    @FXML
    	    void showMessage(MouseEvent event) {
    	    	if (messagesTV.getItems().isEmpty())
    	    		return;
    	    		contentTF.getChildren().clear();
    	    		messagesTV.refresh();
    	    		Text header = new Text("Message Content:\n\n");
    	    		header.setFont(new Font("Calibri", 20));
    	    		Message selectedMsg = myMessages.get(messagesTV.getSelectionModel().getSelectedIndex());
    	    		selectedMsg.setRead(true);
    	    		MyData data = new MyData("msgRead");
    	    		data.add("msgID", selectedMsg.getMsgID());
    	    		rc.getCC().send(data);
    	    		Text msg = new Text((selectedMsg.getContent()));
    	    		msg.setFont(new Font("Calibri", 16));
    	    		contentTF.getChildren().add(header);
    	    		contentTF.getChildren().add(msg);
    	    		if (!selectedMsg.wasHandled()) {
    	    		switch (selectedMsg.getAction()) {
    	    		case "3Late":
    	    			VBox actionBox = new VBox(5);
    	    			actionBox.getChildren().add(new Text(selectedMsg.getContent()));
    	    			actionBox.getChildren().add(new Label("Change "+ selectedMsg.getRegarding().getMemberCard().getFirstName()+"'s status: "));
    	    			ChoiceBox<Member.Status> statusBox = new ChoiceBox<>();
    	        		statusBox.getItems().addAll(Member.Status.values());
    	        		statusBox.getSelectionModel().select(selectedMsg.getRegarding().getStatus());
    	    			actionBox.getChildren().add(statusBox);
    	    			actionBox.getChildren().add(new MyImage("save", "client/images/buttons/save.jpg", e->{
    	    				if (ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Confirmation", "Are you sure?")==ButtonType.OK) {
    	    				selectedMsg.getRegarding().setUserStatus(statusBox.getSelectionModel().getSelectedItem());
    	    				data.setAction(selectedMsg.getAction()); // 3Late
    	    				data.add("memberID", selectedMsg.getRegarding().getID());
    	    				data.add("newStatus", selectedMsg.getRegarding().getStatus());
    	    				rc.getCC().send(data);
    	    				if (rc.getCC().getFromServer().getAction().equals("Success")) {
    	    				ClientConsole.newAlert(AlertType.INFORMATION, null, "Success", "Thank you for taking action. Case is now closed.");
    	    				selectedMsg.setHandled(true);
    	    				contentTF.getChildren().remove(actionBox);
    	    				contentTF.getChildren().add(new Text("\nThank you for taking action."));
    	    				} else
    	    					ClientConsole.newAlert(AlertType.ERROR, null, "Something went wrong", "Update didn't go through properly...");
    	    				}
    	    			}));
    	    			contentTF.getChildren().add(actionBox);
    	    			break;
    	    		} }else
    	    			contentTF.getChildren().add(new Text("\n\nAction was already taken."));
    	    	}
    	    }
}
