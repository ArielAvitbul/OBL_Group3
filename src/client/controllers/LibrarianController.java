package client.controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import javax.print.attribute.standard.DateTimeAtCompleted;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

import client.ClientConsole;
import client.MyImage;
import common.Book;
import common.Borrow;
import common.CopyInBorrow;
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
import javafx.geometry.Orientation;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

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
    		if(checkPossibility( event)==0)
    			return;
    		else
    			rc.setBottom(event);
	    }
  	  private int checkPossibility(MouseEvent event) {
			// TODO Auto-generated method stub
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
    	protected class ExceptionalEvent{
    		@FXML
    		void initialize() {
    			exceptionEventList.getItems().addAll(Violation.Type.values());
    		}

    	    @FXML
    	    private ChoiceBox<Violation.Type> exceptionEventList;

    	    @FXML
    	    private ImageView addButton;

    	    @FXML
    	    void addException(MouseEvent event) {
    	    	MyData data=new MyData("addViolation");
    	    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna add this violation?", "Press ok to add this violation.").get() == ButtonType.OK) {
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
	    			member.setMemberCard((MemberCard)rc.getCC().getFromServer().getData("member_card"));
	    			break;
	    		case "fail":
	    		default:
	    			ClientConsole.newAlert(AlertType.INFORMATION, "", "Failed", "Something went wrong, your information was not saved.");
	    			break;
	    		}
    	    }

    	    @FXML
    	    void entered(MouseEvent event) {

    	    }

    	    @FXML
    	    void exited(MouseEvent event) {

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
    	    	returnDatePicker.setDayCellFactory(picker -> new DateCell(){
	    	        public void updateItem(LocalDate date, boolean empty) {
	    	            super.updateItem(date, empty);
	    	            LocalDate today = LocalDate.now();
	    	            setDisable(empty || date.compareTo(today) < 0 );
	    	            if(date.isAfter(LocalDate.now().plusDays(14)))
	    	            	setDisable(true);
	    	        }
	    	    });

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
		    	ArrayList<String> freeTxt = new ArrayList<String>(Arrays.asList(freeTextField.getText().split(" ")));
    				ArrayList<Book> result = rc.getSearchResults(nameField.getText(), authorsField.getText(), freeTxt, GenrePane);
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
				System.out.println(returnDatePicker.getValue().getYear());
				fromPicker.setYear(returnDatePicker.getValue().getYear()-1900);
				System.out.println(fromPicker);
				Timestamp toServer = new Timestamp(fromPicker.getTime());
				System.out.println(toServer);
				Borrow newBorrow = new Borrow(newCopyToBorrow.getBookID() , member.getID(), new Timestamp(System.currentTimeMillis()) , toServer);
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
    			rc.setBottom(event, "memberManagement");
    		}
    		
    	    @FXML
    	    void returnCopyIsSelected(MouseEvent event) {
    	    	CopyInBorrow copy = returnsTable.getSelectionModel().getSelectedItem();
    	    	if(copy !=null)
    	    	{
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
    	    	else if(copy==null&&isEmpty==0) ClientConsole.newAlert(AlertType.INFORMATION, null, "Book is not choose","Plese Choose Book" );
    	    		
		    		
    	    }
    	}
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
    	    	ArrayList<Borrow> currBorrows = new ArrayList<Borrow>();
    	    			for(Borrow toCheck : member.getMemberCard().getBorrowHistory()) 
    	    				if(isCurrBorrow(member.getMemberCard().getBorrowHistory().indexOf(toCheck)))
    	    					currBorrows.add(toCheck);
    	    	    	MyData data = new MyData("getCopiesInBorrow");
    	    	    	data.add("borrows", currBorrows);
    	    	    	rc.getCC().send(data);
    	    	    	switch(rc.getCC().getFromServer().getAction()) {
    	    	    	case "copiesInBorrow":
    	    	    		if((ArrayList<Book>) rc.getCC().getFromServer().getData("copies")==null) 
    	    	    			ClientConsole.newAlert(AlertType.INFORMATION, "No Active Borrows!", null, "You dont have any borrows to extend!");
    	    	    		else {
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
    	    	rc.setBottom(event, "memberManagement");
    	    }
    	    protected boolean isCurrBorrow(int index) {
    	    	return member.getMemberCard().getBorrowHistory().get(index).getReturnDate().after(new java.util.Date());

    	    }
    	    @FXML
    	    void manualyExtend(MouseEvent event) {
    	    	MyData toSend = new MyData("BorrowToExtend");
    	    	toSend.add("TheCopyInBorrow", borrowsTV.getSelectionModel().getSelectedItem());
    	    	toSend.add("requester", "employee");
				Date fromPicker = new Date();
				fromPicker.setDate(newReturnDate.getValue().getDayOfMonth());
				int fix = newReturnDate.getValue().getMonthValue() == 1 ? 12 : newReturnDate.getValue().getMonthValue()-1;
				fromPicker.setMonth(fix);
				System.out.println(newReturnDate.getValue().getYear());
				fromPicker.setYear(newReturnDate.getValue().getYear()-1900);
				System.out.println(fromPicker);
				Timestamp toServer = new Timestamp(fromPicker.getTime());
				toSend.add("fromPicker", toServer);
				rc.getCC().send(toSend);
				switch(rc.getCC().getFromServer().getAction()) {
				case "ExtensionSucceed":
					ClientConsole.newAlert(AlertType.INFORMATION, null ,"Your borrow has been extended!", "your return date has been updated by your previous borrow length!");
					break;
				case "ExtensionFailed":
					ClientConsole.newAlert(AlertType.ERROR, null ,"Extension Failed!", (String)rc.getCC().getFromServer().getData("reason"));
					break;
				case "hasReservations":
					if((ClientConsole.newAlert(AlertType.CONFIRMATION, null , "Reserved Book!", (String)rc.getCC().getFromServer().getData("msgToPrint")).get()==ButtonType.OK)) {
						toSend.add("requester", "employeeAfterConfirmation");
						rc.getCC().send(toSend);
						switch(rc.getCC().getFromServer().getAction()) {
					case "ExtensionSucceed":
						ClientConsole.newAlert(AlertType.INFORMATION, null ,"Your borrow has been extended!", "your return date has been updated by your previous borrow length!");
						break;
					case "ExtensionFailed":
						ClientConsole.newAlert(AlertType.ERROR, null ,"Extension Failed!", (String)rc.getCC().getFromServer().getData("reason"));
						break;
					}
					}
					break;
				}
    	    	
    	    	}
    	    @FXML
    	    void selectCopy(MouseEvent event) {
    	    	returnDateVbox.setVisible(true);
    	    	CopyInBorrow selected = borrowsTV.getSelectionModel().getSelectedItem();
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
			colTopicInventory.setCellValueFactory(new PropertyValueFactory<Book, String>("topics"));
			colAuthorsInventory.setCellValueFactory(new PropertyValueFactory<Book, String>("authorsNames"));
		}
		/*@FXML
		 void handle(MouseEvent event) {
		       if (event.isPrimaryButtonDown() && event.getClickCount() == 2 && 
		            ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna update this book?", "Once changed, the old information would be lost.").get() == ButtonType.OK)
		            	rc.setBottom(event, "bookManagement", books.get(inventoryTable.getSelectionModel().getSelectedIndex()));
		        }*/
	    @FXML
	    void goToUpdate(MouseEvent event) {
	    	if(inventoryTable.getSelectionModel().getSelectedItem() !=null)
	    	rc.setBottom(event, "bookManagement", books.get(inventoryTable.getSelectionModel().getSelectedIndex()));
	    	else ClientConsole.newAlert(AlertType.INFORMATION, null, "Book Not Choose", "Plese chose book from the list.");
	    }
	    @FXML
	    void deleteChosen(MouseEvent event) {
	    	Book book = inventoryTable.getSelectionModel().getSelectedItem();

	    	if(ClientConsole.newAlert(AlertType.CONFIRMATION, null, "Are You Sure?", "you have chosen the book "+book.getBookName()+" to delete.\nPress OK to continue!").get()==ButtonType.OK) {
	    		MyData deleteBook = new MyData("deleteBook");
	    		deleteBook.add("book", book);
	    		rc.getCC().send(deleteBook);
	    		String result = (String)rc.getCC().getFromServer().getAction();
	    		if (result.equals("succeed")) {
	    			ClientConsole.newAlert(AlertType.INFORMATION, null, "The book is delete", (String)rc.getCC().getFromServer().getData("succeed"));
	    			}
	    		else if (result.equals("book_in_borrow")) {
	    			ClientConsole.newAlert(AlertType.INFORMATION, null, "Cant delete book", (String)rc.getCC().getFromServer().getData("book_in_borrow"));
	    			}
	    	}
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
	    
		protected class BookManagement {
			private Book book;
	    	public BookManagement(Book book) {
	    		System.out.println("book on management: "+book);
	    		this.book=book;
	    	}
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
	    		shellLocation.setText(book.getShellLocation());
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
	    	    private TextField shellLocation;

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

	    	    @FXML
	    	    void updateBook(MouseEvent event) {
	    	    	Book selected = (Book)inventoryTable.getSelectionModel().getSelectedItem();
	    	    	ArrayList<String> genres = new ArrayList<String>();
	    	    	for (Node p : genrePane.getChildren())
	    	    		if (((CheckBox)p).isSelected())
	    	    			genres.add(p.getId());
	    	    	if (ClientConsole.newAlert(AlertType.CONFIRMATION, "", "Are you sure you wanna save these changes?", "Once changed, the old information would be lost.").get() == ButtonType.OK) {
	    	    		MyData data = new MyData("updateBook");
	    	    		data.add("bookID", book.getBookID());
	    	    		data.add("editionNumber", Float.parseFloat(editionNumber.getText()));
	    	    		data.add("numberOfCopies", Integer.parseInt(numberOfCopies.getText()));
	    	    		data.add("shellLocation", shellLocation.getText());
	    	    		data.add("currentNumberOfCopies", selected.getCurrentNumberOfCopies()+Integer.parseInt(numberOfCopies.getText())-selected.getNumberOfCopies());
	    	    		if(choicePopular.getSelectionModel().getSelectedItem().equals("Yes"))
	    		    		data.add("isPopular", true);
	    		    	else if (choicePopular.getSelectionModel().getSelectedItem().equals("No"))
	    		    		data.add("isPopular", false);
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
		
	    protected class AddBook {
	    	@FXML
	    	void initialize() {
	    		popularChoice.getItems().addAll("Yes", "No");

	    	}
	    	File newFile;
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
	        private TextField shellLocation;

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

	        @FXML
		    void addBook(MouseEvent event) {
		    	try {
		    	MyData data= new MyData("addNewBook");
		    	data.add("bookName", bookName.getText());
		    	data.add("authorsNames", authors.getText());
		    	data.add("editionNumber", editionNumber.getText());
		    	data.add("printDate", printDate.getValue());
		    	ArrayList<String> genres = new ArrayList<String>();
    	    	for (Node p : genresPane.getChildren())
    	    		if (((CheckBox)p).isSelected())
    	    			genres.add(p.getId());
		    	data.add("shortDescription", shortDescription.getText());
		    	data.add("numberOfCopies", numberOfCopies.getText());
		    	data.add("purchaseDate", new Date(System.currentTimeMillis()));
		    	data.add("shellLocation", shellLocation.getText());
		    	if(popularChoice.getSelectionModel().getSelectedItem().equals("Yes"))
		    		data.add("isPopular", true);
		    	else if (popularChoice.getSelectionModel().getSelectedItem().equals("No"))
		    		data.add("isPopular", false);
		    	data.add("currentNumberOfCopies",numberOfCopies.getText());
		    	data.add("topics",genres);
		    	data.add("tableOfContent",bookName.getText());
		    	Calendar calendar = Calendar.getInstance();
		    	java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
		    	data.add("purchaseDate", ourJavaDateObject);
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
	    	System.out.println(messagesTV.getSelectionModel().getSelectedIndex());
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
