package application;

import java.util.Locale.Category;

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
	
	public static Document createXMLDocument() {
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
}
