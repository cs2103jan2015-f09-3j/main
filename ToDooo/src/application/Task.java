package application;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javafx.util.Pair;

public class Task {
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
	private Status _status;
	private Date _startDate;
	private Date _endDate;
	private ArrayList<RecurringTask> _recurringTasks;
	
	public Task() {
		// convert from node
	}
	
	public Task(String userInput) {
		this(userInput, null);
	}

	public Task(String userInput, String id) {	
		_taskType = InputParser.getTaskTypeFromString(userInput);	
		
		if (id == null) {
			_id = generateId(_taskType);	
		} else {
			_id = id;	
		}
		
		_originalText = InputParser.removeActionFromString(userInput, _id);
		
		_isValid = Command.isValidNumOfDateCommands(userInput);
		if (_isValid) {
			List<Date> dates = getDates(userInput);
			
			if ((_taskType.equals(TaskType.TIMED) && dates.size() < 2) ||
				(!_taskType.equals(TaskType.FLOATING) && dates == null)) {
				Main.systemFeedback = Constant.MSG_INVALID_FORMAT;
				_isValid = false;
			} else {
				setDatesForTaskType(dates);			
				_category = InputParser.getCategoryFromString(userInput);
				
				_isValid = setRecurringDetails(dates, userInput);
				
				if (_isValid) {
					_priority = InputParser.getPriorityFromString(userInput);		
					_toDo = generateToDoString(userInput);
					_status = Task.getStatus(_endDate);
					
					updateIdWithTaskType();
					_isValid = true;
				}
			}
		} else {
			if (Command.verifyRecurringCommands(userInput) != null) {
				Main.systemFeedback = Constant.MSG_INVALID_RECURRING;
			} else {
				Main.systemFeedback = Constant.MSG_INVALID_FORMAT;
			}			
		}
	}

	
	public String getId() {
		return _id;
	}

	public void setId(String id) {
		_id = id;
	}
	
	public TaskType getTaskType() {
		return _taskType;
	}

	public void setTaskType(TaskType _taskType) {
		this._taskType = _taskType;
	}
	
	public String getToDo() {
		return _toDo;
	}

	public void setToDo(String toDo) {
		_toDo = toDo;
	}

	public String getOriginalText() {
		return _originalText;
	}

	public void setOriginalText(String originalText) {
		_originalText = originalText;
	}

	public Date getOn() {
		return _on;
	}

	public void setOn(Date on) {
		_on = on;
	}

	public Date getFrom() {
		return _from;
	}

	public void setFrom(Date from) {
		_from = from;
	}

	public Date getTo() {
		return _to;
	}

	public void setTo(Date to) {
		_to = to;
	}

	public Date getBy() {
		return _by;
	}

	public void setBy(Date by) {
		_by = by;
	}

	public String getCategory() {
		return _category;
	}

	public void setCategory(String category) {
		_category = category;
	}

	public boolean getIsRecurring() {
		return _isRecurring;
	}

	public void setIsRecurring(boolean isRecurring) {
		_isRecurring = isRecurring;
	}

	public Frequency getRepeat() {
		return _repeat;
	}

	public void setRepeat(Frequency repeat) {
		_repeat = repeat;
	}

	public Priority getPriority() {
		return _priority;
	}

	public void setPriority(Priority priority) {
		_priority = priority;
	}
	
	public boolean getIsValid() {
		return _isValid;
	}

	public void setIsValid(boolean isValid) {
		_isValid = isValid;
	}
	
	public int getRepeatDay() {
		return _repeatDay;
	}

	public void setRepeatDay(int repeatDay) {
		_repeatDay = repeatDay;
	}

	public Date getRepeatUntil() {
		return _repeatUntil;
	}

	public void setRepeatUntil(Date repeatUntil) {
		_repeatUntil = repeatUntil;
	}
	
	public Date getEndDate() {
		return _endDate;
	}

	public void setEndDate(Date endDate) {
		_endDate = endDate;
	}

	public Status getStatus() {
		return _status;
	}

	public void setStatus(Status status) {
		_status = status;
	}

	public ArrayList<RecurringTask> getRecurringTasks() {
		return _recurringTasks;
	}

	public void setRecurringTasks(ArrayList<RecurringTask> recurringTask) {
		_recurringTasks = recurringTask;
	}
	
	public Date getStartDate() {
		return _startDate;
	}

	public void setStartDate(Date startDate) {
		_startDate = startDate;
	}
	
	public static Task createRecurringChildItem(Task originalTask, String recurringId, Status recurringStatus, 
			Date onDate, Date byDate, Date startDate) {
		Task t = new Task();
		
		t.setBy(byDate);
		t.setCategory(originalTask.getCategory());
		t.setEndDate(originalTask.getEndDate());
		t.setFrom(originalTask.getFrom());
		t.setId(recurringId);
		t.setIsRecurring(originalTask.getIsRecurring());
		t.setIsValid(originalTask.getIsValid());
		t.setOn(onDate);
		t.setOriginalText(originalTask.getOriginalText());
		t.setPriority(originalTask.getPriority());
		t.setRecurringTasks(originalTask.getRecurringTasks());
		t.setRepeat(originalTask.getRepeat());
		t.setRepeatDay(originalTask.getRepeatDay());
		t.setRepeatUntil(originalTask.getRepeatUntil());
		t.setStartDate(startDate);
		t.setStatus(recurringStatus);
		t.setTaskType(originalTask.getTaskType());
		t.setTo(originalTask.getTo());
		t.setToDo(originalTask.getToDo());
		
		return t;
	}

