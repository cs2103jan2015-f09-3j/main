package controller;

import application.Command;
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
			// delete
			break;
		case SEARCH :
			// search
			break;
		default :
			// invalid command
			break;
		}
		
		return systemMsg;
	}

	private String executeAdd(String userInput, TaskType taskType) {
		Task task = new Task(userInput, taskType);	
		
		Main.storage.writeUndoToFile(Undo.ADD, task.getId());
		String systemMsg = Main.list.addTaskToFileDocument(task);
		
		return systemMsg;
	}
}
