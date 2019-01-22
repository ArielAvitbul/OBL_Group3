package client.controllers;

import java.util.ArrayList;

import client.ClientConsole;
import client.MyData;
import common.Book;
import common.CopyInBorrow;
import common.Librarian;
import common.Member;
import common.MemberCard;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
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
    	protected Member getMember() {
    		return member;
    	}
    	@FXML
	    void replacePage(MouseEvent event) {
	    	rc.setBottom(event);
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
        protected class ViewRequests {
    		@FXML
    		void entered(MouseEvent e) {
    			rc.mouseEntered(e);
    		}
    		@FXML
    		void exited(MouseEvent e) {
    			rc.mouseExited(e);
    		}
    		@FXML
    		void goBack(MouseEvent event) {
    			rc.setBottom(event, "memberManagement");
    		}
    	}
    	protected class BorrowCopy {
    		@FXML
    		void entered(MouseEvent e) {
    			rc.mouseEntered(e);
    		}
    		@FXML
    		void exited(MouseEvent e) {
    			rc.mouseExited(e);
    		}
    		@FXML
    		void goBack(MouseEvent event) {
    			rc.setBottom(event, "memberManagement");
    		}
    	}
    	protected class ReturnCopy {
    		@FXML
			void initialize() {
    			bookNameCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow, String>("bookName"));
    			borrowIDCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow, Integer>("borrowID"));
    			borrowDateCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow, Object>("borrowDate"));
    			returnDateCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow, Object>("returnDate"));
    			MyData returnBook = new MyData ("returnBook");
    			returnBook.add("ID", member.getID());
    				rc.getCC().send(returnBook);
    			String result = (String)rc.getCC().getFromServer().getAction();
	    			if (result.equals("listOfReturnBooks")) 
	    			{
	    				ArrayList<CopyInBorrow> returnBookList = (ArrayList<CopyInBorrow>) rc.getCC().getFromServer().getData("returnBooklist");
	    				returnsTable.getItems().addAll(returnBookList);
	    			}
	    		}
    		
		    @FXML
    	    private TableColumn<CopyInBorrow, String> bookNameCol;

    	    @FXML
    	    private TableColumn<CopyInBorrow, Integer> borrowIDCol;
    	    
    	    @FXML
    	    private TableColumn<CopyInBorrow, Object> borrowDateCol;
    	    
    	    @FXML
    	    private TableColumn<CopyInBorrow, Object> returnDateCol;
    	    
    	    @FXML
    	    private AnchorPane pane;

    	    @FXML
    	    private ImageView saveInfo;

    	    @FXML
    	    private TableView<CopyInBorrow> returnsTable;
    		@FXML
    		void entered(MouseEvent e) {
    			rc.mouseEntered(e);
    		}
    		@FXML
    		void exited(MouseEvent e) {
    			rc.mouseExited(e);
    		}
    		@FXML
    		void goBack(MouseEvent event) {
    			rc.setBottom(event, "memberManagement");
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
	protected class BookManagement {
    	public BookManagement(Book book) {
    		System.out.println("book on management: "+book);
    	}
    }
	protected class InventoryManagementController {
		private ArrayList<Book> books;
		@FXML
		void initialize() {
				rc.getCC().send(new MyData("getBooks"));
			books = (ArrayList<Book>)rc.getCC().getFromServer().getData("books"); // TODO: replace this with actual book results
			inventoryTable.getItems().addAll(books);
			colNameInventory.setCellValueFactory(new PropertyValueFactory<Book, String>("bookName"));
			colTopicInventory.setCellValueFactory(new PropertyValueFactory<Book, String>("topic"));
			colAuthorsInventory.setCellValueFactory(new PropertyValueFactory<Book, String>("authorsNames"));
		}
		@FXML
		 void handle(MouseEvent event) {
		        if (event.isPrimaryButtonDown() && event.getClickCount() == 2 && 
		            ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna update this book?", "Once changed, the old information would be lost.").get() == ButtonType.OK)
		            	rc.setBottom(event, "bookManagement", books.get(inventoryTable.getSelectionModel().getSelectedIndex()));
		        }
		@FXML
		private TableView<Book> inventoryTable;
	    @FXML
	    private TableColumn<Book, String> colNameInventory;

	    @FXML
	    private TableColumn<Book, String> colTopicInventory;

	    @FXML
	    private TableColumn<Book, String> colAuthorsInventory;


	    @FXML
	    private ImageView searchBook;

	    @FXML
	    void entered(MouseEvent event) {
	    	rc.mouseEntered(event);
	    }
	    @FXML
	    void exited(MouseEvent event) {
	    	rc.mouseExited(event);
	    }
	    @FXML
	    void replacePage(MouseEvent event) {
	    	rc.setBottom(event);
	    }
	    @FXML
	    void add(MouseEvent event) {
	    	rc.setBottom(event);
	}
}
}
