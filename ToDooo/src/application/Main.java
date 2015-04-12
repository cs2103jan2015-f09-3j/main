package application;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Stack;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Main extends Application {
	public static Storage storage;
	public static ToDoList list;
	public static InputParser inputParser;
	public static Stack<Undo> undos;
	public static Stack<Undo> redos;
	public static boolean toUpdate;
	public static boolean shouldResetCaret;
	public static String systemFeedback;
	public static ArrayList<Task> searchResults;
	
	public static Stage priStage;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			priStage = primaryStage;
			
			Parent root = FXMLLoader.load(Main.class.getResource(Constant.PATH_MAIN));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource(Constant.PATH_CSS).toExternalForm());
			priStage.getIcons().add(new Image(Constant.IMAGE_LOGO));
			priStage.setScene(scene);
			priStage.resizableProperty().setValue(Boolean.FALSE);
			priStage.setHeight(Constant.HEIGHT_STAGE);
			priStage.setWidth(Constant.WIDTH_STAGE);
			priStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		initialise();				
		launch(args);
	}
	
	public static void initialise() {		
		Main.storage = Storage.getInstance();
		Main.inputParser = InputParser.getInstance();
		
		Main.list = new ToDoList();
		Main.undos = new Stack<Undo>();
		Main.redos = new Stack<Undo>();
		Main.searchResults = new ArrayList<Task>();
		
		toUpdate = false;
		shouldResetCaret = false;
		systemFeedback = "";
	}
	
	public static String getFolderPath() {
		URL url = new Main().getClass().getProtectionDomain().getCodeSource().getLocation();
		
		String parentPath = null;
		try {
			String jarPath = URLDecoder.decode(url.getFile(), Constant.DECODE_SETTING); 
			parentPath = new File(jarPath).getParentFile().getPath();
		} catch (Exception e) {
			
		}
		
		return parentPath;
	}
}
