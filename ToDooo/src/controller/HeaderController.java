package controller;

import java.io.IOException;

import org.fxmisc.richtext.StyleClassedTextArea;
import org.w3c.dom.Node;

import application.Command;
import application.Constant;
import application.InputParser;
import application.Main;
import application.Task;
import application.Undo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

public class HeaderController{
	
	private MainController mainCon;
	@FXML private Label lblLogo;
	@FXML public Label lblSysMsg;
	@FXML private AnchorPane paneHead;
	@FXML private ImageView settingIcon;
	@FXML private ImageView backIcon;
	@FXML private Pane txtAreaPane;
	@FXML public StyleClassedTextArea textArea;
	
	@FXML
	public void processCmd(KeyEvent e) throws IOException {			
		if (e.getCode() == KeyCode.ENTER) {
			String userInput = textArea.getText();	
			if (userInput.equals("")) {
				return;
			}
			
			String systemMsg = null;
			Command commandType = InputParser.getActionFromString(userInput);	

			if (Main.toUpdate && commandType.equals(Command.UPDATE)) {
				systemMsg = executeUpdate(userInput);
				
				Main.toUpdate = false;
				textArea.clear();
			} else {
				systemMsg = executeCommand(userInput, commandType);
			}
			
			mainCon.bodyController.loadListByDate("All");
			lblSysMsg.setText(systemMsg);	
		} 
	}
	
	@FXML
	public void onKeyTyped(KeyEvent e) {
		boolean toReset = textArea.getText().trim().equals("");
		if (toReset) {
			resetTextArea();
		}
	}
		
	@FXML
	public void loadSettingMouse(MouseEvent e) throws IOException {
		executeSetting();
	}
	
	@FXML
	public void goBackToMainMouse(MouseEvent e) throws IOException {
		executeGoBack();
	}
	
	@FXML
	public void loadSearchResult(KeyEvent e) throws IOException {
		mainCon.showPageInBody("/view/SearchResult.fxml");
	}

	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	private String executeCommand(String userInput, Command commandType) throws IOException {
		String systemMsg = null;
		
		switch (commandType) {
		case ADD :
			systemMsg = executeAdd(userInput);
			textArea.clear();
			break;
		case UPDATE :
			systemMsg = executeRetrieveOriginalText(userInput);
			break;
		case DELETE :
			systemMsg = executeDelete(userInput);
			textArea.clear();
			break;
		case SEARCH :
			systemMsg = executeSearch(userInput);
			break;
		case SETTING :
			executeSetting();
			textArea.clear();
			break;
		case GO_BACK :
			// go back to main page
			executeGoBack();
			textArea.clear();
			break;
		default :
			// invalid command
			break;
		}
		
		return systemMsg;
	}
	
	private String executeAdd(String userInput) {
		Task task = new Task(userInput);	
		
		String systemMsg = null;
		if (task.getIsValid()) {
			systemMsg = Main.list.addTaskToList(task);
			
			if (systemMsg.equals(Constant.MSG_ADD_SUCCESS)) {
				Undo undo = new Undo(Command.ADD, task.getId());
				Main.undos.push(undo);				
				Main.redos.clear();
			}
			
		} else {
			systemMsg = Main.systemFeedback;
		}
				
		return systemMsg;
	}
	
	private String executeDelete(String userInput) {
		String systemMsg= null;
		Task removedTask = Main.list.deleteTaskFromList(userInput);
		
		if (removedTask != null) {
			Undo undo = new Undo(Command.DELETE, removedTask);
			Main.undos.push(undo);			
			Main.redos.clear();
			
			systemMsg = Constant.MSG_DELETE_SUCCESS;
		} else {
			systemMsg = Constant.MSG_ITEM_NOT_FOUND;
		}
		
		return systemMsg;
	}
	
	private String executeRetrieveOriginalText(String userInput) {
		String systemMsg = null;
		String targetId = InputParser.getTargetIdFromString(userInput);
		Task originalTask = Main.list.getTaskById(targetId);
				
		if (originalTask != null) {
			textArea.appendText(": " + originalTask.getOriginalText());
			Main.toUpdate = true;
			systemMsg = Constant.MSG_ORIGINAL_RETRIEVED;
		} else {
			systemMsg = Constant.MSG_ORIGINAL_NOT_RETRIEVED;
		}
		
		return systemMsg;
	}
	
	private String executeUpdate(String userInput) {
		String systemMsg = null;
		Pair<Task, String> updatedTasksDetails = Main.list.updateTaskOnList(userInput);
		Task originalTask = updatedTasksDetails.getKey();
		String targetId = updatedTasksDetails.getValue();
		
		if (originalTask != null) {
			Undo undo = new Undo(Command.UPDATE, originalTask, targetId);
			Main.undos.push(undo);
			Main.redos.clear();
			
			systemMsg = Constant.MSG_UPDATE_SUCCESS;
		} else {
			systemMsg = Constant.MSG_UPDATE_FAIL;
		}
		
		return systemMsg;
	}
	
	private String executeSearch(String userInput) {
		String systemMsg = null;
		
		Main.searchResults = Main.list.searchTheList(userInput);
		if (Main.searchResults.isEmpty()) {
			systemMsg = Constant.MSG_NO_RESULTS;
		} else {
			systemMsg = Constant.MSG_SEARCH_SUCCESS.
						replaceFirst(Constant.DELIMETER_SEARCH, 
									  String.valueOf(Main.searchResults.size()));
		}
		
		return systemMsg;
	}
		
	private void executeSetting() throws IOException {
		settingIcon.setVisible(false);
		backIcon.setVisible(true);
		mainCon.showPageInBody("/view/Setting.fxml");
	}
	
	private void executeGoBack() throws IOException {
		settingIcon.setVisible(true);
		backIcon.setVisible(false);
		mainCon.showPageInBody("/view/Body.fxml");
	}
	
	private void resetTextArea() {
		Main.toUpdate = false;
		textArea.clear();
		textArea.positionCaret(0);
	}
}
