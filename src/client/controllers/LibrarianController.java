package client.controllers;

import java.sql.Date;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import client.ClientConsole;
import client.MyData;
import common.Book;
import common.Borrow;
import common.CopyInBorrow;
import common.Librarian;
import common.Member;
import common.MemberCard;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

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
    		private static final long MILLISECONDS_PER_DAY = 86400000;

			Book selected;
			
			@FXML
		    private AnchorPane ChooseBookPane;

		    @FXML
		    private DatePicker returnDatePicker;

		    @FXML
		    private CheckBox Adventure;

		    @FXML
		    private TextField nameField;

		    @FXML
		    private TextField authorsField;

		    @FXML
		    private CheckBox Kids;

		    @FXML
		    private CheckBox SF;

		    @FXML
		    private CheckBox Drama;

		    @FXML
		    private ImageView back_memberManagement;

		    @FXML
		    private CheckBox Thriller;

		    @FXML
		    private CheckBox Book;

		    @FXML
		    private GridPane GenrePane;

		    @FXML
		    private TextField freeTextField;

		    @FXML
		    private ImageView SearchButton;

		    @FXML
		    private ImageView submitBorrow;

		    @FXML
		    private VBox dateSelector;

    	    @FXML
    	    private TableView<Book> SearchResultTable;

    	    @FXML
    	    private TableColumn<Book, String> BookNameCol;

    	    @FXML
    	    private TableColumn<Book, String> BookAuthorCol;

    	    @FXML
    	    private TableColumn<Book, String> AvalCopiesCol;

    	    
    	    @FXML
    	    void initialize() {
    	    	SearchResultTable.setVisible(false);
    	    	dateSelector.setVisible(false);
    	    	submitBorrow.setVisible(false);
    			BookNameCol.setCellValueFactory(new PropertyValueFactory<Book, String>("bookName"));
    			BookAuthorCol.setCellValueFactory(new PropertyValueFactory<Book, String>("authorsNames"));
    			AvalCopiesCol.setCellValueFactory(new PropertyValueFactory<Book, String>("currentNumberOfCopies"));
    			Locale.setDefault(Locale.ENGLISH);
    	    }
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
    		@FXML
    	    void keyBoard(KeyEvent event) {
    			if (event.getCode().equals(KeyCode.ENTER))
    				submitSearch(null);
    	    }
		    @FXML
		    //guyguyguy
		    void submitSearch(MouseEvent  event) {
		    	selected = null;
		    	SearchResultTable.getItems().clear();
    				ArrayList<Book> result = rc.getSearchResults(nameField.getText(), authorsField.getText(), freeTextField.getText(), GenrePane);
    				/* TODO: // Add condition here, incase all field are empty
    					SearchResultTable.setVisible(false);
    	    	    	dateSelector.setVisible(false);
    	    	    	submitBorrow.setVisible(false);
    	    			SearchResultTable.setVisible(false);
    		    		ClientConsole.newAlert(AlertType.INFORMATION, null, "Enter at least one search parameter", (String)rc.getCC().getFromServer().getData("reason"));
    				*/
		    	if (!result.isEmpty()) {
	    	    	SearchResultTable.getItems().addAll(result);
	    	    	SearchResultTable.setVisible(true);
	    	    	submitBorrow.setVisible(true);
	    	    	dateSelector.setVisible(true);
		    	} else {
	    	    	SearchResultTable.setVisible(false);
	    	    	dateSelector.setVisible(false);
	    	    	submitBorrow.setVisible(false);
	    			SearchResultTable.setVisible(false);
	    			ClientConsole.newAlert(AlertType.INFORMATION, null, "No book came up in the search!", (String)rc.getCC().getFromServer().getData("reason"));
	    			nameField.clear();
	    			authorsField.clear();
		    	}
		    }
		    @FXML
		    void selectBook(MouseEvent event) {
		    	selected = SearchResultTable.getSelectionModel().getSelectedItem();
		    }
		    @FXML
		    void submitBorrowRequest(MouseEvent event) {
		    	switch (isValidBorrow(selected)) {
		    		case "NoBookSelected":	
		    			ClientConsole.newAlert(AlertType.ERROR, null , "No book was selected!", "You have to choose a book from the results!");
		    			break;
		    		case "NoCopies":
		    			ClientConsole.newAlert(AlertType.ERROR, null , "No copies!", "This book has no avaliable copies!\nif there are copies, fix it in invantory management.");
		    			break;
		    		case "NoReturnDate":
		    			ClientConsole.newAlert(AlertType.ERROR, null , "No return date!", "Please select a return date");
		    			break;
		    		case "MoreThenThreeDaysAndPopular":
		    			ClientConsole.newAlert(AlertType.ERROR, null , "Popular Book!", "The book you choosed is popular\ntherfore you cannot borrow it\nfor more the three days.\nplease change the return date!");
		    			break;
		    		default:
		    			if(ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Are You Sure?", "you have chosen the book "+selected.getBookName()+" to borrow.\nPress OK to continue!").get()==ButtonType.OK) {
		    			updateNewBorrow(selected);
		    			goBack(null);
		    			}
		    			break;
		    		}
		    	}

			private String isValidBorrow(Book toCheck) {
				if(toCheck == null) 
					return "NoBookSelected";
				if(toCheck.getCurrentNumberOfCopies() == 0)
					return "NoCopies";
				if(returnDatePicker.getValue() == null)
					return "NoReturnDate";
				long diff = daysBetween(new java.util.Date(),Date.from(returnDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
				if(toCheck.isPopular())
					return "MoreThenThreeDaysAndPopular";
				return "OK";
			}		

			/**
			 * 
			 * @param one - java.util.Date instance
			 * @param date - java.util.Date instance
			 * @return difference between them in days.
			 */
			private long daysBetween(java.util.Date one, java.util.Date date) { 
				long difference = (one.getTime()-date.getTime())/MILLISECONDS_PER_DAY; 
				return Math.abs(difference);
			}
			private void updateNewBorrow(Book newCopyToBorrow) {
				Date fromPicker = Date.valueOf(returnDatePicker.getValue());
				Borrow newBorrow = new Borrow(newCopyToBorrow.getBookID() , member.getID(), new Date(System.currentTimeMillis()) , fromPicker);
				MyData toSend = new MyData("newBorrowRequest");
				toSend.add("theBorrow", newBorrow);
				toSend.add("theCopy", newCopyToBorrow);
				rc.getCC().send(toSend);
				switch(rc.getCC().getFromServer().getAction()) {
					case "borrowSuccess":
						ClientConsole.newAlert(AlertType.INFORMATION,null, "Borrow has been registered!" , "Borrow has been registered in the system!");
					break;
					case "borrowFailed":
						ClientConsole.newAlert(AlertType.ERROR, null , "Something went wrong!", (String)rc.getCC().getFromServer().getData("reason"));
					break;
					default:
					break;
				}
				
			}
    	}
        protected class ReturnCopy {
    		@FXML
			void initialize() {
    			bookNameCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow, String>("borroBook"));
    			borrowIDCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow, Integer>("BorrowID"));
    			borrowDateCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow, Object>("BorrowDate"));
    			returnDateCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow, Object>("returnDate"));
    			copyNumberCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow, Integer>("CopyNumber"));
    			MyData returnBook = new MyData ("returnBook");
    			returnBook.add("ID", member.getID());
    				rc.getCC().send(returnBook);
    			String result = (String)rc.getCC().getFromServer().getAction();
	    			if (result.equals("listOfReturnBooks")) 
	    			{
	    				ArrayList<CopyInBorrow> returnBookList = (ArrayList<CopyInBorrow>) rc.getCC().getFromServer().getData("returnBooklist");
	    				returnsTable.getItems().addAll(returnBookList);
	    			}
	    			else if (result.equals("unfind_borrows_Book")) {
		    			ClientConsole.newAlert(AlertType.INFORMATION, null, "No books found", (String)rc.getCC().getFromServer().getData("reason"));
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
    	    private TableColumn<CopyInBorrow, Integer> copyNumberCol;
    	    
    	    @FXML
    	    private AnchorPane pane;

    	    @FXML
    	    private ImageView saveInfo;
    	    @FXML
    	    private ImageView returnCopyButton;

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
    		
    	    @FXML
    	    void returnCopyIsSelected(MouseEvent event) {
    	    	CopyInBorrow copy = returnsTable.getSelectionModel().getSelectedItem();
    	    	
    	    	if(ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Are You Sure?", "you have chosen the book "+copy.getBorroBook().getBookName()+" to return.\nPress OK to continue!").get()==ButtonType.OK) {
		    		MyData returnCopy = new MyData("copyToReturn");
		    		returnCopy.add("copy", copy);
		    		rc.getCC().send(returnCopy);
		    		String result = (String)rc.getCC().getFromServer().getAction();
		    		if (result.equals("succeed")) {
		    			ClientConsole.newAlert(AlertType.INFORMATION, null, "Return Book is succeed", (String)rc.getCC().getFromServer().getData("succeed"));
		    			}
	    			goBack(null);
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
	
	protected class InventoryManagement {
		
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
	    
		protected class BookManagement {
			private Book book;
	    	public BookManagement(Book book) {
	    		System.out.println("book on management: "+book);
	    		this.book=book;
	    	}
	    	@FXML
	    	void initialize() {
	    		bookName.setText(book.getBookName());
	    		authors.setText(book.getAuthorsNames());
	    		editionNumber.setText(String.format("%s", book.getEditionNumber()));
	    		shortDescription.setText(book.getShortDescription());
	    		numberOfCopies.setText(String.format("%s", book.getNumberOfCopies()));
	    		if(book.isPopular()==true)
	    			Popular.setSelected(true);
	    		shellLocation.setText(book.getShellLocation());
	    		if(book.getTopics().contains("Kids"))
	    			checkBoxKids.setSelected(true);
	    		if(book.getTopics().contains("Drama"))
	    			checkBoxDrama.setSelected(true);
	    		if(book.getTopics().contains("Adventure"))
	    			checkBoxAdventure.setSelected(true);
	    		if(book.getTopics().contains("Thriller"))
	    			checkBoxThriller.setSelected(true);
	    		if(book.getTopics().contains("SF"))
	    			checkBoxSF.setSelected(true);
	    		if(book.getTopics().contains("TextBook"))
	    			checkBoxTextBook.setSelected(true);
	    	}
	    	 @FXML
	    	    private TextField bookName;

	    	    @FXML
	    	    private TextField authors;

	    	    @FXML
	    	    private TextField editionNumber;

	    	    @FXML
	    	    private TextField shortDescription;

	    	    @FXML
	    	    private TextField numberOfCopies;

	    	    @FXML
	    	    private CheckBox Popular;

	    	    @FXML
	    	    private TextField shellLocation;

	    	    @FXML
	    	    private CheckBox checkBoxDrama;

	    	    @FXML
	    	    private CheckBox checkBoxAdventure;

	    	    @FXML
	    	    private CheckBox checkBoxKids;

	    	    @FXML
	    	    private CheckBox checkBoxThriller;

	    	    @FXML
	    	    private CheckBox checkBoxSF;

	    	    @FXML
	    	    private CheckBox checkBoxTextBook;
	    	    
	    	    @FXML
	    	    private ImageView updateButton;

	    	    @FXML
	    	    void entered(MouseEvent event) {
	    	    	rc.mouseEntered(event);
	    	    }

	    	    @FXML
	    	    void exited(MouseEvent event) {
	    	    	rc.mouseExited(event);
	    	    }

	    	    @FXML
	    	    void updateBook(MouseEvent event) {
	    	    	Book selected = (Book)inventoryTable.getSelectionModel().getSelectedItem();
	    	    	ArrayList<String> topics = new ArrayList<String>;
	    	    	int currentNumber=selected.getCurrentNumberOfCopies()+Integer.parseInt(numberOfCopies.getText())-selected.getNumberOfCopies();
	    	    	//*OBKASDFGOKSA
	    	    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna save these changes?", "Once changed, the old information would be lost.").get() == ButtonType.OK) {
	    	    		MyData data = new MyData("updateBook");
	    	    		data.add("bookID", book.getBookID());
	    	    		data.add("editionNumber", editionNumber.getText());
	    	    		data.add("numberOfCopies", numberOfCopies.getText());
	    	    		data.add("shellLocation", shellLocation.getText());
	    	    		data.add("currentNumberOfCopies", Integer.toString(currentNumber));
	    	    		if(Popular.isSelected())
	    	    			data.add("isPopular", 1);
	    	    		else
	    	    			data.add("isPopular", 0);
	    	    		data.add("topics", topic);
	    	    		rc.getCC().send(data);
	    	    		switch (rc.getCC().getFromServer().getAction()) {
	    	    		case "success":
	    	    			ClientConsole.newAlert(AlertType.INFORMATION, "", "Success", "Your information was successfuly saved.");
	    	    			break;
	    	    		case "fail":
	    	    		default:
	    	    			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "Something went wrong, your information was not saved.");
	    	    			break;
	    	    		}
	    	    	}
	    	    }


	    }
		
	    protected class AddBook {
	    	@FXML
	        private AnchorPane myPane;

	        @FXML
	        private ImageView addImage;

	        @FXML
	        private TextField bookName;

	        @FXML
	        private TextField authors;

	        @FXML
	        private TextField editionNumber;

	        @FXML
	        private DatePicker printDate;

	        @FXML
	        private TextField shortDescription;

	        @FXML
	        private TextField numberOfCopies;

	        @FXML
	        private CheckBox Popular;

	        @FXML
	        private TextField shellLocation;

	        @FXML
	        private CheckBox checkBoxDrama;

	        @FXML
	        private CheckBox checkBoxAdventure;

	        @FXML
	        private CheckBox checkBoxKids;

	        @FXML
	        private CheckBox checkBoxThriller;

	        @FXML
	        private CheckBox checkBoxSF;

	        @FXML
	        private CheckBox checkBoxTextBook;

	        @FXML
	        private ImageView finishButton;

	        @FXML
		    void addBook(MouseEvent event) {
		    	String kindOfABook=""; 
		    	boolean popular=true;
		    	try {
		    	MyData data= new MyData("addNewBook");
		    	data.add("bookName", bookName.getText());
		    	data.add("authorsNames", authors.getText());
		    	data.add("editionNumber", editionNumber.getText());
		    	data.add("printDate", printDate.getValue());
		    	if(checkBoxDrama.isSelected())
		    		kindOfABook+="Drama ";
		    	if(checkBoxAdventure.isSelected())
		    		kindOfABook+="Adventure ";
		    	if(checkBoxKids.isSelected())
		    		kindOfABook+="Kids ";
		    	if(checkBoxThriller.isSelected())
		    		kindOfABook+="Thriller ";
		    	if(checkBoxSF.isSelected())
		    		kindOfABook+="SF ";
		    	if(checkBoxTextBook.isSelected())
		    		kindOfABook+="textBook ";
		    	data.add("shortDescription", shortDescription.getText());
		    	data.add("numberOfCopies", numberOfCopies.getText());
		    	data.add("purchaseDate", new Date(System.currentTimeMillis()));
		    	data.add("shellLocation", shellLocation.getText());
		    	if(Popular.isSelected())
		    		data.add("isPopular", Popular.isSelected());
		    	data.add("currentNumberOfCopies",numberOfCopies.getText());
		    	data.add("topic",kindOfABook);
		    	data.add("tableOfContent",bookName.getText());
		    	Calendar calendar = Calendar.getInstance();
		    	java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
		    	data.add("purchaseDate", ourJavaDateObject);
		    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Verify", "Are you sure you want to create this book ("+ bookName.getText() +")").get()==ButtonType.OK) {
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
		    	}
		    }

	        @FXML
	        void entered(MouseEvent event) {

	        }

	        @FXML
	        void exited(MouseEvent event) {

	        }
	    }
	}
}
