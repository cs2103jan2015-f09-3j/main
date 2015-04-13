package controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import application.Constant;
import application.Execution;
import application.Frequency;
import application.Main;
import application.Task;
import application.TaskSorter;
import application.TaskStatus;
import application.TaskType;
import application.ToDoList;
import application.DateParser;
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

	private SingleSelectionModel<Tab> selectionModel;
	private Popup tutorialPopup;
	private ArrayList<Task> overdue = new ArrayList<>();
	private ArrayList<Task> today = new ArrayList<>();
	private ArrayList<Task> floating = new ArrayList<>();
	private Date todayDate;
	static Stage detailPopup;
	
	// -----------------------------------------------------------------------------------------------
	// Private methods
	// -----------------------------------------------------------------------------------------------
	//@author A0112498B
	@FXML
	public void initialize() {
		initControllers();
		cleanCompletedTasks();
		Execution.executeCleanCategories();
		loadListsInTabs();
		initTutorialPopup();
		
		selectionModel = bodyController.tPaneMain.
						 getSelectionModel();
		
		Execution.executeStatusCheckTimerTask();
	}
	
	@FXML
	public void onShortcutKey(KeyEvent keyEvent) {	
		scrollList(keyEvent);		
		revertAction(keyEvent); 		
		switchTab(keyEvent);		
		navigateView(keyEvent);		
		showTutorial(keyEvent);
		showOpenFileDialog(keyEvent);
	}
	
	public void showCorrectView() {
		if (settingController.anPaneSetting.isVisible() ||
			searchResultController.anPaneSearchResult.isVisible()) {
			executeGoBack();
		}
	}
	
	//@author A0112537M
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
		headerController.lblSysMsg.getStyleClass().
		add(Constant.CSS_CLASS_LABEL_SYSTEM_MSG);
		Execution.executeSystemMsgTimerTask();
	}

	public void loadListByDate(String displayType) {
		int indexForNextLoop = 0;
		VBox vBox = getContainer(displayType);
		vBox.getChildren().clear();
		ArrayList<Task> taskList = getListForDisplay(displayType);
		ArrayList<Task> temp = getListForDisplay(displayType, taskList);
		
		today.clear();
		overdue.clear();
		floating.clear();
		todayDate = DateParser.getTodayDate().getTime();
		
	    indexForNextLoop = organiseTasks(indexForNextLoop, temp); 
		
	    renderOverdueTask(displayType, vBox);
	    renderTodayTask(displayType, vBox);
	    renderFloatingTask(displayType);
		
		renderTasksWithDate(displayType, indexForNextLoop, temp);
	}
	
	public void loadListByCategory(String displayType) {
		bodyController.vBoxCategory.getChildren().clear();
		
		Task task;
		String category;
		ArrayList<Task> taskList = Main.list.getTasks();
		ArrayList<Task> temp = getListForDisplay(displayType, taskList);
		
		for(int i = 0; i < temp.size(); i++) {
			task = temp.get(i);
			category = task.getCategory();
			
			if(i == 0 || !category.equalsIgnoreCase(temp.get(i - 1).getCategory())) {
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
		ArrayList<Task> temp = getListForDisplay(displayType, taskList);
		
		for(int i = 0; i < temp.size(); i++) {
			task = temp.get(i);
			priority = task.getPriority().toString();
			
			renderTaskItem(Constant.EMPTY_STRING, task, displayType);
			
			if(i != temp.size()-1 && 
			   !priority.equalsIgnoreCase(temp.get(i + 1).getPriority().toString())) {
				addHorizontalBar(getContainer(displayType));
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
	
	public void cleanCompletedTasks() {
		int dayOfWeek;
		int dayOfMonth;
		String cleanRecurrence = Main.storage.readSaveCleanRecurrence();
		Calendar today = DateParser.getTodayDate();
		
		if(cleanRecurrence.equalsIgnoreCase(Frequency.WEEKLY.toString())) {
			dayOfWeek = DateParser.calculateDayOfWeek(today);
			
			if(dayOfWeek == 1) {
				Execution.executeCleanCompletedTasks();
			}
		} else if(cleanRecurrence.equalsIgnoreCase(Frequency.MONTHLY.toString())) {
			dayOfMonth = DateParser.calculateDayOfMonth(today);
			
			if(dayOfMonth == 1) {
				Execution.executeCleanCompletedTasks();
			}
		}
	}
		
	public void loadListsInTabs() {		
		loadListByDate(Constant.TAB_NAME_ALL);
		loadListByCategory(Constant.TAB_NAME_CATEGORY);
		loadListByPriority(Constant.TAB_NAME_PRIORITY);
	}
	
	// -----------------------------------------------------------------------------------------------
	// Private methods
	// -----------------------------------------------------------------------------------------------
	//@author A0112498B
	private void initTutorialPopup() {
		InputStream inputStream = getClass().getResourceAsStream(Constant.IMAGE_TUTORIAL);
		Image image = new Image(inputStream);
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
		Execution.settingController = settingController;
	}

	private void scrollList(KeyEvent keyEvent) {
		boolean shouldReturn = !(Constant.SHORTCUT_PAGE_DOWN.match(keyEvent) ||
								 Constant.SHORTCUT_TA_UNFOCUSED_PAGE_DOWN.match(keyEvent) ||
								 Constant.SHORTCUT_PAGE_UP.match(keyEvent) ||
								 Constant.SHORTCUT_TA_UNFOCUSED_PAGE_UP.match(keyEvent));
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
				
		setScrollPaneVvalue(keyEvent, targetScrollPane);
	}

	/*
	 * Set the position of the scroll pane based on
	 * the key pressed
	 */
	private void setScrollPaneVvalue(KeyEvent keyEvent, ScrollPane targetScrollPane) {
		double currentPosition = targetScrollPane.getVvalue();
		double newPosition = currentPosition;
		
		if (Constant.SHORTCUT_PAGE_DOWN.match(keyEvent) || 
			Constant.SHORTCUT_TA_UNFOCUSED_PAGE_DOWN.match(keyEvent)) {
			
			newPosition = currentPosition + Constant.POSITION_OFFSET_VERTICAL;
		} else if (Constant.SHORTCUT_PAGE_UP.match(keyEvent) || 
				   Constant.SHORTCUT_TA_UNFOCUSED_PAGE_UP.match(keyEvent)) {
			
			newPosition = currentPosition - Constant.POSITION_OFFSET_VERTICAL;
		}
		
		targetScrollPane.setVvalue(newPosition);
	}

	private void showOpenFileDialog(KeyEvent keyEvent) {
		if (Constant.SHORTCUT_OPEN_FILE_DIALOG.match(keyEvent)) {
			if (settingController.anPaneSetting.isVisible()) {
				settingController.openFileDialog();
			}
			
		}
	}
	
	private void showTutorial(KeyEvent keyEvent) {
		if (Constant.SHORTCUT_TUTORIAL.match(keyEvent)) {
			if (tutorialPopup.isFocused()) {
				tutorialPopup.hide();
			} else {
				double positionX = Main.priStage.getX() + 
								   Constant.POSITION_OFFSET_X_POPUP;							
				double positionY = (Main.priStage.getY() - 
									headerController.textArea.getLayoutY()) * 
								    Constant.POSITION_OFFSET_Y_POPUP;
				
				tutorialPopup.show(Main.priStage, positionX, positionY);
			}
		}
	}

	private void navigateView(KeyEvent keyEvent) {
		if (Constant.SHORTCUT_GO_BACK.match(keyEvent)) {
			executeGoBack();
		} else if (Constant.SHORTCUT_SETTING.match(keyEvent)) {
			executeSetting();				
		}
	}
	

	private void revertAction(KeyEvent keyEvent) {
		if (Constant.SHORTCUT_UNDO.match(keyEvent)) {
			String systemMsg = Execution.executeUndo();
			
			loadListsInTabs();
			setSystemMessage(systemMsg);
		} else if (Constant.SHORTCUT_REDO.match(keyEvent)) {
			String systemMsg = Execution.executeRedo();
			
			loadListsInTabs();
			setSystemMessage(systemMsg);
		}
	}
		

	private void switchTab(KeyEvent keyEvent) {
		if (Constant.SHORTCUT_TAB_ALL.match(keyEvent)) {
			selectionModel.select(Constant.TAB_INDEX_ALL);	
			
		} else if (Constant.SHORTCUT_TAB_CATEGORY.match(keyEvent)) {
			selectionModel.select(Constant.TAB_INDEX_CATEGORY);		
			
		} else if (Constant.SHORTCUT_TAB_PRIORITY.match(keyEvent)) {
			selectionModel.select(Constant.TAB_INDEX_PRIORITY);			
		}
	}
	
	
	//@author A0112537M	
	private ArrayList<Task> getListForDisplay(String displayType,
											  ArrayList<Task> taskList) {
		ArrayList<Task> temp = null;
		
		if(displayType.equalsIgnoreCase(Constant.VIEW_NAME_SEARCH_RESULT)) {
			temp = TaskSorter.getTasksSortedByDate(taskList);
			
		} else if(displayType.equalsIgnoreCase(Constant.TAB_NAME_ALL)) {
			ArrayList<Task> unsortedTemp = 
					ToDoList.generateTaskItems(taskList, Constant.TAB_NAME_ALL);
			temp = TaskSorter.getTasksSortedByDate(unsortedTemp);
			
		} else if(displayType.equalsIgnoreCase(Constant.TAB_NAME_CATEGORY)) {
			ArrayList<Task> unsortedTemp = 
					ToDoList.generateTaskItems(taskList, Constant.TAB_NAME_CATEGORY);
			temp = TaskSorter.getTasksSortedByCategories(unsortedTemp);
			
		} else {
			ArrayList<Task> unsortedTemp = 
					ToDoList.generateTaskItems(taskList, Constant.TAB_NAME_PRIORITY);
			temp = TaskSorter.getTasksSortedByPriorities(unsortedTemp);
		}
		
		return temp;
	}
	
	
	private void renderTasksWithDate(String displayType,
									 int indexForNextLoop, ArrayList<Task> temp) {
		Task task = null;
		String dateA = Constant.EMPTY_STRING;
		String dateB = Constant.EMPTY_STRING;
		
		for(int j = indexForNextLoop; j < temp.size(); j++) {
			task = temp.get(j);
			
			dateA = Constant.FORMAT_DATE_OUTPUT.format(task.getStartDate());
			
			if(j != indexForNextLoop) {
				dateB = Constant.FORMAT_DATE_OUTPUT.format(temp.get(j - 1).getStartDate());
			}
			
			if(j == indexForNextLoop || !dateA.equals(dateB)) {
				renderTaskItem(dateA, task, displayType);
			} else {
				renderTaskItem(Constant.EMPTY_STRING, task, displayType);
			}
		}
	}
	

	private int organiseTasks(int indexForNextLoop,
										ArrayList<Task> tempList) {
		Task task = null;
		Date startDate = null;
		String taskType = null;
		
		for(int i = 0; i < tempList.size(); i++) {
			task = tempList.get(i);
			taskType = task.getTaskType().toString();
			startDate = task.getStartDate();
	    	
			if(taskType.equalsIgnoreCase(TaskType.FLOATING.toString())||
			   DateParser.hasMatchedDateOnly(todayDate, startDate) || 
			   DateParser.isBeforeNow(startDate)) {
				
				organiseTasksByTaskType(task, startDate, taskType);
				indexForNextLoop = i + 1;
			} else {
				indexForNextLoop = i;
				break;
			}
		}
		return indexForNextLoop;
	}

	private void organiseTasksByTaskType(Task task, Date startDate,
							   			 String taskType) {
		boolean isRecurring = task.getIsRecurring();
		TaskStatus taskStatus = task.getStatus();
		
		if(taskType.equalsIgnoreCase(TaskType.FLOATING.toString())) {
			floating.add(task);
		} else {
			if(!taskStatus.equals(TaskStatus.OVERDUE) && 
			   DateParser.hasMatchedDateOnly(startDate, todayDate)) {
				
				if(isRecurring && !taskStatus.equals(TaskStatus.DELETED) || !isRecurring) {
					today.add(task);
				}
			} else if(taskStatus.equals(TaskStatus.OVERDUE) || DateParser.isBeforeNow(startDate)) {
				
				if(!taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
					
					if(isRecurring && !taskStatus.equals(TaskStatus.DELETED) || !isRecurring) {
						overdue.add(task);
					}
				} else {
					overdue.add(task);
				}
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
	
	
	private ArrayList<Task> getListForDisplay(String type) {
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
		
		if(status.equalsIgnoreCase(TaskStatus.DELETED.toString())) {
			return;
		}
		
		if(!header.equals(Constant.EMPTY_STRING)) {
			addTitle(header, vBox);
		}
		
		BorderPane bPane = renderTaskItemContents(task, displayType, taskType,
						  						  status, onDate, byDate, fromDate, 
						  						  toDate, startDate, paneColor);
		
		vBox.getChildren().add(bPane);
	}

	private BorderPane renderTaskItemContents(Task task, String displayType,
											  String taskType, String status, 
											  Date onDate, Date byDate,
											  Date fromDate, Date toDate, 
											  Date startDate, String paneColor) {
		BorderPane bPane = addBorderPane(paneColor);
		HBox hBoxLeft = addLeftHBox(bPane);
		HBox hBoxRight = addRightHBox(bPane);
		addIcon(task, hBoxLeft);
		addId(task, hBoxLeft);
		addPriorityBar(task, hBoxLeft);
		addDescription(task, hBoxLeft, displayType);
		
		if(!displayType.equalsIgnoreCase(Constant.TAB_NAME_CATEGORY)) {
			addCategory(task, hBoxLeft);
		}
		
		if(!taskType.equalsIgnoreCase(TaskType.FLOATING.toString())) {
			if(displayType.equalsIgnoreCase(Constant.TAB_NAME_ALL) || 
			   displayType.equalsIgnoreCase(Constant.VIEW_NAME_SEARCH_RESULT)) {
				
				addDateTimeInAllOrSearchResult(taskType, status, displayType, 
											   onDate, byDate, fromDate, toDate, 
											   startDate, hBoxRight, hBoxLeft);
			} else {
				addDateTimeInCategoryOrPriority(taskType, status, displayType, 
												onDate, byDate, fromDate, toDate, 
												startDate, hBoxRight, hBoxLeft);
			}
		}
		return bPane;
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
		TaskStatus status = task.getStatus();
		TaskType taskType = task.getTaskType();
		boolean isOverdue = false;
		
		if(!taskType.equals(TaskType.FLOATING) 
				&& DateParser.isBeforeNow(task.getStartDate())) {
			isOverdue = true;	
		}
		
		if(taskType.equals(TaskType.TIMED) && 
				DateParser.hasMatchedDateOnly(task.getFrom(), task.getTo()) && 
				DateParser.isAfterNow(task.getTo())) {
			isOverdue = false;	
		}
		
		if(status.equals(TaskStatus.COMPLETED)) {
			return Constant.CSS_CLASS_BORDERPANE_COMPLETED;
		} else if(isOverdue) {
			return Constant.CSS_CLASS_BORDERPANE_OVERDUE;
		} else {
			return Constant.CSS_CLASS_BORDERPANE_ALL;
		}
	}
	
	
	private SimpleDateFormat getDateFormat(String taskType, Date startDate,
			Date fromDate, Date toDate) {
		SimpleDateFormat dateFormat;
		
		if(DateParser.isAfterDate(todayDate, startDate)) {
			dateFormat = Constant.FORMAT_DATE_TIME_OUTPUT;
		} else {
			dateFormat = Constant.FORMAT_TIME_OUTPUT;
		}
		
		if((taskType.equalsIgnoreCase(TaskType.DATED.toString()) || 
				taskType.equalsIgnoreCase(TaskType.EVENT.toString())) &&
				DateParser.isBeforeNow(startDate)) {
			dateFormat = Constant.FORMAT_DATE_TIME_OUTPUT;
		} else if(taskType.equalsIgnoreCase(TaskType.TIMED.toString()) && 
				DateParser.hasMatchedDateOnly(fromDate, toDate) && 
				DateParser.isBeforeNow(toDate)) {
			dateFormat = Constant.FORMAT_DATE_TIME_OUTPUT;	
		}
		
		return dateFormat;
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
			default :
				// invalid task type
				// do nothing
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
	
	
	private void addDescription(Task task, HBox hBox, String displayType) {
		Label lblDesc = new Label(task.getToDo());
		
		setLengthForDesc(lblDesc, task, displayType);
		
		lblDesc.getStyleClass().add(Constant.CSS_CLASS_LABEL_DESCRIPTION);
		hBox.getChildren().add(lblDesc);
	}
	
	
	private void addCategory(Task task, HBox hBox) {
		String category = task.getCategory();
		Label lblCategory;
		
		if(!category.equalsIgnoreCase(Constant.CATEGORY_UNCATEGORISED)) {
			lblCategory = new Label(task.getCategory().toUpperCase());
			lblCategory.getStyleClass().add(Constant.CSS_CLASS_LABEL_CATEGORY);
			hBox.getChildren().add(lblCategory);
		}
	}
	
	
	private void addDateTimeInAllOrSearchResult(String taskType, String status, 
												String displayType, Date onDate, 
												Date byDate, Date fromDate, Date toDate, 
												Date startDate, HBox hBoxRight, HBox hBoxLeft) {
		SimpleDateFormat dateFormat = null;

		dateFormat = getDateFormat(taskType, startDate, fromDate, toDate);

		if(taskType.equalsIgnoreCase(TaskType.EVENT.toString())) {
			dateFormat = getDateFormat(taskType, startDate, fromDate, toDate);
			
			addSingleDateTime(onDate, hBoxRight, Constant.EMPTY_STRING, dateFormat);

		} else if(taskType.equalsIgnoreCase(TaskType.DATED.toString())) {
			dateFormat = getDateFormat(taskType, startDate, fromDate, toDate);
			
			addSingleDateTime(byDate, hBoxRight, Constant.STR_BEFORE_DATE_BY, dateFormat);

		} else if(taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
			dateFormat = getDateFormat(taskType, startDate, fromDate, toDate);
			
			if(DateParser.compareDate(fromDate, toDate)) {
				addDoubleDateTime(fromDate, toDate, hBoxRight, 
								  Constant.STR_BEFORE_DATE_FROM, 
								  Constant.STR_BEFORE_DATE_TO, 
								  dateFormat, displayType);

			} else if(DateParser.compareDate(startDate, fromDate)) {
				addSingleDateTime(fromDate, hBoxRight, 
								  Constant.STR_BEFORE_DATE_FROM, 
								  dateFormat);

			} else if(DateParser.compareDate(startDate, toDate)) {
				addSingleDateTime(toDate, hBoxRight, 
								  Constant.STR_BEFORE_DATE_TO, 
								  dateFormat);

			} else {
				addDoubleDateTime(fromDate, toDate, hBoxRight, 
								  Constant.STR_BEFORE_DATE_FROM, 
								  Constant.STR_BEFORE_DATE_TO, 
								  Constant.FORMAT_DATE_OUTPUT_FOR_TIMED_TASK, 
								  displayType);
			}
		}
	}
	
	private void addDateTimeInCategoryOrPriority(String taskType, String status, 
												 String displayType, Date onDate, 
												 Date byDate, Date fromDate, Date toDate, 
												 Date startDate, HBox hBoxRight, HBox hBoxLeft) {
		
		if(taskType.equalsIgnoreCase(TaskType.EVENT.toString())) {
			addSingleDateTime(onDate, hBoxRight, Constant.EMPTY_STRING, 
							  Constant.FORMAT_DATE_TIME_OUTPUT);
			
		} else if(taskType.equalsIgnoreCase(TaskType.DATED.toString())) {
			addSingleDateTime(byDate, hBoxRight, Constant.STR_BEFORE_DATE_BY, 
							  Constant.FORMAT_DATE_TIME_OUTPUT);
			
		} else if(taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
			addDoubleDateTime(fromDate, toDate, hBoxRight, 
							  Constant.STR_BEFORE_DATE_FROM,
							  Constant.STR_BEFORE_DATE_TO, 
							  Constant.FORMAT_DATE_TIME_OUTPUT, displayType);
		}
	}
	
	
	private void addSingleDateTime(Date date, HBox hBox, String strBeforeTime, 
								   SimpleDateFormat dateFormat) {
		Label lbl = new Label(strBeforeTime);
		lbl.getStyleClass().add(Constant.CSS_CLASS_LABEL_BEFORE_TIME);
		
		Label lblDateTime = new Label(dateFormat.format(date));
		lblDateTime.getStyleClass().add(Constant.CSS_CLASS_LABEL_DATETIME);
		
		hBox.getChildren().add(lbl);
		hBox.getChildren().add(lblDateTime);
	}
	
	
	private void addDoubleDateTime(Date dateA, Date dateB, HBox hBox, 
								   String strA, String strB, 
								   SimpleDateFormat dateFormat, 
								   String displayType) {
		
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
}
