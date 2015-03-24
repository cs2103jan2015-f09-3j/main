package controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import application.Constant;
import application.Main;
import application.Task;
import application.TaskType;
import application.ToDoList;
import application.Undo;
import application.DateParser;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import controller.HeaderController;
import controller.BodyController;

public class MainController{
	
	@FXML AnchorPane anPaneHeader;
	@FXML AnchorPane anPaneBody;
	@FXML HeaderController headerController;
	@FXML BodyController bodyController;
	@FXML SettingController settingController;
	@FXML SearchResultController searchResultController;
	private Timer timer;
	
	@FXML
	public void initialize() {
		headerController.init(this);
		bodyController.init(this);
		searchResultController.init(this);
		loadListByDate("main");
	}

	@FXML
	public void onShortcutKey(KeyEvent e) {	
		String systemMsg = null;
		
		if (Constant.SHORTCUT_UNDO.match(e)) {
			systemMsg = executeUndo();
			loadListByDate("main");
		} else if (Constant.SHORTCUT_REDO.match(e)) {
			systemMsg = executeRedo();
			loadListByDate("main");
		} else {
			return;
		}
		
		headerController.lblSysMsg.setText(systemMsg);	
		
		executeSystemMsgTimerTask();		
	}
	
	public void showPageInBody(String fxmlFileName) throws IOException {
		anPaneBody.getChildren().clear();
		anPaneBody.getChildren().setAll(FXMLLoader.load(getClass().getResource(fxmlFileName)));
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
	
	public void executeSystemMsgTimerTask() {
		timer = new Timer();
		timer.schedule(new SystemMsgTimerTask(), Constant.TIMER_SYSTEM_MSG_DURATION);
	}
	
	private class SystemMsgTimerTask extends TimerTask {
        public void run() {
    		Platform.runLater(new Runnable() {
    		    @Override
    		    public void run() {
    		    	headerController.lblSysMsg.setText("");					
    		    }
    		});
            timer.cancel();
        }
    }

	public void loadListByDate(String displayType) {
		VBox vBox = getContainer(displayType);
		
		vBox.getChildren().clear();
		
		int indexForNextLoop = 0;
		Task task;
	    Date startDate;
	    String taskType;
	    String date1 = "";
		String date2 = "";
		
		ArrayList<Task> taskList = getList(displayType);
		ArrayList<Task> unsortedTemp = ToDoList.generateTaskListForView(taskList);
		ArrayList<Task> overdue = new ArrayList<>();
		ArrayList<Task> today = new ArrayList<>();
		ArrayList<Task> floating = new ArrayList<>();
	
		Task tClass = new Task();
		ArrayList<Task> temp = tClass.viewListByAll(unsortedTemp);
		
	    Date todayDate = getTodayDate().getTime();
		
	    for(int i = 0; i < temp.size(); i++) {
			task = temp.get(i);
			taskType = task.getTaskType().toString();
			startDate = task.getStartDate();
	    	
			if(taskType.equalsIgnoreCase(TaskType.FLOATING.toString())||
				DateParser.compareDate(todayDate, startDate) || startDate.before(todayDate)) {
				
				if(taskType.equalsIgnoreCase(TaskType.FLOATING.toString())) {
					floating.add(task);
				} else {
					if(startDate.before(todayDate)) {
						overdue.add(task);
					} else if(DateParser.compareDate(todayDate, startDate)) {
						today.add(task);
					}
				}
				
				indexForNextLoop = i+1;
			} else {
				indexForNextLoop = i;
				break;
			}
		} 
		
	    if(!overdue.isEmpty()) {
			printList(overdue, Constant.OVERDUE_TITLE, displayType);
			
			if(!floating.isEmpty() && today.isEmpty()) {
				addHorizontalBar(vBox);
			}
		}
		
		if(!today.isEmpty()) {
			printList(today, Constant.TODAY_TITLE, displayType);
			
			if(!floating.isEmpty()) {
				addHorizontalBar(vBox);
			}
		}
		
		if(!floating.isEmpty()) {
			printList(floating, "", displayType);
		}
		
		for(int j = indexForNextLoop; j < temp.size(); j++) {
			task = temp.get(j);
			
			date1 = Constant.DATEOUTPUT.format(task.getStartDate());
			
			if(j != indexForNextLoop) {
				date2 = Constant.DATEOUTPUT.format(temp.get(j-1).getStartDate());
			}
			
			if(j == indexForNextLoop || !date1.equals(date2)) {
				generateListByDate(date1, task, displayType);
			} else {
				generateListByDate("", task, displayType);
			}
		}
	}
	
	private VBox getContainer(String type) {
		if(type.equalsIgnoreCase("main")) {
			return bodyController.vBoxAll;
		} else if(type.equalsIgnoreCase("category")) {
			return bodyController.vBoxCategory;
		} else if(type.equalsIgnoreCase("priority")) {
			return bodyController.vBoxPriority;
		} else {
			return searchResultController.vBoxSearchResult;
		}
	}
	
	private ArrayList<Task> getList(String type) {
		if(type.equalsIgnoreCase("main")) {
			return Main.list.getTasks();
		} else {
			return Main.searchResults;
		}
	}
	
	private Calendar getTodayDate() {
		Calendar c = new GregorianCalendar();
		
	    c.set(Calendar.HOUR_OF_DAY, 0); 
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    
	    return c;
	}
	
	private void generateListByDate(String header, Task t, String displayType) {
		String taskType = t.getTaskType().toString();
		Date onDate = t.getOn();
		Date byDate = t.getBy();
		Date fromDate = t.getFrom();
		Date toDate = t.getTo();
		Date startDate = t.getStartDate();
		VBox vBox = getContainer(displayType);
		String paneColor = getStyle(displayType);
		
		if(!header.equals("")) {
			addTitle(header, vBox);
		}
		
		BorderPane bPane = new BorderPane();
		bPane.getStyleClass().add(paneColor);
		
		HBox hBox1 = new HBox();
		bPane.setLeft(hBox1);
		
		HBox hBox2 = new HBox();
		bPane.setRight(hBox2);
		
		addIcon(t, hBox1);
		addId(t, hBox1);
		addPriorityBar(t, hBox1);
		addDesc(t, hBox1);
		addCategory(t, hBox1);
		
		if(!taskType.equalsIgnoreCase(TaskType.FLOATING.toString())) {
			
			if(taskType.equalsIgnoreCase(TaskType.EVENT.toString())) {
				
				addSingleDateTime(onDate, hBox2, "", Constant.TIMEOUTPUT);
				
			} else if(taskType.equalsIgnoreCase(TaskType.DATED.toString())) {
				
				addSingleDateTime(byDate, hBox2, "by", Constant.TIMEOUTPUT);
				
			} else if(taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
				
				if(DateParser.compareDate(fromDate, toDate)) {
					addDoubleDateTime(fromDate, toDate, hBox2, "from", "to", Constant.TIMEOUTPUT);
				} else if(DateParser.compareDate(t.getStartDate(), t.getFrom())) {
					addSingleDateTime(t.getFrom(), hBox2, "from", Constant.TIMEOUTPUT);
				} else if(DateParser.compareDate(startDate, toDate)) {
					addSingleDateTime(toDate, hBox2, "to", Constant.TIMEOUTPUT);
				} else {
					addDoubleDateTime(fromDate, toDate, hBox2, "from", "to", Constant.DATEOUTPUT_FOR_TIMEDTASK);
				}
				
			}
		} 
		
		vBox.getChildren().add(bPane);
	}
	
	private void printList(ArrayList<Task> al, String title, String displayType) {
		for(int d = 0; d < al.size(); d++) {
			if(d == 0) {
				generateListByDate(title, al.get(d), displayType);
			} else {
				generateListByDate("", al.get(d), displayType);
			}
		}
	}
	
	private String getStyle(String displayType) {
		if(displayType.equalsIgnoreCase("searchResult")) {
			return "bPaneSearchResult";
		} else {
			return "bPaneAll";
		}
	}
	
	private void addHorizontalBar(VBox vBox) {
		Line hBar = new Line();
		hBar.setStartX(4.5);
		hBar.setStartY(0.5);
		hBar.setEndX(760);
		hBar.setEndY(0.5);
		hBar.getStyleClass().add("hBar");
		Pane paneHBar = new Pane(hBar);
		paneHBar.getStyleClass().add("paneHBar");
		vBox.getChildren().add(paneHBar);
	}
	
	private void addTitle(String header, VBox container) {
		Label lblTitle = new Label();
		if(header.equals(Constant.TODAY_TITLE) || header.equals(Constant.OVERDUE_TITLE)) {
			lblTitle.getStyleClass().add("todayTitle");
		} else {
			lblTitle.getStyleClass().add("dateTitle");
		}
		lblTitle.setText(header);
		container.getChildren().add(lblTitle);
	}
	
	private void addIcon(Task t, HBox hBox) {
		String imgName = "";
		switch (t.getTaskType()) {
		case EVENT: imgName = Constant.EVENT_ICON;
		break;
		case FLOATING: imgName = Constant.FLOATING_ICON;
		break;
		case TIMED: imgName = Constant.TIMED_ICON;
		break;
		case DATED: imgName = Constant.DATED_ICON;
		break;
		}
		
		Image img = new Image(imgName);
		ImageView icon = new ImageView();
		icon.setImage(img);
		icon.getStyleClass().add("iconImage");
		icon.setFitHeight(20);
        icon.setPreserveRatio(true);
        hBox.getChildren().add(icon);
	}
	
	private void addId(Task t, HBox hBox) {
		Label lblId = new Label(t.getId());
		lblId.getStyleClass().add("labelId");
		hBox.getChildren().add(lblId);
	}
	
	private void addPriorityBar(Task t, HBox hBox) {
		Line priorityBar = new Line();
		priorityBar.setStartY(18);
		priorityBar.getStyleClass().add("priorityBar");
		switch (t.getPriority()) {
			case HIGH: priorityBar.setStroke(Constant.HIGH_PRIORITY);
			break;
			case MEDIUM: priorityBar.setStroke(Constant.MEDIUM_PRIORITY);
			break;
			case LOW: priorityBar.setStroke(Constant.LOW_PRIORITY);
			break;
			case NEUTRAL: priorityBar.setStroke(Constant.NEUTRAL_PRIORITY);
			break;
		}
		
		hBox.getChildren().add(priorityBar);
	}
	
	private void addDesc(Task t, HBox hBox) {
		Label lblDesc = new Label(t.getToDo());
		lblDesc.getStyleClass().add("labelDesc");
		hBox.getChildren().add(lblDesc);
	}
	
	private void addCategory(Task t, HBox hBox) {
		if(!t.getCategory().equalsIgnoreCase(Constant.CATEGORY_UNCATEGORISED)) {
			Label lblCategory = new Label(t.getCategory());
			lblCategory.getStyleClass().add("labelCategory");
			hBox.getChildren().add(lblCategory);
		}
	}
	
	private void addSingleDateTime(Date d, HBox hBox, String strBeforeTime, SimpleDateFormat f) {
		Label lbl = new Label(strBeforeTime);
		lbl.getStyleClass().add("labelBeforeTime");
		
		Label lblDateTime = new Label(f.format(d));
		lblDateTime.getStyleClass().add("labelDateTime");
		
		hBox.getChildren().add(lbl);
		hBox.getChildren().add(lblDateTime);
	}
	
	private void addDoubleDateTime(Date d1, Date d2, HBox hBox, String str1, String str2, SimpleDateFormat f) {
		Label lbl1 = new Label(str1);
		lbl1.getStyleClass().add("labelBeforeTime");
		Label lbl2 = new Label(str2);
		lbl2.getStyleClass().add("labelBeforeTime");
		
		Label lblDateTime1 = new Label(f.format(d1));
		lblDateTime1.getStyleClass().add("labelDateTime");
		Label lblDateTime2 = new Label(f.format(d2));
		lblDateTime2.getStyleClass().add("labelDateTime");
		
		hBox.getChildren().add(lbl1);
		hBox.getChildren().add(lblDateTime1);
		hBox.getChildren().add(lbl2);
		hBox.getChildren().add(lblDateTime2);
	}
}
