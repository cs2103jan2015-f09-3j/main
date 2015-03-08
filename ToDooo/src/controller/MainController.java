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
		loadListByDate("All");
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

	public void loadListByDate(String tabName) {
		ArrayList<Task> overdue = new ArrayList<>();
		ArrayList<Task> today = new ArrayList<>();
		ArrayList<Task> floating = new ArrayList<>();
		Calendar c = new GregorianCalendar();
	    c.set(Calendar.HOUR_OF_DAY, 0); 
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    Date todayDate = c.getTime();
	    int indexForNextLoop = 0;
	    
		for(int i = 0; i < taskList.size(); i++) {
			
			if(taskList.get(i).getTaskType().name().equalsIgnoreCase("FLOATING")||
				compareDate(todayDate, getDate(taskList.get(i))) || getDate(taskList.get(i)).before(todayDate)) {
				if(taskList.get(i).getTaskType().name().equalsIgnoreCase("FLOATING")) {
					floating.add(taskList.get(i));
				} else if(taskList.get(i).getTaskType().name().equalsIgnoreCase("EVENT") ||
						taskList.get(i).getTaskType().name().equalsIgnoreCase("DATED")) {
					if(getDate(taskList.get(i)).before(todayDate)) {
						overdue.add(taskList.get(i));
					} else if(compareDate(todayDate, getDate(taskList.get(i)))) {
						today.add(taskList.get(i));
					}
				} else if(taskList.get(i).getTaskType().name().equalsIgnoreCase("TIMED")) {
					if((taskList.get(i).getFrom().before(todayDate) && taskList.get(i).getTo().after(todayDate)) ||
							compareDate(todayDate, taskList.get(i).getFrom()) || 
							compareDate(todayDate, taskList.get(i).getTo())) {
						today.add(taskList.get(i));
					} else if(taskList.get(i).getTo().before(todayDate)) {
						overdue.add(taskList.get(i));
					}
				}
			} else {
				indexForNextLoop = i;
				break;
			}
		} 
		
		if(!today.isEmpty()) {
			for(int d = 0; d < today.size(); d++) {
				if(d == 0) {
					generateList("TODAY", today.get(d), tabName, today.get(d).getTaskType().name());
				} else {
					generateList("", today.get(d), tabName, today.get(d).getTaskType().name());
				}
			}
		}
		
		if(!floating.isEmpty()) {
			for(int a = 0; a < floating.size(); a++) {
				generateList("", floating.get(a), tabName, floating.get(a).getTaskType().name());
			}
		}
		
		if(!overdue.isEmpty()) {
			for(int b = 0; b < overdue.size(); b++) {
				if(b == 0) {
					generateList("OVERDUE", overdue.get(b), tabName, today.get(b).getTaskType().name());
				} else {
					generateList("", overdue.get(b), tabName, today.get(b).getTaskType().name());
				}
			}
		}
		
		String date1 = "";
		String date2 = "";
		
		for(int j = indexForNextLoop; j < taskList.size(); j++) {
			date1 = Constant.DATEOUTPUT.format(getDate(taskList.get(j)));
			
			if(j != indexForNextLoop) {
				date2 = Constant.DATEOUTPUT.format(getDate(taskList.get(j-1)));
			}
			
			if(j == indexForNextLoop || !date1.equals(date2)) {
				generateList(date1, taskList.get(j), tabName, taskList.get(j).getTaskType().name());
			} else {
				generateList("", taskList.get(j), tabName, taskList.get(j).getTaskType().name());
			}
		}
	}
	
	private void generateList(String header, Task t, String tab, String taskType) {
		setConstraintsOnGridPane();
		Label lblTitle = new Label();
		lblTitle.getStyleClass().add("labelTitle");
		lblTitle.setText(header);
		bodyController.vBoxAll.getChildren().add(lblTitle);
		
		GridPane gPane = new GridPane();
		gPane.getColumnConstraints().addAll(col0,col1,col2,col3,col4,col5);
		gPane.getRowConstraints().addAll(row);
		gPane.getStyleClass().add("gridPaneList");
		
		switch(tab) {
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
		gPane.add(checkBoxPane, 0, 0);
		
		Label lblId = new Label(t.getId());
		lblId.getStyleClass().add("labelList");
		Pane paneId = new Pane(lblId);
		paneId.getStyleClass().add("paneList");
		gPane.add(paneId, 1, 0);
		
		Label lblDesc = new Label(t.getToDo());
		lblDesc.getStyleClass().add("labelList");
		Pane paneDesc = new Pane(lblDesc);
		paneDesc.getStyleClass().add("paneList");
		gPane.add(paneDesc, 2, 0);
		
		String output = "";
		
		if(!taskType.equalsIgnoreCase("FLOATING")) {
			DateFormat timeOutput = new SimpleDateFormat("h:mm a");
			output = timeOutput.format(getDate(t));
		} else {
			output = "---";
		}
			
		Label lblDateTime = new Label(output);
		lblDateTime.getStyleClass().add("labelList");
		Pane paneDateTime = new Pane(lblDateTime);
		paneDateTime.getStyleClass().add("paneList");
		gPane.add(paneDateTime, 3, 0);
		
		
		Label lblCategory = new Label(t.getCategory());
		lblCategory.getStyleClass().add("labelList");
		Pane paneCategory = new Pane(lblCategory);
		paneCategory.getStyleClass().add("paneList");
		gPane.add(paneCategory, 4, 0);
		
		Label lblPriority = new Label("###");
		lblPriority.getStyleClass().add("labelList");
		Pane panePriority = new Pane(lblPriority);
		panePriority.getStyleClass().add("paneList");
		gPane.add(panePriority, 5, 0);	
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
		return null;
	}
}
