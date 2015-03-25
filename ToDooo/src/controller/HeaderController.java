package controller;

import java.io.IOException;

import org.fxmisc.richtext.StyleClassedTextArea;

import application.Command;
import application.Constant;
import application.Main;
import application.SearchAttribute;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	@FXML public  Label lblSysMsg;
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
			
			String systemMsg = Command.executeUserInput(userInput, this);
			if (systemMsg.equals(Constant.MSG_ORIGINAL_NOT_RETRIEVED)) {
				resetTextArea();
			}	
			lblSysMsg.setText(systemMsg);	
			
			mainCon.loadListByDate(Constant.TAB_NAME_ALL);	
			mainCon.loadListByCategory(Constant.TAB_NAME_CATEGORY);
			mainCon.loadListByPriority(Constant.TAB_NAME_PRIORITY);
			mainCon.executeSystemMsgTimerTask();	
		} 			
	}
	
	public void onTextChanged() {	
		textArea.clearStyle(0);		
		String textAreaText = textArea.getText();
		
		boolean toReset = textAreaText.trim().equals("");
		if (toReset) {
			resetTextArea();
		}
		
		if (Main.shouldResetCaret) {
			textArea.positionCaret(textAreaText.length() - 1);
			Main.shouldResetCaret = false;
		}
		
		highlightKeyWords();
	}
	
	
	private void highlightKeyWords() {		
		String inputString = textArea.getText();
		String lowerCase = inputString.toLowerCase();
		
		highlightActionCommands(lowerCase);
		highlightDatesCommands(lowerCase);
		highlightCategory(lowerCase);
		highlightPriority(lowerCase);
		highlightRecurringCommands(lowerCase);
		highlightSearchAttributes(lowerCase);
	}
	
	private void highlightRecurringCommands(String lowerCase) {
		String basicCommand = null;
		String advancedCommand = null;
		int startIndex = -1;
		int endIndex = -1;
		
		for (Command command : Constant.COMMAND_RECURRING) {
			basicCommand = command.getBasicCommand() + " " + 
						   Command.RECURRING_UNTIL.getBasicCommand() + " ";
			advancedCommand = command.getAdvancedCommand() + " " +
							  Command.RECURRING_UNTIL.getAdvancedCommand() + " ";;
			
			if (lowerCase.contains(basicCommand)) {
				startIndex = lowerCase.indexOf(basicCommand);
				endIndex = startIndex + basicCommand.length();
			} else if (lowerCase.contains(advancedCommand)) {
				startIndex = lowerCase.indexOf(advancedCommand);
				endIndex = startIndex + advancedCommand.length();
			}
			
			if (startIndex != -1 && endIndex != -1) {
				textArea.setStyleClass(startIndex, endIndex, 
									   Constant.CSS_CLASS_RECURRING_COMMANDS);			
				break;
			} 
			
			startIndex = -1;
			endIndex = -1;
		}
	}
	
	private void highlightPriority(String lowerCase) {
		String basicCommand = null;
		String advancedCommand = null;
		int startIndex = -1;
		int endIndex = -1;
		
		for (Command command : Constant.COMMAND_PRIORITIES) {
			basicCommand = command.getBasicCommand() + " ";
			advancedCommand = command.getAdvancedCommand() + " ";
			
			if (lowerCase.contains(basicCommand)) {
				startIndex = lowerCase.indexOf(basicCommand);
				endIndex = startIndex + basicCommand.length();
			} else if (lowerCase.contains(advancedCommand)) {
				startIndex = lowerCase.indexOf(advancedCommand);
				endIndex = startIndex + advancedCommand.length();
			}
			
			if (startIndex != -1 && endIndex != -1) {
				textArea.setStyleClass(startIndex, endIndex, 
									   Constant.CSS_CLASS_PRIORITY);			
				break;
			} 
			
			startIndex = -1;
			endIndex = -1;
		}
	}
	
	private void highlightCategory(String lowerCase) {
		String lowerCaseTemp = lowerCase + " ";
		String basicCommand = Command.CATEGORY.getBasicCommand();
		int startIndex = -1;
		int endIndex = -1;
		
		if (lowerCaseTemp.contains(basicCommand)) {			
			startIndex = lowerCaseTemp.indexOf(basicCommand);
			endIndex = lowerCaseTemp.length();
			
			lowerCaseTemp = lowerCaseTemp.substring(startIndex, endIndex);
			endIndex = startIndex + lowerCaseTemp.indexOf(" ");
			textArea.setStyleClass(startIndex, endIndex, Constant.CSS_CLASS_CATEGORY);
		}
	}
	
	private void highlightDatesCommands(String lowerCase) {
		String basicCommand = null;
		String advancedCommand = null;
		int startIndex = -1;
		int endIndex = -1;
		
		for (Command command : Constant.COMMAND_DATES) {						
			basicCommand = command.getBasicCommand() + " ";
			advancedCommand = command.getAdvancedCommand() + " ";
			
			if (lowerCase.contains(basicCommand)) {
				startIndex = lowerCase.indexOf(basicCommand);
				endIndex = startIndex + basicCommand.length();
			} else if (lowerCase.contains(advancedCommand)) {
				startIndex = lowerCase.indexOf(advancedCommand);
				endIndex = startIndex + advancedCommand.length();
			}
			
			if (startIndex != -1 && endIndex != -1) {			
				textArea.setStyleClass(startIndex, endIndex, 
						   Constant.CSS_CLASS_DATE_COMMANDS);				
			} 
			
			startIndex = -1;
			endIndex = -1;
		}
	}
	
	private void highlightActionCommands(String lowerCase) {
		String basicCommand = null;
		String advancedCommand = null;
		int startIndex = -1;
		int endIndex = -1;
		
		for (Command command : Constant.COMMAND_ACTIONS) {
			basicCommand = command.getBasicCommand() + " ";
			advancedCommand = command.getAdvancedCommand() + " ";
			
			if (lowerCase.contains(basicCommand) && 
				lowerCase.indexOf(basicCommand) == 0) {
				startIndex = lowerCase.indexOf(basicCommand);
				endIndex = startIndex + basicCommand.length();
			} else if (lowerCase.contains(advancedCommand) &&
					   lowerCase.indexOf(advancedCommand) == 0) {
				startIndex = lowerCase.indexOf(advancedCommand);
				endIndex = startIndex + advancedCommand.length();
			}
			
			if (startIndex != -1 && endIndex != -1) {
				textArea.setStyleClass(startIndex, endIndex, 
									   Constant.CSS_CLASS_ACTION_COMMANDS);			
				break;
			} 
			
			startIndex = -1;
			endIndex = -1;
		}
	}
			
	private void highlightSearchAttributes(String lowerCase) {
		String command = null;
		int startIndex = -1;
		int endIndex = -1;
		
		for (SearchAttribute searchAttribute : SearchAttribute.values()) {
			command = searchAttribute.getCommand();
			
			if (lowerCase.contains(command)) {
				startIndex = lowerCase.indexOf(command);
				endIndex = startIndex + command.length();
			} 
			
			if (startIndex != -1 && endIndex != -1) {
				textArea.setStyleClass(startIndex, endIndex, 
									   Constant.CSS_CLASS_SEARCH_ATTRIBUTES);			
			} 
			
			startIndex = -1;
			endIndex = -1;
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
		executeSearchResult();
	}

	public void init(MainController mainController) {
		mainCon = mainController;
		
		textArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onTextChanged();
		    }
		});		
	}
	
	public void executeSetting() throws IOException {
		settingIcon.setVisible(false);
		backIcon.setVisible(true);
		mainCon.bodyController.anPaneMain.setVisible(false);
		mainCon.settingController.anPaneSetting.setVisible(true);
		mainCon.searchResultController.anPaneSearchResult.setVisible(false);
	}
	
	public void executeGoBack() throws IOException {
		settingIcon.setVisible(true);
		backIcon.setVisible(false);
		mainCon.bodyController.anPaneMain.setVisible(true);
		mainCon.settingController.anPaneSetting.setVisible(false);
		mainCon.searchResultController.anPaneSearchResult.setVisible(false);
	}
	
	public void executeSearchResult() {
		settingIcon.setVisible(false);
		backIcon.setVisible(true);
		mainCon.bodyController.anPaneMain.setVisible(false);
		mainCon.settingController.anPaneSetting.setVisible(false);
		mainCon.searchResultController.anPaneSearchResult.setVisible(true);
		mainCon.searchResultController.loadSearchList();
	}
	
	public void resetTextArea() {
		Main.toUpdate = false;	

		textArea.clearStyle(0);
		textArea.clear();
		textArea.positionCaret(0);		
	}
	

}
