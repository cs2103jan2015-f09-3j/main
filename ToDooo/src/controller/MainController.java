package controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import application.Constant;
import application.Main;
import application.Task;
import application.Undo;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import controller.HeaderController;
import controller.BodyController;

public class MainController{
	
	private ArrayList<Task> taskList = Main.list.getTasks();
	//private ArrayList<Task> categoryList = Main.list.getCategories();
	//private ArrayList<Task> priorityList = new ArrayList<>();
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
		loadList("All", taskList);
	}

	@FXML
	public void onShortcutKey(KeyEvent e) {	
		String systemMsg = null;
		
		if (Constant.SHORTCUT_UNDO.match(e)) {
			systemMsg = executeUndo();
		} else if (Constant.SHORTCUT_REDO.match(e)) {
			systemMsg = executeRedo();
		} else {
			return;
		}
		
		headerController.txtCmd.getParent().requestFocus();
		headerController.lblSysMsg.setText(systemMsg);
	}
	
	private Date getDate(Task t) {
		if(t.getOn() != null) {
			return t.getOn();
		} else if(t.getBy() != null) {
			return t.getBy();
		} else if (t.getFrom() != null && t.getTo() != null) {
			return t.getFrom();
		} else {
			return null;
		}
	}
	
	private boolean compareDate(Date d1, Date d2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(d1);
		cal2.setTime(d2);
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		
		return sameDay;
	}

	public void loadList(String tabName, ArrayList<Task> list) {
	
	setConstraintsOnGridPane();
	
	for(int i = 0; i < list.size(); i++) {
		if(i == 0 || list.get(i).getCategory() != list.get(i-1).getCategory()) {
			Label lblTitle = new Label(list.get(i).getCategory());
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
		
		Label lblDateTime = new Label(getDate(taskList.get(i)).toString());
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

	private String executeUndo() {
		String systemMsg = null;
		
		boolean canUndo = !(Main.undos.isEmpty());
		if (canUndo) {
			Undo undo = Main.undos.pop();
			systemMsg = undo.undoAction();
		} else {
			systemMsg = Constant.MSG_NO_UNDO;
		}
		
		return systemMsg;
	}
	
	private String executeRedo() {
		String systemMsg = null;
		
		boolean canRedo = !(Main.redos.isEmpty());
		if (canRedo) {
			Undo redo = Main.redos.pop();
			systemMsg = redo.redoAction();
		} else {
			systemMsg = Constant.MSG_NO_REDO;
		}
		
		return systemMsg;
	}
}
