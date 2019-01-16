package client.controllers;

import java.util.ArrayList;

import client.ClientConsole;
import client.MyData;
import common.Book;
import common.BookReservation;
import common.Member;
import common.MemberCard;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class MemberController {
	private Member member;
	protected ReaderController rc;
	public MemberController(ReaderController rc, Member member) {
		this.rc=rc;
		this.member = member;
	}
	public Member getMember() {
		return member;
	}
		@FXML
		void initialize() {
			idField.setText(String.valueOf(member.getId()));
			nameField.setText(member.getMemberCard().getFirstName());
			statusField.setText(String.valueOf(member.getStatus()));
			emailField.setText(member.getMemberCard().getEmailAddress());
			phoneField.setText(member.getMemberCard().getPhoneNumber());
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
	    private TextField phoneField;
	    
	    @FXML
	    private ImageView saveButton;

	    @FXML
	    private AnchorPane pane;
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
	    	rc.setBottom(event);
	    }
	    @FXML
	    void saveInfo(MouseEvent event) {
	    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna save these changes?", "Once changed, the old information would be lost.").get() == ButtonType.OK) {
	    		MyData data = new MyData("saveInfo");
	    		data.add("id", Integer.parseInt(idField.getText()));
	    		data.add("email", emailField.getText());
	    		data.add("phone", phoneField.getText());
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
	    protected class HistoryController {
    		// sapir's
    	}
    	protected class OrderBookController {
    		@FXML
    		void initialize() {
    			//TODO: replace this with actual search results
    	//		resultTable.getItems().addAll(Arrays.asList("Harry Potter and the Chamber of Secrets","lolz","The Ugev","The Ugev 2"));
					rc.getCC().send(new MyData("getBooks"));
    			ArrayList<Book> books = (ArrayList<Book>)rc.getCC().getFromServer().getData("books"); // TODO: replace this with actual book results
    			resultTable.getItems().addAll(books);
    			nameCol.setCellValueFactory(new PropertyValueFactory<Book, String>("bookName"));
    			genreCol.setCellValueFactory(new PropertyValueFactory<Book, String>("topic"));
    			authorsCol.setCellValueFactory(new PropertyValueFactory<Book, String>("authorsNames"));
    		}

    	    @FXML
    	    private TableColumn<Book, String> genreCol;

    	    @FXML
    	    private TableColumn<Book, String> nameCol;
    	    
    	    @FXML
    	    private TableColumn<Book, String> authorsCol;
    	    
    		@FXML
    		private TableView<Book> resultTable;

    		@FXML
    		private AnchorPane pane;

    	    @FXML
    	    void enetered(MouseEvent event) {
    	    	rc.mouseEntered(event);
    	    }

    	    @FXML
    	    void exited(MouseEvent event) {
    	    	rc.mouseExited(event);
    	    }

    	    @FXML
    	    void orderBook(MouseEvent event) {
    	    	Book book = resultTable.getSelectionModel().getSelectedItem();
				if (!getMember().getMemberCard().checkBookReserved(book.getBookID())) {
    	    	MyData data = new MyData("orderBook");
    	    	data.add("id", getMember().getId());
    	    	data.add("bookID", book.getBookID());
    	    	rc.getCC().send(data);
				switch (rc.getCC().getFromServer().getAction()) {
				case "success":
					getMember().getMemberCard().addBookReservation(((BookReservation)rc.getCC().getFromServer().getData("reservation")));
					ClientConsole.newAlert(AlertType.INFORMATION, null, "Success!", book.getBookName() +" was successfuly ordered.");
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
    	    
    	}
}
