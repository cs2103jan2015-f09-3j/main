//@author A0112537M
package controller;

import application.Constant;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class SearchResultController {
	
	private MainController mainCon;
	@FXML AnchorPane anPaneSearchResult;
	@FXML VBox vBoxSearchResult;
	
	
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	public void loadSearchList() {
		vBoxSearchResult.getChildren().clear();
		mainCon.loadListByDate(Constant.VIEW_NAME_SEARCH_RESULT);
	}
}
