//@author A0112498B
package application;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import javafx.util.Pair;
import org.w3c.dom.Document;
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
		_tasks = Main.storage.loadTasksXmlToArrayList();
		_categories = Main.storage.loadCategoriesXmlToArrayList();
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
			_tasks = Main.storage.loadTasksXmlToArrayList();
			_categories = Main.storage.loadCategoriesXmlToArrayList();
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
	// ---------------------------------------------------------
	// Add-related methods
	// ---------------------------------------------------------
	public String addTaskToList(Task task) {	
		ArrayList<Task> backupList = addToArrayListAndBackup(task);		
		String result = writeListToFile(task, backupList);
		
		return result;
	}
		
	public Pair<String, Task> addTaskBackToList(Task task, boolean isUndo) {
		Task removedTask = null;
		if (task.getIsRecurring() && isUndo) {
			Pair<Task, String> deleteDetailsPair = deleteTaskById(task.getId());
			removedTask = deleteDetailsPair.getKey();
		}
			
		String result = addTaskToList(task);
		
		return new Pair<String, Task>(result, removedTask);
	}
	
	// ---------------------------------------------------------
	// Delete-related methods
	// ---------------------------------------------------------
	public Pair<Task, String> deleteTaskFromList(String userInput) {
		String targetId = InputParser.getTargetIdFromString(userInput);
				
		Pair<Task, String> deleteDetailsPair = deleteTaskById(targetId);
		
		return deleteDetailsPair;
	}
	
	public Pair<Task, String> deleteTaskById(String targetId) {
		if (targetId == null) {
			return null;
		}
		
		String taskId = targetId;
		String recurringTaskId = null;
		Task task = null;
		Task removedTask = null;
		int index = 0;		
		boolean isFound = false;
		Iterator<Task> taskIterator = _tasks.iterator();
		
		boolean isRecurringTaskId = targetId.contains(Constant.PREFIX_RECURRING_ID);					
		if (isRecurringTaskId) {			
			recurringTaskId = InputParser.getChildIdFromRecurringId(targetId);			
			taskId = InputParser.getTaskIdFromRecurringId(targetId);
		}
		
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
				writeListToFile(task, backupList);
				
				break;
			}
			
			index++;
		}
		
		return new Pair<Task, String>(removedTask, recurringTaskId);
	}
	
	//@author A0112856E-unused
	/*
	 * We have decided to give up on this feature as there 
	 * were other more pressing bugs to fix and we feel that
	 * we will not have the time to do testing and bug fixing
	 * on this feature.
	 */
	public ArrayList<Task> deleteMultipleTasksFromList(String userInput) {
		ArrayList<Task> removedTasks = new ArrayList<Task>();
		String[] targetIds = InputParser.getTargetIdsFromString(userInput);
		
		for (String targetId : targetIds) {
			Pair<Task, String> deleteDetailsPair = deleteTaskById(targetId);
			Task removedTask = deleteDetailsPair.getKey();
			
			removedTasks.add(removedTask);
		}
		
		return removedTasks;
	}
	
	// ---------------------------------------------------------
	// Update-related methods
	// ---------------------------------------------------------
	//@author A0112498B	
	public Pair<Task, String> updateTaskOnList(String userInput) {
		String targetId = InputParser.getTargetIdFromUpdateString(userInput);
		if (targetId == null || 
			targetId.toLowerCase().
			contains(Constant.PREFIX_RECURRING_ID)) {
			Main.systemFeedback = Constant.MSG_ITEM_NOT_FOUND;
			return null;
		}		
				
		Pair<Task, String> deleteDetailsPair = deleteTaskById(targetId);
		Task originalTask = deleteDetailsPair.getKey();

		String updatedId = null;
		if (originalTask != null) {
			updatedId = addUpdatedTask(userInput, targetId, originalTask);
		}		
		
		
		return new Pair<Task, String>(originalTask, updatedId);
	}
	
	public Task replaceTaskOnList(Task taskToUpdateWith, String targetId) {		
		Pair<Task, String> deleteDetailsPair = deleteTaskById(targetId);
		Task originalTask = deleteDetailsPair.getKey();
		
		if (originalTask != null) {
			addTaskBackToList(taskToUpdateWith, false);
		}
		
		return originalTask;
	}
	
	public void checkAndUpdateStatus() {
		ArrayList<Task> backupList = deepCloneArrayList(_tasks);
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
			
			if (task.getIsRecurring()) {
				updateOverdueRecurringTaskStatus(task);				
			} 

			updateOverdueTaskStatus(task);
		}
		
		_tasks = TaskSorter.getTasksSortedByDate(_tasks);
		String result = Main.storage.writeListToFile(_tasks); 
		
		if (!result.equals(Constant.MSG_ADD_SUCCESS)) {
			_tasks = backupList;
		} 
	}
	
	// ---------------------------------------------------------
	// Search-related methods
	// ---------------------------------------------------------
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
	
	//@author A0112537M
	public Task selectTaskFromList(String userInput) {
		Task selectedTask = null;
		String targetId = InputParser.getTargetIdFromString(userInput);
		
		selectedTask = selectTaskById(targetId);
		_selectedTask = selectedTask;
		
		return selectedTask;
	}
	
	// ---------------------------------------------------------
	// Methods used for rendering view
	// ---------------------------------------------------------				
	/*
	 * Generates a list that consist of Task objects and child task items 
	 * represented as Task objects.
	 */
	public static ArrayList<Task> generateTaskItems(ArrayList<Task> tasks, String displayType) {
		ArrayList<Task> tempTasks = new ArrayList<>();
		Task task = null;
		boolean isRecurring = false;
		String taskType = null;
		
		for(int i = 0; i < tasks.size(); i++) {
			task = tasks.get(i);
			taskType = task.getTaskType().toString();
			isRecurring = task.getIsRecurring();
			
			if(!taskType.equalsIgnoreCase(TaskType.TIMED.toString())) {
				if(isRecurring) {
					generateRecurChildTasks(tempTasks, task, taskType);
				} else {
					tempTasks.add(task);
				}
			} else {
				if(displayType.equalsIgnoreCase(Constant.TAB_NAME_CATEGORY) || 
					displayType.equalsIgnoreCase(Constant.TAB_NAME_PRIORITY)) {
					tempTasks.add(task);
				} else {
					generateRepeatedTimedTask(tempTasks, task);
				}
			}
		}
		
		return tempTasks;
	}
	
	// ---------------------------------------------------------
	// Complete/Uncomplete -related methods
	// ---------------------------------------------------------
	//@author A0112856E
	public Pair<Task, String> completeTaskOnList(String userInput) {		
		String taskId = InputParser.getTargetIdFromString(userInput);
		if (taskId == null) {
			return null;
		}
		
		String parentId = null;
		String targetId = null;		
		if (taskId.contains(Constant.PREFIX_RECURRING_ID)) {
			parentId = InputParser.getTaskIdFromRecurringId(taskId);
			targetId = parentId;
		} else {
			targetId = taskId;
		}
		//-------------------------------------------------------------------------------		
		Task completedTask = null;
		Pair<Task, String> deleteDetailsPair = deleteTaskById(targetId);
		Task originalTask = deleteDetailsPair.getKey();
		String completedTaskId = null;
		
		if (originalTask != null) {
			completedTask = originalTask.deepCloneTask();
			
			if (parentId != null) {
				boolean hasSet = completeRecurringChildTask(taskId, completedTask);
				
				if (hasSet) {
					completedTaskId = parentId;
				}				
			} else {
				updateParentAndChildTaskStatus(completedTask, TaskStatus.COMPLETED);
				completedTaskId = targetId;
			}
			
			addTaskBackToList(completedTask, false);		
			
		} else {
			return null;
		}		
		
		return new Pair<Task, String>(originalTask, completedTaskId);
	}
	
	public Pair<Task, String> uncompleteTaskOnList(String userInput) {
		String taskId = InputParser.getTargetIdFromString(userInput);		
		if (taskId == null) {
			return null;
		}
				
		String parentId = null;
		String targetId = null;		
		if (taskId.contains(Constant.PREFIX_RECURRING_ID)) {
			parentId = InputParser.getTaskIdFromRecurringId(taskId);
			targetId = parentId;
		} else {
			targetId = taskId;
		}
				
		//-------------------------------------------------------------------------------		
		Task uncompletedTask = null;
		Pair<Task, String> deleteDetailsPair = deleteTaskById(targetId);
		Task originalTask = deleteDetailsPair.getKey();
		String completedTaskId = null;
		
		if (originalTask != null) {
			uncompletedTask = originalTask.deepCloneTask();
			
			if (parentId != null) {
				boolean hasSet = uncompleteRecurringChildTask(taskId, uncompletedTask);
				
				if (hasSet) {
					completedTaskId = parentId;
				}				
			} else {
				uncompleteParentAndChildTask(uncompletedTask);
				completedTaskId = targetId;
			}
			
			addTaskBackToList(uncompletedTask, false);		
			
		} else {
			return null;
		}		
		
		return new Pair<Task, String>(originalTask, completedTaskId);
	}
			
	// -----------------------------------------------------------------------------------------------
	// Private Methods
	// -----------------------------------------------------------------------------------------------	
	// ---------------------------------------------------------
	// Add-related methods
	// ---------------------------------------------------------
	//@author A0112498B	
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
	
	// ---------------------------------------------------------
	// Methods used for rendering view
	// ---------------------------------------------------------	
	//@author A0112537M
	private static Task getRecurChildItemForEventOrDated(Task task,
														 Date recurringDate, 
														 String taskType, 
														 String recurringId,
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
	
	private static void generateRepeatedTimedTask(ArrayList<Task> tempTasks,
			  									  Task task) {
		Task newTask = null;
		Calendar originalStart = Calendar.getInstance();
		originalStart.setTime(task.getFrom());
		Calendar start = Calendar.getInstance();
		start.setTime(task.getFrom());
		Calendar end = Calendar.getInstance();
		end.setTime(task.getTo());

		for (Date date = start.getTime(); !start.after(end); 
			 start.add(Calendar.DATE, 1), date = start.getTime()) {

			if(DateParser.hasMatchedDateOnly(start, end)) {
				if(DateParser.hasMatchedDateOnly(originalStart, end)) {
					newTask = Task.createRecurringChildItem(task, task.getId(), task.getStatus(), 
							task.getOn(), task.getBy(), originalStart.getTime());
				} else {
					newTask = Task.createRecurringChildItem(task, task.getId(), task.getStatus(), 
							task.getOn(), task.getBy(), end.getTime());
				}
			} else {
				newTask = Task.createRecurringChildItem(task, task.getId(), task.getStatus(), 
						task.getOn(), task.getBy(), date);
			}

			tempTasks.add(newTask);
		}
	}

	private static void generateRecurChildTasks(ArrayList<Task> tempTasks,
												Task task, String taskType) {
		Date recurringDate;
		String recurringId;
		TaskStatus recurringStatus;
		for(int j = 0; j < task.getRecurringTasks().size(); j++) {
			Task taskA = null;
			
			recurringDate = task.getRecurringTasks().get(j).getRecurDate();
			
			recurringId = task.getId() + Constant.PREFIX_RECURRING_ID + 
					task.getRecurringTasks().get(j).getRecurringTaskId();
			
			recurringStatus = task.getRecurringTasks().get(j).getStatus();		
			
			taskA = getRecurChildItemForEventOrDated(task, recurringDate, 
													 taskType, recurringId,
													 recurringStatus, taskA);
			
			tempTasks.add(taskA);
		}
	}

	/*
	 * Note: different from getTaskById
	 * Selects the task from the newly generated task items
	 * by matching the id
	 */
	private Task selectTaskById(String targetId) {
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
			
	// ---------------------------------------------------------
	// Update-related methods
	// ---------------------------------------------------------	
	//@author A0112498B	
	/*
	 * Update status of all recurring tasks whose date
	 * has past.
	 */
	private void updateOverdueRecurringTaskStatus(Task task) {
		ArrayList<RecurringTask> recurringTasks = task.getRecurringTasks();
		TaskStatus status = null;
		Date date = null;
		
		for (RecurringTask recurringTask : recurringTasks) {
			status = recurringTask.getStatus();
			
			if (status.equals(TaskStatus.COMPLETED) ||
				status.equals(TaskStatus.DELETED) ||
				status.equals(TaskStatus.OVERDUE)) {
					continue;
			}
			
			date = recurringTask.getRecurDate();
			
			if (DateParser.isBeforeNow(date)) {
				recurringTask.setStatus(TaskStatus.OVERDUE);
			}
		}
	}	

	private void updateOverdueTaskStatus(Task task) {
		Date date = task.getEndDate();
		
		if (DateParser.isBeforeNow(date)) {
			task.setStatus(TaskStatus.OVERDUE);
		}
	}
	
	//@author A0112856E
	/*
	 * Cannot be used for uncomplete function because parent
	 * and child tasks may have different status
	 */
	private void updateParentAndChildTaskStatus(Task task, TaskStatus status) {
		if (task.getIsRecurring()) {
			ArrayList<RecurringTask> recurringTasks = 
					task.getRecurringTasks();
			
			for (RecurringTask recurTask : recurringTasks) {
				if (recurTask.getStatus().
					equals(TaskStatus.DELETED)) {
					continue;
				}
				
				recurTask.setStatus(status);
			}
		}
		
		task.setStatus(status);
	}
	
	// ---------------------------------------------------------
	// Complete/Uncomplete -related methods
	// ---------------------------------------------------------		
	private boolean completeRecurringChildTask(String targetId, Task parentTask) {
		boolean hasSet = false;		
		String recurringTaskId = InputParser.getChildIdFromRecurringId(targetId);		
			
		int count = 0;
		String recurTaskId = "-1";
		TaskStatus status = null;
		ArrayList<RecurringTask> recurTasks = parentTask.getRecurringTasks();
		
		if (recurTasks == null) {
			return hasSet;
		}
		
		for (RecurringTask recurTask : recurTasks) {	
			recurTaskId = recurTask.getRecurringTaskId();
			status = recurTask.getStatus();
			
			if (status.equals(TaskStatus.DELETED) ||
				status.equals(TaskStatus.COMPLETED)) {
				count++;
			} else if (recurTaskId.equals(recurringTaskId)){
				recurTask.setStatus(TaskStatus.COMPLETED);	
				hasSet = true;	
				
				count++;
			}
		} 
		
		if (count == recurTasks.size()) {
			parentTask.setStatus(TaskStatus.COMPLETED);
		}
		
		return hasSet;
	}
	
	private boolean uncompleteRecurringChildTask(String targetId, Task parentTask) {
		boolean hasSet = false;		
		String recurringTaskId = InputParser.getChildIdFromRecurringId(targetId);		
			
		int count = 0;
		String recurTaskId = "-1";
		TaskStatus status = null;
		Date recurDate = null;
		TaskStatus newStatus = null;
		ArrayList<RecurringTask> recurTasks = parentTask.getRecurringTasks();
		
		if (recurTasks == null) {
			return hasSet;
		}
		
		for (RecurringTask recurTask : recurTasks) {	
			recurTaskId = recurTask.getRecurringTaskId();
			status = recurTask.getStatus();
			recurDate = recurTask.getRecurDate();
			
			if (recurTaskId.equals(recurringTaskId)){
				newStatus = TaskStatus.getTaskStatus(recurDate);
				
				recurTask.setStatus(newStatus);	
				status = newStatus;
				hasSet = true;	
			}
			
			/*
			 * This condition is placed after the set
			 * which differs from the complete counterpart
			 * so that the task that is to be unmarked is
			 * not counted.
			 */
			if (status.equals(TaskStatus.DELETED) ||
				status.equals(TaskStatus.COMPLETED)) {
				count++;
			} 
		} 
		
		if (count < recurTasks.size()) {
			Date endDate = parentTask.getEndDate();
			
			newStatus = TaskStatus.getTaskStatus(endDate);
			parentTask.setStatus(newStatus);
		}
		
		return hasSet;
	}
	
	private void uncompleteParentAndChildTask(Task task) {
		Date endDate = null;
		
		if (task.getIsRecurring()) {
			ArrayList<RecurringTask> recurringTasks = 
					task.getRecurringTasks();			
			
			for (RecurringTask recurTask : recurringTasks) {
				if (recurTask.getStatus().
					equals(TaskStatus.DELETED)) {
					continue;
				}
				
				endDate = recurTask.getRecurDate();
				recurTask.setStatus(TaskStatus.getTaskStatus(endDate));
			}
		}
		
		endDate = task.getEndDate();
		task.setStatus(TaskStatus.getTaskStatus(endDate));
	}
		
	// ---------------------------------------------------------
	// Misc
	// ---------------------------------------------------------	
	//@author A0112498B	
	private String writeListToFile(Task task, ArrayList<Task> backupList) {
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
}
