package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Storage {
	private static Storage _storage;
	private static Transformer _transformer;
	private static XPath _xPath;
	private static DocumentBuilder _documentBuilder;
	
	private Storage() {		
		initTransformer();			
		_xPath = XmlManager.getNewXPath();
		_documentBuilder = XmlManager.getNewDocBuilder();
	}
	
	public static Storage getInstance() {
		if (_storage == null) {
			_storage = new Storage();
		}
		
		return _storage;
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
			exception.printStackTrace();
		}
	}
	
	
	public String writeFile(Document document) {
		return writeFile(document, Main.list.getListFilePath());
	}
	
	public String writeFile(Document document, String savePath) {
		try {
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = 
					new StreamResult(new File(savePath));
			_transformer.transform(domSource, streamResult);
		} catch (TransformerException exception) {
			return Constant.MSG_SAVE_FAIL;
		}
		
		return Constant.MSG_SAVE_SUCCESS;
	}

	public int readNextId() {
		int nextId = 0;
		Document fileDoc = getFileDocument();	
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_NEXT_ID);
			
			Double value = (Double)expression.
						   evaluate(fileDoc, XPathConstants.NUMBER);
			
			nextId = value.intValue();
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return nextId;
	}

	public String readSavePath() {
		String savePath = null;
		Document settingDoc = getSettingDocument();		
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_SETTING + "/" +
								   Constant.TAG_SETTING_SAVE);
			
			savePath = (String) expression.
					   evaluate(settingDoc, XPathConstants.STRING);
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return savePath;
	}

	public Document getFileDocument() {
		try {
			return _documentBuilder.parse(Main.storage.readSavePath()); 			
		} catch (SAXException | IOException exception) {
			exception.printStackTrace();
		}			
		return null;
	}
	
	public Document getSettingDocument() {
		try {			
			return _documentBuilder.parse(Constant.PATH_SETTING); 			
		} catch (SAXException | IOException exception) {
			exception.printStackTrace();
		}			
		return null;
	}

	public ArrayList<Task> loadXmlToArrayList() {
		Document document = getFileDocument();
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_TASKS + "/" +
								   Constant.TAG_TASK);
			
			NodeList taskNodes = (NodeList) expression.
								 evaluate(document, XPathConstants.NODESET);
			
			Node taskNode = null;
			for (int i = 0; i < taskNodes.getLength(); i++) {
				taskNode = taskNodes.item(i);
				
				Task task = XmlManager.transformNodeToTask(taskNode);
				tasks.add(task);
			}
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return tasks;
	}
	
	public ArrayList<String> loadCategoriesToArrayList() {
		Document document = getFileDocument();
		ArrayList<String> categories = new ArrayList<String>();
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_CATEGORIES + "/" +
								   Constant.TAG_CATEGORY);
			
			NodeList categoryNodes = (NodeList) expression.
								 	 evaluate(document, XPathConstants.NODESET);
			
			Node categoryNode = null;
			String categoryString = null;
			for (int i = 0; i < categoryNodes.getLength(); i++) {
				categoryNode = categoryNodes.item(i);
				categoryString = categoryNode.getTextContent();
				
				categories.add(categoryString);
			}
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return categories;
	}

	public void updateAndSaveFile() {
		Document document = XmlManager.getUpdatedXmlDocument();
		
		writeFile(document);		
	}
	
	public void updateDirPathInSetting(String dirPath) {
		Document doc = getSettingDocument();

		Node save = doc.getElementsByTagName(Constant.TAG_SETTING_SAVE).item(0);
		save.setTextContent(dirPath + "\\" + Constant.PATH_FILE_NAME);
		
		writeFile(doc, Constant.PATH_SETTING);
	}
	
	public void updateFilePathInSetting(String filePath) {
		Document doc = getSettingDocument();

		Node save = doc.getElementsByTagName(Constant.TAG_SETTING_SAVE).item(0);
		save.setTextContent(filePath);
		
		writeFile(doc, Constant.PATH_SETTING);
	}
	
	public String writeTaskToFile(Task task) {
		Document document = getFileDocument();
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_TASKS);
			
			Element tasksNode = (Element) expression.
							   evaluate(document, XPathConstants.NODE);
			
			Element taskNode = XmlManager.transformTaskToXml(document, task);
			
			tasksNode.appendChild(taskNode);
			
			writeFile(document);
		} catch (Exception exception) {
			exception.printStackTrace();
			return Constant.MSG_ADD_FAIL;
		}
		
		return Constant.MSG_ADD_SUCCESS;
	}
	
	public boolean hasWrittenCategoryToFile(String category) {
		Document document = getFileDocument();
		boolean hasWritten = false;
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_CATEGORIES);
			
			Element categoriesNode = (Element) expression.
					   evaluate(document, XPathConstants.NODE);
	
			XmlManager.createAndAppendChildElement(document, categoriesNode, 
					   							   Constant.TAG_CATEGORY, category);	
			
			writeFile(document);
			
			hasWritten = true;
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return hasWritten;
	}
	
	public Task deleteTaskFromFileById(String targetId) {
		Document fileDoc = getFileDocument();	
		Task removedTask = null;
		
		NodeList nodes = getNodesById(fileDoc, targetId);
		if (nodes.getLength() > 0) {
			Node targetNode = nodes.item(0);
			
			removedTask = XmlManager.transformNodeToTask(targetNode);			
			targetNode.getParentNode().removeChild(targetNode);
			
			cleanAndWriteFile(fileDoc);
		} 
		
		return removedTask;
	}
	
	public Task deleteRecurringTaskFromFileById(String taskId, String recurringTaskId) {
		Document fileDoc = getFileDocument();	
		Task removedTask = null;
		
		NodeList nodes = getRecurringTasksNodesById(fileDoc, taskId, recurringTaskId);
		if (nodes.getLength() > 0) {
			Element targetNode = (Element) nodes.item(0);			
			
			NodeList taskNodes = getNodesById(fileDoc, taskId);
			Element taskNode = (Element) taskNodes.item(0);
			removedTask = XmlManager.transformNodeToTask(taskNode);		
			
			targetNode.getParentNode().removeChild(targetNode);
			cleanAndWriteFile(fileDoc);
		} 
		
		return removedTask;
	}
	
	private NodeList getNodesById(Document document, String targetId) {
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_TASKS + "/" +
								   Constant.TAG_TASK + "[@" +
								   Constant.TAG_ATTRIBUTE_ID + "='" + 
								   targetId + "']");
			
			return (NodeList)expression.evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return null;
	}	
	
	private NodeList getRecurringTasksNodesById(Document document, String taskId, 
												String recurringTaskId) {
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_TASKS + "/" +
								   Constant.TAG_TASK + "[@" +
								   Constant.TAG_ATTRIBUTE_ID + "='" + 
								   taskId + "']" + "/" +
								   Constant.TAG_RECURRING_TASKS + "/" +
								   Constant.TAG_RECURRING_TASK + "[@" +
								   Constant.TAG_RECURRING_ID + "='" + 
								   recurringTaskId + "']");
			
			return (NodeList)expression.evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
		
		return null;
	}
	
	public void cleanAndWriteFile() {
		Document document = getFileDocument();
		cleanAndWriteFile(document);
	}

	public void cleanAndWriteFile(Document document) {
		try {
			NodeList nodes = (NodeList) _xPath.evaluate(Constant.XML_WHITESPACE_NODE_XPATH, 
														document, XPathConstants.NODESET);
			
			for (int i = 0; i < nodes.getLength(); i++) {
			    Node node = nodes.item(i);
			    node.getParentNode().removeChild(node);
			}
			
			writeFile(document);
		} catch (XPathExpressionException exception) {
			exception.printStackTrace();
		}
	}
	
	public void writeNextIdInFile(int nextId) {
		Document document = getFileDocument();
		
		try {
			XPathExpression expression = 
					_xPath.compile("/" + Constant.TAG_FILE + "/" +
								   Constant.TAG_NEXT_ID);
			
			Element nextIdNode = (Element) expression.
							   	 evaluate(document, XPathConstants.NODE);
			
			nextIdNode.setTextContent(String.valueOf(nextId));
			
			writeFile(document);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public void moveFile(String path) {
		String pathInSetting = readSavePath();
		Path oldPath = Paths.get(pathInSetting);
		Path newPath = Paths.get(path);
		
		try {
			Files.move(oldPath, newPath.resolve(oldPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
		updateDirPathInSetting(path);
		
		Main.list = new ToDoList();
	}
}