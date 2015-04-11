//@author A0112498B
package application;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javafx.util.Pair;

public class Task implements Cloneable {
	private String _id;
	private TaskType _taskType;
	private String _toDo;
	private String _originalText;
	private Date _on;
	private Date _from;
	private Date _to;
	private Date _by;
	private String _category;
	private boolean _isRecurring;
	private Frequency _repeat;
	private int _repeatDay;
	private Date _repeatUntil;
	private Priority _priority;
	private boolean _isValid;
	private TaskStatus _status;
	private Date _startDate;
	private Date _endDate;
	private ArrayList<RecurringTask> _recurringTasks;
	
	// -----------------------------------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------------------------------		
	public Task() {		
		// Attributes are to be occupied by calling set
	}
	
	public Task(String userInput) {
		this(userInput, null);
	}
	
	public Task(String userInput, String id) {	
		this(userInput, id, null);
	}
	
	public Task (String userInput, String id, Task originalTask) {
		_isValid = false;
		_taskType = InputParser.getTaskTypeFromString(userInput);	
		
		if (id == null) {
			_id = String.valueOf(Main.list.getNextId());	
		} else {
			_id = id;	
		}
		
		_originalText = InputParser.removeActionFromString(userInput, _id);
		
		boolean hasValidDateCount = Command.hasValidNumOfDateCommands(userInput);
		if (hasValidDateCount) {
			List<Date> dates = getDates(userInput);
			
			boolean isInvalidFormat = (_taskType.equals(TaskType.TIMED) && 
									   dates.size() < Constant.MAX_NUM_OF_DATES) ||
									  (!_taskType.equals(TaskType.FLOATING) && 
									   dates == null);
			
			if (isInvalidFormat) {
				Main.systemFeedback = Constant.MSG_INVALID_FORMAT;
			} else {
				setTaskContentAttributes(userInput, originalTask, dates);
			}
		} else {
			boolean isInvalidRecurring = 
					Command.verifyRecurringCommands(userInput) != null;
			
			if (isInvalidRecurring) {
				Main.systemFeedback = Constant.MSG_INVALID_RECURRING;
			} else {
				Main.systemFeedback = Constant.MSG_INVALID_FORMAT;
			}			
		}
	}
	
	// -----------------------------------------------------------------------------------------------
	// Get methods
	// -----------------------------------------------------------------------------------------------	
	public String getId() {
		return _id;
	}

	public TaskType getTaskType() {
		return _taskType;
	}
	
	public String getToDo() {
		return _toDo;
	}
	
	public String getOriginalText() {
		return _originalText;
	}

	public Date getOn() {
		return _on;
	}

	public Date getFrom() {
		return _from;
	}

	public Date getTo() {
		return _to;
	}

	public Date getBy() {
		return _by;
	}

	public String getCategory() {
		return _category;
	}
	
	public boolean getIsRecurring() {
		return _isRecurring;
	}

	public Frequency getRepeat() {
		return _repeat;
	}

	public Priority getPriority() {
		return _priority;
	}
	
	public boolean getIsValid() {
		return _isValid;
	}

	public int getRepeatDay() {
		return _repeatDay;
	}

	public Date getRepeatUntil() {
		return _repeatUntil;
	}

	public Date getEndDate() {
		return _endDate;
	}

	public TaskStatus getStatus() {
		return _status;
	}

	public ArrayList<RecurringTask> getRecurringTasks() {
		return _recurringTasks;
	}

