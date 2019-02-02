package client.controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import client.ClientConsole;
import client.controllers.MemberController.ExtensionRequestController;
import common.Book;
import common.Borrow;
import common.CopyInBorrow;
import common.BookReservation;
import common.Librarian;
import common.Manager;
import common.Member;
import common.MemberCard;
import common.Message;
import common.MyData;
import common.MyFile;
import common.Violation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

/**
 * a controller for all librarian's actions
 * @author Ariel
 *
 */
public class LibrarianController {
	private ReaderController rc;
	private Librarian librarian;
	/**
	 * Builder for LibrarianController
	 * @param rc - ReaderController link
	 * @param librarian - the librarian's instance after login
	 */
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
    /**
     * This function handles the action after clicking 'Search Member' button
     * @param event - MouseEvent
     */
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
    				member.getMemberCard().getFirstName() +" "+member.getMemberCard().getLastName()+" ("+member.getID()+") was found! Click OK to work on member.")==ButtonType.OK) {
    			rc.setBottom("memberManagement",member);
    		}
    	} else
    		ClientConsole.newAlert(AlertType.INFORMATION, null, "No results.", "The Database doesn't contain such member with that ID.");
    	} catch (NumberFormatException e) {
    		ClientConsole.newAlert(AlertType.ERROR, null, "ID Field", "9 digits only in ID field!");
    	}
    }
    /**
     * This class handles Member Management page
     * @author Ariel
     *
     */
    protected class MemberManagement {
    	private Member member;
    	/**
    	 * @param workedOnMc - Controller to use on member that the librarian searched.
    	 */
    	private MemberController workedOnMc = new MemberController(rc, this.member);
    	/**
    	 * 
    	 * @param member - Searched member instance
    	 */
    	public MemberManagement(Member member) {
    		this.member=member;
    	}
    	/**
    	 * 
    	 * @return member instance searched
    	 */
    	protected Member getMember() {
    		return member;
    	}

    	@FXML
	    void replacePage(MouseEvent event) {
    		if(checkPossibility( event)==0)
    			return;
    		else
    			rc.setBottom(event);
	    }
        @FXML
        void goBack(MouseEvent event) {
        	rc.setBottom("librarianArea");
        }
  	  private int checkPossibility(MouseEvent event) {
	
	    	if (member.getStatus().equals(Member.Status.FREEZE) && (((ImageView)event.getSource()).getId().equals("borrowCopy"))) {
	    		ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "This user is freeze. He can't borrow any book");
	    		return 0;
	    	}
	    	if (member.getStatus().equals(Member.Status.LOCK) && (((ImageView)event.getSource()).getId().equals("borrowCopy"))) {
	    		ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "This user is lock. He can't borrow any book");
	    		return 0;
	    	}
	    	return 1;

		}
    	@FXML
    	void initialize() {
    		if (librarian instanceof Manager) 
    			statusBox.getItems().addAll(Member.Status.values());
    		else
    			statusBox.getItems().add(member.getStatus());
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
        /**
         * Handles the functionality of clicking 'Save' at member management page
         * @param event - MouseEvent
         */
        @FXML
        private GridPane infoGrid;
        @FXML
        void saveMemberInfo(MouseEvent event) {
	    		MyData data = new MyData("saveInfo");
	    		for (Node n : infoGrid.getChildren()) {
	    			if ((n instanceof TextField || n instanceof PasswordField) && ((TextField)n).getText().isEmpty()) {
	    				ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "Some information is missing, please try again after fixing the issue.");
	    			return;
	    			}
	    		}
	    		Member tempMember = member; //incase of a failure
	    		member.setUserStatus(statusBox.getSelectionModel().getSelectedItem());
	    		member.setUserName(usernameField.getText());
	    		member.setPassword(passwordField.getText());
	    		member.getMemberCard().setFirstName(firstnameField.getText());
	    		member.getMemberCard().setLastName(lastnameField.getText());
	    		member.getMemberCard().setEmailAddress(emailField.getText());
	    		member.getMemberCard().setPhoneNumber(phoneField.getText());
	    		data.add("member", member);
	    		if(checkFields()==1) {
	    			if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna save these changes?", "Once changed, the old information would be lost.") == ButtonType.OK) {
	    		rc.getCC().send(data);
	    		switch (rc.getCC().getFromServer().getAction()) {
	    		case "success":
	    			ClientConsole.newAlert(AlertType.INFORMATION, "", "Success", "Your information was successfuly saved.");
	    			break;
	    		case "fail":
	    		default:
	    			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "Something went wrong, your information was not saved.");
	    			member=tempMember;
	    			break;
	    		}
	    			}
        }
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
		/**
         * class that responsible of adding new violation to the member.
         * @author sapir carmi
         *
         */
    	protected class ExceptionalEvent{
    		@FXML
    		void initialize() {
    			exceptionEventList.getItems().addAll(Violation.Type.values());
    			exceptionEventList.setValue(Violation.Type.OTHER);
    		}

    	    @FXML
    	    private ChoiceBox<Violation.Type> exceptionEventList;

    	    @FXML
    	    private ImageView addButton;
    	   
    	    @FXML
    	    void goBack(MouseEvent event) {
    	    	rc.setBottom("memberManagement");
    	    }
    	    /**
    	     * This function update the data to send to the server, and send.
    	     * @param event - click on add button on add violation screen
    	     */
    	    @FXML
    	    void addException(MouseEvent event) {
    	    	MyData data=new MyData("addViolation");
    	    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna add this violation?", "Press ok to add this violation.") == ButtonType.OK) {
    	    		data.add("id", member.getID());
    		    	data.add("violationDate", new Timestamp(System.currentTimeMillis()));
    	    		data.add("violation",exceptionEventList.getSelectionModel().getSelectedItem().toString() );
    	    		switch(exceptionEventList.getSelectionModel().getSelectedItem().toString()) {
    				case "LATE_RETURN":
    					data.add("violationType", 0);
    					break;
    				case "BOOK_IS_LOST":
    					data.add("violationType", 1);
    					break;
    				case "DAMAGED_BOOK":
    					data.add("violationType", 2);
    					break;
    				case "OTHER":
    					data.add("violationType", 3);
    					break;
    	    	}
    	    		rc.getCC().send(data);
    	    }
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

    	    @FXML
    	    void entered(MouseEvent event) {
    	    	rc.mouseEntered(event);
    	    }

    	    @FXML
    	    void exited(MouseEvent event) {
    	    	rc.mouseExited(event);
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
    			rc.setBottom("memberManagement");
    		}
    	}
        /**
         * The BorrowCopy class is the controller that handles the borrow process's GUI
         * @author Good Guy
         *
         */
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
    			rc.setBottom("memberManagement");
    		}
    		@FXML
    	    void keyBoard(KeyEvent event) {
    			if (event.getCode().equals(KeyCode.ENTER)) 
    				submitSearch(null);
    	    }
		    @FXML
		    void submitSearch(MouseEvent  event) {
		    	selected = null;
		    	SearchResultTable.getItems().clear();
		    	ArrayList<String> freeTxt = new ArrayList<String>(Arrays.asList(freeTextField.getText().split(" ")));
    				ArrayList<Book> result = rc.getSearchResults(nameField.getText(), authorsField.getText(), freeTxt, GenrePane);
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
		    	if(selected != null) {
		    		if(!selected.isPopular()) {
		    			returnDatePicker.setDayCellFactory(picker -> new DateCell(){
		    				public void updateItem(LocalDate date, boolean empty) {
		    					super.updateItem(date, empty);
		    					LocalDate today = LocalDate.now();
		    					setDisable(empty || date.compareTo(today) < 0 );
		    					if(date.isAfter(LocalDate.now().plusDays(14)))
		    						setDisable(true);
		    				}
		    			});
		    		}
		    		else {
		    			returnDatePicker.setDayCellFactory(picker -> new DateCell(){
		    				public void updateItem(LocalDate date, boolean empty) {
		    					super.updateItem(date, empty);
		    					LocalDate today = LocalDate.now();
		    					setDisable(empty || date.compareTo(today) < 0 );
		    					if(date.isAfter(LocalDate.now().plusDays(3)))
		    	            	setDisable(true);
		    	        	}
	    	    		});
		    		}
		    	}
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
		    			if(ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Are You Sure?", "you have chosen the book "+selected.getBookName()+" to borrow.\nPress OK to continue!")==ButtonType.OK) {
		    			updateNewBorrow(selected);
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
				if(toCheck.isPopular()&& diff>2)
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
				//Date fromPicker = Date.valueOf(returnDatePicker.getValue());
				Date fromPicker = new Date();
				fromPicker.setDate(returnDatePicker.getValue().getDayOfMonth());
				int fix = returnDatePicker.getValue().getMonthValue() == 1 ? 12 : returnDatePicker.getValue().getMonthValue()-1;
				fromPicker.setMonth(fix);
				fromPicker.setYear(returnDatePicker.getValue().getYear()-1900);
				Timestamp toServer = new Timestamp(fromPicker.getTime());
				Borrow newBorrow = new Borrow(newCopyToBorrow.getBookID() , member.getID(), new Timestamp(System.currentTimeMillis()) , toServer);
				MyData toSend = new MyData("newBorrowRequest");
				toSend.add("theBorrow", newBorrow);
				toSend.add("theCopy", newCopyToBorrow);
				toSend.add("id" , getMember().getID());
				rc.getCC().send(toSend);
				switch(rc.getCC().getFromServer().getAction()) {
					case "borrowSuccess":
						int index = SearchResultTable.getItems().indexOf(newCopyToBorrow);
						SearchResultTable.getItems().remove(newCopyToBorrow);
						SearchResultTable.getItems().add(index,(Book)((MyData)rc.getCC().getFromServer().getData("UpdatedBookAndBorrow")).getData("theCopy"));
						ClientConsole.newAlert(AlertType.INFORMATION,null, "Borrow has been registered!" , "Borrow has been registered in the system!");
						getMember().setMemberCard((MemberCard)rc.getCC().getFromServer().getData("updatedMemberCard"));					
					break;
					case "borrowFailed":
						ClientConsole.newAlert(AlertType.ERROR, null , "Something went wrong!", (String)rc.getCC().getFromServer().getData("reason"));
					break;
					default:
					break;
				}
				
			}
    	}
        /**
         * @author feldman
         *class for the librarian area to return copy of book
         *class start after librarian enter ID and press on "return copy" button
         *the initialize bring all the book that the userID borrow and not return yet organized in table.
         */
        protected class ReturnCopy {
    		@FXML
			void initialize() {
    			bookNameCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow, String>("borroBook"));
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
		    			isEmpty = 1;
		    			}
	    		}
    		int isEmpty = 0;
		    @FXML
    	    private TableColumn<CopyInBorrow, String> bookNameCol;
    
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
    			rc.setBottom("memberManagement");
    		}
    		/**
    		 * author feldman
    		 * after labririan choose book from the list of book the function send the server for  
    		 * hanlde the return book in the server side.
    		 * @param event 
    		 * strat after press return button.
    		 */

    	    @FXML
    	    void returnCopyIsSelected(MouseEvent event) {
    	    	CopyInBorrow copy = returnsTable.getSelectionModel().getSelectedItem();
    	    	if(copy !=null)
    	    	{
    	    	if(ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Are You Sure?", "you have chosen the book "+copy.getBorroBook().getBookName()+" to return.\nPress OK to continue!")==ButtonType.OK) {
		    		MyData returnCopy = new MyData("copyToReturn");
		    		returnCopy.add("copy", copy);
		    		rc.getCC().send(returnCopy);
		    		String result = (String)rc.getCC().getFromServer().getAction();
		    		if (result.equals("succeed")) {
		    			ClientConsole.newAlert(AlertType.INFORMATION, null, "Return Book is succeed", (String)rc.getCC().getFromServer().getData("succeed"));
		    			returnsTable.getItems().clear();
		    			initialize();
		    			}
	    			}
    	    	}
    	    	else if(copy==null&&isEmpty==0) ClientConsole.newAlert(AlertType.INFORMATION, null, "Book is not choose","Plese Choose Book" );
    	    		
		    		
    	    }
    	}
        /**
         * 
         * @author Good Guy
         * The ManualExtension class is the controller of the manual extension GUI part.
         *
         */
    	protected class ManualExtension {
    	    @FXML
    	    private VBox returnDateVbox;
    	    @FXML
    	    private ImageView back_memberManagement;
    	    @FXML
    	    private DatePicker newReturnDate;
    	    @FXML
    	    private TableView<CopyInBorrow> borrowsTV;
    	    @FXML
    	    private TableColumn<CopyInBorrow, String> bookNameCol;

    	    @FXML
    	    private TableColumn<CopyInBorrow, String> bookAuthorCol;

    	    @FXML
    	    private TableColumn<CopyInBorrow, Date> returnDateCol;

    	    @FXML
    	    void initialize() {
    	    	ExtensionRequestController checkExtendable = workedOnMc.new ExtensionRequestController();
    	    	borrowsTV.getItems().clear();
    	    	newReturnDate.getEditor().clear();
    	    	ArrayList<Borrow> currBorrows = new ArrayList<Borrow>();
    	    			for(Borrow toCheck : member.getMemberCard().getBorrowHistory()) 
    	    				if(checkExtendable.isExtendableBorrow(member.getMemberCard().getBorrowHistory().indexOf(toCheck) , member))
    	    					currBorrows.add(toCheck);
    	    	    	MyData data = new MyData("getCopiesInBorrow");
    	    	    	data.add("borrows", currBorrows);
    	    	    	rc.getCC().send(data);
    	    	    	switch(rc.getCC().getFromServer().getAction()) {
    	    	    	case "copiesInBorrow":
    	    	    		if((ArrayList<Book>) rc.getCC().getFromServer().getData("copies")==null) 
    	    	    			ClientConsole.newAlert(AlertType.INFORMATION, "No Active Borrows!", null, "You dont have any borrows to extend!");
    	    	    		else {
    	    	    			borrowsTV.setPlaceholder(new Label("No Extendable Borrows!"));
    	    	    			ArrayList<CopyInBorrow> copies = (ArrayList<CopyInBorrow>) rc.getCC().getFromServer().getData("copies");
    	    	    			borrowsTV.getItems().addAll(copies);
    	    	    			bookNameCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow,String>("borroBook"));  	    			
    	    	    			bookAuthorCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow,String>("bookAuthor"));
    	    	    			returnDateCol.setCellValueFactory(new PropertyValueFactory<CopyInBorrow,Date>("returnDate"));
    	    	    		}
    	    	    		break;
    	    	    		}
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
    	    	rc.setBottom("memberManagement");
    	    }
    	    /**
    	     * The manualyExtend method is responsible for handling the manual extension request, performed by the librarian
    	     * @author Good Guy
    	     * @param event - the event that caused this method call
    	     */
    	    @FXML
    	    void manualyExtend(MouseEvent event) {
    	    	MyData toSend = new MyData("BorrowToExtend");
    	    	toSend.add("TheCopyInBorrow", borrowsTV.getSelectionModel().getSelectedItem());
    	    	toSend.add("requester", "employee");
				Date fromPicker = new Date();
				fromPicker.setDate(newReturnDate.getValue().getDayOfMonth());
				int fix = newReturnDate.getValue().getMonthValue() == 1 ? 12 : newReturnDate.getValue().getMonthValue()-1;
				fromPicker.setMonth(fix);
				fromPicker.setYear(newReturnDate.getValue().getYear()-1900);
				Timestamp toServer = new Timestamp(fromPicker.getTime());
				toSend.add("fromPicker", toServer);
				rc.getCC().send(toSend);
				switch(rc.getCC().getFromServer().getAction()) {
				case "ExtensionSucceed":
					ClientConsole.newAlert(AlertType.INFORMATION, null ,"Your borrow has been extended!", "your return date has been updated by your previous borrow length!");
					getMember().setMemberCard((MemberCard)rc.getCC().getFromServer().getData("updatedMemberCard"));
					initialize();
					break;
				case "ExtensionFailed":
					ClientConsole.newAlert(AlertType.ERROR, null ,"Extension Failed!", (String)rc.getCC().getFromServer().getData("reason"));
					break;
				case "hasReservations":
					if((ClientConsole.newAlert(AlertType.CONFIRMATION, null , "Reserved Book!", (String)rc.getCC().getFromServer().getData("msgToPrint"))==ButtonType.OK)) {
						toSend.add("requester", "employeeAfterConfirmation");
						rc.getCC().send(toSend);
						switch(rc.getCC().getFromServer().getAction()) {
					case "ExtensionSucceed":
						ClientConsole.newAlert(AlertType.INFORMATION, null ,"Your borrow has been extended!", "your return date has been updated by your previous borrow length!");
						getMember().setMemberCard((MemberCard)rc.getCC().getFromServer().getData("updatedMemberCard"));
						initialize();
						break;
					case "ExtensionFailed":
						ClientConsole.newAlert(AlertType.ERROR, null ,"Extension Failed!", (String)rc.getCC().getFromServer().getData("reason"));
						break;
					  }
					}
					break;
				}
    	    	
    	    	}
    	    /**
    	     * The selectCopy method responsible on selecting a copy from TableView and setting date picker accordingly
    	     * @author Good Guy
    	     * @param event - the event on which this method was called
    	     * 
    	     */
    	    @FXML
    	    void selectCopy(MouseEvent event) {
    	    	CopyInBorrow selected = borrowsTV.getSelectionModel().getSelectedItem();
    	    	if(selected != null) {
    	    	returnDateVbox.setVisible(true);
	            LocalDate min = selected.getNewBorrow().getReturnDate().toLocalDateTime().toLocalDate();
	            LocalDate max = selected.getNewBorrow().getReturnDate().toLocalDateTime().toLocalDate();
    	    	newReturnDate.setDayCellFactory(picker -> new DateCell(){
	    	        public void updateItem(LocalDate date, boolean empty) {
	    	            super.updateItem(date, empty);
	                    setDisable(date.isAfter(max.plusDays(ReaderController.getDifferenceDays(selected.getNewBorrow().getReturnDate(), selected.getNewBorrow().getBorrowDate()))) || date.isBefore(min));
	    	        	}
    	    		});

    	    	}
    	    }

    	}
     	 /**
   	  * class for handle resevation lists and borrow books in reservation
   	  * @author feldman
   	  *
   	  */
    	protected class PickOrderController{
    	
    	    @FXML
    	    void goBack(MouseEvent event) {
    	    	rc.setBottom("memberManagement");
    	    }
    	    @FXML
    	    private ImageView BorrowButton;
    	    @FXML
    	    private DatePicker dateBorrow;
    	    @FXML
    	    private TableColumn<Book, String> BookNameCol;

    	    @FXML
    	    private TableView<Book> ExtensionCurrBooks;
    	    @FXML
    	    void initialize() {
    	    	dateBorrow.setVisible(false);
    	    BookNameCol.setCellValueFactory(new PropertyValueFactory<Book, String>("bookName"));	
    			MyData orderBooks = new MyData ("orderBooks");
    			orderBooks.add("ID", member.getID());
    				rc.getCC().send(orderBooks);
    			String result = (String)rc.getCC().getFromServer().getAction();
	    			if (result.equals("listOfReturnBooks")) 
	    			{
	    				ArrayList<Book> returnBookList = (ArrayList<Book>) rc.getCC().getFromServer().getData("returnBooklist");
	    				ExtensionCurrBooks.getItems().addAll(returnBookList);
	    			}
	    			else if (result.equals("unfind_borrows_Book")) {
		    			ClientConsole.newAlert(AlertType.INFORMATION, null, "No books found", (String)rc.getCC().getFromServer().getData("reason"));

		    			}
    	    }
    	    /**
    	     * method start when book choose in the table, and limit the datepicker to currect days 
    	     * the user can borrow.
    	     * @param event
    	     */
    	    @FXML
    	    void selectedBook(MouseEvent event) {
    	    	if(ExtensionCurrBooks.getSelectionModel().getSelectedItem()!=null)
    	    	{
    	    		dateBorrow.setVisible(true);
    	    		Book newCopyToBorrow = ExtensionCurrBooks.getSelectionModel().getSelectedItem();
    		    	if(!newCopyToBorrow.isPopular()) {
    		    		dateBorrow.setDayCellFactory(picker -> new DateCell(){
    		    	        public void updateItem(LocalDate date, boolean empty) {
    		    	            super.updateItem(date, empty);
    		    	            LocalDate today = LocalDate.now();
    		    	            setDisable(empty || date.compareTo(today) < 0 );
    		    	            if(date.isAfter(LocalDate.now().plusDays(14)))
    		    	            	setDisable(true);
    		    	        }
    		    	    });
    			    	}
    			    	else {
    			    		dateBorrow.setDayCellFactory(picker -> new DateCell(){
    			    	        public void updateItem(LocalDate date, boolean empty) {
    			    	            super.updateItem(date, empty);
    			    	            LocalDate today = LocalDate.now();
    			    	            setDisable(empty || date.compareTo(today) < 0 );
    			    	            if(date.isAfter(LocalDate.now().plusDays(3)))
    			    	            	setDisable(true);
    			    	        }
    			    	    });
    			    	}
    	    	}
    	    }
    	    /**
    	     * @author feldman
    	     * method handled the borrow request after the book is arrived and the user want to borrow it.
    	     * @param event
    	     */
    	    @FXML
    	    void borrowit(MouseEvent event) {
    	    	if(ExtensionCurrBooks.getSelectionModel().getSelectedItem()!=null)
    	    	{

    	    		Book newCopyToBorrow = ExtensionCurrBooks.getSelectionModel().getSelectedItem();
    	    		Date fromPicker = new Date();
    				fromPicker.setDate(dateBorrow.getValue().getDayOfMonth());
    				int fix = dateBorrow.getValue().getMonthValue() == 1 ? 12 : dateBorrow.getValue().getMonthValue()-1;
    				fromPicker.setMonth(fix);
    				fromPicker.setYear(dateBorrow.getValue().getYear()-1900);
    				Timestamp toServer = new Timestamp(fromPicker.getTime());

    				Borrow newBorrow = new Borrow(newCopyToBorrow.getBookID() , member.getID(), new Timestamp(System.currentTimeMillis()) , toServer);
    				MyData toSend = new MyData("BorrowOrderRequest");
    				toSend.add("theBorrow", newBorrow);
    				toSend.add("theCopy", newCopyToBorrow);
    				toSend.add("ID", member.getID());
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

    	    @FXML
    	    void entered(MouseEvent event) {
    	    	rc.mouseEntered(event);
    	    }

    	    @FXML
    	    void exited(MouseEvent event) {
    	    	rc.mouseExited(event);
    	    }

    	    }
    }
    /**
     * This class handles the Create User page management
     * @author Ariel
     *
     */
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
	    /**
	     * Handles functionality of 'Submit' button
	     * @param event - MouseEvent
	     */
	    @FXML
	    void goBack(MouseEvent event) {
	    	rc.setBottom("librarianArea");
	    }
	    @FXML
	    void submit(MouseEvent event) {
	    	try {
	    	MyData data = new MyData("createUser");
	    	data.add("username", usernameField.getText());
	    	data.add("password", passwordField.getText());
	    	data.add("id", Integer.parseInt(idField.getText()));
	    	data.add("firstname", firstnameField.getText());
	    	data.add("lastname", lastnameField.getText());
	    	data.add("email", emailField.getText());
	    	data.add("phone", phoneField.getText());
	    	if(usernameField.getText().equals("")) {
	    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert a user name. please insert now");
    			return;
	    	}
	    	else
	    		data.add("username", usernameField.getText());
	    	if(passwordField.getText().equals("")) {
	    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert a password. please insert now");
    			return;
	    	}
	    	else
	    		data.add("password", passwordField.getText());
	    	if(idField.getText().equals("")) {
	    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert an ID. please insert now");
    			return;
	    	}
	    	else
	    		data.add("id", Integer.parseInt(idField.getText()));
	    	if(firstnameField.getText().equals("")) {
	    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert a first name. please insert now");
    			return;
	    	}
	    	else
	    		data.add("firstname", firstnameField.getText());
	    	if(lastnameField.getText().equals("")) {
	    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert a last name. please insert now");
    			return;
	    	}
	    	else
	    		data.add("lastname", lastnameField.getText());
	    	if(emailField.getText().equals("")) {
	    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert an email address. please insert now");
    			return;
	    	}
	    	else
	    		data.add("email", emailField.getText());
	    	if(phoneField.getText().equals("")) {
	    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert a phone number. please insert now");
    			return;
	    	}
	    	else
	    		data.add("phone", phoneField.getText());
	    	if(checkFields()==1) {
	    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Verify", "Are you sure you want to create this user ("+ usernameField.getText() +")")==ButtonType.OK) {
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
	    	}
	    	}catch (NumberFormatException e) {
	    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "ID must be written in numbers");
	    		idField.clear();
	    	}
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

	}

	/**
	 * 
	 * @author sapir carmi and guy feldman
	 * This class is responsible on the inventory management of the library, like add books, update and delete.
	 * 
	 */
	protected class InventoryManagement {
		
		private ArrayList<Book> books;
		@FXML
		void initialize() {
				rc.getCC().send(new MyData("getBooks"));
			books = (ArrayList<Book>)rc.getCC().getFromServer().getData("books"); // TODO: replace this with actual book results
			inventoryTable.getItems().addAll(books);
			colNameInventory.setCellValueFactory(new PropertyValueFactory<Book, String>("bookName"));
			colTopicInventory.setCellValueFactory(new PropertyValueFactory<Book, String>("topics"));
			colAuthorsInventory.setCellValueFactory(new PropertyValueFactory<Book, String>("authorsNames"));
		}
	    @FXML
	    void goBack(MouseEvent event) {
	    	rc.setBottom("librarianArea");
	    }
		/*@FXML
		 void handle(MouseEvent event) {
		       if (event.isPrimaryButtonDown() && event.getClickCount() == 2 && 
		            ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna update this book?", "Once changed, the old information would be lost.") == ButtonType.OK)
		            	rc.setBottom(event, "bookManagement", books.get(inventoryTable.getSelectionModel().getSelectedIndex()));
		        }*/
		/**
		 * @author guy feldman
		 * @param event - the event of click on update book.
		 */
	    @FXML
	    void goToUpdate(MouseEvent event) {
	    	if(inventoryTable.getSelectionModel().getSelectedItem() !=null)
	    	rc.setBottom("bookManagement", books.get(inventoryTable.getSelectionModel().getSelectedIndex()));
	    	else ClientConsole.newAlert(AlertType.INFORMATION, null, "Book Not Choose", "Plese chose book from the list.");
	    }
	    /**
	     * strat when librarian choose to delete book
	     * librarian choose book from the list and the functiom send the book to the server
	     * for delete.
	     * @param event
	     */
	    @FXML
	    void deleteChosen(MouseEvent event) {
	    	if(inventoryTable.getSelectionModel().getSelectedItem() !=null)
	    	{
	    	Book book = inventoryTable.getSelectionModel().getSelectedItem();

	    	if(ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Are You Sure?", "you have chosen the book "+book.getBookName()+" to delete.\nPress OK to continue!")==ButtonType.OK) {
	    		MyData deleteBook = new MyData("deleteBook");
	    		deleteBook.add("book", book);
	    		rc.getCC().send(deleteBook);
	    		String result = (String)rc.getCC().getFromServer().getAction();
	    		if (result.equals("succeed")) {
	    			ClientConsole.newAlert(AlertType.INFORMATION, null, "The book is delete", (String)rc.getCC().getFromServer().getData("succeed"));
	    			inventoryTable.getItems().clear();
	    			initialize();
	    			}
	    		else if (result.equals("book_in_borrow")) {
	    			ClientConsole.newAlert(AlertType.INFORMATION, null, "Cant delete book", (String)rc.getCC().getFromServer().getData("book_in_borrow"));
	    			}
	    	}
	    	}
	    	else 
	    		ClientConsole.newAlert(AlertType.INFORMATION, null, "Book not select", "Please choose book to delete");
	    }
		@FXML
		private TableView<Book> inventoryTable;
		
	    @FXML
	    private ImageView delButton;

	    @FXML
	    private ImageView upButton;

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
	    /**
	     * 
	     * @author sapir carmi
	     * The class that responsible of update book parameters.
	     */
		protected class BookManagement {
			private Book book;
	    	public BookManagement(Book book) {
	    		this.book=book;
	    	}
	    	/**
	    	 * This function responsible of the page of update book. 
	    	 * It takes the information of the specific book and update the page
	    	 */
	    	@FXML
	    	void initialize() {
	    		choicePopular.getItems().addAll("Yes","No");
	    		bookName.setText(book.getBookName());
	    		authors.setText(book.getAuthorsNames());
	    		editionNumber.setText(String.format("%s", book.getEditionNumber()));
	    		shortDescription.setText(book.getShortDescription());
	    		numberOfCopies.setText(String.format("%s", book.getNumberOfCopies()));
	    		if(book.isPopular()==true)
	    			choicePopular.getSelectionModel().select("Yes");
	    		else choicePopular.getSelectionModel().select("No");
	    		shelfLocation.setText(book.getShelfLocation());
	    		if(book.getTopics().contains("Kids"))
	    			Kids.setSelected(true);
	    		if(book.getTopics().contains("Drama"))
	    			Drama.setSelected(true);
	    		if(book.getTopics().contains("Adventure"))
	    			Adventure.setSelected(true);
	    		if(book.getTopics().contains("Thriller"))
	    			Thriller.setSelected(true);
	    		if(book.getTopics().contains("SF"))
	    			SF.setSelected(true);
	    		if(book.getTopics().contains("TextBook"))
	    			TextBook.setSelected(true);
	    	}
    		File newFile;
    	    @FXML
    	    private ChoiceBox<String> choicePopular;
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
	    	    private TextField shelfLocation;

	    	    @FXML
	    	    private CheckBox Drama;

	    	    @FXML
	    	    private CheckBox Adventure;

	    	    @FXML
	    	    private CheckBox Kids;

	    	    @FXML
	    	    private CheckBox Thriller;

	    	    @FXML
	    	    private CheckBox SF;

	    	    @FXML
	    	    private CheckBox TextBook;
	    	    
	    	    @FXML
	    	    private ImageView updateButton;
	    	    @FXML
	    	    private GridPane genrePane;

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
	    	    	rc.setBottom("inventoryManagement");
	    	    }
	    	    /**
	    	     * This function check if the file is pdf file.
	    	     * @author guy feldman
	    	     * 
	    	     * @param event - click on upload.
	    	     * 
	    	     */
	    	    @FXML
	    	    void chooseTOCtoUpdate(ActionEvent event) {
	    	    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
	    	    	FileChooser fileChooser = new FileChooser();
	    	    	fileChooser.getExtensionFilters().add(extFilter);
		    		newFile = fileChooser.showOpenDialog(null);

		    		if (newFile != null) {

		    			System.out.println("File selected: " + newFile.getName());
		    		}
		    		else {
		    			System.out.println("File selection cancelled.");
		    		}
	    	    }
	    	    /**
	    	     * This function update the book, and check if there is any parameter that is empty. If there is one like this, it's send an error to the user.
	    	     * @author sapir carmi
	    	     * @param event - click on update book
	    	     */
	    	    @FXML
	    	    void updateBook(MouseEvent event) {
	    	    	Book selected = (Book)inventoryTable.getSelectionModel().getSelectedItem();
	    	    	ArrayList<String> genres = new ArrayList<String>();
	    	    	for (Node p : genrePane.getChildren())
	    	    		if (((CheckBox)p).isSelected())
	    	    			genres.add(p.getId());
	    	    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna save these changes?", "Once changed, the old information would be lost.") == ButtonType.OK) {
	    	    		MyData data = new MyData("updateBook");
	    	    		data.add("bookID", book.getBookID());
	    	    		if(!editionNumber.getText().equals(""))
	    	    			data.add("editionNumber", Float.parseFloat(editionNumber.getText()));
	    	    		else {
	    	    			ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert new edittion number. please insert now");
	    	    			return;
	    	    		}
	    	    		if(!numberOfCopies.getText().equals("")) {
	    	    		data.add("numberOfCopies", Integer.parseInt(numberOfCopies.getText()));
	    	    		data.add("currentNumberOfCopies", selected.getCurrentNumberOfCopies()+Integer.parseInt(numberOfCopies.getText())-selected.getNumberOfCopies());
	    	    		}
	    	    		else {
	    	    			ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert new number of copies. please insert now");
	    	    			return;
	    	    		}
	    	    		if(!shelfLocation.getText().equals(""))
	    	    			data.add("shelfLocation", shelfLocation.getText());
		    	    		else {
		    	    			ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert new shelf location. please insert now");
		    	    			return;
		    	    		}
	    	    		if(choicePopular.getSelectionModel().getSelectedItem().equals("Yes"))
	    		    		data.add("isPopular", true);
	    		    	else if (choicePopular.getSelectionModel().getSelectedItem().equals("No"))
	    		    		data.add("isPopular", false);
	    	    		for (Node p : genrePane.getChildren())
		    	    		if (((CheckBox)p).isSelected())
		    	    			genres.add(p.getId());
	    	    		if(genres.isEmpty()) {
	    	    			ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert new shelf location topics. please insert now");
	    	    			return;
	    	    		}
	    	    		else
	    	    			data.add("genres", genres);
		    	    	if (newFile !=null)
		    	    	{
		    	    		data.add("bookID", book.getBookID());
		  				  MyFile msg= new MyFile(newFile.getName());
						  msg.setWriteToPath("./src/server/TableOfContents");
						  try{		      
							      byte [] mybytearray  = new byte [(int)newFile.length()];
							      FileInputStream fis = new FileInputStream(newFile);
							      BufferedInputStream bis = new BufferedInputStream(fis);			  

							      msg.initArray(mybytearray.length);
							      bis.read(msg.getMybytearray(),0,mybytearray.length);
							      data.add("getFile", msg);
								  bis.close();
								  Boolean k = true;
								  data.add("FileChose", k);
							    }
							catch (Exception e) {
								System.out.println("Error send (Files)msg) to Server");
							}
		    	    	}

	    	    		rc.getCC().send(data);
	    	    		switch (rc.getCC().getFromServer().getAction()) {
	    	    		case "success":
	    	    			ClientConsole.newAlert(AlertType.INFORMATION, "", "Success", "Your information was successfuly saved.");
	    	    			break;
	    	    		case "number_of_copies_less_than_zero":
	    	    			ClientConsole.newAlert(AlertType.INFORMATION, "", "Update faild", "Enter positive and integer number.");
	    	    			break;
	    	    		case "fail":
	    	    		default:
	    	    			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "Something went wrong, your information was not saved.");
	    	    			break;
	    	    		}
	    	    }


	    }
		}
		/**
		 * class that responsible on adding a new book to the system
		 * @author sapir carmi and guy feldman
		 *
		 */
	    protected class AddBook {
	    	@FXML
	    	void initialize() {
	    		popularChoice.getItems().addAll("Yes", "No");
	    		popularChoice.setValue("Yes");
	    		printDate.setDayCellFactory(picker->new DateCell() {
					public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				 LocalDate today = LocalDate.now();
				 setDisable(empty || date.compareTo(today) > 0 );
					}
			});
	    	}
	    	File newFile = null;
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
	        private Button chooseFileButton;

	        @FXML
	        private ChoiceBox<String> popularChoice;

	        @FXML
	        private TextField shelfLocation;

	        @FXML
	        private CheckBox Drama;

	        @FXML
	        private CheckBox Adventure;

	        @FXML
	        private CheckBox Kids;

	        @FXML
	        private CheckBox Thriller;

	        @FXML
	        private CheckBox SF;

	        @FXML
	        private CheckBox TextBook;

	        @FXML
	        private ImageView finishButton;
	        @FXML
	        private GridPane genresPane;
	        
	        @FXML
	        void goBack(MouseEvent event) {
	        	rc.setBottom("inventoryManagement");
	        }
	        /**
	         * function that responsible of checking if the file is a pdf file.
	         * @param event - click on choose file
	         */
	        @FXML
	        void chooseFile(ActionEvent event) {
    	    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
    	    	FileChooser fileChooser = new FileChooser();
    	    	fileChooser.getExtensionFilters().add(extFilter);
	    		newFile = fileChooser.showOpenDialog(null);

	    		if (newFile != null) {

	    			System.out.println("File selected: " + newFile.getName());
	    		}
	    		else {
	    			System.out.println("File selection cancelled.");
	    		}
	        }
	        /**
	         * function that adding a new book , and check if there is any empty parameter. If there is, the book is don't add.
	         * @param event - click on add button on addBook fxml.
	         * 
	         */
	        @FXML
		    void addBook(MouseEvent event) {
		    	try {
		    	MyData data= new MyData("addNewBook");
		    	if(!bookName.getText().equals(""))
		    		data.add("bookName", bookName.getText());
		    	else{
	    			ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert a book name. please insert now");
	    			return;
	    		}
		    	if(!authors.getText().equals(""))	
		    		data.add("authorsNames", authors.getText());
		    	else {
		    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert an authors names. please insert now");
	    			return;
		    	}
		    	if(!editionNumber.getText().equals(""))
		    		data.add("editionNumber", editionNumber.getText());
		    	else {
		    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert an edition number. please insert now");
	    			return;
		    	}
		    	if(!(printDate.getValue()==null))
		    		data.add("printDate", printDate.getValue());
		    	else {
		    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert a print date. please insert now");
	    			return;
		    	}
		    	ArrayList<String> genres = new ArrayList<String>();
    	    	for (Node p : genresPane.getChildren())
    	    		if (((CheckBox)p).isSelected())
    	    			genres.add(p.getId());
    	    	if(!genres.isEmpty())
    	    		data.add("topics",genres);
    	    	else {
		    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't choose topics. please select now");
	    			return;
		    	}
    	    	if(!shortDescription.getText().equals(""))
    	    		data.add("shortDescription", shortDescription.getText());
    	    	else {
		    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert a short description. please insert now");
	    			return;
		    	}
    	    	if(!numberOfCopies.getText().equals("")) {
    	    		data.add("numberOfCopies", numberOfCopies.getText());
    	    		data.add("currentNumberOfCopies",numberOfCopies.getText());
    	    	}
    	    	else {
    	    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert a number of copies. please insert now");
	    			return;
    	    	}
		    	data.add("purchaseDate", new Date(System.currentTimeMillis()));
		    	if(!shelfLocation.getText().equals("")) 
		    		data.add("shelfLocation", shelfLocation.getText());
		    	else {
		    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert a shelf location. please insert now");
	    			return;
		    	}
		    	if(popularChoice.getSelectionModel().getSelectedItem().equals("Yes"))
		    		data.add("isPopular", true);
		    	else if (popularChoice.getSelectionModel().getSelectedItem().equals("No"))
		    		data.add("isPopular", false);
		    	data.add("currentNumberOfCopies",numberOfCopies.getText());
		    	data.add("tableOfContent",bookName.getText());
		    	Calendar calendar = Calendar.getInstance();
		    	java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
		    	data.add("purchaseDate", ourJavaDateObject);
		    	if(newFile==null)
		    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "You didn't insert any file to table of contents");
		    	else {
		    	MyFile msg= new MyFile(newFile.getName());
				  msg.setWriteToPath("./src/server/TableOfContents");
				  try{		      
					      byte [] mybytearray  = new byte [(int)newFile.length()];
					      FileInputStream fis = new FileInputStream(newFile);
					      BufferedInputStream bis = new BufferedInputStream(fis);			  

					      msg.initArray(mybytearray.length);
					      bis.read(msg.getMybytearray(),0,mybytearray.length);
					      data.add("getFile", msg);
						  bis.close();
					    }
					catch (Exception e) {
						System.out.println("Error send (Files)msg) to Server");
					}
		    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Verify", "Are you sure you want to create this book ("+ bookName.getText() +")")==ButtonType.OK) {
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
		    	}
		    	} catch (NumberFormatException e) {
		    		ClientConsole.newAlert(AlertType.ERROR, null, "Error", "ID must be written in numbers");
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
	    }
	}
	protected class ShowInbox {
    	private ArrayList<Message> myMessagse;
	    @FXML
	    private AnchorPane ChooseBookPane;

	    @FXML
	    private TableView<Message> messagesTV;

	    @FXML
	    private TableColumn<Message, String> fromColumn;

	    @FXML
	    private TableColumn<Message, Date> dateColumn;
	    
	    @FXML
	    private ImageView deleteMsg;

	    @FXML
	    private TextFlow contentTF;

	    @FXML
	    void goBack(MouseEvent event) {
	    	rc.setBottom("librarianArea");
	    }
	    @FXML
	    void initialize() {
	    	MyData data = new MyData("getMessages");
	    	data.add("librerian", librarian.getID());
	    	rc.getCC().send(data);
	    	switch(rc.getCC().getFromServer().getAction()) {
	    	case "messages":
	    		myMessagse = (ArrayList<Message>)rc.getCC().getFromServer().getData("messages");
	    		messagesTV.getItems().addAll(myMessagse);
	    		fromColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("from"));
	    		dateColumn.setCellValueFactory(new PropertyValueFactory<Message, Date>("date"));
	    		break;
	    	case "noMessages":
	    		messagesTV.setPlaceholder(new Label("No New Messages!"));
	    		break;
	    	}

	    }

	    @FXML
	    void deleteMsg(MouseEvent event) {
	    	Message toDelete = messagesTV.getSelectionModel().getSelectedItem();
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
	    		contentTF.getChildren().clear();
	    		Text header = new Text("Message Content:\n\n");
	    		header.setFont(new Font("Calibri", 20));
	    		Text msg = new Text((myMessagse.get(messagesTV.getSelectionModel().getSelectedIndex()).getContent()));
	    		msg.setFont(new Font("Calibri", 16));
	    		contentTF.getChildren().add(header);
	    		contentTF.getChildren().add(msg);
	    	}
	    }


	}

