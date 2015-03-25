package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class BodyController{

	private MainController mainCon;
	private SearchResultController searchResultCon;
	
	@FXML AnchorPane anPaneMain;
	@FXML VBox vBoxAll;
	@FXML VBox vBoxCategory;
	@FXML VBox vBoxPriority;
	@FXML Tab tabAll;
	@FXML Tab tabCategory;
	@FXML Tab tabPriority;
	@FXML TabPane tPaneMain;
	
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	
}
