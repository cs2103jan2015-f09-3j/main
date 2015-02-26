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
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ToDoList {
	private int _nextId;
	private String _listFilePath;
	private ArrayList<Task> _tasks;
	private ArrayList<String> _categories;

	public ToDoList() {		
		_listFilePath = Main.storage.readSavePath();		
		
		createListFileIfNotExist();	
		
		_nextId = Main.storage.readNextId();
		_tasks = Main.storage.loadXmlToArrayList();
		_categories = Main.storage.loadCategoriesToArrayList();
	}
	
	public String getListFilePath() {
		return _listFilePath;
	}

	public void setListFilePath(String listFilePath) {
		_listFilePath = listFilePath;
	}
	
	public ArrayList<Task> getTasks() {
		return _tasks;
	}

	public void setTasks(ArrayList<Task> tasks) {
		_tasks = tasks;
	}

	public int getNextId() {
		return _nextId;
	}

	public void setNextId(int nextId) {
		_nextId = nextId;
	}

	public ArrayList<String> getCategories() {
		return _categories;
	}

	public void setCategories(ArrayList<String> categories) {
		_categories = categories;
	}

	private void createListFileIfNotExist() {
		File file = new File(_listFilePath);
		String result = Constant.MSG_SAVE_SUCCESS;
		
		boolean shouldCreate = !(file.exists());
		if (shouldCreate) {
			result = prepareNewList();
		}
	}
		
	private String prepareNewList() {
		Document document = XmlManager.initDocument();
		
		String result = Main.storage.writeFile(document, _listFilePath);
		return result;
	}
	
	public String addTaskToList(Task task) {
		String result = Constant.MSG_ADD_SUCCESS;		
		int beforeSize = _tasks.size();
		
		_tasks.add(task);
		
		if (_tasks.size() == beforeSize) {
			result = Constant.MSG_ADD_FAIL;
		}
		
		return result;
	}
	
	public void addCategoryToList(String category) {
		boolean shouldAdd = true;
		
		for (String taskCategory : _categories) {
			if (taskCategory.equalsIgnoreCase(category)) {
				shouldAdd = false;
				
				break;
			}
		}
		
		if (shouldAdd) {
			_categories.add(category);
			_nextId++;
		}
	}

	public Task deleteTaskFromList(String userInput) {
		Task removedTask = null;
		String targetId = InputParser.getTargetIdFromString(userInput);
		
		removedTask = deleteTaskById(targetId);
		
		return removedTask;
	}
	
	public Task updateTaskOnList(String userInput) {
		String targetId = InputParser.getTargetIdFromUpdateString(userInput);		
		Task updatedTask = new Task(userInput, targetId);
		
		Task originalTask = deleteTaskById(targetId);
		
		if (originalTask != null) {
			_tasks.add(updatedTask);
		}
		
		return originalTask;
	}
	
	public Task getTaskById(String targetId) {		
		Task targetTask = null;
		boolean isFound = false;
		
		for (Task task : _tasks) {
			isFound = (task.getId().equals(targetId));
			
			if(isFound) {
				targetTask = task;				
				break;
			}
		}
		
		return targetTask;
	}
	
	private Task deleteTaskById(String targetId) {
		Task removedTask = null;
		int index = 0;
		
		boolean isFound = false;
		for (Task task : _tasks) {
			isFound = (task.getId().equals(targetId));
			
			if (isFound) {
				removedTask = task;
				break;
			}
			
			index++;
		}
		
		_tasks.remove(index);
		
		return removedTask;
	}
	
	public String getSavePathDirectory() {
		String savePath = Main.storage.readSavePath();
		
		if (savePath.equals(Constant.PATH_DEFAULT)) {
			String workingDirectory = 
					System.getProperty(Constant.PATH_GET_PROPERTY);
			
			savePath = (workingDirectory + "\\" + savePath);
		}
		
		return savePath;
	}
}
