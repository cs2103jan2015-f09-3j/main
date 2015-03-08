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
	private Priority _priority;
	private int _dayOfWeek;
	private Date _repeatUntil;
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
		_originalText = removeActionFromString(userInput);
		
		List<Date> dates = getDates(userInput);
		
		if ((_taskType.equals(TaskType.TIMED) && dates.size() < 2) ||
			(!_taskType.equals(TaskType.FLOATING) && dates.size() == 0)) {
			_isValid = false;
		} else {			
			setDatesForTaskType(dates);			
			_category = InputParser.getCategoryFromString(userInput);
			
			_isValid = setRepeatFrequency(dates, userInput);
			if (_isValid) {
				_priority = InputParser.getPriorityFromString(userInput);		
				_toDo = generateToDoString(userInput);
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
		
		toDoString = removeCategoryFromString(toDoString);		
		toDoString = removePriorityFromString(toDoString);		
		toDoString = removeRecurringFromString(toDoString);
		toDoString = extractDescriptionFromString(toDoString);	
		
		return toDoString.trim(); 
	}

	private String removeRecurringFromString(String toDoString) {
		if (_isRecurring) {
			Command recurringCommand = _repeat.getCommand();
			String lowerCase = toDoString.toLowerCase();
			int endIndex = -1;
			
			if (lowerCase.contains(recurringCommand.getBasicCommand())) {
				endIndex = lowerCase.indexOf(recurringCommand.getBasicCommand());
			} else if (lowerCase.contains(recurringCommand.getAdvancedCommand())) {
				endIndex = lowerCase.indexOf(recurringCommand.getAdvancedCommand());
			}
			
			boolean canSubstring = (endIndex != -1);
			if (canSubstring) {
				toDoString = toDoString.substring(0, endIndex);
			}
		}
		return toDoString;
	}

	private String removePriorityFromString(String toDoString) {
		boolean isPrioritised = (!_priority.equals(Priority.NEUTRAL));
		
		if (isPrioritised) {
			Command priorityCommand = _priority.getCommand();
			String lowerCase = toDoString.toLowerCase();
			int endIndex = -1;
			
			if (lowerCase.contains(priorityCommand.getBasicCommand())) {
				endIndex = lowerCase.indexOf(priorityCommand.getBasicCommand());				
			} else if (lowerCase.contains(priorityCommand.getAdvancedCommand())) {
				endIndex = lowerCase.indexOf(priorityCommand.getAdvancedCommand());
			} 
			
			boolean canSubstring = (endIndex != -1);
			if (canSubstring) {
				toDoString = toDoString.substring(0, endIndex);
			}
		}
		return toDoString;
	}

	private String removeCategoryFromString(String toDoString) {
		boolean isCategorised = (!_category.equals(Constant.CATEGORY_UNCATEGORISED));
		String lowerCase = toDoString.toLowerCase();
		
		if (isCategorised) {
			String categoryCommand = Command.CATEGORY.getBasicCommand() + 
									 _category;
			int endIndex = lowerCase.indexOf(categoryCommand);			
			
			toDoString = toDoString.substring(0, endIndex);
		}
		return toDoString;
	}
	
	private String removeActionFromString(String userInput) {
		String lowerCase = userInput.toLowerCase();
		String addBasicCmd = Command.ADD.getBasicCommand();
		String addAdvancedCmd = Command.ADD.getAdvancedCommand();
		String updateBasicCmd = Command.UPDATE.getBasicCommand();
		String updateAdvancedCmd = Command.UPDATE.getAdvancedCommand();
		
		int lengthOfBasicAddCommand = addBasicCmd.length();
		int lengthOfAdvancedAddCommand = addAdvancedCmd.length();
		String toDoString = userInput;
		
		if (lowerCase.contains(addBasicCmd) &&
			lowerCase.indexOf(addBasicCmd) == Constant.START_INDEX){
			toDoString = userInput.substring(lengthOfBasicAddCommand, 
						 userInput.length()).trim();
		} else if (lowerCase.contains(Command.ADD.getAdvancedCommand()) &&
				   lowerCase.indexOf(addAdvancedCmd) == Constant.START_INDEX) {	
			toDoString = userInput.substring(lengthOfAdvancedAddCommand, 
						 userInput.length()).trim();
		} 
		
		if (lowerCase.contains(updateBasicCmd) ||
			lowerCase.contains(updateAdvancedCmd)) {			
			if (_id != null && lowerCase.contains(_id.toLowerCase())) {
				toDoString = userInput.substring(userInput.indexOf(Constant.COMMAND_DELIMETER) + 2, 
						 	 userInput.length());
			}
		}
		
		return toDoString;
	}

	private String extractDescriptionFromString(String userInput) {
		String extractedString = removeActionFromString(userInput);
		
		int beginIndex = 0;
		int endIndex = extractedString.length();
		
		String lowerCase = extractedString.toLowerCase();
		String toBeRemoved = null;
		Command typeCommand = null;
		switch(_taskType) {
			case EVENT :	
				typeCommand = Command.ON;
				break;
			case TIMED :				
				typeCommand = Command.FROM;
				break;
			case DATED :
				typeCommand = Command.BY;
				break;
			default :
				// floating task
				// no need to do anything since there is no date
				break;
		}
		
		if (typeCommand != null) {
			beginIndex = lowerCase.indexOf(typeCommand.getBasicCommand());
			
			boolean notBasicCommand = (beginIndex == -1);
			if (notBasicCommand) {
				beginIndex = lowerCase.indexOf(typeCommand.getAdvancedCommand());
			}
			
			boolean toExtract = (beginIndex != -1);
			if (toExtract) {
				toBeRemoved = extractedString.substring(beginIndex, endIndex);
				extractedString = extractedString.replace(toBeRemoved, "");
			}			
		}		
		return extractedString;
	}
	

	private List<Date> getDates(String userInput) {
		String toBeRemoved = extractDescriptionFromString(userInput);
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
	
	private boolean setRepeatFrequency(List<Date> dates, String userInput) {
		boolean isValid = true;
		_isRecurring = Command.isRecurred(userInput);
		
		if (dates != null) {
			_repeat = InputParser.getFrequencyFromString(userInput);
		}
		
		if (_isRecurring && dates == null) {
			isValid = false; 						
		}
		
		return isValid;
	}	
	
}
