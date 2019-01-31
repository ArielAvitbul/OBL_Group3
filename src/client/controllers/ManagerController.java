package client.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import common.Manager;
import common.MyData;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ManagerController {
	private ReaderController rc;
	private Manager manager;
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
		private ComboBox<String> activityBox;
		@FXML
	    private ComboBox<String> ReportBox;

	    @FXML
	    private AnchorPane pane;

	    @FXML
	    private ImageView produceReport;
	    @FXML
	    private VBox calculationBox;
	    @FXML
	    private HBox chartContainer;
	    @FXML
	    private VBox sideBox;
	    @FXML
		void entered(MouseEvent event) {
			rc.mouseEntered(event);
		}

		@FXML
		void exited(MouseEvent event) {
			rc.mouseExited(event);
		}
		@FXML void initialize() {
			ReportBox.getItems().add("Borrow Report");
			ReportBox.getItems().add("Late Return Report");
			ReportBox.getItems().add("Activity Report");
			ReportBox.getSelectionModel().select("Borrow Report");
			activityBox = new ComboBox<>();
			activityBox.getItems().add("New Report");
			rc.getCC().send(new MyData("getActivityReports"));
			for (Entry<String, Object> d : (((HashMap<String,Object>)rc.getCC().getFromServer().getData()).entrySet()))
				activityBox.getItems().add(((Timestamp)d.getValue()).toString());
			ReportBox.valueProperty().addListener(new ChangeListener<String>() {
		        @Override
		        public void changed(ObservableValue ov, String t, String t1) {
		            if (ov.getValue().equals("Activity Report")) {
		    			activityBox.getSelectionModel().select("New Report");
		            	Label label = new Label("Test");
		            	label.setId("test");
		            	sideBox.getChildren().add(2, activityBox);
		            } else
		            	sideBox.getChildren().remove(activityBox);
		          }    
		      });
		}
		@FXML
		void produceReport(MouseEvent event) {
			MyData data = new MyData(ReportBox.getSelectionModel().getSelectedItem());
			if (sideBox.getChildren().contains(activityBox) && !activityBox.getSelectionModel().getSelectedItem().equals("New Report"))
				data.add("ts", activityBox.getSelectionModel().getSelectedItem());
			rc.getCC().send(data);
				MyData dfs = rc.getCC().getFromServer();
		        calculationBox.getChildren().clear();
		        chartContainer.getChildren().clear();
				switch (dfs.getAction()) {
				case "Activity Report"://active,inactive,frozen,totalBorrowedCopies,lateReturners
					
						String time = (String)dfs.getData("ts");
						if (activityBox.getSelectionModel().getSelectedItem().equals("New Report"))
						activityBox.getItems().add(time);
						Label date = new Label(time.substring(0, time.length()-2));
						date.setFont(new Font(16));
						calculationBox.getChildren().add(date);
				    	chartContainer.getChildren().add(createActivityReport((Integer)dfs.getData("active"),(Integer)dfs.getData("locked"),(Integer)dfs.getData("frozen"),(Integer)dfs.getData("totalBorrowedCopies"),(Integer)dfs.getData("lateReturners")));
					break;
				 case "Borrow Report"://regular,popular
					 chartContainer.getChildren().add(createBorrowReport((HashMap<Boolean, ArrayList<Float>>) dfs.getData("borrows"),(Float)dfs.getData("maxVal"),(Float)dfs.getData("median"),(Float)dfs.getData("average")));
					break;
				 case "Late Return Report"://(HashMap<String,Integer>)dfs.getData("result")
					 chartContainer.getChildren().add(createLateReturnReport((HashMap<Integer,MyData>)dfs.getData("result"),(Float)dfs.getData("maxVal"),(Float)dfs.getData("median"),(Float)dfs.getData("average")));
					break;
				}
		}

		private PieChart createActivityReport(int active, int locked, int frozen, int totalBorrowedCopies, int lateReturners) {
			ObservableList<PieChart.Data> pieChartData =
			FXCollections.observableArrayList(
			new PieChart.Data("Active", active),
			new PieChart.Data("Locked", locked),
			new PieChart.Data("Frozen", frozen),
			new PieChart.Data("Total Borrowed Copies", totalBorrowedCopies),
			new PieChart.Data("Late Returners", lateReturners));
			final PieChart piechart = new PieChart(pieChartData);
			pieChartData.forEach(d ->d.nameProperty().bind(Bindings.concat(d.pieValueProperty().intValue(), " ", d.getName())));
			piechart.setTitle("Activity Report");
			return piechart;
		}
		
		private BarChart<String,Number> createBorrowReport(HashMap<Boolean,ArrayList<Float>> borrows, float maxVal, float median, float average) {
			XYChart.Series<String, Number> regular = new XYChart.Series<String, Number>();
			 regular.setName("Regular");
			 XYChart.Series<String, Number> popular = new XYChart.Series<String, Number>();
			 popular.setName("Popular");
			 addCalcBox(average,median);
			 int biggest=0;
			 if (maxVal<10)
				 maxVal=10;
			 for (float i=0;i<maxVal;i+=maxVal/10) {
				 String cat = String.format("%.02f", i==0? i : i+0.01)+"-"+(String.format("%.02f", i+maxVal/10));
				 Data<String, Number> Pcol = new XYChart.Data<String, Number>(cat,0);
				 Data<String, Number> Rcol = new XYChart.Data<String, Number>(cat,0);
				 for (Entry<Boolean, ArrayList<Float>> s : borrows.entrySet()) {
							for (float d : s.getValue()) {
								if (d>=(i==0? i :i+0.01)&&d<=(i+maxVal/10)) {
								if (s.getKey()) {// isPopular?
									Pcol.setYValue(Pcol.getYValue().intValue()+1);
									if (Pcol.getYValue().intValue()>biggest)
										biggest=Pcol.getYValue().intValue();
								}else {
									Rcol.setYValue(Rcol.getYValue().intValue()+1);
									if (Rcol.getYValue().intValue()>biggest)
										biggest=Rcol.getYValue().intValue();
								}
								}
							}
					}
				 popular.getData().add(Pcol);
				 regular.getData().add(Rcol);
			 }
			 	final CategoryAxis xAxis = new CategoryAxis();
		        final NumberAxis yAxis = new NumberAxis("Amount",0,biggest,1);
		    	BarChart<String,Number> barchart = new BarChart<String,Number>(xAxis,yAxis,FXCollections.observableArrayList(regular,popular));
		        barchart.setPrefWidth(650);
		        xAxis.setLabel("Duration");
				barchart.setTitle("Borrow Report");
				return barchart;
		}
		
		private BarChart<String,Number> createLateReturnReport(HashMap<Integer,MyData> result,float maxVal, float median, float average) {
			 ObservableList<Series<String, Number>> series = FXCollections.observableArrayList();
			 int biggest=0;
			 addCalcBox(average,median);
			 for (Entry<Integer, MyData> data : result.entrySet()) {
				 XYChart.Series<String, Number> s = new XYChart.Series<String, Number>();
				 s.setName((String)data.getValue().getAction());
				 for (float i=0;i<maxVal;i+=maxVal/10) {
					 String cat = String.format("%.02f", i==0? i : i+0.01)+"-"+(String.format("%.02f", i+maxVal/10));
					 Data<String, Number> d = new XYChart.Data<String, Number>(cat,0);
					 for (Float f : (ArrayList<Float>)data.getValue().getData("durations")) {
					 if (f>=(i==0? i :i+0.01)&&f<=(i+maxVal/10)) {
						 d.setYValue(d.getYValue().intValue()+1);
						 if (d.getYValue().intValue()>biggest)
								biggest=d.getYValue().intValue();
					 }
					 }
					 s.getData().add(d);
				 }
				 series.add(s);
			 }
			 
			 final CategoryAxis xAxis = new CategoryAxis();
		     final NumberAxis yAxis = new NumberAxis("Amount",0,biggest,1);
		     BarChart<String,Number> barchart = new BarChart<String,Number>(xAxis,yAxis,series);
		     barchart.setPrefWidth(650);
		     xAxis.setLabel("Duration");
		     barchart.setTitle("Late Return Report");
			 return barchart;
		}
		
		private void addCalcBox(float averageValue, float medianValue) {
			Label avg = new Label("Average: "+ String.format("%.02f", averageValue));
			avg.setFont(new Font(16));
			Label median = new Label("Median: "+ String.format("%.02f", medianValue));
			median.setFont(new Font(16));
			calculationBox.getChildren().add(avg);
			calculationBox.getChildren().add(median);
		}

		}
	}