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
	private Status _status;
	private Date _startDate;
	private Date _endDate;
	private ArrayList<RecurringTask> _recurringTasks;
	
	// -----------------------------------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------------------------------			
	public Task() {
		// convert from node
	}
	
	public Task(String userInput) {
		this(userInput, null);
	}
	
	public Task(String userInput, String id) {	
		_taskType = InputParser.getTaskTypeFromString(userInput);	
		
		if (id == null) {
			_id = String.valueOf(Main.list.getNextId());	
		} else {
			_id = id;	
		}
		
		_originalText = InputParser.removeActionFromString(userInput, _id);
		
		_isValid = Command.hasValidNumOfDateCommands(userInput);
		if (_isValid) {
			List<Date> dates = getDates(userInput);
			
			int maxNum = 2;
			if ((_taskType.equals(TaskType.TIMED) && dates.size() < maxNum) ||
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
					_status = Status.getTaskStatus(_endDate);
					
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

	public Status getStatus() {
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

	public void setStatus(Status status) {
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
		
	public ArrayList<RecurringTask> deepCloneArrayList() {
		ArrayList<RecurringTask> copies = new ArrayList<RecurringTask>();
		Iterator<RecurringTask> iterator = _recurringTasks.iterator();
		
		while(iterator.hasNext()){
			copies.add(iterator.next().clone());
		}
		
		return copies;
	}
	
	public static Task createRecurringChildItem(Task originalTask, String recurringId, 
												Status recurringStatus, Date onDate, 
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
				_recurringTasks.remove(index);				
				break;
			}
			
			index++;
		}
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
		
	// ---------------------------------------------------------
	// Recurring Tasks-related details tags
	// ---------------------------------------------------------
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

	private void generateRecurringTasks(Date startDate) {
		Calendar calendarStart = DateParser.createCalendar(startDate);
		Calendar calendarEnd = DateParser.createCalendar(_repeatUntil);
		int startDateDayOfMonth = DateParser.calculateDayOfMonth(calendarStart);
		int startDateMonth = DateParser.calculateMonth(calendarStart);
		
		
		while (calendarStart.before(calendarEnd) || calendarStart.equals(calendarEnd)) {
			switch (_repeat) {
				case WEEKLY :
					if (hasAddedWeeklyRecurringTask(calendarStart)) {
						continue;
					}
					break;
				case MONTHLY :
					if (hasAddedMonthlyRecurringTask(calendarStart, startDateDayOfMonth)) {
						continue;
					}
					break;
				case YEARLY :
					if (hasAddedYearlyRecurringTask(calendarStart, 
							startDateDayOfMonth, startDateMonth)) {
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
												int startDateMonth) {
		boolean hasAdded = false;
		int dayOfMonth;
		int month;
		dayOfMonth = DateParser.calculateDayOfMonth(calendarStart);	
		month = DateParser.calculateMonth(calendarStart);
		
		if (dayOfMonth == startDateDayOfMonth &&
			month == startDateMonth) {
			addToRecurringTasks(calendarStart);
			
			calendarStart.add(Calendar.YEAR, 1);
			hasAdded = true;
		}
		
		return hasAdded;
	}

	private boolean hasAddedMonthlyRecurringTask(Calendar calendarStart, int startDateDayOfMonth) {
		boolean hasAdded = false;
		int dayOfMonth = DateParser.calculateDayOfMonth(calendarStart);	
		
		if (dayOfMonth == startDateDayOfMonth) {
			addToRecurringTasks(calendarStart);
			
			calendarStart.add(Calendar.MONTH, 1);
			hasAdded = true;
		}
		
		return hasAdded;
	}

	private boolean hasAddedWeeklyRecurringTask(Calendar calendarStart) {
		boolean hasAdded = false;
		int dayOfWeek = DateParser.calculateDayOfWeek(calendarStart);
		
		if (dayOfWeek == _repeatDay) {
			addToRecurringTasks(calendarStart);
			
			calendarStart.add(Calendar.WEEK_OF_YEAR, 1);
			hasAdded = true;
		}
		
		return hasAdded;
	}
	
	private void addToRecurringTasks(Calendar calendarStart) {
		String recurringTaskId = generateRecurringTaskId();
		RecurringTask recurringTask = 
				new RecurringTask(recurringTaskId, calendarStart);
		
		_recurringTasks.add(recurringTask);
	}
	
	private String generateRecurringTaskId() {
		return String.valueOf(_recurringTasks.size() + 1);
	}
}
