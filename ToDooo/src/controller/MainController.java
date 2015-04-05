package controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.fortuna.ical4j.model.property.Status;
import application.Constant;
import application.Execution;
import application.Main;
import application.Task;
import application.TaskSorter;
import application.TaskType;
import application.ToDoList;
import application.Undo;
import application.DateParser;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.control.Tab;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
	private SingleSelectionModel<Tab> selectionModel;
	private Popup tutorialPopup;
	private ArrayList<Task> overdue = new ArrayList<>();
	private ArrayList<Task> today = new ArrayList<>();
	private ArrayList<Task> floating = new ArrayList<>();
	private Date todayDate;
	static Stage detailPopup;
	
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
		settingController.init(this);
		
		Execution.mainController = this;
		Execution.headerController = headerController;
	}

	@FXML
	public void onShortcutKey(KeyEvent e) {	
		scrollList(e);		
		revertAction(e); 		
		switchTab(e);		
		navigateView(e);		
		showTutorial(e);
		
	}

	private void scrollList(KeyEvent e) {
		boolean shouldReturn = !(Constant.SHORTCUT_PAGE_DOWN.match(e) ||
								 Constant.SHORTCUT_TA_UNFOCUSED_PAGE_DOWN.match(e) ||
								 Constant.SHORTCUT_PAGE_UP.match(e) ||
								 Constant.SHORTCUT_TA_UNFOCUSED_PAGE_UP.match(e));
		if (shouldReturn) {
			return;
		}
		
		String tabAllId = bodyController.tabAll.getId();
		String tabCategoryId = bodyController.tabCategory.getId();
		String tabPriorityId = bodyController.tabPriority.getId();
		
		Tab selectedTab = selectionModel.getSelectedItem();			
		String selectedTabId = selectedTab.getId();
		ScrollPane targetScrollPane = null;	
		
		if (selectedTabId.equals(tabAllId)) {
			targetScrollPane = bodyController.sPaneAll;
		} else if (selectedTabId.equals(tabCategoryId)) {
			targetScrollPane = bodyController.sPaneCategory;
		} else if (selectedTabId.equals(tabPriorityId)) {
			targetScrollPane = bodyController.sPanePriority;
		}
		
		if (targetScrollPane == null) {
			return;
		}
				
		double currentPosition = targetScrollPane.getVvalue();
		double newPosition = currentPosition;
		
		if (Constant.SHORTCUT_PAGE_DOWN.match(e) || 
			Constant.SHORTCUT_TA_UNFOCUSED_PAGE_DOWN.match(e)) {
			
			newPosition = currentPosition + Constant.POSITION_OFFSET_VERTICAL;
		} else if (Constant.SHORTCUT_PAGE_UP.match(e) || 
				   Constant.SHORTCUT_TA_UNFOCUSED_PAGE_UP.match(e)) {
			
			newPosition = currentPosition - Constant.POSITION_OFFSET_VERTICAL;
		}
		
		targetScrollPane.setVvalue(newPosition);
	}

	private void showTutorial(KeyEvent e) {
		if (Constant.SHORTCUT_TUTORIAL.match(e)) {
			if (tutorialPopup.isFocused()) {
				tutorialPopup.hide();
			} else {
				double positionX = Main.priStage.getX() + 
								   Constant.POSITION_OFFSET_X_POPUP;							
				double positionY = (Main.priStage.getY() - headerController.textArea.getLayoutY()) * 
								   Constant.POSITION_OFFSET_Y_POPUP;
				
				tutorialPopup.show(Main.priStage, positionX, positionY);
			}
		}
	}

	private void navigateView(KeyEvent e) {
		if (Constant.SHORTCUT_GO_BACK.match(e)) {
			executeGoBack();
		} else if (Constant.SHORTCUT_SETTING.match(e)) {
			executeSetting();				
		}
	}

	private void revertAction(KeyEvent e) {
		if (Constant.SHORTCUT_UNDO.match(e)) {
			String systemMsg = executeUndo();
			
			loadListsInTabs();
			setSystemMessage(systemMsg);
		} else if (Constant.SHORTCUT_REDO.match(e)) {
			String systemMsg = executeRedo();
			
			loadListsInTabs();
			setSystemMessage(systemMsg);
		}
	}

	private void switchTab(KeyEvent e) {
		if (Constant.SHORTCUT_TAB_ALL.match(e)) {
			selectionModel.select(Constant.TAB_INDEX_ALL);			
		} else if (Constant.SHORTCUT_TAB_CATEGORY.match(e)) {
			selectionModel.select(Constant.TAB_INDEX_CATEGORY);			
		} else if (Constant.SHORTCUT_TAB_PRIORITY.match(e)) {
			selectionModel.select(Constant.TAB_INDEX_PRIORITY);			
		}
	}
	public void setSystemMessage(String systemMsg) {
		int sysMsgType = checkSystemMsg(systemMsg);
		
		if(sysMsgType == Constant.SYS_MSG_TYPE_SUCCESS) {
			headerController.imgSysMsg.setImage(new Image(Constant.ICON_SUCCESS));
			headerController.lblSysMsg.setTextFill(Constant.COLOR_SUCCESS);
		} else if(sysMsgType == Constant.SYS_MSG_TYPE_ERROR) {
			headerController.imgSysMsg.setImage(new Image(Constant.ICON_ERROR));
			headerController.lblSysMsg.setTextFill(Constant.COLOR_ERROR);
		} else if (sysMsgType == Constant.SYS_MSG_TYPE_FEEDBACK){
			headerController.imgSysMsg.setImage(new Image(Constant.ICON_FEEDBACK));
			headerController.lblSysMsg.setTextFill(Constant.COLOR_FEEDBACK);;
		}
		
		headerController.lblSysMsg.setText(systemMsg);	
		headerController.lblSysMsg.getStyleClass().add(Constant.CSS_CLASS_LABEL_SYSTEM_MSG);
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
    		    	headerController.lblSysMsg.setText(Constant.EMPTY_STRING);	
    		    	headerController.imgSysMsg.setImage(null);
    		    }
    		});
            timer.cancel();
        }
    }

	public void loadListByDate(String displayType) {
		int indexForNextLoop = 0;
		Task task;
	    Date startDate;
	    String taskType;
	    String dateA = Constant.EMPTY_STRING;
		String dateB = Constant.EMPTY_STRING;
		VBox vBox = getContainer(displayType);
		vBox.getChildren().clear();
		ArrayList<Task> taskList = getList(displayType);
		ArrayList<Task> temp = getAppropriateList(displayType, taskList);
		
		today.clear();
		overdue.clear();
		floating.clear();
		todayDate = DateParser.getTodayDate().getTime();
		
	    for(int i = 0; i < temp.size(); i++) {
			task = temp.get(i);
			taskType = task.getTaskType().toString();
			startDate = task.getStartDate();
	    	
			if(taskType.equalsIgnoreCase(TaskType.FLOATING.toString())||
				DateParser.compareDate(todayDate, startDate) || startDate.before(todayDate)) {
				findFloatingOrOverdueOrTodayTask(task, startDate, taskType);
				indexForNextLoop = i+1;
			} else {
				indexForNextLoop = i;
				break;
			}
		} 
		
	    renderOverdueTask(displayType, vBox);
	    renderTodayTask(displayType, vBox);
	    renderFloatingTask(displayType);
		
		for(int j = indexForNextLoop; j < temp.size(); j++) {
			task = temp.get(j);
			
			dateA = Constant.FORMAT_DATE_OUTPUT.format(task.getStartDate());
			
			if(j != indexForNextLoop) {
				dateB = Constant.FORMAT_DATE_OUTPUT.format(temp.get(j-1).getStartDate());
			}
			
			if(j == indexForNextLoop || !dateA.equals(dateB)) {
				renderTaskItem(dateA, task, displayType);
			} else {
				renderTaskItem(Constant.EMPTY_STRING, task, displayType);
			}
		}
	}
	
	public void loadListByCategory(String displayType) {
		bodyController.vBoxCategory.getChildren().clear();
		
		Task task;
		String category;
		ArrayList<Task> taskList = Main.list.getTasks();
		ArrayList<Task> temp = getAppropriateList(displayType, taskList);
		
		for(int i = 0; i < temp.size(); i++) {
			task = temp.get(i);
			category = task.getCategory();
			
			if(i == 0 || !category.equalsIgnoreCase(temp.get(i-1).getCategory())) {
				renderTaskItem(category.toUpperCase(), task, displayType);
			} else {
				renderTaskItem(Constant.EMPTY_STRING, task, displayType);
			}
		}
	}
	
	public void loadListByPriority(String displayType) {
		bodyController.vBoxPriority.getChildren().clear();
		
		Task task;
		String priority;
		ArrayList<Task> taskList = Main.list.getTasks();
		ArrayList<Task> temp = getAppropriateList(displayType, taskList);
		
		for(int i = 0; i < temp.size(); i++) {
			task = temp.get(i);
			priority = task.getPriority().toString();
			
			renderTaskItem(Constant.EMPTY_STRING, task, displayType);
			
			if(i != temp.size()-1 && !priority.equalsIgnoreCase(temp.get(i+1).getPriority().toString())) {
				addHorizontalBar(getContainer(displayType));
			} 
		}
	}
	
	private ArrayList<Task> getAppropriateList(String displayType,
			ArrayList<Task> taskList) {
		ArrayList<Task> temp;
		
		if(displayType.equalsIgnoreCase(Constant.VIEW_NAME_SEARCH_RESULT)) {
			temp = TaskSorter.getTasksSortedByDate(taskList);
		} else if(displayType.equalsIgnoreCase(Constant.TAB_NAME_ALL)) {
			ArrayList<Task> unsortedTemp = ToDoList.generateTaskItems(taskList, Constant.TAB_NAME_ALL);
			temp = TaskSorter.getTasksSortedByDate(unsortedTemp);
		} else if(displayType.equalsIgnoreCase(Constant.TAB_NAME_CATEGORY)) {
			ArrayList<Task> unsortedTemp = ToDoList.generateTaskItems(taskList, Constant.TAB_NAME_CATEGORY);
			temp = TaskSorter.getTasksSortedByCategories(unsortedTemp);
		} else {
			ArrayList<Task> unsortedTemp = ToDoList.generateTaskItems(taskList, Constant.TAB_NAME_PRIORITY);
			temp = TaskSorter.getTasksSortedByPriorities(unsortedTemp);
		}
		
		return temp;
	}

	private void findFloatingOrOverdueOrTodayTask(Task task, Date startDate,
			String taskType) {
		if(taskType.equalsIgnoreCase(TaskType.FLOATING.toString())) {
			floating.add(task);
		} else {
			if(startDate.before(todayDate)) {
				overdue.add(task);
			} else if(DateParser.compareDate(todayDate, startDate)) {
				today.add(task);
			}
		}
	}

	private void renderFloatingTask(String displayType) {
		if(!floating.isEmpty()) {
			renderLists(floating, Constant.EMPTY_STRING, displayType);
		}
	}

	private void renderTodayTask(String displayType, VBox vBox) {
		if(!today.isEmpty()) {
			renderLists(today, Constant.TITLE_TODAY, displayType);
			
			if(!floating.isEmpty()) {
				addHorizontalBar(vBox);
			}
		}
	}

	private void renderOverdueTask(String displayType, VBox vBox) {
		if(!overdue.isEmpty()) {
	    	renderLists(overdue, Constant.TITLE_OVERDUE, displayType);
			
			if(!floating.isEmpty() && today.isEmpty()) {
				addHorizontalBar(vBox);
			}
		}
	}
	
	private VBox getContainer(String displayType) {
		if(displayType.equalsIgnoreCase(Constant.TAB_NAME_ALL)) {
			return bodyController.vBoxAll;
		} else if(displayType.equalsIgnoreCase(Constant.TAB_NAME_CATEGORY)) {
			return bodyController.vBoxCategory;
		} else if(displayType.equalsIgnoreCase(Constant.TAB_NAME_PRIORITY)) {
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
	
	private void renderTaskItem(String header, Task task, String displayType) {
		String taskType = task.getTaskType().toString();
		String status = task.getStatus().toString();
		Date onDate = task.getOn();
		Date byDate = task.getBy();
		Date fromDate = task.getFrom();
		Date toDate = task.getTo();
		Date startDate = task.getStartDate();
		VBox vBox = getContainer(displayType);
		String paneColor = getStyle(task, displayType);
		
		if(!header.equals(Constant.EMPTY_STRING)) {
			addTitle(header, vBox);
		}
		
		BorderPane bPane = addBorderPane(paneColor);
		HBox hBoxLeft = addLeftHBox(bPane);
		HBox hBoxRight = addRightHBox(bPane);
		addIcon(task, hBoxLeft);
		addId(task, hBoxLeft);
		addPriorityBar(task, hBoxLeft);
		addDesc(task, hBoxLeft, displayType);
		
		if(!displayType.equalsIgnoreCase(Constant.TAB_NAME_CATEGORY) || status.equalsIgnoreCase(Status.COMPLETED)) {
			addCategory(task, hBoxLeft);
		}
		
		if(displayType.equalsIgnoreCase(Constant.TAB_NAME_ALL) || 
				displayType.equalsIgnoreCase(Constant.VIEW_NAME_SEARCH_RESULT)) {
			addDateTimeInAllOrSearchResult(taskType, status, displayType, onDate, byDate, fromDate, toDate, startDate,
					hBoxRight, hBoxLeft);
		} else {
			addDateTimeInCategoryOrPriority(taskType, status, displayType, onDate, byDate, fromDate, toDate, startDate,
					hBoxRight, hBoxLeft);
		}
		
		vBox.getChildren().add(bPane);
	}

	private HBox addRightHBox(BorderPane bPane) {
		HBox hBox = new HBox();
		bPane.setRight(hBox);
		return hBox;
	}

	private HBox addLeftHBox(BorderPane bPane) {
		HBox hBox = new HBox();
		bPane.setLeft(hBox);
		return hBox;
	}

	private BorderPane addBorderPane(String paneColor) {
		BorderPane bPane = new BorderPane();
		bPane.getStyleClass().add(paneColor);
		return bPane;
	}
	
	private void renderLists(ArrayList<Task> taskList, String title, String displayType) {
		for(int i = 0; i < taskList.size(); i++) {
			if(i == 0) {
				renderTaskItem(title, taskList.get(i), displayType);
			} else {
				renderTaskItem(Constant.EMPTY_STRING, taskList.get(i), displayType);
			}
		}
	}
	
	private String getStyle(Task task, String displayType) {
		boolean isOverdue = false;
		Date today = DateParser.getTodayDate().getTime();
		String status = task.getStatus().toString();
		String taskType = task.getTaskType().toString();
		
		if(!taskType.equalsIgnoreCase(TaskType.FLOATING.toString())) {
			isOverdue = DateParser.isAfterDate(today, task.getStartDate());
		}
		
		if(status.equalsIgnoreCase(Status.COMPLETED)) {
			return Constant.CSS_CLASS_BORDERPANE_COMPLETED;
		} else if(isOverdue) {
			return Constant.CSS_CLASS_BORDERPANE_OVERDUE;
		} else {
			return Constant.CSS_CLASS_BORDERPANE_ALL;
		}
	}
	
	private void addHorizontalBar(VBox vBox) {
		Line hBar = new Line();	
		
		hBar.setStartX(Constant.POSITION_START_X_HORIZONTAL_BAR);
		hBar.setStartY(Constant.POSITION_START_Y_HORIZONTAL_BAR);
		hBar.setEndX(Constant.POSITION_END_X_HORIZONTAL_BAR);
		hBar.setEndY(Constant.POSITION_END_Y_HORIZONTAL_BAR);
		hBar.getStyleClass().add(Constant.CSS_CLASS_LINE_HORIZONTAL_BAR);
		
		Pane paneHBar = new Pane(hBar);
		paneHBar.getStyleClass().add(Constant.CSS_CLASS_PANE_HORIZONTAL_BAR);
		
		vBox.getChildren().add(paneHBar);
	}
	
	private void addTitle(String header, VBox container) {
		Label lblTitle = new Label();
		if(header.equals(Constant.TITLE_TODAY)) {
			lblTitle.getStyleClass().add(Constant.CSS_CLASS_LABEL_TITLE_TODAY);
		} else if(header.equals(Constant.TITLE_OVERDUE)) {
			lblTitle.getStyleClass().add(Constant.CSS_CLASS_LABEL_TITLE_OVERDUE);
		} else {
			lblTitle.getStyleClass().add(Constant.CSS_CLASS_LABEL_TITLE_DATE);
		}
		lblTitle.setText(header);
		container.getChildren().add(lblTitle);
	}
	
	private void addIcon(Task task, HBox hBox) {
		String imgName = Constant.EMPTY_STRING;
		switch (task.getTaskType()) {
			case EVENT : 
				imgName = Constant.ICON_EVENT;
			break;
			case FLOATING : 
				imgName = Constant.ICON_FLOATING;
			break;
			case TIMED : 
				imgName = Constant.ICON_TIMED;
			break;
			case DATED : 
				imgName = Constant.ICON_DATED;
			break;
		}
		
		Image img = new Image(imgName);
		ImageView icon = new ImageView();
		icon.setImage(img);
		icon.getStyleClass().add(Constant.CSS_CLASS_IMAGEVIEW_ICON_IMAGE);
		icon.setFitHeight(Constant.FIT_HEIGHT_ICON);
        icon.setPreserveRatio(true);
        hBox.getChildren().add(icon);
	}
	
	private void addId(Task task, HBox hBox) {
		Label lblId = new Label(task.getId());
		lblId.getStyleClass().add(Constant.CSS_CLASS_LABEL_ID);
		hBox.getChildren().add(lblId);
	}
	
	private void addPriorityBar(Task task, HBox hBox) {
		Line priorityBar = new Line();
		
		priorityBar.setStartY(Constant.POSITION_START_Y_PRIORITY_BAR);
		priorityBar.getStyleClass().add(Constant.CSS_CLASS_LINE_PRIORITY_BAR);
		
		switch (task.getPriority()) {
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
	
	private void addDesc(Task task, HBox hBox, String displayType) {
		Label lblDesc = new Label(task.getToDo());
		
		setLengthForDesc(lblDesc, task, displayType);
		
		lblDesc.getStyleClass().add(Constant.CSS_CLASS_LABEL_DESCRIPTION);
		hBox.getChildren().add(lblDesc);
	}
	
	private void addCategory(Task task, HBox hBox) {
		String category = task.getCategory();
		Label lblCategory;
		
		if(!category.equalsIgnoreCase(Constant.CATEGORY_UNCATEGORISED)) {
			lblCategory = new Label(task.getCategory());
			lblCategory.getStyleClass().add(Constant.CSS_CLASS_LABEL_CATEGORY);
			hBox.getChildren().add(lblCategory);
		}
	}
	
	private void addDateTimeInAllOrSearchResult(String taskType, String status, String displayType, Date onDate, 
			Date byDate, Date fromDate, Date toDate, Date startDate, HBox hBoxRight, HBox hBoxLeft) {
		SimpleDateFormat dateFormat;
		
		if(status.equalsIgnoreCase(Constant.TITLE_OVERDUE)) {
			dateFormat = Constant.FORMAT_DATE_TIME_OUTPUT;
		} else {
			dateFormat = Constant.FORMAT_TIME_OUTPUT;
		}
		
		if(taskType.equalsIgnoreCase(TaskType.EVENT.toString())) {
			addSingleDateTime(onDate, hBoxRight, Constant.EMPTY_STRING, Constant.FORMAT_TIME_OUTPUT);
		} else if(taskType.equalsIgnoreCase(TaskType.DATED.toString())) {
			addSingleDateTime(byDate, hBoxRight, Constant.STR_BEFORE_DATE_BY, Constant.FORMAT_TIME_OUTPUT);
		} else if(taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
			
			if(DateParser.compareDate(fromDate, toDate)) {
				addDoubleDateTime(fromDate, toDate, hBoxRight, Constant.STR_BEFORE_DATE_FROM, Constant.STR_BEFORE_DATE_TO, 
						Constant.FORMAT_TIME_OUTPUT, displayType);
			} else if(DateParser.compareDate(startDate, fromDate)) {
				addSingleDateTime(fromDate, hBoxRight, Constant.STR_BEFORE_DATE_FROM, Constant.FORMAT_TIME_OUTPUT);
			} else if(DateParser.compareDate(startDate, toDate)) {
				addSingleDateTime(toDate, hBoxRight, Constant.STR_BEFORE_DATE_TO, Constant.FORMAT_TIME_OUTPUT);
			} else {
				addDoubleDateTime(fromDate, toDate, hBoxRight, Constant.STR_BEFORE_DATE_FROM, Constant.STR_BEFORE_DATE_TO, 
						Constant.FORMAT_DATE_OUTPUT_FOR_TIMED_TASK, displayType);
			}
		}
	}
	
	private void addDateTimeInCategoryOrPriority(String taskType, String status, String displayType, Date onDate, 
			Date byDate, Date fromDate, Date toDate, Date startDate, HBox hBoxRight, HBox hBoxLeft) {
		
		if(taskType.equalsIgnoreCase(TaskType.EVENT.toString())) {
			addSingleDateTime(onDate, hBoxRight, Constant.EMPTY_STRING, Constant.FORMAT_DATE_TIME_OUTPUT);
		} else if(taskType.equalsIgnoreCase(TaskType.DATED.toString())) {
			addSingleDateTime(byDate, hBoxRight, Constant.STR_BEFORE_DATE_BY, Constant.FORMAT_DATE_TIME_OUTPUT);
		} else if(taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
			addDoubleDateTime(fromDate, toDate, hBoxRight, Constant.STR_BEFORE_DATE_FROM, Constant.STR_BEFORE_DATE_TO, 
					Constant.FORMAT_DATE_TIME_OUTPUT, displayType);
		}
	}
	
	private void addSingleDateTime(Date date, HBox hBox, String strBeforeTime, SimpleDateFormat dateFormat) {
		Label lbl = new Label(strBeforeTime);
		lbl.getStyleClass().add(Constant.CSS_CLASS_LABEL_BEFORE_TIME);
		
		Label lblDateTime = new Label(dateFormat.format(date));
		lblDateTime.getStyleClass().add(Constant.CSS_CLASS_LABEL_DATETIME);
		
		hBox.getChildren().add(lbl);
		hBox.getChildren().add(lblDateTime);
	}
	
	private void addDoubleDateTime(Date dateA, Date dateB, HBox hBox, String strA, String strB, 
			SimpleDateFormat dateFormat, String displayType) {
		
		Label lblA = new Label(strA);
		lblA.getStyleClass().add(Constant.CSS_CLASS_LABEL_BEFORE_TIME);
		Label lblB = new Label(strB);
		lblB.getStyleClass().add(Constant.CSS_CLASS_LABEL_BEFORE_TIME);
		
		Label lblDateTime1 = new Label(dateFormat.format(dateA));
		lblDateTime1.getStyleClass().add(Constant.CSS_CLASS_LABEL_DATETIME);
		Label lblDateTime2 = new Label(dateFormat.format(dateB));
		lblDateTime2.getStyleClass().add(Constant.CSS_CLASS_LABEL_DATETIME);
		
		hBox.getChildren().add(lblA);
		hBox.getChildren().add(lblDateTime1);
		hBox.getChildren().add(lblB);
		hBox.getChildren().add(lblDateTime2);
	}
	
	private void setLengthForDesc(Label desc, Task task, String displayType) {
		if(displayType.equalsIgnoreCase(Constant.TAB_NAME_ALL) || 
				displayType.equalsIgnoreCase(Constant.VIEW_NAME_SEARCH_RESULT)) {
			switch(task.getTaskType()) {
				case EVENT :
				case DATED :
					desc.setMaxWidth(Constant.WIDTH_MAX_LABEL_EVENT_ALL);
					break;
				case TIMED :
					desc.setMaxWidth(Constant.WIDTH_MAX_LABEL_TIMED_ALL);
					break;
				default :
					desc.setMaxWidth(Constant.WIDTH_MAX_LABEL_FLOATING);
					break;
			}
		} else {
			switch(task.getTaskType()) {
				case EVENT :
				case DATED :
					desc.setMaxWidth(Constant.WIDTH_MAX_LABEL_EVENT_CAT);
					break;
				case TIMED :
					desc.setMaxWidth(Constant.WIDTH_MAX_LABEL_TIMED_CAT);
					break;
				default :
					desc.setMaxWidth(Constant.WIDTH_MAX_LABEL_FLOATING);
					break;
			}
		}
	}

	public void executeSetting() {
		headerController.settingIcon.setVisible(false);
		headerController.backIcon.setVisible(true);
		
		bodyController.anPaneMain.setVisible(false);
		
		settingController.anPaneSetting.setVisible(true);
		searchResultController.anPaneSearchResult.setVisible(false);
	}
	
	public void executeGoBack() {
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
	
	private int checkSystemMsg(String sysMsg) {
		for(int i = 0; i < Constant.SYS_MSG_KEYWORD_SUCCESS.length; i++) {
			if(sysMsg.contains(Constant.SYS_MSG_KEYWORD_SUCCESS[i])) {
				return Constant.SYS_MSG_TYPE_SUCCESS;
			}
		}
		
		for(int j = 0; j < Constant.SYS_MSG_KEYWORD_ERROR.length; j++) {
			if(sysMsg.contains(Constant.SYS_MSG_KEYWORD_ERROR[j])) {
				return Constant.SYS_MSG_TYPE_ERROR;
			}
		}
		
		return Constant.SYS_MSG_TYPE_FEEDBACK;
	}
	
	public void viewDetails(Task task) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Constant.PATH_DETAIL));
        Parent root1 = (Parent) fxmlLoader.load();
		
		detailPopup = new Stage();
		Scene scene = new Scene(root1);
		
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
	        public void handle(KeyEvent ke) {
	            if (Constant.SHORTCUT_DETAIL.match(ke)) {
	                detailPopup.close();
	            }
	        }
	    });
		
		detailPopup.initStyle(StageStyle.UNDECORATED);
		detailPopup.setScene(scene); 
        detailPopup.show();
	}
}