	public Date getStartDate() {
		return _startDate;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Set methods
	// -----------------------------------------------------------------------------------------------
	public void setId(String id) {
		_id = id;
	}
	
	public void setTaskType(TaskType _taskType) {
		this._taskType = _taskType;
	}

	public void setToDo(String toDo) {
		_toDo = toDo;
	}

	public void setOriginalText(String originalText) {
		_originalText = originalText;
	}

	public void setOn(Date on) {
		_on = on;
	}

	public void setFrom(Date from) {
		_from = from;
	}

	public void setTo(Date to) {
		_to = to;
	}

	public void setBy(Date by) {
		_by = by;
	}

	public void setCategory(String category) {
		_category = category;
	}

	public void setIsRecurring(boolean isRecurring) {
		_isRecurring = isRecurring;
	}

	public void setRepeat(Frequency repeat) {
		_repeat = repeat;
	}

	public void setPriority(Priority priority) {
		_priority = priority;
	}
	
	public void setIsValid(boolean isValid) {
		_isValid = isValid;
	}
	
	public void setRepeatDay(int repeatDay) {
		_repeatDay = repeatDay;
	}

	public void setRepeatUntil(Date repeatUntil) {
		_repeatUntil = repeatUntil;
	}
	
	public void setEndDate(Date endDate) {
		_endDate = endDate;
	}

	public void setStatus(TaskStatus status) {
		_status = status;
	}

	public void setRecurringTasks(ArrayList<RecurringTask> recurringTask) {
		_recurringTasks = recurringTask;
	}
	
	public void setStartDate(Date startDate) {
		_startDate = startDate;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Public Methods
	// -----------------------------------------------------------------------------------------------
	@Override
	public Task clone() {
		Task clone = null;
		
		try {
			clone = (Task) super.clone();
		} catch(CloneNotSupportedException e){
            throw new RuntimeException(e); 
        }
		
		 return clone;
	}
	
	public Task deepCloneTask() {
		Task copy = this.clone();
		
		if (copy.getIsRecurring()) {
			copy.setRecurringTasks(copy.deepCloneArrayList());
		}
		
		return copy;
	}
		
	public ArrayList<RecurringTask> deepCloneArrayList() {
		ArrayList<RecurringTask> copies = new ArrayList<RecurringTask>();
		Iterator<RecurringTask> iterator = _recurringTasks.iterator();
		
		while(iterator.hasNext()){
			copies.add(iterator.next().clone());
		}
		
		return copies;
	}
	
	//@author A0112537M
	// Creating a repeated task with a different date for recurring task
	public static Task createRecurringChildItem(Task originalTask, String recurringId, 
												TaskStatus recurringStatus, Date onDate, 
												Date byDate, Date startDate) {
		Task task = new Task();
		
		task.setBy(byDate);
		task.setCategory(originalTask.getCategory());
		task.setEndDate(originalTask.getEndDate());
		task.setFrom(originalTask.getFrom());
		task.setId(recurringId);
		task.setIsRecurring(originalTask.getIsRecurring());
		task.setIsValid(originalTask.getIsValid());
		task.setOn(onDate);
		task.setOriginalText(originalTask.getOriginalText());
		task.setPriority(originalTask.getPriority());
		task.setRecurringTasks(originalTask.getRecurringTasks());
		task.setRepeat(originalTask.getRepeat());
		task.setRepeatDay(originalTask.getRepeatDay());
		task.setRepeatUntil(originalTask.getRepeatUntil());
		task.setStartDate(startDate);
		task.setStatus(recurringStatus);
		task.setTaskType(originalTask.getTaskType());
		task.setTo(originalTask.getTo());
		task.setToDo(originalTask.getToDo());
		
		return task;
	}
	
	//@author A0112498B
	public void deleteRecurringTaskById(String recurringTaskId) {
		RecurringTask recurringTask = null;
		int index = 0;		
		boolean isFound = false;
		Iterator<RecurringTask> taskIterator = _recurringTasks.iterator();
		
		while (taskIterator.hasNext()) {
			recurringTask = taskIterator.next();
			
			isFound = (recurringTask.getRecurringTaskId().
					   equals(recurringTaskId));
			
			if (isFound) {
				/*
				 * Setting the status to Status.DELETED instead of deleting
				 * the task from the xml in order for the update function
				 * to work correctly when updating recurring tasks
				 */
				recurringTask.setStatus(TaskStatus.DELETED);
				break;
			}
			
			index++;
		}
	}
	
	/*
	 * return true when the start date of the calling task matches
	 * with the search key
	 */
	public boolean hasDateMatch(SearchAttribute attribute, String searchKey) {
		boolean hasMatched = false;
		Date dateKey = Main.inputParser.getSearchDateFromString(searchKey);
		
		if (dateKey == null || _startDate == null) {
			return false;
		} else if (DateParser.hasMatchedDateOnly(dateKey, _startDate)){
			hasMatched = true;
		}
				
		return hasMatched;
	}
	
	public boolean hasMatchedAllAttributes(ArrayList<Pair<SearchAttribute, String>> attributePairs) {
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
			case ID:
				taskDetailString = _id.toLowerCase();
				break;
			case DESCRIPTION:
				taskDetailString = _toDo.toLowerCase();
				break;
			case CATEGORY:
				taskDetailString = _category.toLowerCase();
				break;
			case PRIORITY:
				taskDetailString = _priority.toString().toLowerCase();
				break;
			case DATE:
				if (hasDateMatch(attribute, searchKey)) {
					matched++;

				}
				continue;
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
	
	/*
	 * Determine the date for start and end date based
	 * on the task type
	 */
	public static void setStartEndDate(Task task, Date date, String text) {
		if (!text.equals(Constant.XML_TEXT_NIL)) {
			task.setStartDate(date);
			
			if (task.getTaskType().equals(TaskType.TIMED)) {
				task.setEndDate(task.getTo());
			} else {
				task.setEndDate(date);
			}			
		}
	}
	
	// -----------------------------------------------------------------------------------------------
	// Private Methods
	// -----------------------------------------------------------------------------------------------
	private String generateToDoString(String userInput) {
		String toDoString = userInput;
		
		toDoString = InputParser.removeCategoryFromString(toDoString, _category);		
		toDoString = InputParser.removePriorityFromString(toDoString, _priority);		
		toDoString = InputParser.removeRecurringFromString(toDoString, _isRecurring, _repeat);
		toDoString = InputParser.extractDescriptionFromString(toDoString, _taskType, _id);	
		
		return toDoString.trim(); 
	}

	private List<Date> getDates(String userInput) {
		String toBeRemoved = InputParser.extractDescriptionFromString(userInput, _taskType, _id);
		String detailsString = userInput.replace(toBeRemoved, "");
		
		List<Date> dates = Main.inputParser.getDatesFromString(detailsString);
		return dates;
	}
	
	private void setDatesForTaskType(List<Date> dates) {
		_on = null;
		_from = null;
		_to = null;
		_by = null;
		_endDate = null;
		
		switch(_taskType) {
			case EVENT :
				_on = dates.get(0);
				
				_startDate = _on;
				_endDate = _on;
				break;
			case TIMED :				
				_from = dates.get(0);
				_to = dates.get(1);
				
				_startDate= _from;
				_endDate = _to;
				break;
			case DATED :
				_by = dates.get(0);
				
				_startDate = _by;
				_endDate = _by;
				break;
			default :
				// floating task
				// does not require action since no date in array
				break;
		}
	}
	
	private void setTaskContentAttributes(String userInput, Task originalTask,
			  							  List<Date> dates) {
		setDatesForTaskType(dates);
		_category = InputParser.getCategoryFromString(userInput);

		boolean isValidOperation = processRecurring(dates, userInput,
								   originalTask);
		if (isValidOperation) {
			_priority = InputParser.getPriorityFromString(userInput);
			_toDo = generateToDoString(userInput);
			_status = TaskStatus.getTaskStatus(_endDate);

			_isValid = true;
		}
	}
		
	// ---------------------------------------------------------
	// Recurring Tasks-related details tags
	// ---------------------------------------------------------
	private boolean processRecurring(List<Date> dates, String userInput, Task originalTask) {
		_recurringTasks = new ArrayList<RecurringTask>();
		_isRecurring = false;
		_repeatUntil = null;
		_repeat = Frequency.NIL;
		_repeatDay = -1;
		
		boolean isValid = true;
		Command recurringCommand = Command.verifyRecurringCommands(userInput);
		if (RecurringTask.isValidRecurringTaskType(_taskType)) {
			Date untilDate = InputParser.getUntilDateFromString(userInput);
			
			if (untilDate != null && DateParser.isBeforeDate(untilDate, _endDate)) { 
				// user attempted to create a recurring task with invalid until date
				isValid = false;
				Main.systemFeedback = Constant.MSG_INVALID_UNTIL_DATE;				
				
			} if (recurringCommand == null) { 				
				isValid = verifyUserInputForRecurrence(userInput, isValid,
						  untilDate);				
				
			} else {
				if (dates != null) {
					isValid = true;	
					setRecurringDetails(dates, originalTask, recurringCommand, untilDate);									
				} else {
					isValid = false;
					Main.systemFeedback = Constant.MSG_INVALID_RECURRING;
				}
			}
		} else if (recurringCommand != null) {
			isValid = false;
			Main.systemFeedback = Constant.MSG_INVALID_RECURRING;
		}
				
		return isValid;
	}

	/*
	 * returns true if the user input is a valid operation
	 * returns false if it is a recurrence type command but has no until date
	 */
	private boolean verifyUserInputForRecurrence(String userInput,
												 boolean isValid, Date untilDate) {
		boolean hasRecurringCommands = Command.hasRecurringCommands(userInput);
		
		if (hasRecurringCommands && untilDate == null) {
			isValid = false;
			Main.systemFeedback = Constant.MSG_NO_UNTIL_DATE;			
		} else if (!hasRecurringCommands && untilDate == null) { 
			isValid = true; 
		}
		return isValid;
	}

	private void setRecurringDetails(List<Date> dates, Task originalTask,
									 Command recurringCommand, Date untilDate) {
		_isRecurring = true;
		_repeatUntil = untilDate;
		_repeat = Frequency.getFrequency(recurringCommand);	
		
		Date startDate = dates.get(0);
		_repeatDay = DateParser.calculateDayOfWeek(startDate);
		
		generateRecurringTasks(startDate, originalTask);
	}		

	private void generateRecurringTasks(Date startDate, Task originalTask) {
		Calendar calendarStart = DateParser.createCalendar(startDate);
		Calendar calendarEnd = DateParser.createCalendar(_repeatUntil);
		int startDateDayOfMonth = DateParser.calculateDayOfMonth(calendarStart);
		int startDateMonth = DateParser.calculateMonth(calendarStart);
		
		ArrayList<RecurringTask> recurringTasks = null;
		if (originalTask != null) {
			recurringTasks = originalTask.getRecurringTasks();
		}
				
		while (calendarStart.before(calendarEnd) || calendarStart.equals(calendarEnd)) {
			switch (_repeat) {
				case WEEKLY :
					if (hasAddedWeeklyRecurringTask(calendarStart, recurringTasks)) {
						continue;
					}
					break;
				case MONTHLY :
					if (hasAddedMonthlyRecurringTask(calendarStart, 
													 startDateDayOfMonth, 
													 recurringTasks)) {
						continue;
					}
					break;
				case YEARLY :
					if (hasAddedYearlyRecurringTask(calendarStart, 
													startDateDayOfMonth, 
													startDateMonth, 
													recurringTasks)) {
						continue;
					}
					break;					
				default :
					// invalid task object
					return;
			}
			
			calendarStart.add(Calendar.DAY_OF_MONTH, 1);
		}
	}

	private boolean hasAddedYearlyRecurringTask(Calendar calendarStart, 
												int startDateDayOfMonth, 
												int startDateMonth,
												ArrayList<RecurringTask> recurringTasks) {
		boolean hasAdded = false;
		int dayOfMonth;
		int month;
		dayOfMonth = DateParser.calculateDayOfMonth(calendarStart);	
		month = DateParser.calculateMonth(calendarStart);
		
		if (dayOfMonth == startDateDayOfMonth &&
			month == startDateMonth) {
			addToRecurringTasks(calendarStart, recurringTasks);
			
			calendarStart.add(Calendar.YEAR, 1);
			hasAdded = true;
		}
		
		return hasAdded;
	}

	private boolean hasAddedMonthlyRecurringTask(Calendar calendarStart, int startDateDayOfMonth,
												 ArrayList<RecurringTask> recurringTasks) {
		boolean hasAdded = false;
		int dayOfMonth = DateParser.calculateDayOfMonth(calendarStart);	
		
		if (dayOfMonth == startDateDayOfMonth) {
			addToRecurringTasks(calendarStart, recurringTasks);
			
			calendarStart.add(Calendar.MONTH, 1);
			hasAdded = true;
		}
		
		return hasAdded;
	}

	private boolean hasAddedWeeklyRecurringTask(Calendar calendarStart, 
												ArrayList<RecurringTask> recurringTasks) {
		boolean hasAdded = false;
		int dayOfWeek = DateParser.calculateDayOfWeek(calendarStart);
		
		if (dayOfWeek == _repeatDay) {
			addToRecurringTasks(calendarStart, recurringTasks);
			
			calendarStart.add(Calendar.WEEK_OF_YEAR, 1);
			hasAdded = true;
		}
		
		return hasAdded;
	}
	
	private void addToRecurringTasks(Calendar calendarStart, 
									 ArrayList<RecurringTask> recurringTasks) {
		String recurringTaskId = generateRecurringTaskId();
		RecurringTask recurringTask = 
				new RecurringTask(recurringTaskId, calendarStart);
		
		if (recurringTasks != null) {
			useOriginalRecurTaskStatus(recurringTasks, recurringTask);
		}
		
		_recurringTasks.add(recurringTask);
	}

	/*
	 * If the date and time of the new recurring task is a match
	 * to the original entry, take the original status.
	 */
	private void useOriginalRecurTaskStatus(ArrayList<RecurringTask> recurringTasks, 
											RecurringTask recurringTask) {
		boolean hasMatched = false;
		
		Date thisRecurDate = recurringTask.getRecurDate();
		Date thatRecurDate = null;
		
		for (RecurringTask recTask : recurringTasks) {
			thatRecurDate = recTask.getRecurDate();
			hasMatched = DateParser.hasMatchedDateTime(thisRecurDate, thatRecurDate);
			
			if (hasMatched) {
				recurringTask.setStatus(recTask.getStatus());
				
				break;
			}
		}
	}
	
	private String generateRecurringTaskId() {
		return String.valueOf(_recurringTasks.size() + 1);
	}
}
