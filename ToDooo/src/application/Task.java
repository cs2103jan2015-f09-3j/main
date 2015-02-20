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
	
	public Task(String userInput, TaskType taskType) {
		this(userInput, taskType, generateId(taskType));
	}
	
	public Task(String userInput, TaskType taskType, String id) {
		_id = id;		
		_taskType = taskType;
		_originalText = userInput;
		
		List<Date> dates = Main.inputParser.getDatesFromString(userInput);
		setDatesForTaskType(dates, taskType);
		
		_category = InputParser.getCategoryFromString(userInput);
		
		setRepeatFrequency(dates, userInput);
		
		_priority = InputParser.getPriorityFromString(userInput);
		
		_toDo = generateToDoString(userInput, taskType);
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
	
	private static String generateId(TaskType taskType) {
		int nextId = Main.storage.readNextId();
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
	
	private String generateToDoString(String userInput, TaskType taskType) {
		int lengthOfBasicAddCommand = Command.ADD.getBasicCommand().length();
		int lengthOfAdvancedAddCommand = Command.ADD.getAdvancedCommand().length();
		String toDoString = userInput;
		String on = "on";
		String from = "from";
		String to = "to";
		String by = "by";
		
		if (userInput.contains(Command.ADD.getBasicCommand())){
			toDoString = userInput.substring(lengthOfBasicAddCommand, 
						 userInput.length()).trim();
		} else if (userInput.contains(Command.ADD.getAdvancedCommand())) {	
			toDoString = userInput.substring(lengthOfAdvancedAddCommand, 
						 userInput.length()).trim();
		} 
		
		switch(taskType) {
			case EVENT :
				if (toDoString.contains(Command.ON.getAdvancedCommand())) {
					toDoString.replace(Command.ON.getAdvancedCommand(), on);
				}
				break;
			case TIMED : 
				if (toDoString.contains(Command.FROM.getAdvancedCommand())) {
					toDoString.replace(Command.FROM.getAdvancedCommand(), from);
				}
				
				if (toDoString.contains(Command.TO.getAdvancedCommand())) {
					toDoString.replace(Command.TO.getAdvancedCommand(), to);
				}
				break;
			case DATED :
				if (toDoString.contains(Command.BY.getAdvancedCommand())) {
					toDoString.replace(Command.BY.getAdvancedCommand(), by);
				} 
				break;
			default :
				// floating task
				break;
		}
		
		boolean isCategorised = (!_category.equals(Constant.CATEGORY_UNCATEGORISED));
		if (isCategorised) {
			toDoString = toDoString.replace(Command.CATEGORY.getBasicCommand() + 
												 _category, "");
		}
		
		boolean isPrioritised = (!_priority.equals(Priority.NEUTRAL));
		if (isPrioritised) {
			Command priorityCommand = _priority.getCommand();
			
			if (toDoString.contains(priorityCommand.getBasicCommand())) {
				toDoString = toDoString.replace(priorityCommand.getBasicCommand(), "");
			} else if (toDoString.contains(priorityCommand.getAdvancedCommand())) {
				toDoString = toDoString.replace(priorityCommand.getAdvancedCommand(), "");
			} 
		}
		
		if (_isRecurring) {
			Command recurringCommand = _repeat.getCommand();
			
			if (toDoString.contains(recurringCommand.getBasicCommand())) {
				toDoString = toDoString.replace(recurringCommand.getBasicCommand(), "");
			} else if (toDoString.contains(recurringCommand.getAdvancedCommand())) {
				toDoString = toDoString.replace(recurringCommand.getAdvancedCommand(), "");
			}
		}
		
		return toDoString.trim(); 
	}

	private void setDatesForTaskType(List<Date> dates, TaskType taskType) {
		_on = null;
		_from = null;
		_to = null;
		_by = null;
		
		switch(taskType) {
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
	
	private void setRepeatFrequency(List<Date> dates, String userInput) {
		if (dates != null) {
			_isRecurring = Command.isRecurred(userInput);
			_repeat = InputParser.getFrequencyFromString(userInput);
		} else {
			_isRecurring = false;
			_repeat = Frequency.NIL;
		}
	}
}
