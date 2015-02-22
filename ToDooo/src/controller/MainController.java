package controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import controller.HeaderController;
import controller.BodyController;

public class MainController{
	
	@FXML AnchorPane anPaneHeader;
	@FXML AnchorPane anPaneBody;
	@FXML HeaderController headerController;
	@FXML BodyController bodyController;
	
	@FXML
	public void initialize() {
		headerController.init(this);
		bodyController.init(this);
	}

	public void sendToPaneAll(String text) {
		//bodyController.lblAll.setText(text);
	}

	public void showInTabAll(String text) {
		bodyController.anPaneAll.getChildren().add(new Label(text));		
	}
	
	public void showPageInBody(String fxmlFileName) throws IOException {
		anPaneBody.getChildren().clear();
		anPaneBody.getChildren().setAll(FXMLLoader.load(getClass().getResource(fxmlFileName)));
	}
	
	
}
