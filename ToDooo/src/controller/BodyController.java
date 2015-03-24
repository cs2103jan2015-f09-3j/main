package controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import application.Constant;
import application.Main;
import application.Task;
import application.DateParser;
import application.TaskType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;

public class BodyController{

	private MainController mainCon;
	
	@FXML AnchorPane anPaneMain;
	@FXML VBox vBoxAll;
	@FXML VBox vBoxCategory;
	@FXML VBox vBoxPriority;
	
	@FXML
	public void initialize() {
		loadListByDate("main");
	}
	
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	public ArrayList<Task> cloneTaskList(ArrayList<Task> taskList) {
		ArrayList<Task> tempTaskList = new ArrayList<>();
		Task task;
		String taskType;
		Date recurringDate;
		String recurringId;
		boolean isRecurring;
		
		for(int i = 0; i < taskList.size(); i++) {
			task = taskList.get(i);
			taskType = task.getTaskType().toString();
			isRecurring = task.getIsRecurring();
			
			if(!taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
				if(isRecurring && taskType.equalsIgnoreCase(TaskType.EVENT.toString())) {
					for(int j = 0; j < task.getRecurringTasks().size(); j++) {
						recurringDate = task.getRecurringTasks().get(j).getRecurDate();
						recurringId = task.getRecurringTasks().get(j).getRecurringTaskId();
						
						Task t1 = copyItems(task, recurringId, recurringDate, task.getBy(), recurringDate);
						
						tempTaskList.add(t1);
					}
				} else if(isRecurring && taskType.equalsIgnoreCase(TaskType.DATED.toString())) {
					for(int j = 0; j < task.getRecurringTasks().size(); j++) {
						recurringDate = task.getRecurringTasks().get(j).getRecurDate();
						recurringId = task.getRecurringTasks().get(j).getRecurringTaskId();
						Task t2 = copyItems(task, recurringId, task.getOn(), recurringDate, recurringDate);
						
						tempTaskList.add(t2);
					}
				} else {
					Task t3 = new Task();
					t3 = task;
					tempTaskList.add(t3);
				}
			} else {
				Calendar start = Calendar.getInstance();
				start.setTime(task.getFrom());
				Calendar end = Calendar.getInstance();
				end.setTime(task.getTo());
				
				for (Date date = start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
					Task t4 = copyItems(task, task.getId(), task.getOn(), task.getBy(), date);
					
					tempTaskList.add(t4);
				}
			}
		}
		
		return tempTaskList;
	}
	
