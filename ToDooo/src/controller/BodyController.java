package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class BodyController{

	private MainController main;
	@FXML AnchorPane anPaneAll;
	@FXML AnchorPane anPaneCat;
	@FXML AnchorPane anPanePri;

	public void init(MainController mainController) {
		main = mainController;
	}

}
