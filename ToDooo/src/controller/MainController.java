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

import net.fortuna.ical4j.model.property.Status;
import application.Constant;
import application.Main;
import application.Priority;
import application.Task;
import application.TaskSorter;
import application.TaskType;
import application.ToDoList;
import application.Undo;
import application.DateParser;
import javafx.application.Platform;
import javafx.event.Event;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.control.Tab;
import javafx.stage.Popup;
import javafx.stage.PopupWindow.AnchorLocation;
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
	}

	@FXML
	public void onShortcutKey(KeyEvent e) {	
		scrollList(e);		
		revertAction(e); 		
		switchTab(e);		
		navigateView(e);		
		showTutorial(e);
		//closeDetailPopup(e);
		
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
		
		if(sysMsgType == 1) {
			headerController.imgSysMsg.setImage(new Image(Constant.ICON_SUCCESS));
			headerController.lblSysMsg.setTextFill(Constant.COLOR_SUCCESS);
		} else if(sysMsgType == -1) {
			headerController.imgSysMsg.setImage(new Image(Constant.ICON_ERROR));
			headerController.lblSysMsg.setTextFill(Constant.COLOR_ERROR);
		} else if (sysMsgType == 0){
			headerController.imgSysMsg.setImage(new Image(Constant.ICON_FEEDBACK));
			headerController.lblSysMsg.setTextFill(Constant.COLOR_FEEDBACK);;
		}
		
		headerController.lblSysMsg.setText(systemMsg);	
		headerController.lblSysMsg.getStyleClass().add("labelSysMsg");
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
	    String dateA = "";
		String dateB = "";
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
				renderTaskItem("", task, displayType);
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
				renderTaskItem("", task, displayType);
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
			
			renderTaskItem("", task, displayType);
			
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
			renderLists(floating, "", displayType);
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
		String status = t.getStatus().toString();
		Date onDate = t.getOn();
		Date byDate = t.getBy();
		Date fromDate = t.getFrom();
		Date toDate = t.getTo();
		Date startDate = t.getStartDate();
		VBox vBox = getContainer(displayType);
		String paneColor = getStyle(t, displayType);
		
		if(!header.equals("")) {
			addTitle(header, vBox);
		}
		
		BorderPane bPane = addBorderPane(paneColor);
		HBox hBoxLeft = addLeftHBox(bPane);
		HBox hBoxRight = addRightHBox(bPane);
		addIcon(t, hBoxLeft);
		addId(t, hBoxLeft);
		addPriorityBar(t, hBoxLeft);
		addDesc(t, hBoxLeft, displayType);
		
		if(!displayType.equalsIgnoreCase(Constant.TAB_NAME_CATEGORY) || status.equalsIgnoreCase(Status.COMPLETED)) {
			addCategory(t, hBoxLeft);
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
		HBox hBox2 = new HBox();
		bPane.setRight(hBox2);
		return hBox2;
	}

	private HBox addLeftHBox(BorderPane bPane) {
		HBox hBox1 = new HBox();
		bPane.setLeft(hBox1);
		return hBox1;
	}

	private BorderPane addBorderPane(String paneColor) {
		BorderPane bPane = new BorderPane();
		bPane.getStyleClass().add(paneColor);
		return bPane;
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
	
	private String getStyle(Task t, String displayType) {
		boolean isOverdue = false;
		Date today = DateParser.getTodayDate().getTime();
		String status = t.getStatus().toString();
		String taskType = t.getTaskType().toString();
		
		if(!taskType.equalsIgnoreCase(TaskType.FLOATING.toString())) {
			isOverdue = DateParser.isAfterDate(today, t.getStartDate());
		}
		
		if(status.equalsIgnoreCase(Status.COMPLETED)) {
			return "bPaneCompleted";
		} else if(isOverdue) {
			return "bPaneOverdue";
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
		if(header.equals(Constant.TITLE_TODAY)) {
			lblTitle.getStyleClass().add("todayTitle");
		} else if(header.equals(Constant.TITLE_OVERDUE)) {
			lblTitle.getStyleClass().add("overdueTitle");
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
	
	private void addDesc(Task t, HBox hBox, String displayType) {
		Label lblDesc = new Label(t.getToDo());
		
		setLengthForDesc(lblDesc, t, displayType);
		
		lblDesc.getStyleClass().add("labelDesc");
		hBox.getChildren().add(lblDesc);
	}
	
	private void addCategory(Task t, HBox hBox) {
		String category = t.getCategory();
		Label lblCategory;
		
		if(!category.equalsIgnoreCase(Constant.CATEGORY_UNCATEGORISED)) {
			lblCategory = new Label(t.getCategory());
			lblCategory.getStyleClass().add("labelCategory");
			hBox.getChildren().add(lblCategory);
		}
	}
	
	private void addDateTimeInAllOrSearchResult(String taskType, String status, String displayType, Date onDate, 
			Date byDate, Date fromDate, Date toDate, Date startDate, HBox hBoxRight, HBox hBoxLeft) {
		
		if(taskType.equalsIgnoreCase(TaskType.EVENT.toString())) {
			addSingleDateTime(onDate, hBoxRight, "", Constant.FORMAT_TIME_OUTPUT);
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
			addSingleDateTime(onDate, hBoxRight, "", Constant.FORMAT_DATE_TIME_OUTPUT);
		} else if(taskType.equalsIgnoreCase(TaskType.DATED.toString())) {
			addSingleDateTime(byDate, hBoxRight, Constant.STR_BEFORE_DATE_BY, Constant.FORMAT_DATE_TIME_OUTPUT);
		} else if(taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
			addDoubleDateTime(fromDate, toDate, hBoxRight, Constant.STR_BEFORE_DATE_FROM, Constant.STR_BEFORE_DATE_TO, 
					Constant.FORMAT_DATE_TIME_OUTPUT, displayType);
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
				return 1;
			}
		}
		
		for(int j = 0; j < Constant.SYS_MSG_KEYWORD_ERROR.length; j++) {
			if(sysMsg.contains(Constant.SYS_MSG_KEYWORD_ERROR[j])) {
				return -1;
			}
		}
		
		return 0;
	}
	
	public void viewDetails(Task task) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Detail.fxml"));
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
		
		detailPopup.setScene(scene); 
        detailPopup.show();
	}
}
