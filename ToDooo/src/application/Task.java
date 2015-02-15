package application;
import java.util.Date;

public class Task {
	private int _id;
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
		// -id = list.readNextId() (use the list object to call)
		// to be done after writing the method to read setting file
		// append D, E, F or T to id 
		
		_taskType = taskType;
		_toDo = generateToDoString(userInput, taskType); // incomplete
		_originalText = userInput;
		
		Date[] dates = Parser.getDatesFromString(userInput, taskType); // incomplete
		setDatesForTaskType(dates, taskType);
		
		_category = Parser.getCategoryFromString(userInput);
		
		_isRecurring = Command.isRecurred(userInput);
		_repeat = Parser.getFrequencyFromString(userInput);
		
		_priority = Parser.getPriorityFromString(userInput);
	}
	
	private String generateToDoString(String userInput, TaskType taskType) {
		// remove /add or /a (remember to check if exist because can add
		// without using the 2 keywords.
		String toDoString = userInput;
		int lengthOfBasicAddCommand = Command.ADD.getBasicCommand().length();
		int lengthOfAdvancedAddCommand = Command.ADD.getAdvancedCommand().length();
		String on = "on";
		String from = "from";
		String to = "to";
		String by = "by";
		
		if (toDoString.contains(Command.ADD.getBasicCommand())){
			toDoString = toDoString.substring(lengthOfBasicAddCommand, toDoString.length());
			toDoString = toDoString.trim();
		} else if (toDoString.contains(Command.ADD.getAdvancedCommand())) {	
			toDoString = toDoString.substring(lengthOfAdvancedAddCommand, toDoString.length());
			toDoString = toDoString.trim();
		}
		
		switch(taskType) {
			case EVENT :
				// remove the slash in /on
				if (toDoString.contains(Command.ON.getBasicCommand())) {
					toDoString.replaceFirst(Command.ON.getBasicCommand(), on);
				} else if (userInput.contains(Command.ON.getAdvancedCommand())) {
					toDoString.replaceFirst(Command.ON.getAdvancedCommand(), on);
				}
				break;
			case TIMED : 
				// remove the slash in /from & /to
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
				// remove the slash in /by
				if (toDoString.contains(Command.BY.getBasicCommand())) {
					toDoString.replaceFirst(Command.BY.getBasicCommand(), by);
				} else if (toDoString.contains(Command.BY.getAdvancedCommand())) {
					toDoString.replaceFirst(Command.BY.getAdvancedCommand(), by);
				} 
				break;
			default :
				// refactoring advice is to always have default
				// but I dont want to do anything if it's not these 3 cases.
				break;
		}
		
		return toDoString; // return the processed string.
	}

	private void setDatesForTaskType(Date[] dates, TaskType taskType) {
		_date = null;
		_from = null;
		_to = null;
		_by = null;
		
		switch(taskType) {
			case EVENT :
				_date = dates[0];
				break;
			case TIMED : 
				_from = dates[0];
				_to = dates[1];
				break;
			case DATED :
				_by = dates[0];
				break;
			default :
				// floating task
				// does not require action since no date in array
				break;
		}
	}
}
