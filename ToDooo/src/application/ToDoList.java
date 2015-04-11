//@author A0112498B
package application;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
	private Task _selectedTask;
	
	// -----------------------------------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------------------------------
	public ToDoList() {				
		_listFilePath = Main.storage.readSavePath();		
		
		createListFileIfNotExist();	
		
		_nextId = Main.storage.readNextId();
		_tasks = Main.storage.loadXmlToArrayList();
		_categories = Main.storage.loadCategoriesToArrayList();
	}
	
	/*
	 * Created for unit testing
	 */
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
	
	// -----------------------------------------------------------------------------------------------
	// Get Methods
	// -----------------------------------------------------------------------------------------------
	public String getListFilePath() {
		return _listFilePath;
	}
	
	public ArrayList<Task> getTasks() {
		return _tasks;
	}
	
	public int getNextId() {
		return _nextId;
	}
	
	public ArrayList<String> getCategories() {
		return _categories;
	}
	
	public Task getSelectedTask() {
		return _selectedTask;
	}
	
	
	// -----------------------------------------------------------------------------------------------
	// Set Methods
	// -----------------------------------------------------------------------------------------------
	public void setListFilePath(String listFilePath) {
		_listFilePath = listFilePath;
	}	

	public void setTasks(ArrayList<Task> tasks) {
		_tasks = tasks;
	}

	public void setNextId(int nextId) {
		_nextId = nextId;
	}

	public void setCategories(ArrayList<String> categories) {
		_categories = categories;
	}
	
	public void setSelectedTask(Task selectedTask) {
		_selectedTask = selectedTask;
	}

	
	// -----------------------------------------------------------------------------------------------
	// Public Methods
	// -----------------------------------------------------------------------------------------------
	public String addTaskToList(Task task) {	
		ArrayList<Task> backupList = addToArrayListAndBackup(task);		
		String result = writeToFile(task, backupList);
		
		return result;
	}
		
	public Pair<String, Task> addTaskBackToList(Task task, boolean isUndo) {
		Task removedTask = null;
		if (task.getIsRecurring() && isUndo) {
			removedTask = deleteTaskById(task.getId());	
		}
			
		String result = addTaskToList(task);
		
		return new Pair<String, Task>(result, removedTask);
	}
	
	public Task deleteTaskFromList(String userInput) {
		Task removedTask = null;
		String targetId = InputParser.getTargetIdFromString(userInput);
				
		removedTask = deleteTaskById(targetId);
		
		return removedTask;
	}
	
	//@author A0112856E
	public ArrayList<Task> deleteMultipleTasksFromList(String userInput) {
		ArrayList<Task> removedTasks = new ArrayList<Task>();
		String[] targetIds = InputParser.getTargetIdsFromString(userInput);
		
		for (String targetId : targetIds) {
			Task removedTask = deleteTaskById(targetId);
			removedTasks.add(removedTask);
		}
		
		return removedTasks;
	}
	
	//@author A0112498B
	public Task selectTaskFromList(String userInput) {
		Task selectedTask = null;
		String targetId = InputParser.getTargetIdFromString(userInput);
		
		selectedTask = selectTaskById(targetId);
		_selectedTask = selectedTask;
		
		return selectedTask;
	}
	
	public Pair<ArrayList<Task>, String> searchTheList(String userInput) {
		ArrayList<Task> tasks = ToDoList.generateTaskItems(_tasks, Constant.EMPTY_STRING);
		
		userInput = InputParser.verifyAndCorrectSearchString(userInput);
		
		ArrayList<Task> searchResults = new ArrayList<Task>();
		String systemMsg = null;	
		ArrayList<Pair<SearchAttribute, String>> attributePairs =
				InputParser.getSearchAttributePairFromString(userInput);
				
		if (attributePairs.isEmpty()) {
			systemMsg = Constant.MSG_SEARCH_INVALID;
		} else {
			boolean hasMatched = false;
			for (Task task : tasks) {
				hasMatched = task.hasMatchedAllAttributes(attributePairs);
				
				if (hasMatched) {
					searchResults.add(task);
				}
			}
		}	
		
		Pair<ArrayList<Task>, String> searchResultsPair = new
				Pair<ArrayList<Task>, String>(searchResults, systemMsg);
		
		return searchResultsPair;
	}
	
	public Pair<Task, String> updateTaskOnList(String userInput) {
		String targetId = InputParser.getTargetIdFromUpdateString(userInput);
		if (targetId == null || 
			targetId.toLowerCase().
			contains(Constant.PREFIX_RECURRING_ID)) {
			Main.systemFeedback = Constant.MSG_ITEM_NOT_FOUND;
			return null;
		}		
		
		Task originalTask = deleteTaskById(targetId);

		String updatedId = null;
		if (originalTask != null) {
			updatedId = addUpdatedTask(userInput, targetId, originalTask);
		}		
		
		
		return new Pair<Task, String>(originalTask, updatedId);
	}
	
	public Task replaceTaskOnList(Task taskToUpdateWith, String targetId) {		
		Task originalTask = deleteTaskById(targetId);
		
		if (originalTask != null) {
			addTaskBackToList(taskToUpdateWith, false);
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
			
			recurringTaskId = InputParser.getChildIdFromRecurringId(targetId);			
			taskId = InputParser.getTaskIdFromRecurringId(targetId);
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
				ArrayList<Task> backupList = deepCloneArrayList(_tasks);
				removedTask = backupList.get(index);
				
				if (isRecurringTaskId) {
					task.deleteRecurringTaskById(recurringTaskId);
				} else {
					_tasks.remove(index);
				}
				
				_tasks = TaskSorter.getTasksSortedByDate(_tasks);
				writeToFile(task, backupList);
				
				break;
			}
			
			index++;
		}
		
		return removedTask;
	}
	
	public Task selectTaskById(String targetId) {
		String taskId = targetId;
		Task task = null;
		Task selectedTask = null;
		ArrayList<Task> taskList = generateTaskItems(_tasks, Constant.EMPTY_STRING);
		Iterator<Task> taskIterator = taskList.iterator();
		
		while (taskIterator.hasNext()) {
			task = taskIterator.next();
			
			if(task.getId().equals(taskId)) {
				selectedTask = task;
				break;
			}
		}
		
		return selectedTask;
	}
			
	public static String getSavePathDirectory() {
		String savePath = Main.storage.readSavePath();
		
		if (savePath.equals(Constant.PATH_FILE_NAME)) {
			String workingDirectory = 
					System.getProperty(Constant.PATH_GET_PROPERTY);
			
			savePath = (workingDirectory + "\\" + savePath);
		}
		
		return savePath;
	}
	
	//@author A0112537M
	public static ArrayList<Task> generateTaskItems(ArrayList<Task> tasks, String displayType) {
		ArrayList<Task> tempTasks = new ArrayList<>();
		Task task;
		Date recurringDate;
		boolean isRecurring;
		String taskType;
		String recurringId;
		TaskStatus recurringStatus;
		
		for(int i = 0; i < tasks.size(); i++) {
			task = tasks.get(i);
			taskType = task.getTaskType().toString();
			isRecurring = task.getIsRecurring();
			
			if(!taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
				if(isRecurring) {
					for(int j = 0; j < task.getRecurringTasks().size(); j++) {
						Task taskA = null;
						recurringDate = task.getRecurringTasks().get(j).getRecurDate();
						recurringId = task.getId() + Constant.PREFIX_RECURRING_ID + 
								task.getRecurringTasks().get(j).getRecurringTaskId();
						recurringStatus = task.getRecurringTasks().get(j).getStatus();
						taskA = getRecurChildItemForEventOrDated(task, recurringDate, taskType, recurringId,
								recurringStatus, taskA);
						tempTasks.add(taskA);
					}
				} else {
					Task t2 = new Task();
					t2 = task;
					tempTasks.add(t2);
				}
			} else {
				if(displayType.equalsIgnoreCase(Constant.TAB_NAME_CATEGORY) || 
						displayType.equalsIgnoreCase(Constant.TAB_NAME_PRIORITY)) {
					Task t2 = new Task();
					t2 = task;
					tempTasks.add(t2);
				} else {
					Calendar start = Calendar.getInstance();
					start.setTime(task.getFrom());
					Calendar end = Calendar.getInstance();
					end.setTime(task.getTo());
					
					for (Date date = start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
						Task t3 = Task.createRecurringChildItem(task, task.getId(), task.getStatus(), 
								task.getOn(), task.getBy(), date);
						tempTasks.add(t3);
					}
				}
			}
		}
		
		return tempTasks;
	}
	
	//@author A0112856E
	public Pair<Task, String> completeTaskOnList(String userInput) {
		String targetId = InputParser.getTargetIdFromString(userInput);
		Task completedTask = null;
		
		if (!targetId.contains(Constant.PREFIX_RECURRING_ID)) {
			completedTask = getTaskById(targetId);
			if(completedTask == null) {
				return null;
			}
			completedTask.setStatus(TaskStatus.COMPLETED);
		} else {
			String parentId = InputParser.getTaskIdFromRecurringId(targetId);
			completedTask = updateStatusOfRecurringTaskOnList(targetId, parentId, TaskStatus.COMPLETED);
			targetId = parentId;
		}
		
		Task originalTask = null;
		String completedTaskId = null;
		if (completedTask != null) {
			originalTask = deleteTaskById(targetId);
			
			if (originalTask != null) {
				addTaskBackToList(completedTask, false);
				String taskId = InputParser.getTargetIdFromString(userInput);
				if (taskId.contains(Constant.PREFIX_RECURRING_ID)) {
					completedTaskId = taskId;
				} else {
					completedTaskId = completedTask.getId();
				}
			}
		} else {
			return null;
		}
		
		return new Pair<Task, String>(originalTask, completedTaskId);
	}
		
	public Pair<Task, String> uncompleteTaskOnList(String userInput) {
		String targetId = InputParser.getTargetIdFromString(userInput);
		Task uncompletedTask = null;
		
		if (!targetId.contains(Constant.PREFIX_RECURRING_ID)) {
			uncompletedTask = getTaskById(targetId);
			if(uncompletedTask == null) {
				return null;
			}
			Date endDate = uncompletedTask.getEndDate();
			TaskStatus status = TaskStatus.getTaskStatus(endDate);
			if (status.equals(TaskStatus.OVERDUE)) {
				uncompletedTask.setStatus(TaskStatus.OVERDUE);
			} else {
				uncompletedTask.setStatus(TaskStatus.ONGOING);
			}
			
		} else {
			String parentId = InputParser.getTaskIdFromRecurringId(targetId);
			uncompletedTask = updateStatusOfRecurringTaskOnList(targetId, parentId, TaskStatus.ONGOING);
			targetId = parentId;
		}
		
		Task originalTask = null;
		String uncompletedTaskId = null;
		if (uncompletedTask != null) {
			originalTask = deleteTaskById(targetId);
			
			if (originalTask != null) {
				addTaskBackToList(uncompletedTask, false);
				String taskId = InputParser.getTargetIdFromString(userInput);
				if (taskId.contains(Constant.PREFIX_RECURRING_ID)) {
					uncompletedTaskId = taskId;
				} else {
					uncompletedTaskId = uncompletedTask.getId();
				}
			}
		} else {
			return null;
		}
		
		return new Pair<Task, String>(originalTask, uncompletedTaskId);
	}

	public Task updateStatusOfRecurringTaskOnList(String targetId, String parentId, TaskStatus newStatus) {
		Task task = null;
		boolean isFound = false;
		Iterator<Task> taskIterator = _tasks.iterator();
		ArrayList<RecurringTask> recurTasks = new ArrayList<RecurringTask>();
		
		String recurringTaskId = InputParser.getChildIdFromRecurringId(targetId);		
		
		while (taskIterator.hasNext()) {
			task = taskIterator.next();
			isFound = task.getId().equals(parentId);
			
			if (isFound) {
				recurTasks = task.getRecurringTasks();
				if(recurTasks != null) {
					for (RecurringTask recurTask : recurTasks) {					
						if (recurTask.getRecurringTaskId().equals(recurringTaskId) && 
								!recurTask.getStatus().equals(TaskStatus.DELETED)) {
							recurTask.setStatus(newStatus);
							
							break;
						}
					}
				} else {
					return null;
				}
				
				break;
			}
		}
		
		return task;
	}
	
	public void checkAndUpdateStatus() {
		ArrayList<Task> backupList = deepCloneArrayList(_tasks);
		Date date = null;
		TaskStatus status = null;
		TaskType type = null;
		
		for (Task task : _tasks) {
			type = task.getTaskType();
			status = task.getStatus();
			
			if (type.equals(TaskType.FLOATING) ||
				status.equals(TaskStatus.COMPLETED) ||
				status.equals(TaskStatus.DELETED) ||
				status.equals(TaskStatus.OVERDUE)) {
				continue;
			}
			
			date = task.getEndDate();
			
			if (DateParser.isAfterNow(date)) {
				task.setStatus(TaskStatus.OVERDUE);
			}
		}
		
		_tasks = TaskSorter.getTasksSortedByDate(_tasks);
		String result = Main.storage.writeListToFile(_tasks); 
		
		if (!result.equals(Constant.MSG_ADD_SUCCESS)) {
			_tasks = backupList;
		} 
	}
	
	// -----------------------------------------------------------------------------------------------
	// Private Methods
	// -----------------------------------------------------------------------------------------------
	//@author A0112498B
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
	
	private String writeToFile(Task task, ArrayList<Task> backupList) {
		processTasksID();
		
		String result = Main.storage.writeListToFile(_tasks); 
		
		if (result.equals(Constant.MSG_ADD_SUCCESS)) {
			_nextId = _tasks.size() + 1;
			Main.storage.writeNextIdInFile(_nextId);
			
			addCategoryToList(task.getCategory());
		} else {
			_tasks = backupList;
		}
		return result;
	}
	
	private static Task getRecurChildItemForEventOrDated(Task task,
			Date recurringDate, String taskType, String recurringId,
			TaskStatus recurringStatus, Task taskA) {
		if(taskType.equalsIgnoreCase(TaskType.EVENT.toString())) {
			taskA = Task.createRecurringChildItem(task, recurringId, recurringStatus, 
					recurringDate, task.getBy(), recurringDate);
		} else if(taskType.equalsIgnoreCase(TaskType.DATED.toString())) {
			taskA = Task.createRecurringChildItem(task, recurringId, recurringStatus, 
					task.getOn(), recurringDate, recurringDate);
		}
		return taskA;
	}	

	private ArrayList<Task> addToArrayListAndBackup(Task task) {
		ArrayList<Task> backupList = deepCloneArrayList(_tasks);
		
		_tasks.add(task);
		_tasks = TaskSorter.getTasksSortedByDate(_tasks);
		
		return backupList;
	}
	
	private String addUpdatedTask(String userInput, String targetId,
								  Task originalTask) {
		Task updatedTask = new Task(userInput, targetId, originalTask);

		String updatedId = null;
		if (updatedTask != null) {
			addTaskBackToList(updatedTask, false);
			updatedId = updatedTask.getId();
		}

		return updatedId;
	}
	
	private ArrayList<Task> deepCloneArrayList(ArrayList<Task> tasks) {
		ArrayList<Task> backupList = new ArrayList<Task>();
		Iterator<Task> iterator = tasks.iterator();
		
		while(iterator.hasNext()){
			Task copy = iterator.next().clone();
			if (copy.getIsRecurring()) {
				copy.setRecurringTasks(copy.deepCloneArrayList());
			}
						
			backupList.add(copy);
		}
		
		return backupList;
	}
	
	private void processTasksID() {
		Task task = null;
		
		for (int i = 0; i < _tasks.size(); i++) {
			task = _tasks.get(i);
			task.setId(String.valueOf(i + 1));
		}
	}
}
