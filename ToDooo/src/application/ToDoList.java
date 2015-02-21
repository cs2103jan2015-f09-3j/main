package application;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
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
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ToDoList {
	private String _listFilePath;

	public ToDoList() {		
		_listFilePath = Main.storage.readSavePath();		
		createListFileIfNotExist();		
	}
	
	public String getListFilePath() {
		return _listFilePath;
	}

	public void setListFilePath(String listFilePath) {
		_listFilePath = listFilePath;
	}

	private void createListFileIfNotExist() {
		File file = new File(_listFilePath);
		String result = Constant.MSG_SAVE_SUCCESS;
		
		boolean shouldCreate = !(file.exists());
		if (shouldCreate) {
			result = prepareNewList();
		}
	}
	
	private XPath getNewXPath() {
        XPathFactory xpathFactory = XPathFactory.newInstance();

        return xpathFactory.newXPath();
	}

	private String prepareNewList() {
		Document document = XmlManager.createXMLDocument();
								
		Element rootTag = document.createElement(Constant.TAG_FILE);
		document.appendChild(rootTag);
		
		XmlManager.createAndAppendChildElement(document, rootTag, 
									Constant.TAG_NEXT_ID, 
									String.valueOf(Constant.START_ID));
		
		Element categoriesTag = XmlManager.createAndAppendWrapper(document, rootTag,
							    Constant.TAG_CATEGORIES);
		
		XmlManager.createAndAppendChildElement(document, categoriesTag, 
									Constant.TAG_CATEGORY, 
									String.valueOf(Constant.CATEGORY_UNCATEGORISED));
		
		XmlManager.createAndAppendWrapper(document, rootTag, 
							   String.valueOf(Constant.TAG_TASKS));
		
		String result = Main.storage.writeFile(document, _listFilePath);
		return result;
	}
	
	public String addTaskToList(Task task) {
		Document document = Main.storage.getFileDocument();
		
		Element rootTag = document.getDocumentElement();
		
		Element nextIdTag = (Element) rootTag.
									  getElementsByTagName(Constant.TAG_NEXT_ID).
									  item(Constant.START_INDEX);
		
		Element tasksTag = (Element) rootTag.
									 getElementsByTagName(Constant.TAG_TASKS).
									 item(Constant.START_INDEX);
		
		boolean isCategorised = !(task.getCategory().equals(Constant.CATEGORY_UNCATEGORISED));
		if (isCategorised) {
			Element categoriesTag = (Element) rootTag.
											  getElementsByTagName(Constant.TAG_CATEGORIES).
											  item(Constant.START_INDEX);

			XmlManager.createAndAppendChildElement(document, categoriesTag, Constant.TAG_CATEGORY,
										task.getCategory().toString());
		}
		
		int nextId = Main.storage.readNextId() + 1;
		nextIdTag.setTextContent(String.valueOf(nextId));				
		
		Element taskTag = document.createElement(Constant.TAG_TASK);
		taskTag.setAttribute(Constant.TAG_ATTRIBUTE_ID, task.getId());
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_TYPE, 
									task.getTaskType().toString());
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_TODO, 
									task.getToDo());
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_ORIGINAL, 
									task.getOriginalText());				
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_ON, 
									InputParser.getDateString(task.getOn()));	
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_FROM, 
									InputParser.getDateString(task.getFrom()));
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_TO, 
									InputParser.getDateString(task.getTo()));
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_BY, 
									InputParser.getDateString(task.getBy()));	
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_CATEGORY, 
									task.getCategory().toString());
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_RECURRING, 
									String.valueOf(task.getIsRecurring()));
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_REPEAT, 
									task.getRepeat().toString());
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_PRIORITY, 
									task.getPriority().toString());
		
		tasksTag.appendChild(taskTag);		
		
		String result = Main.storage.writeFile(document);
		return result;
	}

	public Task deleteTaskFromList(String userInput) {
		String targetId = InputParser.getDeleteTargetIdFromString(userInput);
		
		Task removedTask = Main.storage.deleteTaskFromFileById(targetId);
		
		return removedTask;
	}
}
