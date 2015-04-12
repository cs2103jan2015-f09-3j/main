//@author A0112498B
package application;

import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XmlManager {
	// -----------------------------------------------------------------------------------------------
	// Public Methods
	// -----------------------------------------------------------------------------------------------
	public static DocumentBuilder getNewDocBuilder() {		
		try {
			DocumentBuilderFactory documentFactory = 
					DocumentBuilderFactory.newInstance();
			
			return documentFactory.newDocumentBuilder();
		} catch (ParserConfigurationException exception) {
			exception.printStackTrace();
		}
		
		return null;
	}
		
	public static XPath getNewXPath() {
        XPathFactory xpathFactory = XPathFactory.newInstance();

        return xpathFactory.newXPath();
	}
	
	/*
	 * Create and initialize an XML document 
	 * with the file's global information tags
	 */
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
	
	public static void createAndAppendChildElement(Document document,
												   Element parentElement, 
												   String tag, String content) {
		Element element = document.createElement(tag);
		Text text = document.createTextNode(content);
		element.appendChild(text);
		parentElement.appendChild(element);
	}

	public static Element createAndAppendWrapper(Document document,
										   		 Element parentElement, 
										   		 String tag) {
		Element element = document.createElement(tag);
		parentElement.appendChild(element);

		return element;
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
		Task.setStartEndDate(task, task.getOn(), text);
				
		text = getTextByTagName(element, Constant.TAG_FROM);
		task.setFrom(Main.inputParser.getDateFromString(text));
		
		text = getTextByTagName(element, Constant.TAG_TO);
		task.setTo(Main.inputParser.getDateFromString(text));
		Task.setStartEndDate(task, task.getFrom(), text);
		
		text = getTextByTagName(element, Constant.TAG_BY);
		task.setBy(Main.inputParser.getDateFromString(text));
		Task.setStartEndDate(task, task.getBy(), text);
		
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
		
		text = getTextByTagName(element, Constant.TAG_STATUS);
		task.setStatus(TaskStatus.valueOf(text));
				
		if (task.getIsRecurring()) {
			ArrayList<RecurringTask> recurringTasks = 
					transformRecurringTasksNodesToArrayList(element);
			task.setRecurringTasks(recurringTasks);
		}
		
		return task;
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
		
		XmlManager.createAndAppendChildElement(document, taskTag, Constant.TAG_STATUS, 
				   							   task.getStatus().toString());
						
		createAndAppendRecurringTasksNodes(document, taskTag, task);
		
		return taskTag;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Private Methods
	// -----------------------------------------------------------------------------------------------
	private static Document createXmlDocument() {
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
	
	private static String getTextByTagName(Element parentElement, String tagName) {
		return parentElement.getElementsByTagName(tagName).
			   item(0).
			   getTextContent();
	}
	
	/*
	 * Transform XML node to an arraylist of recurring task objects
	 */
	private static ArrayList<RecurringTask> transformRecurringTasksNodesToArrayList(Element element) {
		ArrayList<RecurringTask> recurringTasks = new ArrayList<RecurringTask>();
		
		Element recurringTasksWrapper = 
				(Element) element.getElementsByTagName(Constant.TAG_RECURRING_TASKS).
				item(0);
		
		NodeList recurringTasksNodes = recurringTasksWrapper.
									   getElementsByTagName(Constant.TAG_RECURRING_TASK);
		
		for (int i = 0; i < recurringTasksNodes.getLength(); i++) {
			RecurringTask recurringTask = transformNodeToRecurringTask(
					recurringTasksNodes, i);
			
			recurringTasks.add(recurringTask);
		}
				
		return recurringTasks;
	}

	/*
	 * Transform XML node to a recurring task object
	 */
	private static RecurringTask transformNodeToRecurringTask(
			NodeList recurringTasksNodes, int i) {
		RecurringTask recurringTask = new RecurringTask();
		Element recurringTaskNode = (Element) recurringTasksNodes.item(i);
		
		String text = recurringTaskNode.getAttribute(Constant.TAG_RECURRING_ID);
		recurringTask.setRecurringTaskId(text);
		
		text = getTextByTagName(recurringTaskNode, Constant.TAG_RECURRING_STATUS);
		recurringTask.setStatus(TaskStatus.valueOf(text));		
		
		text = getTextByTagName(recurringTaskNode, Constant.TAG_RECURRING_DATE);
		recurringTask.setRecurDate(Main.inputParser.getDateFromString(text));
		return recurringTask;
	}
	
	/*
	 * Transform recurring task objects to XML nodes
	 */
	private static void createAndAppendRecurringTasksNodes(Document document, Element taskTag, Task task) {
		if (task.getIsRecurring()) {
			Element recurringTasksWrapper = XmlManager.createAndAppendWrapper(document, taskTag, 
					   						String.valueOf(Constant.TAG_RECURRING_TASKS));
						
			ArrayList<RecurringTask> recurringTasks = task.getRecurringTasks();
			
			
			for (RecurringTask recurringTask : recurringTasks) {
				Element recurringTaskTag = createRecurringTaskNode(document,
						recurringTask);	
				
				recurringTasksWrapper.appendChild(recurringTaskTag);
			}
		}
	}

	/*
	 * Transform recurring task object to XML node
	 */
	private static Element createRecurringTaskNode(Document document,
			RecurringTask recurringTask) {
		Element recurringTaskTag = document.createElement(Constant.TAG_RECURRING_TASK);
		recurringTaskTag.setAttribute(Constant.TAG_RECURRING_ID, recurringTask.getRecurringTaskId());
		
		XmlManager.createAndAppendChildElement(document, recurringTaskTag, 
											   Constant.TAG_RECURRING_STATUS, 
											   recurringTask.getStatus().toString());		
		
		XmlManager.createAndAppendChildElement(document, recurringTaskTag, 
											   Constant.TAG_RECURRING_DATE, 
											   InputParser.getDateString(recurringTask.getRecurDate()));
		return recurringTaskTag;
	}
	
	
}
