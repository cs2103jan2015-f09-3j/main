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
	private ArrayList<Task> taskList = Main.list.getTasks();
	
	@FXML VBox vBoxAll;
	@FXML VBox vBoxCategory;
	@FXML VBox vBoxPriority;
	
	@FXML
	public void initialize() {
		loadListByDate(Constant.TAB_NAME_ALL);
	}
	
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	private Date getDate(Task t) {
		if(t.getOn() != null) {
			return t.getOn();
		} else if(t.getBy() != null) {
			return t.getBy();
		} else if (t.getFrom() != null) {
			return t.getFrom();
		} else {
			return null;
		}
	}
	
	private ArrayList<Task> cloneTaskList() {
		ArrayList<Task> tempTaskList = new ArrayList<>();
		for(int i = 0; i < taskList.size(); i++) {
			if(!taskList.get(i).getTaskType().name().equalsIgnoreCase("TIMED")) {
				if(taskList.get(i).getIsRecurring() && taskList.get(i).getTaskType().name().equalsIgnoreCase("EVENT")) {
					for(int j = 0; j < taskList.get(i).getRecurringTasks().size(); j++) {
						String input = taskList.get(i).getToDo() + "/on " + Main.inputParser.getDateString(taskList.get(i).getRecurringTasks().get(j).getRecurDate());
						Task t1 = new Task(input);
						t1.setId(taskList.get(i).getRecurringTasks().get(j).getRecurringTaskId());
						tempTaskList.add(t1);
					}
					
				} else if(taskList.get(i).getIsRecurring() && taskList.get(i).getTaskType().name().equalsIgnoreCase("DATED")) {
					for(int j = 0; j < taskList.get(i).getRecurringTasks().size(); j++) {
						String input = taskList.get(i).getToDo() + "/by " + Main.inputParser.getDateString(taskList.get(i).getRecurringTasks().get(j).getRecurDate());
						Task t2 = new Task(input);
						t2.setId(taskList.get(i).getRecurringTasks().get(j).getRecurringTaskId());
						tempTaskList.add(t2);
					}
					
				} else {
					Task t3 = new Task();
					t3 = taskList.get(i);
					tempTaskList.add(t3);
				}
				
			} else {
				Calendar start = Calendar.getInstance();
				start.setTime(taskList.get(i).getFrom());
				Calendar end = Calendar.getInstance();
				end.setTime(taskList.get(i).getTo());
				
				for (Date date = start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
					Task t4 = new Task();
					t4.setBy(taskList.get(i).getBy());
					t4.setCategory(taskList.get(i).getCategory());
					t4.setEndDate(taskList.get(i).getEndDate());
					t4.setFrom(taskList.get(i).getFrom());
					t4.setId(taskList.get(i).getId());
					t4.setIsRecurring(taskList.get(i).getIsRecurring());
					t4.setIsValid(taskList.get(i).getIsValid());
					t4.setOn(taskList.get(i).getOn());
					t4.setOriginalText(taskList.get(i).getOriginalText());
					t4.setPriority(taskList.get(i).getPriority());
					t4.setRecurringTasks(taskList.get(i).getRecurringTasks());
					t4.setRepeat(taskList.get(i).getRepeat());
					t4.setRepeatDay(taskList.get(i).getRepeatDay());
					t4.setRepeatUntil(taskList.get(i).getRepeatUntil());
					t4.setStartDate(date);
					t4.setStatus(taskList.get(i).getStatus());
					t4.setTaskType(taskList.get(i).getTaskType());
					t4.setTo(taskList.get(i).getTo());
					t4.setToDo(taskList.get(i).getToDo());
					tempTaskList.add(t4);
				}
			}
		}
		return tempTaskList;
	}
	
	public void loadListByDate(String tabName) {
		vBoxAll.getChildren().clear();
		
		ArrayList<Task> unsortedTemp = cloneTaskList();
		ArrayList<Task> overdue = new ArrayList<>();
		ArrayList<Task> today = new ArrayList<>();
		ArrayList<Task> floating = new ArrayList<>();
		
		//sort the tempTaskList arraylist
		Task tClass = new Task();
		ArrayList<Task> temp = tClass.viewListByAll(unsortedTemp);
		
		Calendar c = new GregorianCalendar();
	    c.set(Calendar.HOUR_OF_DAY, 0); 
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    Date todayDate = c.getTime();
	    int indexForNextLoop = 0;
		
	    for(int i = 0; i < temp.size(); i++) {
			
			if(temp.get(i).getTaskType().name().equalsIgnoreCase("FLOATING")||
				DateParser.compareDate(todayDate, temp.get(i).getStartDate()) || temp.get(i).getStartDate().before(todayDate)) {
				
				if(temp.get(i).getTaskType().name().equalsIgnoreCase("FLOATING")) {
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
			for(int b = 0; b < overdue.size(); b++) {
				if(b == 0) {
					generateListByDate("OVERDUE", overdue.get(b), tabName, overdue.get(b).getTaskType().name());
				} else {
					generateListByDate("", overdue.get(b), tabName, overdue.get(b).getTaskType().name());
				}
			}
		}
		
		if(!today.isEmpty()) {
			for(int d = 0; d < today.size(); d++) {
				if(d == 0) {
					generateListByDate("TODAY", today.get(d), tabName, today.get(d).getTaskType().name());
				} else {
					generateListByDate("", today.get(d), tabName, today.get(d).getTaskType().name());
				}
			}
			
			if(!floating.isEmpty()) {
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
		}
		
		if(!floating.isEmpty()) {
			for(int a = 0; a < floating.size(); a++) {
				generateListByDate("", floating.get(a), tabName, floating.get(a).getTaskType().name());
			}
		}
		
		String date1 = "";
		String date2 = "";
		
		for(int j = indexForNextLoop; j < temp.size(); j++) {
			date1 = Constant.DATEOUTPUT.format(temp.get(j).getStartDate());
			
			if(j != indexForNextLoop) {
				date2 = Constant.DATEOUTPUT.format(temp.get(j-1).getStartDate());
			}
			
			if(j == indexForNextLoop || !date1.equals(date2)) {
				generateListByDate(date1, temp.get(j), tabName, temp.get(j).getTaskType().name());
			} else {
				generateListByDate("", temp.get(j), tabName, temp.get(j).getTaskType().name());
			}
		}
		
	}
	
	private void generateListByDate(String header, Task t, String tab, String taskType) {
		String imgName = "";
		
		if(!header.equals("")) {
			Label lblTitle = new Label();
			if(header.equals("TODAY") || header.equals("OVERDUE")) {
				lblTitle.getStyleClass().add("todayTitle");
			} else {
				lblTitle.getStyleClass().add("dateTitle");
			}
			lblTitle.setText(header);
			vBoxAll.getChildren().add(lblTitle);
		}
		
		BorderPane bPane = new BorderPane();
		bPane.getStyleClass().add("bPaneAll");
		
		HBox hBox1 = new HBox();
		bPane.setLeft(hBox1);
		
		if(tab.equalsIgnoreCase("All")) {
			vBoxAll.getChildren().add(bPane);
		} else {
			// other additional tab
		}
		
		switch(taskType) {
		case "EVENT": imgName = "images/eventIcon.png";
		break;
		case "FLOATING": imgName = "images/floatingIcon.png";
		break;
		case "TIMED": imgName = "images/timedIcon.png";
		break;
		case "DATED": imgName = "images/datedIcon.png";
		break;
		}
		
		Image img = new Image(imgName);
		ImageView icon = new ImageView();
		icon.setImage(img);
		icon.getStyleClass().add("iconImage");
		icon.setFitHeight(20);
        icon.setPreserveRatio(true);
        hBox1.getChildren().add(icon);
        
        Label lblId = new Label(t.getId());
		lblId.getStyleClass().add("labelId");
		hBox1.getChildren().add(lblId);
		
		Line priorityBar = new Line();
		priorityBar.setStartY(18);
		priorityBar.getStyleClass().add("priorityBar");
		switch (t.getPriority().name()) {
			case "HIGH": priorityBar.setStroke(Color.rgb(196, 1, 9));
			break;
			case "MEDIUM": priorityBar.setStroke(Color.rgb(248, 135, 46));
			break;
			case "LOW": priorityBar.setStroke(Color.rgb(249, 212, 35));
			break;
			case "NEUTRAL": priorityBar.setStroke(Color.WHITE);
			break;
		}
		
		hBox1.getChildren().add(priorityBar);
		
		Label lblDesc = new Label(t.getToDo());
		lblDesc.getStyleClass().add("labelDesc");
		hBox1.getChildren().add(lblDesc);
		
		if(!t.getCategory().equalsIgnoreCase("Uncategorised")) {
			Label lblCategory = new Label(t.getCategory());
			lblCategory.getStyleClass().add("labelCategory");
			hBox1.getChildren().add(lblCategory);
		}
		
		HBox hBox2 = new HBox();
		bPane.setRight(hBox2);
		
		Label lblFrom = new Label();
		lblFrom.getStyleClass().add("labelBeforeTime");
		Label lblTo = new Label();
		lblTo.getStyleClass().add("labelBeforeTime");
		
		Label lblDateTime1 = new Label();
		lblDateTime1.getStyleClass().add("labelDateTime");
		Label lblDateTime2 = new Label();
		lblDateTime2.getStyleClass().add("labelDateTime");
		
		String output = "";
		
		if(!taskType.equalsIgnoreCase("FLOATING")) {
			
			if(taskType.equalsIgnoreCase("EVENT")) {
				lblDateTime1.setText(Constant.TIMEOUTPUT.format(t.getOn()));
				hBox2.getChildren().add(lblFrom);
				hBox2.getChildren().add(lblDateTime1);
			} else if(taskType.equalsIgnoreCase("DATED")) {
				lblFrom.setText("by");
				lblDateTime1.setText(Constant.TIMEOUTPUT.format(t.getBy()));
				hBox2.getChildren().add(lblFrom);
				hBox2.getChildren().add(lblDateTime1);
			} else if(taskType.equalsIgnoreCase("TIMED")) {
				if(DateParser.compareDate(t.getFrom(), t.getTo())) {
					lblFrom.setText("from");
					lblDateTime1.setText(Constant.TIMEOUTPUT.format(t.getFrom()));
					lblTo.setText("to");
					lblDateTime2.setText(Constant.TIMEOUTPUT.format(t.getTo()));
					hBox2.getChildren().add(lblFrom);
					hBox2.getChildren().add(lblDateTime1);
					hBox2.getChildren().add(lblTo);
					hBox2.getChildren().add(lblDateTime2);
				} else if(DateParser.compareDate(t.getStartDate(), t.getFrom())) {
					lblFrom.setText("from");
					lblDateTime1.setText(Constant.TIMEOUTPUT.format(t.getFrom()));
					hBox2.getChildren().add(lblFrom);
					hBox2.getChildren().add(lblDateTime1);
				} else if(DateParser.compareDate(t.getStartDate(),t.getTo())) {
					lblTo.setText("to");
					lblDateTime2.setText(Constant.TIMEOUTPUT.format(t.getTo()));
					hBox2.getChildren().add(lblTo);
					hBox2.getChildren().add(lblDateTime2);
				} else {
					lblFrom.setText("from");
					lblDateTime1.setText(Constant.DATEOUTPUT_FOR_TIMEDTASK.format(t.getFrom()));
					lblTo.setText("to");
					lblDateTime2.setText(Constant.DATEOUTPUT_FOR_TIMEDTASK.format(t.getTo()));
					hBox2.getChildren().add(lblFrom);
					hBox2.getChildren().add(lblDateTime1);
					hBox2.getChildren().add(lblTo);
					hBox2.getChildren().add(lblDateTime2);
				}
			}
		} else {
			lblDateTime1.setText("");
		}
		
		
		
	}


}