	public void loadListByDate(String displayType) {
		vBoxAll.getChildren().clear();
		
		int indexForNextLoop = 0;
		String date1 = "";
		String date2 = "";
		
		ArrayList<Task> taskList = getList(displayType);
		ArrayList<Task> unsortedTemp = cloneTaskList(taskList);
		ArrayList<Task> overdue = new ArrayList<>();
		ArrayList<Task> today = new ArrayList<>();
		ArrayList<Task> floating = new ArrayList<>();
	
		Task tClass = new Task();
		ArrayList<Task> temp = tClass.viewListByAll(unsortedTemp);
		
	    Date todayDate = getTodayDate().getTime();
		
	    for(int i = 0; i < temp.size(); i++) {
			
			if(temp.get(i).getTaskType().name().equalsIgnoreCase(TaskType.FLOATING.toString())||
				DateParser.compareDate(todayDate, temp.get(i).getStartDate()) || temp.get(i).getStartDate().before(todayDate)) {
				
				if(temp.get(i).getTaskType().name().equalsIgnoreCase(TaskType.FLOATING.toString())) {
					floating.add(temp.get(i));
				} else {
					if(temp.get(i).getStartDate().before(todayDate)) {
						overdue.add(temp.get(i));
					} else if(DateParser.compareDate(todayDate, temp.get(i).getStartDate())) {
						today.add(temp.get(i));
					}
				}
				
				indexForNextLoop = i+1;
			} else {
				indexForNextLoop = i;
				break;
			}
		} 
		
	    if(!overdue.isEmpty()) {
			printList(overdue, Constant.OVERDUE_TITLE);
			
			if(!floating.isEmpty() && today.isEmpty()) {
				addHorizontalBar();
			}
		}
		
		if(!today.isEmpty()) {
			printList(today, Constant.TODAY_TITLE);
			
			if(!floating.isEmpty()) {
				addHorizontalBar();
			}
		}
		
		if(!floating.isEmpty()) {
			printList(floating, "");
		}
		
		for(int j = indexForNextLoop; j < temp.size(); j++) {
			date1 = Constant.DATEOUTPUT.format(temp.get(j).getStartDate());
			
			if(j != indexForNextLoop) {
				date2 = Constant.DATEOUTPUT.format(temp.get(j-1).getStartDate());
			}
			
			if(j == indexForNextLoop || !date1.equals(date2)) {
				generateListByDate(date1, temp.get(j));
			} else {
				generateListByDate("", temp.get(j));
			}
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
	
	private Task copyItems(Task originalTask, String recurringId, Date onDate, Date byDate, Date startDate) {
		Task t = new Task();
		t.setBy(byDate);
		t.setCategory(originalTask.getCategory());
		t.setEndDate(originalTask.getEndDate());
		t.setFrom(originalTask.getFrom());
		t.setId(recurringId);
		t.setIsRecurring(originalTask.getIsRecurring());
		t.setIsValid(originalTask.getIsValid());
		t.setOn(onDate);
		t.setOriginalText(originalTask.getOriginalText());
		t.setPriority(originalTask.getPriority());
		t.setRecurringTasks(originalTask.getRecurringTasks());
		t.setRepeat(originalTask.getRepeat());
		t.setRepeatDay(originalTask.getRepeatDay());
		t.setRepeatUntil(originalTask.getRepeatUntil());
		t.setStartDate(startDate);
		t.setStatus(originalTask.getStatus());
		t.setTaskType(originalTask.getTaskType());
		t.setTo(originalTask.getTo());
		t.setToDo(originalTask.getToDo());
		
		return t;
	}
	
	private void generateListByDate(String header, Task t) {
		if(!header.equals("")) {
			addTitle(header, vBoxAll);
		}
		
		BorderPane bPane = new BorderPane();
		bPane.getStyleClass().add("bPaneAll");
		
		HBox hBox1 = new HBox();
		bPane.setLeft(hBox1);
		
		HBox hBox2 = new HBox();
		bPane.setRight(hBox2);
		
		addIcon(t, hBox1);
		addId(t, hBox1);
		addPriorityBar(t, hBox1);
		addDesc(t, hBox1);
		addCategory(t, hBox1);
		
		if(!t.getTaskType().toString().equalsIgnoreCase(TaskType.FLOATING.toString())) {
			
			if(t.getTaskType().toString().equalsIgnoreCase(TaskType.EVENT.toString())) {
				addSingleDateTime(t.getOn(), hBox2, "", Constant.TIMEOUTPUT);
			} else if(t.getTaskType().toString().equalsIgnoreCase(TaskType.DATED.toString())) {
				addSingleDateTime(t.getBy(), hBox2, "by", Constant.TIMEOUTPUT);
			} else if(t.getTaskType().toString().equalsIgnoreCase(TaskType.TIMED.toString())) {
				if(DateParser.compareDate(t.getFrom(), t.getTo())) {
					addDoubleDateTime(t.getFrom(), t.getTo(), hBox2, "from", "to", Constant.TIMEOUTPUT);
				} else if(DateParser.compareDate(t.getStartDate(), t.getFrom())) {
					addSingleDateTime(t.getFrom(), hBox2, "from", Constant.TIMEOUTPUT);
				} else if(DateParser.compareDate(t.getStartDate(),t.getTo())) {
					addSingleDateTime(t.getTo(), hBox2, "to", Constant.TIMEOUTPUT);
				} else {
					addDoubleDateTime(t.getFrom(), t.getTo(), hBox2, "from", "to", Constant.DATEOUTPUT_FOR_TIMEDTASK);
				}
			}
		} 
		
		vBoxAll.getChildren().add(bPane);
	}
	
	private void printList(ArrayList<Task> al, String title) {
		for(int d = 0; d < al.size(); d++) {
			if(d == 0) {
				generateListByDate(title, al.get(d));
			} else {
				generateListByDate("", al.get(d));
			}
		}
	}
	
	private void addHorizontalBar() {
		Line hBar = new Line();
		hBar.setStartX(4.5);
		hBar.setStartY(0.5);
		hBar.setEndX(760);
		hBar.setEndY(0.5);
		hBar.getStyleClass().add("hBar");
		Pane paneHBar = new Pane(hBar);
		paneHBar.getStyleClass().add("paneHBar");
		vBoxAll.getChildren().add(paneHBar);
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
		switch(t.getTaskType().toString()) {
		case "EVENT": imgName = Constant.EVENT_ICON;
		break;
		case "FLOATING": imgName = Constant.FLOATING_ICON;
		break;
		case "TIMED": imgName = Constant.TIMED_ICON;
		break;
		case "DATED": imgName = Constant.DATED_ICON;
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
		switch (t.getPriority().name()) {
			case "HIGH": priorityBar.setStroke(Constant.HIGH_PRIORITY);
			break;
			case "MEDIUM": priorityBar.setStroke(Constant.MEDIUM_PRIORITY);
			break;
			case "LOW": priorityBar.setStroke(Constant.LOW_PRIORITY);
			break;
			case "NEUTRAL": priorityBar.setStroke(Constant.NEUTRAL_PRIORITY);
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
