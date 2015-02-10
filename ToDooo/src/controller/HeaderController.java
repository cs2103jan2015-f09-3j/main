package controller;

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
			main.showInTabAll(txtCmd.getText());
		}
	}

	public void init(MainController mainController) {
		main = mainController;
		
	}

}
