package application;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javafx.util.Pair;

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

import test.ToDoListTest;

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
	
	// For testing purpose
	public ToDoList(boolean isTest) {
		String testListPath = ToDoListTest.PATH_TEST_FILE;
		
		if (isTest) {
			_listFilePath = testListPath;
			
			createListFileIfNotExist();	
			
			_nextId = Main.storage.readNextId();
			_tasks = Main.storage.loadXmlToArrayList();
			_categories = Main.storage.loadCategoriesToArrayList();
		}
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
		
		boolean shouldCreate = !(file.exists());
		if (shouldCreate) {
			prepareNewList();
		}
	}
		
	private void prepareNewList() {
		Document document = XmlManager.initDocument();
		
		Main.storage.writeFile(document, _listFilePath);
	}
	
	public String addTaskToList(Task task) {
		String result = Main.storage.writeTaskToFile(task);
		
		if (result.equals(Constant.MSG_ADD_SUCCESS)) {
			_tasks.add(task);
			
			_nextId++;
			Main.storage.writeNextIdInFile(_nextId);
			
			addCategoryToList(task.getCategory());
		}
		
		return result;
	}
	
	public Pair<String, Task> AddTaskBackToList(Task task) {
		Task removedTask = deleteTaskById(task.getId());
		
		String result = Main.storage.writeTaskToFile(task);
		
		if (result.equals(Constant.MSG_ADD_SUCCESS)) {
			_tasks.add(task);
			
			addCategoryToList(task.getCategory());
		}
		
		return new Pair<String, Task>(result, removedTask);
	}
	
	private void addCategoryToList(String category) {
		boolean shouldAdd = true;
		
		for (String taskCategory : _categories) {
			if (taskCategory.equalsIgnoreCase(category)) {
				shouldAdd = false;
				
				break;
			}
		}
		
		if (shouldAdd) {
			boolean hasWritten = 
					Main.storage.hasWrittenCategoryToFile(category);
			
			if (hasWritten) {
				_categories.add(category);
			}
		}
	}

	public Task deleteTaskFromList(String userInput) {
		Task removedTask = null;
		String targetId = InputParser.getTargetIdFromString(userInput);
		
		removedTask = deleteTaskById(targetId);
		
		return removedTask;
	}
	
	// need to check recurring tasks as well
	public ArrayList<Task> searchTheList(String userInput) {
		userInput = InputParser.verifyAndCorrectSearchString(userInput);
		
		ArrayList<Task> searchResults = new ArrayList<Task>();
		ArrayList<Pair<SearchAttribute, String>> attributePairs =
				InputParser.getSearchAttributePairFromString(userInput);
		
		boolean hasMatched = false;
		for (Task task : _tasks) {
			hasMatched = hasMatchedAllAttributes(attributePairs, task);
			
			if (hasMatched) {
				searchResults.add(task);
			}
		}
		
		return searchResults;
	}
	
	private boolean hasMatchedAllAttributes(ArrayList<Pair<SearchAttribute, String>>
											attributePairs, Task task) {
		boolean hasMatched = false;
		SearchAttribute attribute = null;
		String searchKey = null;
		String taskDetailString = null;
		int matched = 0;
		int expectedMatched = attributePairs.size();
		
		for (Pair<SearchAttribute, String> attributePair : attributePairs) {
			attribute = attributePair.getKey();
			searchKey = attributePair.getValue().trim();
			
			switch (attribute) {
				case ID :
					taskDetailString = task.getId().toLowerCase();					
					break;
				case DESCRIPTION :
					taskDetailString = task.getToDo().toLowerCase();	
					break;
				case CATEGORY :
					taskDetailString = task.getCategory().toLowerCase();	
					break;
				case PRIORITY :
					taskDetailString = task.getPriority().
									   toString().toLowerCase();	
				case DATE :
					if (hasDateMatch(task, attribute, searchKey)) {
						matched++;
						continue;
					}
					break;
			}
			
			if (taskDetailString.contains(searchKey)) {
				matched++;
			} 
		}		
		
		if (matched == expectedMatched) {
			hasMatched = true;
		} 
		
		return hasMatched;
	}
	
	private boolean hasDateMatch(Task task, SearchAttribute attribute, String searchKey) {
		boolean hasMatched = false;
		Date date = null;
		Date dateKey = Main.inputParser.getDateFromString(searchKey);
		
		for (Command dateCommand : Constant.COMMAND_DATES) {
			switch (dateCommand) {
				case FROM :
					date = task.getFrom();
					break;
				case TO :
					date = task.getTo();
					break;
				case ON :
					date = task.getOn();
					break;
				case BY :
					date = task.getBy();
					break;
				default :
					date = null;
					break;
			}
			
			if (date != null && dateKey.equals(date)) {
				hasMatched = true;
				
				return hasMatched;
			} 
		}
		
		return hasMatched;
	}
	
	public Pair<Task, String> updateTaskOnList(String userInput) {
		String targetId = InputParser.getTargetIdFromUpdateString(userInput);
		Task updatedTask = new Task(userInput, targetId);
		
		if (targetId == null || 
			targetId.toLowerCase().
			contains(Constant.PREFIX_RECURRING_ID)) {
			Main.systemFeedback = Constant.MSG_ITEM_NOT_FOUND;
			return null;
		}
		
		Task originalTask = deleteTaskById(targetId);
		
		if (originalTask != null) {
			AddTaskBackToList(updatedTask);
		}		
		
		String updatedId = updatedTask.getId();
		return new Pair<Task, String>(originalTask, updatedId);
	}
	
	public Task replaceTaskOnList(Task taskToUpdateWith, String targetId) {		
		Task originalTask = deleteTaskById(targetId);
		
		if (originalTask != null) {
			AddTaskBackToList(taskToUpdateWith);
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
	
	public Task deleteTaskById(String targetId) {
		String taskId = targetId;
		String recurringTaskId = null;
		
		boolean isRecurringTaskId = 
				targetId.contains(Constant.PREFIX_RECURRING_ID);
			
		
		if (isRecurringTaskId) {
			recurringTaskId = targetId;
			
			int endIndex = recurringTaskId.
						   indexOf(Constant.PREFIX_RECURRING_ID);
			taskId = recurringTaskId.substring(0, endIndex);
		}
		
		Task task = null;
		Task removedTask = null;
		int index = 0;		
		boolean isFound = false;
		Iterator<Task> taskIterator = _tasks.iterator();
		
		while (taskIterator.hasNext()) {
			task = taskIterator.next();
			
			isFound = (task.getId().equals(taskId));
			
			if (isFound) {
				if (isRecurringTaskId) {
					removedTask = deleteRecurringTaskById(task, recurringTaskId);
				} else {
					removedTask = Main.storage.deleteTaskFromFileById(targetId);
					
					if (removedTask != null) {
						_tasks.remove(index);
					}
				}
				
				break;
			}
			
			index++;
		}
		
		return removedTask;
	}
	
	private Task deleteRecurringTaskById(Task task, String recurringTaskId) {
		RecurringTask recurringTask = null;
		Task removedTask = null;
		int index = 0;		
		boolean isFound = false;
		Iterator<RecurringTask> taskIterator = 
				task.getRecurringTasks().iterator();
		
		while (taskIterator.hasNext()) {
			recurringTask = taskIterator.next();
			
			isFound = (recurringTask.getRecurringTaskId().
					   equals(recurringTaskId));
			
			if (isFound) {
				removedTask = Main.storage.
							  deleteRecurringTaskFromFileById(task.getId(), 
							  recurringTaskId);
				
				if (removedTask != null) {
					task.getRecurringTasks().remove(index);
				}
				
				break;
			}
			
			index++;
		}
		
		return removedTask;
	}
	
	public String getSavePathDirectory() {
		String savePath = Main.storage.readSavePath();
		
		if (savePath.equals(Constant.PATH_FILE_NAME)) {
			String workingDirectory = 
					System.getProperty(Constant.PATH_GET_PROPERTY);
			
			savePath = (workingDirectory + "\\" + savePath);
		}
		
		return savePath;
	}
}
