package application;
import java.util.Date;
import java.util.List;

public class Task {
	private String _id;
	private TaskType _taskType;
	private String _toDo;
	private String _originalText;
	private Date _date;
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
		_toDo = generateToDoString(userInput, taskType); // incomplete
		_originalText = userInput;
		
		List<Date> dates = Main.inputParser.getDatesFromString(userInput); // incomplete
		setDatesForTaskType(dates, taskType);
		
		_category = InputParser.getCategoryFromString(userInput);
		
		_isRecurring = Command.isRecurred(userInput);
		_repeat = InputParser.getFrequencyFromString(userInput);
		
		_priority = InputParser.getPriorityFromString(userInput);
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
		String toDoString = null;
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
				if (toDoString.contains(Command.ON.getBasicCommand())) {
					toDoString.replaceFirst(Command.ON.getBasicCommand(), on);
				} else if (toDoString.contains(Command.ON.getAdvancedCommand())) {
					toDoString.replaceFirst(Command.ON.getAdvancedCommand(), on);
				}
				break;
			case TIMED : 
				if (toDoString.contains(Command.FROM.getBasicCommand())) {
					toDoString.replaceFirst(Command.FROM.getBasicCommand(), from);
				} else if (toDoString.contains(Command.FROM.getAdvancedCommand())) {
					toDoString.replaceFirst(Command.FROM.getAdvancedCommand(), from);
				}
				if (toDoString.contains(Command.TO.getBasicCommand())) {
					toDoString.replaceFirst(Command.TO.getBasicCommand(), to);
				} else if (toDoString.contains(Command.TO.getAdvancedCommand())) {
					toDoString.replaceFirst(Command.TO.getAdvancedCommand(), to);
				}
				break;
			case DATED :
				if (toDoString.contains(Command.BY.getBasicCommand())) {
					toDoString.replaceFirst(Command.BY.getBasicCommand(), by);
				} else if (toDoString.contains(Command.BY.getAdvancedCommand())) {
					toDoString.replaceFirst(Command.BY.getAdvancedCommand(), by);
				} 
				break;
			default :
				// floating task
				break;
		}
		
		return toDoString; 
	}

	private void setDatesForTaskType(List<Date> dates, TaskType taskType) {
		_date = null;
		_from = null;
		_to = null;
		_by = null;
		
		switch(taskType) {
			case EVENT :
				_date = dates.get(0);
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
}
