package application;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	
	private Date[] getDatesFromString(String userInput, TaskType taskType) {
		Date[] dates = new Date[2];
		String basicCommand, advancedCommand, dateInfo;
		int beginIndex = 0;
		int nextIndex = 1;
		boolean isValidDate;
		
		switch(taskType) {
			case EVENT :
				// get the parameter after /on
				// time is after comma<whitespace> behind date
				basicCommand = Command.ON.getBasicCommand();
				advancedCommand = Command.ON.getAdvancedCommand();
				
				if (userInput.contains(basicCommand)) {
					dateInfo = getDateInfo(userInput, basicCommand);
					isValidDate = isValidDate(dateInfo);
					if (isValidDate) {
						dates[beginIndex] = convertToDate(dateInfo);
					}
				} else if (userInput.contains(advancedCommand)) {
					dateInfo = getDateInfo(userInput, advancedCommand);
					isValidDate = isValidDate(dateInfo);
					if (isValidDate) {
						dates[beginIndex] = convertToDate(dateInfo);
					}
				}
				break;
			case TIMED : 
				// get the parameter after /from
				// time is after comma<whitespace> behind date
				basicCommand = Command.FROM.getBasicCommand();
				advancedCommand = Command.FROM.getAdvancedCommand();
				String basicToCommand = Command.TO.getBasicCommand();
				String advancedToCommand = Command.TO.getAdvancedCommand();
				
				if (userInput.contains(basicCommand)) {
					dateInfo = getDateInfo(userInput, basicCommand);
					isValidDate = isValidDate(dateInfo);
					if (isValidDate) {
						dates[beginIndex] = convertToDate(dateInfo);
					}
				} else if (userInput.contains(advancedCommand)) {
					dateInfo = getDateInfo(userInput, advancedCommand);
					isValidDate = isValidDate(dateInfo);
					if (isValidDate) {
						dates[beginIndex] = convertToDate(dateInfo);
					}
				}
				// get the parameter after /to
				// time is after comma<whitespace> behind date
				if (userInput.contains(basicToCommand)) {
					dateInfo = getDateInfo(userInput, basicCommand);
					isValidDate = isValidDate(dateInfo);
					if (isValidDate) {
						dates[nextIndex] = convertToDate(dateInfo);
					}
				} else if (userInput.contains(advancedToCommand)) {
					dateInfo = getDateInfo(userInput, advancedCommand);
					isValidDate = isValidDate(dateInfo);
					if (isValidDate) {
						dates[nextIndex] = convertToDate(dateInfo);
					}
				}
				break;
			case DATED :
				// get the parameter after /by
				// time is after comma<whitespace> behind date
				basicCommand = Command.BY.getBasicCommand();
				advancedCommand = Command.BY.getAdvancedCommand();
				
				if (userInput.contains(basicCommand)) {
					dateInfo = getDateInfo(userInput, basicCommand);
					isValidDate = isValidDate(dateInfo);
					if (isValidDate) {
						dates[beginIndex] = convertToDate(dateInfo);
					}
				} else if (userInput.contains(advancedCommand)) {
					dateInfo = getDateInfo(userInput, advancedCommand);
					isValidDate = isValidDate(dateInfo);
					if (isValidDate) {
						dates[beginIndex] = convertToDate(dateInfo);
					}
				}
				break;		
			default :
				// floating task
				// does not require action since no date in array
				break;
		}
		return dates;
	}

	private String getDateInfo(String userInput, String taskCommand) {
		int beginIndexOfWord = userInput.indexOf(taskCommand);
		int lengthOfWord = taskCommand.length();
		int beginIndex = 0;
		String inputStartsWithWord = userInput.substring(beginIndexOfWord);
		String wordRemovedFromInput = inputStartsWithWord.substring(lengthOfWord, inputStartsWithWord.length());
		String hashtag = "#";
		String dateInfo = wordRemovedFromInput.trim();
	
		if (dateInfo.contains(hashtag)) {
			int hashTagIndex = wordRemovedFromInput.indexOf(hashtag);
			dateInfo = wordRemovedFromInput.substring(beginIndex, hashTagIndex);
			return dateInfo = dateInfo.trim();
		} else {
			return dateInfo;
		}
	}
	
	private boolean isValidDate(String dateInfo) {
		Date date = convertToDate(dateInfo);
		if (date.equals("null")) {
			return false;
		}
		return true;
	}
	
	private Date convertToDate(String inputDate) {
		SimpleDateFormat numericDateFormat = new SimpleDateFormat ("dd/MM/yyyy");
		SimpleDateFormat alphaDateFormat = new SimpleDateFormat ("dd MMMM yyyy");
		SimpleDateFormat numericDateAndTimeFormat = new SimpleDateFormat ("dd/MM/yyyy HH:mm");
		SimpleDateFormat alphaDateAndTimeFormat = new SimpleDateFormat ("dd MMMM yyyy HH:mm");
		String slash = "/";
		String colon = ":";
		String space = " ";
		Date date = null;
		
		if (inputDate.contains(slash) && inputDate.contains(colon)) {
			try {
				date = numericDateAndTimeFormat.parse(inputDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return date;
		} else if (inputDate.contains(slash)) {
			try {
				date = numericDateFormat.parse(inputDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return date;
		} else if (inputDate.contains(space) && inputDate.contains(colon)) {
			try {
				date = alphaDateAndTimeFormat.parse(inputDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return date;
		} else if (inputDate.contains(space)){
			try {
				date = alphaDateFormat.parse(inputDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return date;
		}
		return date;
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
