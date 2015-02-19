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
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ToDoList {
	private XPath _xPath;
	private String _listFilePath;

	public ToDoList() {		
		_listFilePath = Main.storage.readSavePath();		
		createListFileIfNotExist();		
		_xPath = getNewXPath();
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
		Document document = createXMLDocument();
								
		Element rootTag = document.createElement(Constant.TAG_FILE);
		document.appendChild(rootTag);
		
		Element nextIdTag = document.
						 createElement(Constant.TAG_NEXT_ID);
		Text nextIdText = document.
						  createTextNode(String.valueOf(Constant.START_ID));
		nextIdTag.appendChild(nextIdText);
		rootTag.appendChild(nextIdTag);
		
		Element categoriesTag = document.
							 createElement(Constant.TAG_CATEGORIES);
		rootTag.appendChild(categoriesTag);
		
		Element categoryTag = document.createElement(Constant.TAG_CATEGORY);
		Text categoryText = document.
							createTextNode(Constant.CATEGORY_UNCATEGORISED);
		categoryTag.appendChild(categoryText);
		categoriesTag.appendChild(categoryTag);
		
		Element tasksTag = document.createElement(Constant.TAG_TASKS);
		rootTag.appendChild(tasksTag);
		
		String result = Main.storage.writeFile(document, _listFilePath);
		return result;
	}
	
	public String addTaskToFileDocument(Task task) {
		Document document = Main.storage.getFileDocument();
		
		Element rootTag = document.getDocumentElement();
		
		Element nextIdTag = (Element) rootTag.
									  getElementsByTagName(Constant.TAG_NEXT_ID).
									  item(Constant.START_INDEX);
		int nextId = Main.storage.readNextId() + 1;
		nextIdTag.setTextContent(String.valueOf(nextId));
		
		Element tasksTag = (Element) rootTag.
									 getElementsByTagName(Constant.TAG_TASKS).
									 item(Constant.START_INDEX);	
		
		Element taskTag = document.createElement(Constant.TAG_TASK);
		taskTag.setAttribute(Constant.TAG_ATTRIBUTE_ID, task.getId());
		
		createAndAppendChildElement(document, taskTag, Constant.TAG_TYPE, 
									task.getTaskType().toString());
		
		createAndAppendChildElement(document, taskTag, Constant.TAG_TODO, 
									task.getToDo());
		
		createAndAppendChildElement(document, taskTag, Constant.TAG_ORIGINAL, 
									task.getOriginalText());				
		
		createAndAppendChildElement(document, taskTag, Constant.TAG_ON, 
									InputParser.getDateString(task.getOn()));	
		
		createAndAppendChildElement(document, taskTag, Constant.TAG_FROM, 
									InputParser.getDateString(task.getFrom()));
		
		createAndAppendChildElement(document, taskTag, Constant.TAG_TO, 
									InputParser.getDateString(task.getTo()));
		
		createAndAppendChildElement(document, taskTag, Constant.TAG_BY, 
									InputParser.getDateString(task.getBy()));	
		
		createAndAppendChildElement(document, taskTag, Constant.TAG_CATEGORY, 
									task.getCategory().toString());
		
		createAndAppendChildElement(document, taskTag, Constant.TAG_RECURRING, 
									String.valueOf(task.getIsRecurring()));
		
		createAndAppendChildElement(document, taskTag, Constant.TAG_REPEAT, 
									task.getRepeat().toString());
		
		createAndAppendChildElement(document, taskTag, Constant.TAG_PRIORITY, 
									task.getPriority().toString());
		
		tasksTag.appendChild(taskTag);		
		
		String result = Main.storage.writeFile(document);
		return result;
	}
	
	private void createAndAppendChildElement(Document document, Element parentElement,
										String tag, String content) {
		Element element = document.createElement(tag);
		Text text = document.createTextNode(content);
		element.appendChild(text);
		parentElement.appendChild(element);
	}
		
	private Document createXMLDocument() {
		try {
			DocumentBuilderFactory documentFactory = 
					DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = 
					documentFactory.newDocumentBuilder();
			
			return documentBuilder.newDocument(); 
			
		} catch (ParserConfigurationException exception) {
			exception.printStackTrace();
		}		
		
		return null;
	}
}
