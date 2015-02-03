package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MainController implements Initializable {

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	@FXML private TextField txtCmd;
	@FXML private Label lblAll;
	@FXML private Label lblCat;
	@FXML private Label lblPri;
	
	@FXML public void enterText(KeyEvent e){
		if(e.getCode() == KeyCode.ENTER){
			lblAll.setText("All: " + txtCmd.getText());
			lblCat.setText("Categories: " + txtCmd.getText());
			lblPri.setText("Priorities: " + txtCmd.getText());
		}
	}

}
