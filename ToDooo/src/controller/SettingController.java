package controller;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import application.Constant;
import application.Main;
import application.ToDoList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class SettingController {
	
	private MainController mainCon;
	@FXML AnchorPane anPaneSetting;
	@FXML TextField txtPath;
	@FXML Button btnBrowse;
	@FXML ImageView backIcon;
	
	@FXML 
	public void openFileDialogKey(KeyEvent e) {
		if(e.getCode() == KeyCode.F10) {
			openFileDialog();
		}
	}
	
	@FXML 
	public void openFileDialogMouse(MouseEvent e) {
		openFileDialog();
	}
	
	@FXML
	public void goBackToMainMouse(MouseEvent e) throws IOException {
		mainCon.showPageInBody("/view/Body.fxml");
	}
	
	public void init(MainController mainController) {
		mainCon = mainController;
	}
	
	private void openFileDialog() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Choose Storage Path");
		File selectedDir = dirChooser.showDialog(anPaneSetting.getScene().getWindow());
		txtPath.setText(selectedDir.getAbsolutePath());
	}
	
	private void changePathInSetting() {
		updatePath();
		try {
			File xmlList = new File(Constant.PATH_LISTFILE);
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			if (xmlList.exists()) {
				Document doc = builder.parse(xmlList);
				Element root = doc.getDocumentElement();
				NodeList taskList = doc.getElementsByTagName(Constant.TAG_TASKS);
				if (taskList != null && taskList.getLength() > 0) {
					for (int i = 0; i < taskList.getLength(); i++) {
						Node task = taskList.item(i);
						if (task.getNodeType() == Node.ELEMENT_NODE) {
							Element element = (Element) task;
						}
					}
				}
			} else {
				Main.list = new ToDoList();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updatePath() {
		String newPath = txtPath.getText();
		
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(Constant.PATH_SETTING);
			
			Node root = doc.getFirstChild();
			Node element = root.getFirstChild();
			element.setTextContent(newPath);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(newPath));
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

}
