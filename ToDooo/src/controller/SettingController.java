package controller;

import java.io.File;
import java.io.IOException;

import application.Constant;
import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class SettingController {
	
	private MainController mainCon;
	@FXML AnchorPane anPaneSetting;
	@FXML TextField txtPath;
	@FXML Button btnBrowse;
	@FXML ImageView backIcon;
	
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
	public void goBackToMainMouse(MouseEvent e) throws IOException {
		mainCon.showPageInBody("/view/Body.fxml");
	}
	
	@FXML
	public void initialize() {
		txtPath.setText(Main.storage.readSavePath());
		
	}
	
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	private void openFileDialog() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Choose Storage Path");
		File selectedDir = dirChooser.showDialog(anPaneSetting.getScene().getWindow());
		txtPath.setText(selectedDir.getAbsolutePath());
		
		String newPath = txtPath.getText();
		String pathInSetting = Main.storage.readSavePath();
		
		if (!pathInSetting.equals(newPath)) {
			Main.storage.moveFile(newPath);
		}
	}
}
