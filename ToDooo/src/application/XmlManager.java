package application;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class XmlManager {
	public static Element createAndAppendChildElement(Document document,
												Element parentElement, String tag, String content) {
		Element element = document.createElement(tag);
		Text text = document.createTextNode(content);
		element.appendChild(text);
		parentElement.appendChild(element);

		return element;
	}

	public static Element createAndAppendWrapper(Document document,
										   Element parentElement, String tag) {
		Element element = document.createElement(tag);
		parentElement.appendChild(element);

		return element;
	}
	
	public static Document createXmlDocument() {
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
	
	public static Document initDocument() {
		Document document = XmlManager.createXmlDocument();
								
		Element root = document.createElement(Constant.TAG_FILE);
		document.appendChild(root);
		
		XmlManager.createAndAppendChildElement(document, root, 
									Constant.TAG_NEXT_ID, 
									String.valueOf(Constant.START_ID));
		
		Element categories = XmlManager.createAndAppendWrapper(document, root,
							    Constant.TAG_CATEGORIES);
		
		XmlManager.createAndAppendChildElement(document, categories, 
									Constant.TAG_CATEGORY, 
									String.valueOf(Constant.CATEGORY_UNCATEGORISED));
		
		XmlManager.createAndAppendWrapper(document, root, 
							   String.valueOf(Constant.TAG_TASKS));
		return document;
	}

	public static Task transformNodeToTask(Node node) {
		Task task = new Task();		
		Element element = (Element) node;
		
		String text = element.getAttribute(Constant.TAG_ATTRIBUTE_ID);
		task.setId(text);
		
		text = getTextByTagName(element, Constant.TAG_TYPE);
		task.setTaskType(TaskType.valueOf(text));		
		
		text = getTextByTagName(element, Constant.TAG_TODO);
		task.setToDo(text);
		
		text = getTextByTagName(element, Constant.TAG_ORIGINAL);
		task.setOriginalText(text);
		
		text = getTextByTagName(element, Constant.TAG_ON);
		task.setOn(Main.inputParser.getDateFromString(text));
		
		text = getTextByTagName(element, Constant.TAG_FROM);
		task.setFrom(Main.inputParser.getDateFromString(text));
		
		text = getTextByTagName(element, Constant.TAG_TO);
		task.setTo(Main.inputParser.getDateFromString(text));
		
		text = getTextByTagName(element, Constant.TAG_BY);
		task.setBy(Main.inputParser.getDateFromString(text));
		
		text = getTextByTagName(element, Constant.TAG_CATEGORY);
		task.setCategory(text);
		
		text = getTextByTagName(element, Constant.TAG_RECURRING);
		task.setIsRecurring(Boolean.valueOf(text));
		
		text = getTextByTagName(element, Constant.TAG_REPEAT);
		task.setRepeat(Frequency.valueOf(text));
		
		text = getTextByTagName(element, Constant.TAG_REPEAT_DAY);
		task.setRepeatDay(Integer.valueOf(text));
		
		text = getTextByTagName(element, Constant.TAG_REPEAT_UNTIL);
		task.setRepeatUntil(Main.inputParser.getDateFromString(text));
		
		text = getTextByTagName(element, Constant.TAG_PRIORITY);
		task.setPriority(Priority.valueOf(text));
		
		return task;
	}
	
	public static void setText(Element parentElement, String tagName, String text) {
		Element element = (Element) parentElement.
									  getElementsByTagName(tagName).
									  item(Constant.START_INDEX);
		element.setTextContent(text);
	}
	
	public static String getTextByTagName(Element parentElement, String tagName) {
		return parentElement.getElementsByTagName(tagName).
			   item(Constant.START_INDEX).
			   getTextContent();
	}

	public static Document getUpdatedXmlDocument() {
		Document document = XmlManager.initDocument();	
		Element root =  document.getDocumentElement();
		
		XmlManager.transformNextIdToXml(document, root);
		XmlManager.transformCategoriesToXml(document, root);
		XmlManager.transformTasksToXml(document, root);		
		
		return document;
	}
	
	private static void transformNextIdToXml(Document document, Element root) {
		int nextId = Main.list.getNextId();
		Element nextIdElement = (Element) root.
								getElementsByTagName(Constant.TAG_NEXT_ID).
								item(Constant.START_INDEX);
		
		nextIdElement.setTextContent(String.valueOf(nextId));
	}
	
	private static void transformCategoriesToXml(Document document, Element root) {
		ArrayList<String> categories = Main.list.getCategories();
		
		Element categoriesWrapper = (Element) root.
									getElementsByTagName(Constant.TAG_CATEGORIES).
				   					item(Constant.START_INDEX);
		
		for (String category : categories) {
			if (category.equals(Constant.CATEGORY_UNCATEGORISED)) {
				continue;
			}
			
			XmlManager.createAndAppendChildElement(document, categoriesWrapper, 
												   Constant.TAG_CATEGORY, category);			
		}
	}

	private static void transformTasksToXml(Document document, Element root) {
		ArrayList<Task> tasks = Main.list.getTasks();
		
		Element tasksWrapper = (Element) root.
								getElementsByTagName(Constant.TAG_TASKS).
							   item(Constant.START_INDEX);
		for (Task task : tasks) {
			Element taskTag = transformTaskToXml(document, task);
			
			tasksWrapper.appendChild(taskTag);
		}
	}

	public static Element transformTaskToXml(Document document, Task task) {
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
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_REPEAT_DAY, 
											   String.valueOf(task.getRepeatDay()));	
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_REPEAT_UNTIL, 
											   InputParser.getDateString(task.getRepeatUntil()));	
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_PRIORITY, 
											   task.getPriority().toString());
		return taskTag;
	}
}
