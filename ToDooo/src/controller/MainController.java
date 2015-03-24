package controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import application.Constant;
import application.Main;
import application.Task;
import application.Undo;
import application.DateParser;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Line;
import controller.HeaderController;
import controller.BodyController;

public class MainController{
	
	@FXML AnchorPane anPaneHeader;
	@FXML AnchorPane anPaneBody;
	@FXML HeaderController headerController;
	@FXML BodyController bodyController;
	@FXML SettingController settingController;
	@FXML SearchResultController searchResultController;
	private Timer timer;
	
	@FXML
	public void initialize() {
		headerController.init(this);
		bodyController.init(this);
		bodyController.loadListByDate(Constant.TAB_NAME_ALL);
	}

	@FXML
	public void onShortcutKey(KeyEvent e) {	
		String systemMsg = null;
		
		if (Constant.SHORTCUT_UNDO.match(e)) {
			systemMsg = executeUndo();
			bodyController.loadListByDate(Constant.TAB_NAME_ALL);
		} else if (Constant.SHORTCUT_REDO.match(e)) {
			systemMsg = executeRedo();
			bodyController.loadListByDate(Constant.TAB_NAME_ALL);
		} else {
			return;
		}
		
		headerController.lblSysMsg.setText(systemMsg);	
		
		executeSystemMsgTimerTask();		
	}
	
	public void showPageInBody(String fxmlFileName) throws IOException {
		anPaneBody.getChildren().clear();
		anPaneBody.getChildren().setAll(FXMLLoader.load(getClass().getResource(fxmlFileName)));
	}

	private String executeUndo() {
		String systemMsg = null;
		
		boolean canUndo = !(Main.undos.isEmpty());
		if (canUndo) {
			Undo undo = Main.undos.pop();
			systemMsg = undo.undoAction();
		} else {
			systemMsg = Constant.MSG_NO_UNDO;
		}
		
		return systemMsg;
	}
	
	private String executeRedo() {
		String systemMsg = null;
		
		boolean canRedo = !(Main.redos.isEmpty());
		if (canRedo) {
			Undo redo = Main.redos.pop();
			systemMsg = redo.redoAction();
		} else {
			systemMsg = Constant.MSG_NO_REDO;
		}
		
		return systemMsg;
	}
	
	public void executeSystemMsgTimerTask() {
		timer = new Timer();
		timer.schedule(new SystemMsgTimerTask(), Constant.TIMER_SYSTEM_MSG_DURATION);
	}
	
	private class SystemMsgTimerTask extends TimerTask {
        public void run() {
    		Platform.runLater(new Runnable() {
    		    @Override
    		    public void run() {
    		    	headerController.lblSysMsg.setText("");					
    		    }
    		});
            timer.cancel();
        }
    }
}
