package controller;

import java.io.IOException;
import java.util.ArrayList;

import application.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import controller.HeaderController;
import controller.BodyController;

public class MainController{
	
	private ArrayList<Task> taskList = new ArrayList<>();
	private ColumnConstraints col0;
	private ColumnConstraints col1;
	private ColumnConstraints col2;
	private ColumnConstraints col3;
	private ColumnConstraints col4;
	private ColumnConstraints col5;
	private RowConstraints row;
	
	@FXML AnchorPane anPaneHeader;
	@FXML AnchorPane anPaneBody;
	@FXML HeaderController headerController;
	@FXML BodyController bodyController;
	
	@FXML
	public void initialize() {
		headerController.init(this);
		bodyController.init(this);
		loadList("All");
	}

	public void loadList(String tabName) {
		// temporary method, will be deleted
		loadArrayList();
		setConstraintsOnGridPane();
		
		for(int i = 0; i < taskList.size(); i++) {
			if(i == 0 || taskList.get(i).getCategory() != taskList.get(i-1).getCategory()) {
				Label lblTitle = new Label(taskList.get(i).getCategory());
				lblTitle.getStyleClass().add("labelTitle");
				switch(tabName) {
					case "All": bodyController.vBoxAll.getChildren().add(lblTitle);
					break;
					case "Category": bodyController.vBoxCategory.getChildren().add(lblTitle);
					break;
					case "Priority": bodyController.vBoxPriority.getChildren().add(lblTitle);
					break;
					default:
						// other additional tab
					break;
				}
			}
			
			GridPane gPane = new GridPane();
			gPane.getColumnConstraints().addAll(col0,col1,col2,col3,col4,col5);
			gPane.getRowConstraints().addAll(row);
			gPane.getStyleClass().add("gridPaneList");
			
			switch(tabName) {
				case "All": bodyController.vBoxAll.getChildren().add(gPane);
				break;
				case "Category": bodyController.vBoxCategory.getChildren().add(gPane);
				break;
				case "Priority": bodyController.vBoxPriority.getChildren().add(gPane);
				break;
				default:
					// other additional tab
				break;
			}
			
			CheckBox checkbox = new CheckBox();
			checkbox.getStyleClass().add("labelList");
			Pane checkBoxPane = new Pane(checkbox);
			checkBoxPane.getStyleClass().add("paneList");
			
			Label lblId = new Label("ID" + i);
			lblId.getStyleClass().add("labelList");
			Pane paneId = new Pane(lblId);
			paneId.getStyleClass().add("paneList");
			
			Label lblDesc = new Label(taskList.get(i).getToDo());
			lblDesc.getStyleClass().add("labelList");
			Pane paneDesc = new Pane(lblDesc);
			paneDesc.getStyleClass().add("paneList");
			
			Label lblDateTime = new Label("from 09/12/2015 to 09/14/2015");
			lblDateTime.getStyleClass().add("labelList");
			Pane paneDateTime = new Pane(lblDateTime);
			paneDateTime.getStyleClass().add("paneList");
			
			Label lblCategory = new Label(taskList.get(i).getCategory());
			lblCategory.getStyleClass().add("labelList");
			Pane paneCategory = new Pane(lblCategory);
			paneCategory.getStyleClass().add("paneList");
			
			Label lblPriority = new Label("###");
			lblPriority.getStyleClass().add("labelList");
			Pane panePriority = new Pane(lblPriority);
			panePriority.getStyleClass().add("paneList");
			
			gPane.add(checkBoxPane, 0, 0);
			gPane.add(paneId, 1, 0);
			gPane.add(paneDesc, 2, 0);
			gPane.add(paneDateTime, 3, 0);
			gPane.add(paneCategory, 4, 0);
			gPane.add(panePriority, 5, 0);	
		}
	}
	
	public void showPageInBody(String fxmlFileName) throws IOException {
		anPaneBody.getChildren().clear();
		anPaneBody.getChildren().setAll(FXMLLoader.load(getClass().getResource(fxmlFileName)));
		loadList("All");
	}
	
	// temporary method to load the array list, will be deleted
	private void loadArrayList() {
		taskList.add(new Task());
		taskList.get(0).setToDo("Meet up with poly friends.");
		taskList.get(0).setOn(null);
		taskList.get(0).setCategory("#personal");
		
		taskList.add(new Task());
		taskList.get(1).setToDo("Eat dinner with Amy.");
		taskList.get(1).setOn(null);
		taskList.get(1).setCategory("#personal");
		
		taskList.add(new Task());
		taskList.get(2).setToDo("CNY lunch with family.");
		taskList.get(2).setOn(null);
		taskList.get(2).setCategory("#personal");
		
		taskList.add(new Task());
		taskList.get(3).setToDo("Do CS2103 project.");
		taskList.get(3).setOn(null);
		taskList.get(3).setCategory("#studies");
		
		taskList.add(new Task());
		taskList.get(4).setToDo("Write essay for genes and soc.");
		taskList.get(4).setOn(null);
		taskList.get(4).setCategory("#LSM1302");	
		
		taskList.add(new Task());
		taskList.get(5).setToDo("Buy A4 paper.");
		taskList.get(5).setOn(null);
		taskList.get(5).setCategory("#shoppinglist");
		
		taskList.add(new Task());
		taskList.get(6).setToDo("Meet up with supervisor for FYP.");
		taskList.get(6).setOn(null);
		taskList.get(6).setCategory("#FYP");
		
		taskList.add(new Task());
		taskList.get(7).setToDo("Project discussion.");
		taskList.get(7).setOn(null);
		taskList.get(7).setCategory("#FYP");
		
		taskList.add(new Task());
		taskList.get(8).setToDo("Hiking at tree top.");
		taskList.get(8).setOn(null);
		taskList.get(8).setCategory("#friends");
	}
	
	private void setConstraintsOnGridPane() {
		col0 = new ColumnConstraints();
		col0.setPrefWidth(25);
		col1 = new ColumnConstraints();
		col1.setPrefWidth(30);
		col2 = new ColumnConstraints();
		col2.setPrefWidth(330);
		col3 = new ColumnConstraints();
		col3.setPrefWidth(200);
		col4 = new ColumnConstraints();
		col4.setPrefWidth(120);
		col5 = new ColumnConstraints();
		col5.setPrefWidth(60);
		row = new RowConstraints();
		row.setPrefHeight(30);
	}
}
