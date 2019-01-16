package client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import client.ClientConsole;
import client.MyData;
import client.MyImage;
import common.Book;
import common.Librarian;
import common.Manager;
import common.Member;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ReaderController {
	private ClientConsole cc;
	private HashMap<String,Object> controllers;
	public Label welcomeMsg;
	public ReaderController(ClientConsole cc) {
		this.cc = cc;
	}
	
	protected ClientConsole getCC() {
		return cc;
	}
	@FXML
	void initialize() {
		controllers = new HashMap<>();
	}
	@FXML
    private AnchorPane page;
	@FXML
    private ImageView background;
	@FXML
    private ImageView searchBook;
	@FXML
    private HBox MenuBox;
	@FXML
    private AnchorPane mainPane;
    @FXML
    private PasswordField passField;
    @FXML
    private Button btnLogin;
    @FXML
    private TextField loginIdField;
    @FXML
    private ImageView loginPicture;
    @FXML
    private ImageView loginButton;
    /* This method removes objects from a pane */
	private void removeFrom(Pane pane, ArrayList<String> names) {
		ArrayList<Node> list = new ArrayList<>();
		for (Node n : pane.getChildren())
			if (names.contains(n.getId())) {
				list.add(n);
				if (pane.equals(MenuBox))
					list.add(MenuBox.getChildren().get(MenuBox.getChildren().indexOf(n)+1));
			}
		pane.getChildren().removeAll(list);
	}
    /* This method removes an object from a pane */
	private boolean removeFrom(Pane pane, String name) {
		for (Node n : pane.getChildren()) {
			if (name.equals(n.getId())) {
				if (pane.equals(MenuBox))
					MenuBox.getChildren().remove(MenuBox.getChildren().indexOf(n)+1); // delete his separator
				pane.getChildren().remove(n);
				return true;
			}
		}
		return false;
	}
    void addTo(Pane pane, Node button, boolean enteredexited) {
    	pane.getChildren().add(button);
    	if (pane.equals(MenuBox)) {
    		pane.getChildren().add(new ImageView(new Image("client/images/buttons/separator.png")));
    		button.setPickOnBounds(true); // since the image has a transparent background, we want the mouse to be able to click on it's bounds instead of it's visible graphics.
    	}
    	if (enteredexited) {
    	button.setOnMouseEntered(e-> mouseEntered(e));
    	button.setOnMouseExited(e->mouseExited(e));
    	}
    }
    @FXML
    protected void mouseEntered(MouseEvent ev) {
    	//ImageView button = ((ImageView)ev.getSource());
    	//	button.setImage(new Image("client/images/buttons/"+button.getId()+"Pressed.jpg"));
    	ImageView image = ((ImageView)ev.getSource());
    	ColorAdjust effect = new ColorAdjust();
    	double num = 0.1;
    	if (image.getParent().equals(MenuBox))
    		num=0.5;
    	effect.setBrightness(-num);
     image.setEffect(effect);
    }
    @FXML
    protected void mouseExited(MouseEvent ev) {
    	((ImageView)ev.getSource()).setEffect(null);
    }
    private void resetBottom() {
    	mainPane.getChildren().remove(page); // removes the previous page.
    }
    @FXML
    protected void setBottom(MouseEvent ev) { // button name must be equal to the fxml name
    	String fxml = ((ImageView)ev.getSource()).getId();
    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("client/fxmls/"+fxml+".fxml"));
    	if (!this.controllers.containsKey(fxml)) {
    		switch (fxml) {
    		case "createUser":
    			controllers.put(fxml, ((LibrarianController)controllers.get("librarianArea")).new CreateUser());
    			break;
    		case "searchBook":
    			controllers.put(fxml, new SearchController());
    			break;
    		case "memberArea":
    			controllers.put("memberArea", controllers.get("member"));
    			break;
    		case "librarianArea":
    			controllers.put("librarianArea", controllers.get("librarian"));
    			break;
    		case "managerArea":
    			controllers.put("managerArea", controllers.get("manager"));
    			break;
    		case "history":
    			controllers.put("history",((MemberController)controllers.get("memberArea")).new HistoryController());
    			break;
    		case "orderBook":
    			controllers.put("orderBook",((MemberController)controllers.get("memberArea")).new OrderBookController());
    			break;
    			default: // unrecognized fxml
    				ClientConsole.newAlert(AlertType.ERROR, null, "Unrecognized FXML", "Hey, make sure you wrote the write fxml name and handled it correctly.");
    				//System.exit(1);
    		}
    	}
    	loader.setController(controllers.get(fxml));
    	mainPane.getChildren().remove(page); // removes the previous page.
		try {
			mainPane.getChildren().add(page = loader.load()); // adds the new one
		} catch (IOException e) {e.printStackTrace();}
		page.setLayoutY(240); // sets to the current layout Y value
    }
    /* This function checks if login fields are empty after clicking the login button
     * input: none
     * output: T/F
     */
	private boolean isValidLoginFields() {
		return !(loginIdField.getText().isEmpty() && passField.getText().isEmpty());
	}
	/* Handles login button's ENTER KEY PRESS*/
	@FXML
    void keyBoard(KeyEvent event) {
		if (!passField.getText().isEmpty() && !passField.getText().isEmpty() && event.getCode().equals(KeyCode.ENTER) && !controllers.containsKey("member"))
			submitLogin(null);
    }
	/* This method handles a user logout
	 * input: none
	 * output: none
	 */
	
    /*This function handles a Reader login request and sends it to server if its valid
     * input:none
     * output: successful or unsuccessful login reaction 
     */
    @FXML
    private void submitLogin (MouseEvent event)
    {
    	if(isValidLoginFields())
    	{
    		MyData login = new MyData ("login");
    		login.add("id", Integer.valueOf(loginIdField.getText()));
    		login.add("password", passField.getText());
    				cc.send(login);
    		String result = cc.getFromServer().getAction();
    		if (result.equals("login_approved")) {
    			addTo(mainPane,new MyImage("logout","client/images/buttons/logout.jpg", loginButton.getLayoutX(),loginButton.getLayoutY(), e->submitLogout(e)),true);
    			addTo(mainPane,welcomeMsg = new Label("Welcome, "+cc.getFromServer().getData("MemberLoggedIn")),false);
    			welcomeMsg.setId("welcomeMsg");
    			welcomeMsg.setLayoutX(loginPicture.getLayoutX());
    			welcomeMsg.setLayoutY(loginPicture.getLayoutY());
    			removeFrom(mainPane,new ArrayList<>(Arrays.asList("loginButton","loginIdField","passField","loginPicture")));
    			if (cc.getFromServer().getData("MemberLoggedIn") instanceof Member) {
    				addTo(MenuBox, new MyImage("memberArea","client/images/buttons/memberArea.png",e1->setBottom(e1)),true);
        			controllers.put("member", new MemberController(this,(Member) cc.getFromServer().getData("MemberLoggedIn")));
    			} if (cc.getFromServer().getData("MemberLoggedIn") instanceof Librarian) {
    				addTo(MenuBox, new MyImage("librarianArea","client/images/buttons/librarianArea.png",e1->setBottom(e1)),true);
        			controllers.put("librarian", new LibrarianController(this,(Librarian) cc.getFromServer().getData("MemberLoggedIn")));
    			} if (cc.getFromServer().getData("MemberLoggedIn") instanceof Manager) {
    				addTo(MenuBox, new MyImage("managerArea","client/images/buttons/managerArea.png",e1->setBottom(e1)),true);
        			controllers.put("manager", new ManagerController(this,(Manager) cc.getFromServer().getData("MemberLoggedIn")));
    			}
    			
    		} else if (result.equals("login_failed")) {
    			ClientConsole.newAlert(AlertType.INFORMATION, null, "Login failed!", (String)cc.getFromServer().getData("reason"));
    			passField.clear();
    			loginIdField.clear();
    		}
    	}
    	else
    		ClientConsole.newAlert(AlertType.INFORMATION, null, "Empty fields", "One or more of your fields were empty");
    }
    
		private void submitLogout(MouseEvent event) {
			removeFrom(mainPane, new ArrayList<>(Arrays.asList("welcomeMsg","logout")));
			addTo(mainPane,passField,false);
			passField.clear();
			addTo(mainPane,loginIdField,false);
			loginIdField.clear();
			addTo(mainPane,loginPicture,false);
			addTo(mainPane,loginButton,false); // no need for boolean value to be true; it remembers.
			removeFrom(MenuBox,new ArrayList<>(Arrays.asList("memberArea","librarianArea","managerArea")));
				MyData data = new MyData("logout");
				Member member = ((MemberController)controllers.get("member")).getMember();
				data.add("id", member.getId());
				cc.send(data);
				resetBottom();
   }
		public void popup(MouseEvent event, Object controller) {
			String fxml = ((ImageView)event.getSource()).getId();
			if (fxml.equals("orderBook"))
				fxml="searchBook";
	        try {
	        	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("client/fxmls/"+fxml+".fxml"));
				loader.setController(controller);
				Stage stage = new Stage();
				stage.setScene(new Scene(loader.load()));
				stage.show();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		private class SearchController {
			@FXML
				void initialize() {
			choiceGenere.getItems().add("Drama");
			choiceGenere.getItems().add("Thriller");
			choiceGenere.getItems().add("Adventure");
			TableColumn Name = new TableColumn("Name");
			TableColumn Author = new TableColumn("Author");
			TableColumn Genere = new TableColumn("Genere");
			tableBooks.getColumns().addAll(Name, Author, Genere);
				}

		    @FXML
		    private TextField nameField;

		    @FXML
		    private TextField authorField;

		    @FXML
		    private ChoiceBox<String> choiceGenere;
		    

		    @FXML
		    private TextArea freetextField;
		    

		    @FXML
		    private ListView<String> searchResultList;
		    @FXML
		    private TableView<String> tableBooks;

		    @FXML
		    void entered(MouseEvent event) {

		    }

		    @FXML
		    void exited(MouseEvent event) {

		    }


		    @FXML
		    void submitSearch(MouseEvent  event) {
		    	MyData searchBook = new MyData ("searchBook");
		    	searchBook.add("bookName", nameField.getText());
		    	searchBook.add("authorName", authorField.getText());
		    	//search.add("authorName", choiceGenere.getUserData());
    				cc.send(searchBook);
		    	String result = (String)cc.getFromServer().getAction();
		
	    		if (result.equals("listOfBooks")) {
	    	    	ArrayList<Book> booksList = (ArrayList<Book>) cc.getFromServer().getData("booklist");
	    	    	
	    	    	Iterator<Book> it = booksList.iterator();
	    	    	while(it.hasNext())
	    	    	{
	    	    		tableBooks.getItems().add(it.next().getBookName());
	    	    		tableBooks.getItems().add(it.next().getAuthorsNames());
	    	    		tableBooks.getItems().add(it.next().getTopic());
	    	    	}

	    		}
	    		/*else if (result.equals("unfind_book")) {
	    			ClientConsole.newAlert(AlertType.INFORMATION, null, "No book found!", (String)cc.getFromServer().getData("reason"));
	    			nameField.clear();
	    			authorField.clear();
	    			}*/
		    }

		}
}
