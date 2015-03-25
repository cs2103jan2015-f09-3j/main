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
import application.TaskSorter;
import application.TaskType;
import application.ToDoList;
import application.Undo;
import application.DateParser;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
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
import javafx.scene.control.Tab;
import javafx.stage.Popup;
import javafx.stage.PopupWindow.AnchorLocation;
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
	private TaskSorter taskSorter = new TaskSorter();
	private SingleSelectionModel<Tab> selectionModel;
	private Popup tutorialPopup;
	
	@FXML
	public void initialize() {
		initControllers();
		loadListsInTabs();
		initTutorialPopup();
		
		selectionModel = bodyController.tPaneMain.
						 getSelectionModel();
	}
	
	private void initTutorialPopup() {
		Image image = new Image(Constant.IMAGE_TUTORIAL);
		ImageView imageView = new ImageView(image);
		
		tutorialPopup = new Popup();
		tutorialPopup.getContent().add(imageView);
	}
	
	private void initControllers() {
		headerController.init(this);
		bodyController.init(this);
		searchResultController.init(this);
	}

	@FXML
	public void onShortcutKey(KeyEvent e) {	
		try {
			if (Constant.SHORTCUT_UNDO.match(e)) {
				String systemMsg = executeUndo();
				
				loadListsInTabs();
				setSystemMessage(systemMsg);
			} 
			
			if (Constant.SHORTCUT_REDO.match(e)) {
				String systemMsg = executeRedo();
				
				loadListsInTabs();
				setSystemMessage(systemMsg);
			} 
			
			if (Constant.SHORTCUT_TAB_ALL.match(e)) {
				selectionModel.select(Constant.TAB_INDEX_ALL);			
			} 
			
			if (Constant.SHORTCUT_TAB_CATEGORY.match(e)) {
				selectionModel.select(Constant.TAB_INDEX_CATEGORY);			
			} 
			
			if (Constant.SHORTCUT_TAB_PRIORITY.match(e)) {
				selectionModel.select(Constant.TAB_INDEX_PRIORITY);			
			}
			
			if (Constant.SHORTCUT_TAB_PRIORITY.match(e)) {
				selectionModel.select(Constant.TAB_INDEX_PRIORITY);			
			}
			
			if (Constant.SHORTCUT_GO_BACK.match(e)) {
				executeGoBack();
			}
			
			if (Constant.SHORTCUT_SETTING.match(e)) {
				executeSetting();				
			}
			
			if (Constant.SHORTCUT_TUTORIAL.match(e)) {
				if (tutorialPopup.isFocused()) {
					tutorialPopup.hide();
				} else {
					double positionX = Main.priStage.getX() - 
									   Constant.POSITION_OFFSET_X_POPUP;
					double positionY = Main.priStage.getY() * 
									   Constant.POSITION_OFFSET_Y_POPUP;
					
					tutorialPopup.show(Main.priStage, positionX, positionY);
				}
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
		
	}
	public void setSystemMessage(String systemMsg) {
		headerController.lblSysMsg.setText(systemMsg);			
		executeSystemMsgTimerTask();
	}
	
	public void loadListsInTabs() {
		loadListByDate(Constant.TAB_NAME_ALL);
		loadListByCategory(Constant.TAB_NAME_CATEGORY);
		loadListByPriority(Constant.TAB_NAME_PRIORITY);
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
		ArrayList<Task> temp;
		
		if(displayType.equalsIgnoreCase(Constant.VIEW_NAME_SEARCH_RESULT)) {
			temp = taskSorter.getTasksSortedByDate(taskList);
		} else {
			ArrayList<Task> unsortedTemp = ToDoList.generateTaskItems(taskList);
			temp = taskSorter.getTasksSortedByDate(unsortedTemp);
		}
		
		ArrayList<Task> overdue = new ArrayList<>();
		ArrayList<Task> today = new ArrayList<>();
		ArrayList<Task> floating = new ArrayList<>();
		
	    Date todayDate = DateParser.getTodayDate().getTime();
		
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
	    	renderLists(overdue, Constant.TITLE_OVERDUE, displayType);
			
			if(!floating.isEmpty() && today.isEmpty()) {
				addHorizontalBar(vBox);
			}
		}
		
		if(!today.isEmpty()) {
			renderLists(today, Constant.TITLE_TODAY, displayType);
			
			if(!floating.isEmpty()) {
				addHorizontalBar(vBox);
			}
		}
		
		if(!floating.isEmpty()) {
			renderLists(floating, "", displayType);
		}
		
		for(int j = indexForNextLoop; j < temp.size(); j++) {
			task = temp.get(j);
			
			date1 = Constant.DATEOUTPUT.format(task.getStartDate());
			
			if(j != indexForNextLoop) {
				date2 = Constant.DATEOUTPUT.format(temp.get(j-1).getStartDate());
			}
			
			if(j == indexForNextLoop || !date1.equals(date2)) {
				renderTaskItem(date1, task, displayType);
			} else {
				renderTaskItem("", task, displayType);
			}
		}
	}
	
	public void loadListByCategory(String displayType) {
		bodyController.vBoxCategory.getChildren().clear();
		
		Task task;
		String category;
		ArrayList<Task> taskList = Main.list.getTasks();
		ArrayList<Task> unsortedTemp = ToDoList.generateTaskItems(taskList);
	
		Task tClass = new Task();
		ArrayList<Task> temp = taskSorter.getTasksSortedByCategories(unsortedTemp);
		
		for(int i = 0; i < temp.size(); i++) {
			task = temp.get(i);
			category = task.getCategory();
			
			if(i == 0 || !category.equalsIgnoreCase(temp.get(i-1).getCategory())) {
				renderTaskItem(category, task, displayType);
			} else {
				renderTaskItem("", task, displayType);
			}
		}
	}
	
	public void loadListByPriority(String displayType) {
		bodyController.vBoxPriority.getChildren().clear();
		
		Task task;
		String priority;
		ArrayList<Task> taskList = Main.list.getTasks();
		ArrayList<Task> unsortedTemp = ToDoList.generateTaskItems(taskList);
	
		Task tClass = new Task();
		ArrayList<Task> temp = taskSorter.getTasksSortedByPriorities(unsortedTemp);
		
		for(int i = 0; i < temp.size(); i++) {
			task = temp.get(i);
			priority = task.getPriority().toString();
			
			if(i == 0 || !priority.equalsIgnoreCase(temp.get(i-1).getPriority().toString())) {
				renderTaskItem(priority, task, displayType);
			} else {
				renderTaskItem("", task, displayType);
			}
		}
	}
	
	private VBox getContainer(String type) {
		if(type.equalsIgnoreCase(Constant.TAB_NAME_ALL)) {
			return bodyController.vBoxAll;
		} else if(type.equalsIgnoreCase(Constant.TAB_NAME_CATEGORY)) {
			return bodyController.vBoxCategory;
		} else if(type.equalsIgnoreCase(Constant.TAB_NAME_PRIORITY)) {
			return bodyController.vBoxPriority;
		} else {
			return searchResultController.vBoxSearchResult;
		}
	}
	
	private ArrayList<Task> getList(String type) {
		if(type.equalsIgnoreCase(Constant.VIEW_NAME_SEARCH_RESULT)) {
			return Main.searchResults;
		} else {
			return Main.list.getTasks();
		}
	}
	
	private void renderTaskItem(String header, Task t, String displayType) {
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
		
		if(!displayType.equalsIgnoreCase(Constant.TAB_NAME_CATEGORY)) {
			addCategory(t, hBox1);
		}
		
		if(taskType.equalsIgnoreCase(TaskType.EVENT.toString())) {
			if(displayType.equalsIgnoreCase(Constant.TAB_NAME_ALL) || 
					displayType.equalsIgnoreCase(Constant.VIEW_NAME_SEARCH_RESULT)) {
				addSingleDateTime(onDate, hBox2, "", Constant.TIMEOUTPUT);
			} else {
				addSingleDateTime(onDate, hBox2, "", Constant.DATETIMEOUTPUT);
			}
			
			
		} else if(taskType.equalsIgnoreCase(TaskType.DATED.toString())) {
			
			if(displayType.equalsIgnoreCase(Constant.TAB_NAME_ALL) || 
					displayType.equalsIgnoreCase(Constant.VIEW_NAME_SEARCH_RESULT)) {
				addSingleDateTime(byDate, hBox2, Constant.STR_BEFORE_DATE_BY, Constant.TIMEOUTPUT);
			} else {
				addSingleDateTime(byDate, hBox2, Constant.STR_BEFORE_DATE_BY, Constant.DATETIMEOUTPUT);
			}
			
		} else if(taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
			
			if(displayType.equalsIgnoreCase(Constant.TAB_NAME_ALL) || 
					displayType.equalsIgnoreCase(Constant.VIEW_NAME_SEARCH_RESULT)) {
				if(DateParser.compareDate(fromDate, toDate)) {
					addDoubleDateTime(fromDate, toDate, hBox2, Constant.STR_BEFORE_DATE_FROM, Constant.STR_BEFORE_DATE_TO, 
							Constant.TIMEOUTPUT, displayType);
				} else if(DateParser.compareDate(startDate, fromDate)) {
					addSingleDateTime(fromDate, hBox2, Constant.STR_BEFORE_DATE_FROM, Constant.TIMEOUTPUT);
				} else if(DateParser.compareDate(startDate, toDate)) {
					addSingleDateTime(toDate, hBox2, Constant.STR_BEFORE_DATE_TO, Constant.TIMEOUTPUT);
				} else {
					addDoubleDateTime(fromDate, toDate, hBox2, Constant.STR_BEFORE_DATE_FROM, Constant.STR_BEFORE_DATE_TO, 
							Constant.DATEOUTPUT_FOR_TIMEDTASK, displayType);
				}
			} else {
				if(DateParser.compareDate(fromDate, toDate)) {
					addDoubleDateTime(fromDate, toDate, hBox2, Constant.STR_BEFORE_DATE_FROM, Constant.STR_BEFORE_DATE_TO, 
							Constant.TIMEOUTPUT, displayType);
				} else {
					addDoubleDateTime(fromDate, toDate, hBox2, Constant.STR_BEFORE_DATE_FROM, Constant.STR_BEFORE_DATE_TO, 
							Constant.DATETIMEOUTPUT, displayType);
				}
			}
			
			
		}
		 
		
		vBox.getChildren().add(bPane);
	}
	
	private void renderLists(ArrayList<Task> al, String title, String displayType) {
		for(int d = 0; d < al.size(); d++) {
			if(d == 0) {
				renderTaskItem(title, al.get(d), displayType);
			} else {
				renderTaskItem("", al.get(d), displayType);
			}
		}
	}
	
	private String getStyle(String displayType) {
		if(displayType.equalsIgnoreCase(Constant.VIEW_NAME_SEARCH_RESULT)) {
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
		if(header.equals(Constant.TITLE_TODAY) || header.equals(Constant.TITLE_OVERDUE)) {
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
		case EVENT: imgName = Constant.ICON_EVENT;
		break;
		case FLOATING: imgName = Constant.ICON_FLOATING;
		break;
		case TIMED: imgName = Constant.ICON_TIMED;
		break;
		case DATED: imgName = Constant.ICON_DATED;
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
			case HIGH : 
				priorityBar.setStroke(Constant.COLOR_PRIORITY_HIGH);
			break;
			case MEDIUM : 
				priorityBar.setStroke(Constant.COLOR_PRIORITY_MEDIUM);
			break;
			case LOW : 
				priorityBar.setStroke(Constant.COLOR_PRIORITY_LOW);
			break;
			case NEUTRAL : 
				priorityBar.setStroke(Constant.COLOR_PRIORITY_NEUTRAL);
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
		String category = t.getCategory();
		
		if(!category.equalsIgnoreCase(Constant.CATEGORY_UNCATEGORISED)) {
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
	
	private void addDoubleDateTime(Date d1, Date d2, HBox hBox, String str1, String str2, 
			SimpleDateFormat f, String displayType) {
		
		if(DateParser.compareDate(d1, d2) && (displayType.equalsIgnoreCase(Constant.TAB_NAME_CATEGORY) || 
				displayType.equalsIgnoreCase(Constant.TAB_NAME_PRIORITY))) {
			Label lblShortDate = new Label(Constant.DATEOUTPUT_SHORT.format(d1));
			lblShortDate.getStyleClass().add("labelBeforeTime");
			hBox.getChildren().add(lblShortDate);
		}
		
		Label lbl1 = new Label(str1);
		lbl1.getStyleClass().add("labelBeforeTime");
		Label lbl2 = new Label("  " + str2);
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

	public void executeSetting() throws IOException {
		headerController.settingIcon.setVisible(false);
		headerController.backIcon.setVisible(true);
		
		bodyController.anPaneMain.setVisible(false);
		
		settingController.anPaneSetting.setVisible(true);
		searchResultController.anPaneSearchResult.setVisible(false);
	}
	
	public void executeGoBack() throws IOException {
		headerController.settingIcon.setVisible(true);
		headerController.backIcon.setVisible(false);
		
		bodyController.anPaneMain.setVisible(true);
		
		settingController.anPaneSetting.setVisible(false);
		searchResultController.anPaneSearchResult.setVisible(false);
	}
	
	public void executeSearchResult() {
		headerController.settingIcon.setVisible(false);
		headerController.backIcon.setVisible(true);
		
		bodyController.anPaneMain.setVisible(false);		
		settingController.anPaneSetting.setVisible(false);
		
		searchResultController.anPaneSearchResult.setVisible(true);
		searchResultController.loadSearchList();
	}
	
}
