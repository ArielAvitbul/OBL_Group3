package application;

import java.net.URL;
import java.util.ResourceBundle;

import common.Student;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainController {
	private ClientConsole cc;
	private MyData data;
	public MainController(ClientConsole cc) {
		this.cc = cc;
	}

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
    }
    @FXML
    private TextField studentid;
    @FXML
    private Button view;
    
    @FXML
    private ResourceBundle resources;
    @FXML
    private Label namedisplay;
    @FXML
    private URL location;
    @FXML
    private ComboBox<String> box;
    @FXML
    void initialize() {
    	box.setItems(FXCollections.observableArrayList("Locked" ,"Frozen" ,"Active" ,"NotRegistered"));
    	box.getSelectionModel().selectFirst();
    }
}

