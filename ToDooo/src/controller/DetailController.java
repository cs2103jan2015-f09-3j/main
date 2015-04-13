//@author A0112537M
package controller;

import application.Constant;
import application.Main;
import application.Task;
import application.TaskType;
import controller.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class DetailController {
	
	private MainController mainCon;
	@FXML Label detailLblId;
	@FXML Label detailLblType;
	@FXML Label detailLblDateTime;
	@FXML Label detailLblCategory;
	@FXML Label detailLblPriority;
	@FXML Label detailLblStatus;
	@FXML Label detailLblRecurring;
	@FXML TextArea detailTxtAreaDesc;
	
	public void initMainController(MainController mainController) {
		mainCon = mainController;
	}
	
	@FXML
	public void initialize() {
		Task task = Main.list.getSelectedTask();
		String taskType = task.getTaskType().toString();
		
		detailLblId.setText(task.getId());
		detailTxtAreaDesc.setText(task.getToDo());
		detailTxtAreaDesc.setEditable(false);
		detailTxtAreaDesc.getStyleClass().add(Constant.CSS_CLASS_TEXTAREA_DETAIL);
		detailLblType.setText(task.getTaskType().toString());
		detailLblCategory.setText(task.getCategory().toUpperCase());
		detailLblPriority.setText(task.getPriority().toString());
		detailLblStatus.setText(task.getStatus().toString());
		detailLblDateTime.setText(formatDateByTaskType(task, taskType));
		detailLblRecurring.setText(String.valueOf(task.getIsRecurring()));
	}
	
	private String formatDateByTaskType(Task task, String taskType) {
		String result = Constant.EMPTY_DATE;
		
		if(!taskType.equalsIgnoreCase(TaskType.FLOATING.toString())) {
			if(taskType.equalsIgnoreCase(TaskType.DATED.toString())) {
				result = Constant.STR_BEFORE_DATE_BY + " " + 
						 Constant.FORMAT_DATE_TIME_OUTPUT.format(task.getBy());
				
			} else if(taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
				result = Constant.STR_BEFORE_DATE_FROM + " " + 
						 Constant.FORMAT_DATE_TIME_OUTPUT.format(task.getFrom()) +
						 Constant.STR_BEFORE_DATE_TO + " " + 
						 Constant.FORMAT_DATE_TIME_OUTPUT.format(task.getTo());
				
			} else {
				result = Constant.FORMAT_DATE_TIME_OUTPUT.format(task.getOn());
			}
		}
		
		return result;
	}
}
