package application;

import java.util.ArrayList;
import java.util.Stack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	public static Storage storage;
	public static ToDoList list;
	public static InputParser inputParser;
	public static Stack<Undo> undos;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		initialise();				
		launch(args);
	}
	
	public static void initialise() {
		Main.storage = new Storage();
		Main.inputParser = new InputParser();
		Main.list = new ToDoList();
		Main.undos = new Stack<Undo>();
	}
}
