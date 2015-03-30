package controller;

import application.Constant;
import application.Main;
import application.Task;
import application.TaskType;
import controller.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.TextFlow;

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
	//@FXML TextArea detailTxtAreaAddition;
	
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	@FXML
	public void initialize() {
		Task task = Main.list.getSelectedTask();
		String taskType = task.getTaskType().toString();
		
		detailLblId.setText(task.getId());
		detailTxtAreaDesc.setText(task.getToDo());
		detailTxtAreaDesc.setEditable(false);
		detailTxtAreaDesc.getStyleClass().add("txtAreaDesc");
		detailLblType.setText(task.getTaskType().toString());
		detailLblCategory.setText(task.getCategory().toUpperCase());
		detailLblPriority.setText(task.getPriority().toString());
		detailLblStatus.setText(task.getStatus().toString());
		detailLblDateTime.setText(getDateFormat(task, taskType));
		detailLblRecurring.setText(getIsRecurring(task));
		//detailTxtAreaAddition.setText(task.getAddtionalNote());
		//saveAdditionalNote(detailTxtAreaAddition.getText());
	
	}
	
	private String getDateFormat(Task task, String taskType) {
		String result = "";
		
		if(!taskType.equalsIgnoreCase(TaskType.FLOATING.toString())) {
			if(taskType.equalsIgnoreCase(TaskType.DATED.toString())) {
				result = Constant.STR_BEFORE_DATE_BY + " " + Constant.DATETIMEOUTPUT.format(task.getBy());
			} else if(taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
				result = Constant.STR_BEFORE_DATE_FROM + " " + Constant.DATETIMEOUTPUT.format(task.getFrom()) +
							Constant.STR_BEFORE_DATE_TO + " " + Constant.DATETIMEOUTPUT.format(task.getTo());
			} else {
				result = Constant.DATETIMEOUTPUT.format(task.getOn());
			}
		}
		
		return result;
	}
	
	private String getIsRecurring(Task task) {
		if(task.getIsRecurring() == true) {
			return Constant.BOOLEAN_STRING_TRUE;
		} else {
			return Constant.BOOLEAN_STRING_FALSE;
		}
	}
	
	/*private void saveAdditionalNote(String note) {
		Main.list.getSelectedTask().setAddtionalNote(note);
	}*/	
}
