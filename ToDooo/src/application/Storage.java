package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Storage {
	private static Storage _storage;
	private Transformer _transformer;
	private XPath _xPath;
	private DocumentBuilder _documentBuilder;
	
	//@author A0112498B
	// -----------------------------------------------------------------------------------------------
	// Constructor
	// -----------------------------------------------------------------------------------------------		
	private Storage() {		
		initTransformer();			
		_xPath = XmlManager.getNewXPath();
		_documentBuilder = XmlManager.getNewDocBuilder();
	}
	
	// -----------------------------------------------------------------------------------------------
	// Get methods
	// -----------------------------------------------------------------------------------------------
	public static Storage getInstance() {
		if (_storage == null) {
			_storage = new Storage();
		}
		
		return _storage;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Public methods
	// -----------------------------------------------------------------------------------------------
	// ---------------------------------------------------------
	// Read-related methods
	// ---------------------------------------------------------
	public int readNextId() {
		int nextId = 0;
		Document fileDoc = getFileDocument();	
		
		try {
			XPathExpression expression = 
					_xPath.compile(Constant.XML_XPATH_NEXT_ID);
			
			Double value = (Double)expression.
						   evaluate(fileDoc, XPathConstants.NUMBER);
			
			nextId = value.intValue();
		} catch (XPathExpressionException exception) {
			Main.LOGGER.logp(Level.SEVERE, 
							 Storage.class.getName(), 
							 "readNextId", 
							 exception.getMessage());
		}
		
		return nextId;
	}
	
	public String readSavePath() {
		String savePath = null;
		Document settingDoc = getSettingDocument();		
		
		try {
			XPathExpression expression = 
					_xPath.compile(Constant.XML_XPATH_SETTING_SAVE);
			
			savePath = (String) expression.
					   evaluate(settingDoc, XPathConstants.STRING);
			
			if (savePath.equals(Constant.PATH_DEFAULT)) {
				savePath = Main.getFolderPath() + Constant.PATH_DEFAULT;

			}
		} catch (XPathExpressionException exception) {
			Main.LOGGER.logp(Level.SEVERE, 
							 Storage.class.getName(), 
							 "readSavePath", 
							 exception.getMessage());
		}
		
		return savePath;
	}
	
	/*
	 * Used in unit testing
	 */
	public void updateFilePathInSetting(String filePath) {
		Document doc = getSettingDocument();

		Node save = doc.getElementsByTagName(Constant.TAG_SETTING_SAVE).item(0);
		save.setTextContent(filePath);
		
		writeFile(doc, Constant.PATH_SETTING);
	}
	
	// ---------------------------------------------------------
	// Write-related methods
	// ---------------------------------------------------------
	public String writeFile(Document document) {
		return writeFile(document, Main.list.getListFilePath());
	}
	
	public String writeFile(Document document, String savePath) {
		try {
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = null;
			
			if (savePath.equals(Constant.PATH_SETTING)) {
				savePath = Main.getFolderPath() + savePath;		
			} 
			
			streamResult = new StreamResult(new File(savePath));
			_transformer.transform(domSource, streamResult);
		} catch (TransformerException exception) {
			Main.LOGGER.logp(Level.SEVERE, 
							 Storage.class.getName(), 
							 "writeFile", 
							 exception.getMessage());
		}
		
		return Constant.MSG_SAVE_SUCCESS;
	}


	public ArrayList<Task> loadTasksXmlToArrayList() {
		Document document = getFileDocument();
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		Element root = document.getDocumentElement();
		NodeList taskNodes = root.
				getElementsByTagName(Constant.TAG_TASK);
		
		Node taskNode = null;
		for (int i = 0; i < taskNodes.getLength(); i++) {
			taskNode = taskNodes.item(i);
			
			Task task = XmlManager.transformNodeToTask(taskNode);
			tasks.add(task);
		}
		
		return tasks;
	}

	public ArrayList<String> loadCategoriesXmlToArrayList() {
		Document document = getFileDocument();
		ArrayList<String> categories = new ArrayList<String>();
		
		Element root = document.getDocumentElement();
		NodeList categoryNodes = root.getElementsByTagName(Constant.TAG_CATEGORY);
		
		Node categoryNode = null;
		String categoryString = null;
		for (int i = 0; i < categoryNodes.getLength(); i++) {
			categoryNode = categoryNodes.item(i);
			categoryString = categoryNode.getTextContent();
			
			categories.add(categoryString);
		}
		
		return categories;
	}
	
	public String writeListToFile(ArrayList<Task> tasks) {
		removeAllTaskNodesFromFile();
		Document document = getFileDocument();

		Element root = document.getDocumentElement();
		Node tasksNode = (Node) root.
				getElementsByTagName(Constant.TAG_TASKS).item(0);
		
		Node taskNode = null;
		Task task = null;
		for (int i = 0; i < tasks.size(); i++) {
			task = tasks.get(i);
			
			taskNode = XmlManager.transformTaskToXml(document, task);
			tasksNode.appendChild(taskNode);
		}
		
		cleanAndWriteFile(document);
		
		return Constant.MSG_ADD_SUCCESS;
	}
	
	public boolean hasWrittenCategoryToFile(String category) {
		Document document = getFileDocument();
		boolean hasWritten = false;
		
		Element root = document.getDocumentElement();
		Element categoriesNode = (Element) root.
				getElementsByTagName(Constant.TAG_CATEGORIES).item(0);
		
		XmlManager.createAndAppendChildElement(document, categoriesNode, 
				   Constant.TAG_CATEGORY, category);	

		writeFile(document);
		hasWritten = true;
		
		return hasWritten;
	}
	
	public void writeNextIdInFile(int nextId) {
		Document document = getFileDocument();
		
		Element root = document.getDocumentElement();
		Element nextIdNode = (Element) root.
				getElementsByTagName(Constant.TAG_NEXT_ID).item(0);
		nextIdNode.setTextContent(String.valueOf(nextId));
		
		writeFile(document);
	}
	
	// ---------------------------------------------------------
	// Others
	// ---------------------------------------------------------
	public Document getFileDocument() {
		try {
			return _documentBuilder.parse(Main.storage.readSavePath()); 			
		} catch (SAXException | IOException exception) {
			Main.LOGGER.logp(Level.SEVERE, 
							 Storage.class.getName(), 
							 "getFileDocument", 
							 exception.getMessage());
		}			
		return null;
	}
	
	public Document getSettingDocument() {
		try {						
			return _documentBuilder.parse(Main.getFolderPath() + 
										  Constant.PATH_SETTING); 			
		} catch (SAXException | IOException exception) {
			Main.LOGGER.logp(Level.SEVERE, 
							 Storage.class.getName(), 
							 "getSettingDocument", 
							 exception.getMessage());
		}			
		return null;
	}

	public String moveFile(String path) { 
		String systemMsg;
		
		String pathInSetting = readSavePath();
		Path oldPath = Paths.get(pathInSetting);		
		Path newPath = Paths.get(path); 
		
		try {
			Files.move(oldPath, newPath.resolve(oldPath.getFileName()), 
					   StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
		systemMsg = updateDirPathInSetting(path); 
		
		Main.list = new ToDoList();
		
		return systemMsg;
	}
	
	//@author A0112537M
	/*
	 * Update setting.xml with the period of 
	 * removing completed task from listFile.xml
	 */
	public String updateCleanRecurrenceInSetting(String recurrence) { 
		Document doc = getSettingDocument();

		Node clean = doc.getElementsByTagName(Constant.TAG_SETTING_CLEAN).item(0);
		clean.setTextContent(recurrence);
		
		return writeFile(doc, Constant.PATH_SETTING);
	}
		
	/*
	 * Read the setting of the clean recurrence function
	 */
	public String readSaveCleanRecurrence() {
		String cleanRecurrence = null;
		Document settingDoc = getSettingDocument();		
		
		try {
			XPathExpression expression = 
					_xPath.compile(Constant.XML_XPATH_SETTING_CLEAN);
			
			cleanRecurrence = (String) expression.
					   		  evaluate(settingDoc, XPathConstants.STRING);
			
		} catch (XPathExpressionException exception) {
			Main.LOGGER.logp(Level.SEVERE, 
							 Storage.class.getName(), 
							 "readSaveCleanRecurrence", 
							 exception.getMessage());
		}
		
		return cleanRecurrence;
	}
	
	public void removeEmptyCategory() {
		Document document = getFileDocument();
		
		Element root = document.getDocumentElement();
		NodeList categoryNodes = root.getElementsByTagName(Constant.TAG_CATEGORY);
		
		Node categoryNode = null;
		String categoryString = null;
		boolean isEmpty;
		int restartIndex = -1;
		
		boolean isNotEqual = false;
		for (int i = 0; i < categoryNodes.getLength(); i++) {
			categoryNode = categoryNodes.item(i);
			categoryString = categoryNode.getTextContent();
			isEmpty = isEmptyCategory(categoryString);
			
			isNotEqual = !categoryString.
						 equalsIgnoreCase(Constant.CATEGORY_UNCATEGORISED);
			
			if(isNotEqual && isEmpty) {
				categoryNode.getParentNode().removeChild(categoryNode);
				Main.list.getCategories().remove(categoryString);
				i = restartIndex;
			}	
		}
		
		cleanAndWriteFile(document);
	}
		
	// -----------------------------------------------------------------------------------------------
	// Private methods
	// -----------------------------------------------------------------------------------------------
	//@author A0112856E
	private String updateDirPathInSetting(String dirPath) { 
		Document doc = getSettingDocument();

		Node save = doc.getElementsByTagName(Constant.TAG_SETTING_SAVE).item(0);
		save.setTextContent(dirPath + "\\" + Constant.PATH_FILE_NAME);
		
		return writeFile(doc, Constant.PATH_SETTING);
	}
		
	//@author A0112498B
	private void cleanAndWriteFile(Document document) {
		try {
			NodeList nodes = (NodeList) _xPath.
							evaluate(Constant.XML_WHITESPACE_NODE_XPATH, 
							document, XPathConstants.NODESET);
			
			for (int i = 0; i < nodes.getLength(); i++) {
			    Node node = nodes.item(i);
			    node.getParentNode().removeChild(node);
			}
			
			writeFile(document);
		} catch (XPathExpressionException exception) {
			Main.LOGGER.logp(Level.SEVERE, 
							 Storage.class.getName(), 
							 "cleanAndWriteFile", 
							 exception.getMessage());
		}
	}
	
	private void removeAllTaskNodesFromFile() {
		Document document = getFileDocument();
		Element root = document.getDocumentElement();
		
		try {
			XPathExpression expression = _xPath.compile("/" + Constant.TAG_FILE + 
												"/" + Constant.TAG_TASKS);
			
			Element tasksNode = (Element) expression.
								evaluate(document, XPathConstants.NODE);
			
			tasksNode.getParentNode().removeChild(tasksNode);
			XmlManager.createAndAppendWrapper(document, root, 
					   						  String.valueOf(Constant.TAG_TASKS));
			
			cleanAndWriteFile(document);
		} catch (Exception exception) {
			Main.LOGGER.logp(Level.SEVERE, 
							 Storage.class.getName(), 
							 "removeAllTaskNodesFromFile", 
							 exception.getMessage());
		}
	}
		
	private void initTransformer() {
		try {
			TransformerFactory transformerFactory = 
					TransformerFactory.newInstance();
			
			_transformer = transformerFactory.newTransformer();
			_transformer.setOutputProperty(OutputKeys.INDENT, 
					   					   Constant.XML_OUTPUT_INDENT);
			_transformer.setOutputProperty(Constant.XML_OUTPUT_INDENT_PROPERTY, 
										   Constant.XML_OUTPUT_INDENT_AMOUNT);
		} catch (TransformerConfigurationException exception) {
			Main.LOGGER.logp(Level.SEVERE, 
							 Storage.class.getName(), 
							 "initTransformer", 
							 exception.getMessage());
		}
	}
	
	private boolean isEmptyCategory(String category) {
		boolean isEmpty = true;
		
		ArrayList<Task> taskList = Main.list.getTasks();
		
		for(int i = 0; i < taskList.size(); i++) {
			if(taskList.get(i).getCategory().equalsIgnoreCase(category)) {
				isEmpty = false;
				break;
			}
		}
		
		return isEmpty;
	}
}