	private static String generateId(TaskType taskType) {
		int nextId = Main.list.getNextId();
		String id = null;
		
		switch (taskType) {
			case EVENT :
				id = "E" + nextId;
				break;
			case TIMED :
				id = "T" + nextId;
				break;
			case DATED :
				id = "D" + nextId;
				break;
			case FLOATING :
				id = "F" + nextId;
				break;
			default :
				// invalid task
				// return null
				break;
		}
		
		return id;
	}
	
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
		
	private boolean setRecurringDetails(List<Date> dates, String userInput) {
		_recurringTasks = new ArrayList<RecurringTask>();
		_isRecurring = false;
		_repeatUntil = null;
		_repeat = Frequency.NIL;
		_repeatDay = -1;
		
		boolean isValid = true;
		Command recurringCommand = Command.verifyRecurringCommands(userInput);
		if (RecurringTask.isValidRecurringTaskType(_taskType)) {
			Date untilDate = InputParser.getUntilDateFromString(userInput);
			
			if (recurringCommand == null) {
				boolean hasRecurringCommands = Command.hasRecurringCommands(userInput);
				if (hasRecurringCommands && untilDate == null) {
					isValid = false;
					Main.systemFeedback = Constant.MSG_NO_UNTIL_DATE;						
				} else if (!hasRecurringCommands && untilDate == null) {
					isValid = true;
				}
			} else {
				if (dates != null) {
					_isRecurring = true;
					_repeatUntil = untilDate;
					_repeat = Frequency.getFrequency(recurringCommand);	
					
					Date startDate = dates.get(0);
					_repeatDay = DateParser.calculateDayOfWeek(startDate);
					
					generateRecurringTasks(startDate);	
					isValid = true;				
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
	
	private void updateIdWithTaskType() {
		if (_id == null) {
			return;
		}
		
		char typePrefix = _taskType.toString()
				 	  	  .charAt(0);
		
		char prefix = _id.charAt(0);
		
		if (typePrefix != prefix) {
			_id = _id.replace(prefix, typePrefix);
			
			if (_isRecurring) {
				updateRecurringTasksIdWithTaskType(prefix, typePrefix);
			}
		}
	}
	
	private void updateRecurringTasksIdWithTaskType(char prefix, char typePrefix) {
		String newRecurringTaskId = null;
		for (RecurringTask recurringTask : _recurringTasks) {
			newRecurringTaskId = recurringTask.getRecurringTaskId().
								 replace(prefix, typePrefix);
			
			recurringTask.setRecurringTaskId(newRecurringTaskId);
		}
	}
	
	public static Status getStatus(Date endDate) {
		Status status = Status.OVERDUE;
		
		
		if (endDate == null) { // floating task
			return Status.ONGOING;
		} else {
			Date todayDate = new Date();
			boolean isOngoing = DateParser.isBeforeDate(todayDate, endDate);
			
			if (isOngoing) {
				status = Status.ONGOING;
			}
		}
		
		return status;
	}
	
	private void generateRecurringTasks(Date startDate) {
		Calendar calendarStart = DateParser.createCalendar(startDate);
		Calendar calendarEnd = DateParser.createCalendar(_repeatUntil);
		int dayOfWeek = -1;
		int dayOfMonth = -1;
		int month = -1;
		int startDateDayOfMonth = DateParser.calculateDayOfMonth(calendarStart);
		int startDateMonth = DateParser.calculateMonth(calendarStart);
		
		
		while (calendarStart.before(calendarEnd) || calendarStart.equals(calendarEnd)) {
			switch (_repeat) {
				case WEEKLY :
					dayOfWeek = DateParser.calculateDayOfWeek(calendarStart);
					
					if (dayOfWeek == _repeatDay) {
						addToRecurringTasks(calendarStart);
						
						calendarStart.add(Calendar.WEEK_OF_YEAR, 1);
						continue;
					}					
					break;
				case MONTHLY :
					dayOfMonth = DateParser.calculateDayOfMonth(calendarStart);	
					
					if (dayOfMonth == startDateDayOfMonth) {
						addToRecurringTasks(calendarStart);
						
						calendarStart.add(Calendar.MONTH, 1);
						continue;
					}	
					break;
				case YEARLY :
					dayOfMonth = DateParser.calculateDayOfMonth(calendarStart);	
					month = DateParser.calculateMonth(calendarStart);
					
					if (dayOfMonth == startDateDayOfMonth &&
						month == startDateMonth) {
						addToRecurringTasks(calendarStart);
						
						calendarStart.add(Calendar.YEAR, 1);
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

	private void addToRecurringTasks(Calendar calendarStart) {
		String recurringTaskId = generateRecurringTaskId();
		RecurringTask recurringTask = 
				new RecurringTask(recurringTaskId, calendarStart);
		
		_recurringTasks.add(recurringTask);
	}
	
	private String generateRecurringTaskId() {
		return _id + Constant.PREFIX_RECURRING_ID + 
			   _recurringTasks.size();
	}
	
	public Task deleteRecurringTaskById(String recurringTaskId) {
		RecurringTask recurringTask = null;
		Task removedTask = null;
		int index = 0;		
		boolean isFound = false;
		Iterator<RecurringTask> taskIterator = _recurringTasks.iterator();
		
		while (taskIterator.hasNext()) {
			recurringTask = taskIterator.next();
			
			isFound = (recurringTask.getRecurringTaskId().
					   equals(recurringTaskId));
			
			if (isFound) {
				removedTask = Main.storage.
							  deleteRecurringTaskFromFileById(_id, 
							  recurringTaskId);
				
				if (removedTask != null) {
					_recurringTasks.remove(index);
				}
				
				break;
			}
			
			index++;
		}
		
		return removedTask;
	}
	
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
}
