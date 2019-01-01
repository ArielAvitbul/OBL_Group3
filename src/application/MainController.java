package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainController {
	private ClientConsole cc;
	private MyData data;
	public MainController(ClientConsole cc) {
		this.cc = cc;
	}
/*
    @FXML
    void update(ActionEvent event) throws Exception {
    	if (cc.getStudent()!=null) {
    	data = new MyData("update_statusmembership");
    	data.add("student", cc.getStudent());
    	data.add("selected_status",box.getSelectionModel().getSelectedItem());
    	cc.send(data);
    	Student st = cc.getStudent();
    	namedisplay.setText(st.getName() +"'s StatusMembership is now: "+ st.getStatusMembership());
    	} else
    		ClientConsole.newAlert(AlertType.ERROR, null, "No student to update", "Please view a student first");
    }
    @FXML
    void view(ActionEvent event) throws Exception {
    	if (cc.getStudent()!=null)
    		cc.forgetStudent();
    	data = new MyData("view_student_name");
    	data.add("student_id",studentid.getText());
    	 cc.send(data);
    	 if (cc.getStudent()!=null)
         namedisplay.setText(cc.getStudent().getName());
    	 else
    		 namedisplay.setText("No result");
    	 namedisplay.setVisible(true);
    }*/
	 @FXML
	    private Button search_book_button;

	    @FXML
	    void menu_search(ActionEvent event) {
	    	SearchController controller = new SearchController();
	    	BorderPane bp = (BorderPane) ((Node)event.getSource()).getScene().getRoot();
	    	try {
				cc.addBottom(bp, "search", controller);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	

}

