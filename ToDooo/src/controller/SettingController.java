package controller;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import application.Constant;
import application.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class SettingController {
	
	private Timer timer;
	private MainController mainCon;
	@FXML AnchorPane anPaneSetting;
	@FXML TextField txtPath;
	@FXML Button btnBrowse;
	@FXML ImageView backIcon;
	@FXML Label lblSysMsgSetting;
	
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
	public void initialize() {
		txtPath.setText(Main.storage.readSavePath());
		
	}
	
	//@author A0112498B
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	//@author A0112537M
	public void executeSystemMsgTimerTask() {
		timer = new Timer();
		timer.schedule(new SystemMsgTimerTask(), Constant.TIMER_SYSTEM_MSG_DURATION);
	}
	
	//@author A0112498B
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
		
		displaySystemMessage(systemMsg);
	}
	
	//@author A0112537M
	private void displaySystemMessage(String systemMsg) {
		lblSysMsgSetting.setText(systemMsg);
		
		if(systemMsg.contains(Constant.SYS_MSG_KEYWORD_FILE_SAVED)) {
			lblSysMsgSetting.setTextFill(Constant.COLOR_SUCCESS);
		} else {
			lblSysMsgSetting.setTextFill(Constant.COLOR_ERROR);
		}
		
		executeSystemMsgTimerTask();
	}
	
	//@author A0112498B
	private class SystemMsgTimerTask extends TimerTask {
        public void run() {
    		Platform.runLater(new Runnable() {
    		    @Override
    		    public void run() {
    		    	lblSysMsgSetting.setText(Constant.EMPTY_STRING);	
    		    }
    		});
            timer.cancel();
        }
    }
}
