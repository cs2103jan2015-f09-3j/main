package controller;

import java.util.ArrayList;

import application.Constant;
import application.Main;
import application.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class SearchResultController {
	
	private MainController mainCon;
	private ArrayList<Task> resultList;
	@FXML AnchorPane anPaneSearchResult;
	@FXML VBox vBoxSearchResult;
	
	/*@FXML
	public void initialize() {
		loadSearchList();
	}*/
	
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	public void loadSearchList() {
		resultList = Main.searchResults;
		for(int i = 0; i < resultList.size(); i++) {
			Label lbl = new Label(resultList.get(i).getToDo());
			vBoxSearchResult.getChildren().add(lbl);
		}
		
	}
}
