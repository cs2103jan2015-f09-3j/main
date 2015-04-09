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
	
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	public void executeSystemMsgTimerTask() {
		timer = new Timer();
		timer.schedule(new SystemMsgTimerTask(), Constant.TIMER_SYSTEM_MSG_DURATION);
	}
	
	private void openFileDialog() {
		String systemMsg = "";
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Choose File Location");
		File selectedDir = dirChooser.showDialog(anPaneSetting.getScene().getWindow());
		txtPath.setText(selectedDir.getAbsolutePath());
		
		String newPath = txtPath.getText();
		String pathInSetting = Main.storage.readSavePath();
		
		if (!pathInSetting.equals(newPath)) {
			systemMsg = Main.storage.moveFile(newPath);
		}
		
		displaySystemMessage(systemMsg);
	}
	
	private void displaySystemMessage(String systemMsg) {
		lblSysMsgSetting.setText(systemMsg);
		
		if(systemMsg.contains(Constant.SYS_MSG_KEYWORD_FILE_SAVED)) {
			lblSysMsgSetting.setTextFill(Constant.COLOR_SUCCESS);
		} else {
			lblSysMsgSetting.setTextFill(Constant.COLOR_ERROR);
		}
		
		executeSystemMsgTimerTask();
	}
	
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