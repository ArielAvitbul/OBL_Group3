package client.controllers;

import java.io.IOException;
import java.util.HashMap;

import com.itextpdf.text.DocumentException;

import client.MyData;
import common.Manager;
import common.Report;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class ManagerController {
	private ReaderController rc;
	private Manager manager;
	public ManagerController(ReaderController rc, Manager manager) {
		this.rc=rc;
		this.manager=manager;
	}
	@FXML void initialize() {
		ReportBox.getItems().add("Activity Report");
		ReportBox.getItems().add("Borrow Report");
		ReportBox.getItems().add("Late Return Report");
		ReportBox.getSelectionModel().select("Activity Report");
	}
	@FXML
    private ComboBox<String> ReportBox;
	@FXML
	private AnchorPane pane;

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
	void produceReport(MouseEvent event) {
		MyData data = new MyData(ReportBox.getSelectionModel().getSelectedItem());
		data.add("managerID", manager.getEmployeeID());
		rc.getCC().send(data);
			new Report(rc.getCC().getFromServer());
	}
	protected class ProduceReport {
		@FXML
		void initialize() {
			
			
		}
	}
}
