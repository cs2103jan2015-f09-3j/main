package application;
import java.util.Date;
import java.util.List;

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
	
	public Task() {
		// convert from node
	}
	
	public Task(String userInput) {
		this(userInput, null);
		_id = generateId(_taskType);	
	}

	public Task(String userInput, String id) {
		_id = id;
		_taskType = InputParser.getTaskTypeFromString(userInput);	
		_originalText = InputParser.removeActionFromString(userInput, _id);
		
		List<Date> dates = getDates(userInput);
		
		if ((_taskType.equals(TaskType.TIMED) && dates.size() < 2) ||
			(!_taskType.equals(TaskType.FLOATING) && dates.size() == 0)) {
			Main.systemFeedback = Constant.MSG_INVALID_FORMAT;
			_isValid = false;
		} else {			
			setDatesForTaskType(dates);			
			_category = InputParser.getCategoryFromString(userInput);
			
			_isValid = setRecurringDetails(dates, userInput);
			if (_isValid) {
				_priority = InputParser.getPriorityFromString(userInput);		
				_toDo = generateToDoString(userInput);
				
				updateIdWithTaskType();
				_isValid = true;
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
		
		switch(_taskType) {
			case EVENT :
				_on = dates.get(0);
				break;
			case TIMED :				
				_from = dates.get(0);
				_to = dates.get(1);
				break;
			case DATED :
				_by = dates.get(0);
				break;
			default :
				// floating task
				// does not require action since no date in array
				break;
		}
	}
	
	private boolean setRecurringDetails(List<Date> dates, String userInput) {
		boolean isValid = true;
		_isRecurring = Command.isRecurred(userInput);
		_repeat = Frequency.NIL;
		
		if ((_taskType.equals(TaskType.TIMED) ||
			_taskType.equals(TaskType.FLOATING)) && _isRecurring) {
			Main.systemFeedback = Constant.MSG_INVALID_RECURRING;
			isValid = false;
			
			return isValid;
		}		
		
		if (dates != null) {
			_repeat = InputParser.getFrequencyFromString(userInput);
			
			Date untilDate = InputParser.getUntilDateFromString(userInput);				
			_repeatUntil = untilDate;
			
			if (_repeat.equals(Frequency.WEEKLY) &&
			   (_taskType.equals(TaskType.DATED) ||
				_taskType.equals(TaskType.EVENT))) {
				
				Date startDate = dates.get(Constant.START_INDEX);
				_repeatDay = DateParser.calculateDayOfWeek(startDate);
			} else {
				_repeatDay = -1;
			}			
		}
		
		if (_isRecurring && dates == null) {
			Main.systemFeedback = Constant.MSG_INVALID_FORMAT;			
			isValid = false; 						
		}
		
		return isValid;
	}	
	
	private void updateIdWithTaskType() {
		if (_id == null) {
			return;
		}
		
		char typePrefix = _taskType.toString()
				 	  	  .charAt(Constant.START_INDEX);
		
		char prefix = _id.charAt(Constant.START_INDEX);
		_id = _id.replace(prefix, typePrefix);
	}
}
