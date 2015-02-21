package controller;

import org.w3c.dom.Node;

import application.Command;
import application.Constant;
import application.InputParser;
import application.Main;
import application.Task;
import application.TaskType;
import application.Undo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class HeaderController{
	
	private MainController main;
	@FXML public TextField txtCmd;
	@FXML private Label lblLogo;
	@FXML private AnchorPane paneHead;
	
	@FXML
	public void processCmd(KeyEvent e){
		if(e.getCode() == KeyCode.ENTER){	
			String userInput = txtCmd.getText();		
			String systemMsg = executeCommand(userInput);
			
			txtCmd.clear();
			main.showInTabAll(systemMsg);
		}
	}

	public void init(MainController mainController) {
		main = mainController;
		
	}
	
	private String executeCommand(String userInput) {
		String systemMsg = null;
		Command commandType = InputParser.getActionFromString(userInput);
		TaskType taskType = InputParser.getTaskTypeFromString(userInput);
		
		switch (commandType) {
		case ADD :
			systemMsg = executeAdd(userInput, taskType);
			break;
		case UPDATE :
			// update
			break;
		case DELETE :
			systemMsg = executeDelete(userInput);
			break;
		case SEARCH :
			// search
			break;
		case SETTING :
			// setting
			executeSetting();
			break;
		default :
			// invalid command
			break;
		}
		
		return systemMsg;
	}

	private String executeAdd(String userInput, TaskType taskType) {
		Task task = new Task(userInput, taskType);	
		
		Undo undo = new Undo(Command.ADD, task.getId());
		Main.undos.push(undo);
		
		String systemMsg = Main.list.addTaskToList(task);
		
		return systemMsg;
	}
	
	private String executeDelete(String userInput) {
		String systemMsg= null;
		Task removedTask = Main.list.deleteTaskFromList(userInput);		
		if (removedTask != null) {
			Undo undo = new Undo(Command.DELETE, removedTask);
			Main.undos.push(undo);
			
			systemMsg = Constant.MSG_DELETE_SUCCESS;
		} else {
			systemMsg = Constant.MSG_ITEM_NOT_FOUND;
		}
		
		return systemMsg;
	}
	
	private void executeSetting() {
		// incomplete
	}
}
