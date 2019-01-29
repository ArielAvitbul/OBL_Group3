package client.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import common.Book;
import common.Manager;
import common.MyData;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

public class ManagerController {
	private ReaderController rc;
	private Manager manager;
	private BarChart<String,Number> chart;
	public ManagerController(ReaderController rc, Manager manager) {
		this.rc=rc;
		this.manager=manager;
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
	void replacePage(MouseEvent event) {
		rc.setBottom(event);
	}
	public class Report {
		@FXML
	    private ComboBox<String> ReportBox;

	    @FXML
	    private AnchorPane pane;

	    @FXML
	    private ImageView produceReport;

	    @FXML
		void entered(MouseEvent event) {
			rc.mouseEntered(event);
		}

		@FXML
		void exited(MouseEvent event) {
			rc.mouseExited(event);
		}
		@FXML void initialize() {
			ReportBox.getItems().add("Activity Report");
			ReportBox.getItems().add("Borrow Report");
			ReportBox.getItems().add("Late Return Report");
			ReportBox.getSelectionModel().select("Activity Report");
		}
		@FXML
		void produceReport(MouseEvent event) {
			pane.getChildren().remove(chart);
			MyData data = new MyData(ReportBox.getSelectionModel().getSelectedItem());
			rc.getCC().send(data);
				MyData dfs = rc.getCC().getFromServer();
				final CategoryAxis xAxis = new CategoryAxis();
		        final NumberAxis yAxis = new NumberAxis();
		        chart = new BarChart<String,Number>(xAxis,yAxis);
		        chart.setLayoutX(250);
		        chart.setLayoutY(50);
		        chart.setTitle(dfs.getAction());
				switch (dfs.getAction()) {
				case "Activity Report"://active,inactive,frozen,totalCopiesInBorrow,lateMembers
				//	int active=(Integer)dfs.getData("active"),inactive=(Integer)dfs.getData("inactive"),frozen=(Integer)dfs.getData("frozen"),
				//			totalCopiesInBorrow=(Integer)dfs.getData("totalCopiesInBorrow"),lateMembers=(Integer)dfs.getData("lateMembers");
					int active=20,inactive=15,frozen=14,totalCopiesInBorrow=16,lateMembers=5;
					break;
				 case "Borrow Report"://regular,popular
					 yAxis.setLabel("Days");
						xAxis.setLabel("Names");
					 XYChart.Series<String, Number> regular = new XYChart.Series<String, Number>();
					 regular.setName("Regular");
					 XYChart.Series<String, Number> popular = new XYChart.Series<String, Number>();
					 popular.setName("Popular");
					 int i=0;
						for (Entry<Book, ArrayList<Integer>> s : ((HashMap<Book,ArrayList<Integer>>)dfs.getData("books")).entrySet()) {
							Book b = s.getKey();
								for (int d : s.getValue())
									if (b.isPopular()) 
										popular.getData().add(new XYChart.Data<String, Number>(b.getBookName(), d));
									else
										regular.getData().add(new XYChart.Data<String, Number>(b.getBookName(), d));
						}
						chart.getData().addAll(regular,popular);
					break;
				 case "Late Return Report"://(HashMap<String,Integer>)dfs.getData("result")
					break;
				}
				
				pane.getChildren().add(chart);
		}
		private void addSeries(String title, int value) {
			XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
			series.setName(title);
		        series.getData().add(new XYChart.Data<String, Number>(title, value));
	        chart.getData().add(series);
		}
		private Integer getRange(Integer...integers) {
			Integer ret=0;
			for (Integer num : integers)
				if (num>ret)
					ret=num;
			return ret/10;
		}
	}

}
