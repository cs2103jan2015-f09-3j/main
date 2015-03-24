package controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import application.Constant;
import application.Main;
import application.Task;
import application.DateParser;
import application.TaskType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;

public class BodyController{

	private MainController mainCon;
	private SearchResultController searchResultCon;
	
	@FXML AnchorPane anPaneMain;
	@FXML VBox vBoxAll;
	@FXML VBox vBoxCategory;
	@FXML VBox vBoxPriority;

	
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	
}
