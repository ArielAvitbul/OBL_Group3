package client.controllers;

import java.sql.Date;
import java.util.ArrayList;

import client.ClientConsole;
import client.MyData;
import client.MyImage;
import common.Book;
import common.BookReservation;
import common.Borrow;
import common.CopyInBorrow;
import common.History;
import common.Member;
import common.MemberCard;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
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
	    	rc.setBottom(event);
	    }
	    @FXML
	    void saveInfo(MouseEvent event) {
	    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna save these changes?", "Once changed, the old information would be lost.").get() == ButtonType.OK) {
	    		MyData data = new MyData("saveInfo");
	    		data.add("id", Integer.parseInt(idField.getText()));
	    		data.add("firstName", firstnameField.getText());
	    		data.add("lastName", lastnameField.getText());
	    		data.add("password", passwordField.getText());
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
    		@FXML
    		void initialize() {
    			MyData data = new MyData("history");
    			data.add("id", member.getID());
				rc.getCC().send(data);
			ArrayList<History> arrOb = (ArrayList<History>)rc.getCC().getFromServer().getData("list");
			BorrowTable.getItems().addAll(arrOb);
			System.out.println(arrOb);
			colname.setCellValueFactory(new PropertyValueFactory<History, String>("bookName"));
			colborrowdate.setCellValueFactory(new PropertyValueFactory<History,Date>("borrowDate"));
			colreturndate.setCellValueFactory(new PropertyValueFactory<History,Date>("actualReturnDate"));
		}
        @FXML
    	private AnchorPane pane;
        @FXML
        private ImageView r;
        
		@FXML
		private TableColumn<History, String> colname;

		@FXML
		private TableColumn<History, Date> colborrowdate;

		@FXML
		private TableColumn<History, Date> colreturndate;
		    
		@FXML
		private TableView<History> BorrowTable;
			
    	}
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
	    	    private TableColumn<CopyInBorrow, Date> RetDateCol;
	    	    
	    	    @FXML
	    	    private TableColumn<CopyInBorrow, String> BookAuthorCol;

	    	    @FXML
	    	    void initialize() {
	    	    	ArrayList<CopyInBorrow> copies = null;
	    	    	ArrayList<Borrow> currBorrows = new ArrayList<Borrow>();
	    	    	int i = 0;
	    	    	while(member.getMemberCard().getBorrowHistory().size()>i) {
	    	    		if(isCurrentBorrow(i))
	    	    			currBorrows.add(member.getMemberCard().getBorrowHistory().get(i));
	    	    			i++;
	    	    	}
	    	    	MyData data = new MyData("getCopiesInBorrow");
	    	    	data.add("borrows", currBorrows);
	    	    		rc.getCC().send(data);
	    	    	switch(rc.getCC().getFromServer().getAction()) {
	    	    	case "copiesInBorrow":
	    	    		if((ArrayList<Book>) rc.getCC().getFromServer().getData("copies")==null) 
	    	    			ClientConsole.newAlert(AlertType.INFORMATION, "No Active Borrows!", null, "You dont have any borrows to extend!");
	    	    		else {
	    	    			copies = (ArrayList<CopyInBorrow>) rc.getCC().getFromServer().getData("copies");
	    	    			ExtensionCurrBooks.getItems().addAll(copies);
	    	    			BookNameCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow,String>("borroBook"));  	    			
	    	    			BookAuthorCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow,String>("bookAuthor"));
	    	    			RetDateCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow,Date>("returnDate"));
	    	    			
	    	    		}
	    				rc.addTo(ExtensionAnPane, new MyImage("askForExtend", "\\client\\images\\buttons\\askForExtend.jpg", 231, 358, e->submitExtensionRequest(e)), true);
	    	    		break;
	    	    		}
	    	    	}
	    	    

				private boolean isCurrentBorrow(int i) {
					return member.getMemberCard().getBorrowHistory().get(i).getReturnDate().after(new java.util.Date());
				}	    	
				private void submitExtensionRequest(MouseEvent e) {
	    			CopyInBorrow selected = (CopyInBorrow)ExtensionCurrBooks.getSelectionModel().getSelectedItem();
	    			if(selected.getBorroBook().isPopular()) {
	    				rc.getCC().newAlert(AlertType.INFORMATION, "Popular book!", null, "This book is popular therfore you cannot extend your borrow!");
	    				return;
	    				}
	    			else {
	    					MyData data = new MyData("BorrowToExtend");
	    					data.add("TheBorrow", selected.getNewBorrow());
	    						rc.getCC().send(data);					
	    				}
	    			}
				}
    	protected class OrderBookController {
    		@FXML
    		void initialize() {
    			//TODO: replace this with actual search results
    	//		resultTable.getItems().addAll(Arrays.asList("Harry Potter and the Chamber of Secrets","lolz","The Ugev","The Ugev 2"));
					rc.getCC().send(new MyData("getBooks"));
    			ArrayList<Book> books = (ArrayList<Book>)rc.getCC().getFromServer().getData("books"); // TODO: replace this with actual book results
    			resultTable.getItems().addAll(books);
    			nameCol.setCellValueFactory(new PropertyValueFactory<>("bookName"));
    			genreCol.setCellValueFactory(new PropertyValueFactory<>("topic"));
    			authorsCol.setCellValueFactory(new PropertyValueFactory<>("authorsNames"));
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
    	    	data.add("id", getMember().getID());
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
