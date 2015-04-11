package controller;

import java.io.File;
import java.util.Timer;
import application.Constant;
import application.Execution;
import application.Frequency;
import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class SettingController {
	
	private Timer timer;
	private MainController mainCon;
	
	@FXML public Label lblSysMsgSettingA;
	@FXML public Label lblSysMsgSettingB;
	
	@FXML AnchorPane anPaneSetting;
	@FXML TextField txtPath;
	@FXML Button btnBrowse;
	@FXML ImageView backIcon;		
	@FXML RadioButton radioWeekly;
	@FXML RadioButton radioMonthly;
	
	// -----------------------------------------------------------------------------------------------
	// Public methods
	// -----------------------------------------------------------------------------------------------
	//@author A0112537M
	@FXML 
	public void openFileDialogKey(KeyEvent e) {
		if(Constant.SHORTCUT_OPEN_FILE_DIALOG.match(e)) {
			openFileDialog();
		}
	}
	
	@FXML 
	public void openFileDialogMouse(MouseEvent e) {
		openFileDialog();
	}
	
	@FXML 
	public void setCleanRecurrenceWeekly(MouseEvent e) {
		String sysMsg = Main.storage.
				updateCleanRecurrenceInSetting(Frequency.WEEKLY.toString());
		
		displaySysMsgForClean(sysMsg);
	}
	
	@FXML 
	public void setCleanRecurrenceMonthly(MouseEvent e) {
		String sysMsg = Main.storage.
				updateCleanRecurrenceInSetting(Frequency.MONTHLY.toString());
		
		displaySysMsgForClean(sysMsg);
	}
	
	@FXML
	public void initialize() {
		String cleanRecurrence = Main.storage.readSaveCleanRecurrence();
		
		String savePath = Main.storage.
				readSavePath().replace("/","\\");
		txtPath.setText(savePath);
		if(cleanRecurrence.equalsIgnoreCase(Frequency.WEEKLY.toString())) {
			radioWeekly.setSelected(true);
		} else if(cleanRecurrence.equalsIgnoreCase(Frequency.MONTHLY.toString())) {
			radioMonthly.setSelected(true);
		}
	}
	
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Private methods
	// -----------------------------------------------------------------------------------------------
	private void openFileDialog() {
		String systemMsg = "";
		
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle(Constant.TITLE_SETTING_DIR);
		
		File selectedDir = dirChooser.showDialog(anPaneSetting.getScene().getWindow());		
		txtPath.setText(selectedDir.getAbsolutePath());
		
		String newPath = txtPath.getText();
		String pathInSetting = Main.storage.readSavePath();
		
		if (!pathInSetting.equals(newPath)) {
			systemMsg = Main.storage.moveFile(newPath);
		}
		
		displaySysMsgForSavePath(systemMsg);
	}
	
	private void displaySysMsgForSavePath(String systemMsg) {
		lblSysMsgSettingA.setText(systemMsg);
		
		if(systemMsg.contains(Constant.SYS_MSG_KEYWORD_FILE_SAVED)) {
			lblSysMsgSettingA.setTextFill(Constant.COLOR_SUCCESS);
		} else {
			lblSysMsgSettingA.setTextFill(Constant.COLOR_ERROR);
		}
		
		Execution.executeSysMsgTimerForSavePath();
	}
	
	private void displaySysMsgForClean(String systemMsg) {
		lblSysMsgSettingB.setText(systemMsg);
		
		if(systemMsg.contains(Constant.SYS_MSG_KEYWORD_FILE_SAVED)) {
			lblSysMsgSettingB.setTextFill(Constant.COLOR_SUCCESS);
		} else {
			lblSysMsgSettingB.setTextFill(Constant.COLOR_ERROR);
		}
		
		Execution.executeSysMsgTimerForClean();
	}
}
