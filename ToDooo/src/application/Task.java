package application;
import java.util.Date;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

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
		
		Date[] dates = getDatesFromString(userInput, taskType); // incomplete
		setDatesForTaskType(dates, taskType);
		
		_category = getCategoryFromString(userInput);
		
		_isRecurring = Command.isRecurred(userInput);
		_repeat = getFrequencyFromString(userInput);
		
		_priority = getPriorityFromString(userInput);
	}
	
	private String generateToDoString(String userInput, TaskType taskType) {
		// remove /add or /a (remember to check if exist because can add
		// without using the 2 keywords.
		
		switch(taskType) {
			case EVENT :
				// remove the slash in /on
				break;
			case TIMED : 
				// remove the slash in /from & /to
				break;
			case DATED :
				// remove the slash in /by
				break;
			default :
				// refactoring advice is to always have default
				// but I dont want to do anything if it's not these 3 cases.
				break;
		}
		
		return ""; // return the processed string.
	}
	
	private Date[] getDatesFromString(String userInput, TaskType taskType) {
		Date[] dates = new Date[2];
		
		switch(taskType) {
			case EVENT :
				// get the parameter after /on
				// time is after comma<whitespace> behind date
				break;
			case TIMED : 
				// get the parameter after /from
				// time is after comma<whitespace> behind date
				
				// get the parameter after /to
				// time is after comma<whitespace> behind date
				break;
			case DATED :
				// get the parameter after /by
				// time is after comma<whitespace> behind date
				break;
			default :
				// floating task
				// does not require action since no date in array
				break;
		}
		
		return dates;
	}
	
	private String getCategoryFromString(String userInput) {
		boolean isCategorised = userInput.contains(Command.CATEGORY.getBasicCommand()) &&
								!Command.isPrioritised(userInput);
		
		if (isCategorised) {
			int commandIndex = userInput.indexOf(Command.CATEGORY.getBasicCommand());
			String concatString = userInput.substring(commandIndex + 1, userInput.length());
			
			return concatString.substring(0, concatString.indexOf(" "));
		}
		
		return Constant.CATEGORY_UNCATEGORISED;
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

	private Frequency getFrequencyFromString(String userInput) {
		if (Frequency.isWeekly(userInput)) {
			return Frequency.WEEKLY;
		} else if (Frequency.isMonthly(userInput)) {
			return Frequency.MONTHLY;
		} else if (Frequency.isYearly(userInput)) {
			return Frequency.YEARLY;
		} else {
			return Frequency.NIL;
		}
	}
	
	private Priority getPriorityFromString(String userInput) {
		if (Priority.isHigh(userInput)) {
			return Priority.HIGH;
		} else if (Priority.isMedium(userInput)) {
			return Priority.MEDIUM;
		} else if (Priority.isHigh(userInput)) {
			return Priority.HIGH;
		} else {
			return Priority.NEUTRAL;
		}
	}
}
