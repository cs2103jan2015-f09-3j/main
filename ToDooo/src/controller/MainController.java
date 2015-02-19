package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import controller.HeaderController;
import controller.BodyController;

public class MainController{
	
	@FXML HeaderController headerController;
	@FXML BodyController bodyController;
	
	@FXML public void initialize() {
		headerController.init(this);
		bodyController.init(this);
	}

	public void sendToPaneAll(String text) {
		//bodyController.lblAll.setText(text);
	}

	public void showInTabAll(String text) {
		bodyController.anPaneAll.getChildren().add(new Label(text));		
	}
}
