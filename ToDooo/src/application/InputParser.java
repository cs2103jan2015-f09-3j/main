package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javafx.util.Pair;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class InputParser {
	private Parser _parser;
	
	public InputParser() {
		_parser = new Parser();
	}
	
	// Public Methods	
	public static Command getActionFromString(String userInput) {
		return Command.verifyCrudCommands(userInput);
	}
	
	public static TaskType getTaskTypeFromString(String userInput) {
		return TaskType.verifyTaskType(userInput.toLowerCase());
	}
	
	public Date getDateFromString(String dateString) {
		List<Date> dates = getDatesFromString(dateString);
		
		if (dates != null) {
			return dates.get(Constant.START_INDEX);
		} else {
			return null;
		}
	}
	
	public List<Date> getDatesFromString(String userInput) {
		String checkString = userInput.toLowerCase();
		
		if (checkString.contains(Command.TO.getBasicCommand())) {
			checkString = checkString.replace(Command.TO.getBasicCommand(), 
						       			      Command.TO.toString());
		} else if (checkString.contains(Command.TO.getAdvancedCommand())) {
			checkString = checkString.replace(Command.TO.getAdvancedCommand(), 
											  Command.TO.toString());
		}
		
		List<DateGroup> groups = _parser.parse(checkString);
		
		if (groups.isEmpty()) {
			return null;
		}			
		else {
			List<Date> dates = groups.get(0).getDates();
			
			return dates;
		}
	}
	
	public static String getCategoryFromString(String userInput) {
		boolean isCategorised = Command.isCategorised(userInput);
		
		if (isCategorised) {
			int commandIndex = userInput.indexOf(Command.CATEGORY.getBasicCommand());
			String concatString = userInput.substring(commandIndex + 
								  Command.CATEGORY.getBasicCommand().length(), 
								  userInput.length());
			
			boolean noWhiteSpace = (concatString.indexOf(" ") == -1);
			if (noWhiteSpace) {
				return concatString;
			} else {
				return concatString.substring(0, concatString.indexOf(" "));				
			}
		}
		
		return Constant.CATEGORY_UNCATEGORISED;
	}
	
	public static String getTargetIdFromString(String userInput) {
		int endIndex = userInput.length();
		String targetId = getTargetId(userInput, endIndex);

		if (targetId != null) {
			return targetId.toUpperCase();
		} else {
			return targetId;
		}
	}
	
	public static String getTargetIdFromUpdateString(String userInput) {
		int endIndex = userInput.indexOf(Constant.DELIMETER_UPDATE);
		String targetId = getTargetId(userInput, endIndex);
		
		if (targetId != null) {
			return targetId.toUpperCase();
		} else {
			return targetId;
		}
	}
	
	private static String getTargetId(String userInput, int endIndex) {
		String targetId = null;
		
		try {
			Command command = getActionFromString(userInput);
			String basicCommand = command.getBasicCommand();
			String advancedCommand = command.getAdvancedCommand();
			
			if (userInput.contains(basicCommand)) {
				targetId = userInput.substring(basicCommand.length() + 1, 
											   endIndex);
			} else if (userInput.contains(advancedCommand)) {
				targetId = userInput.substring(advancedCommand.length() + 1, 
											   endIndex);
			}
		} catch (StringIndexOutOfBoundsException exception) {
			return targetId;
		}
				
		return targetId.toUpperCase();
	}
	
	public static Frequency getFrequencyFromString(String userInput) {
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
	
	public static Priority getPriorityFromString(String userInput) {
		if (Priority.isHigh(userInput)) {
			return Priority.HIGH;
		} else if (Priority.isMedium(userInput)) {
			return Priority.MEDIUM;
		} else if (Priority.isLow(userInput)) {
			return Priority.LOW;
		} else {
			return Priority.NEUTRAL;
		}
	}
	
	public static String getDateString(Date date) {
		String dateString = Constant.XML_TEXT_NIL;
		
		if (date != null) {
			dateString = date.toString();
		}
		
		return dateString;
	}
	
	public static Date getUntilDateFromString(String userInput) {
		Date untilDate = null;
		String lowerCase = userInput.toLowerCase();
		int beginIndex = -1;
		int endIndex = userInput.length();
		
		if (lowerCase.contains(Command.RECURRING_UNTIL.getBasicCommand())) {
			beginIndex = lowerCase.indexOf(Command.RECURRING_UNTIL.getBasicCommand());
		} else if (lowerCase.contains(Command.RECURRING_UNTIL.getAdvancedCommand())) {
			beginIndex = lowerCase.indexOf(Command.RECURRING_UNTIL.getAdvancedCommand());
		}
		
		boolean hasUntil = (beginIndex != -1);
		if (hasUntil) {
			lowerCase = lowerCase.substring(beginIndex, endIndex);
			untilDate = Main.inputParser.getDateFromString(lowerCase);
		}
		
		return untilDate;
	}

	public static String removeActionFromString(String userInput, String id) {	
		String toDoString = userInput;		
		
		toDoString = InputParser.removeAddFromString(userInput); 		
		toDoString = InputParser.removeUpdateFromString(toDoString, id);
		
		return toDoString;
	}
	
	private static String removeUpdateFromString(String userInput, String id) {
		if (id == null) {
			return userInput;
		}
		
		String toDoString = userInput;	
		String lowerCase = userInput.toLowerCase();		
		String updateBasicCmd = Command.UPDATE.getBasicCommand();
		String updateAdvancedCmd = Command.UPDATE.getAdvancedCommand();
		
		String updateBasicString = updateBasicCmd + " " +
								   id.toLowerCase() + 
								   Constant.DELIMETER_UPDATE;
		String updateAdvancedString = updateAdvancedCmd + " " +
								      id.toLowerCase() + 
								      Constant.DELIMETER_UPDATE;
		
		if ((lowerCase.contains(updateBasicString) &&
			 lowerCase.indexOf(updateBasicString) == Constant.START_INDEX) ||
			(lowerCase.contains(updateAdvancedString) &&
			 lowerCase.indexOf(updateAdvancedString) == Constant.START_INDEX)) {
			
			int startIndex = userInput.indexOf(Constant.DELIMETER_UPDATE) + 2;
			int endIndex = userInput.length();			
			toDoString = userInput.substring(startIndex, endIndex);
		}
		
		return toDoString;
	}

	private static String removeAddFromString(String userInput) {
		String lowerCase = userInput.toLowerCase();
		String addBasicCmd = Command.ADD.getBasicCommand();
		String addAdvancedCmd = Command.ADD.getAdvancedCommand();
		int lengthOfBasicAddCommand = addBasicCmd.length();
		int lengthOfAdvancedAddCommand = addAdvancedCmd.length();
		
		String toDoString = userInput;
		
		if (lowerCase.contains(addBasicCmd) &&
			lowerCase.indexOf(addBasicCmd) == Constant.START_INDEX){
			toDoString = userInput.substring(lengthOfBasicAddCommand, 
						 userInput.length()).trim();
		} else if (lowerCase.contains(addAdvancedCmd) &&
				   lowerCase.indexOf(addAdvancedCmd) == Constant.START_INDEX) {	
			toDoString = userInput.substring(lengthOfAdvancedAddCommand, 
						 userInput.length()).trim();
		}
		return toDoString;
	}
	
	public static String extractDescriptionFromString(String userInput, TaskType taskType, String id) {
		String extractedString = InputParser.removeActionFromString(userInput, id);
		
		int beginIndex = 0;
		int endIndex = extractedString.length();
		
		String lowerCase = extractedString.toLowerCase();
		String toBeRemoved = null;
		Command typeCommand = null;
		switch(taskType) {
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
	
	public static String removeRecurringFromString(String toDoString, 
											 boolean isRecurring, Frequency repeat) {
		if (isRecurring) {
			Command recurringCommand = repeat.getCommand();
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
	
	public static String removePriorityFromString(String toDoString, Priority priority) {
		boolean isPrioritised = (!priority.equals(Priority.NEUTRAL));
		
		if (isPrioritised) {
			Command priorityCommand = priority.getCommand();
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
	
	public static String removeCategoryFromString(String toDoString, String category) {
		boolean isCategorised = (!category.equals(Constant.CATEGORY_UNCATEGORISED));
		String lowerCase = toDoString.toLowerCase();
		
		if (isCategorised) {
			String categoryCommand = Command.CATEGORY.getBasicCommand() + 
									 category;
			int endIndex = lowerCase.indexOf(categoryCommand);			
			
			toDoString = toDoString.substring(0, endIndex);
		}
		return toDoString;
	}
	
	public static ArrayList<Pair<SearchAttribute, String>> 
				  getSearchAttributePairFromString(String userInput) {
		ArrayList<Pair<SearchAttribute, String>> attributePairs = 
				new ArrayList<Pair<SearchAttribute, String>>();
		String searchKey = null;
		
		ArrayList<SearchAttribute> attributes = 
				SearchAttribute.getSearchAttributes(userInput);
			
		for (SearchAttribute attribute : attributes) {
			searchKey = InputParser.getSearchKeyFromString(userInput, attribute);
			
			if (searchKey != null) {
				attributePairs.
				add(new Pair<SearchAttribute, String>(attribute, searchKey));
			}
		}
		
		return attributePairs;
	}
	
	private static String getSearchKeyFromString(String userInput, 
										   SearchAttribute attribute) {
		String searchKey = null;
		String lowerCase = userInput.toLowerCase();
		String command = attribute.getCommand();
		
		if (lowerCase.contains(command)) {		
			int startIndex = lowerCase.indexOf(command) + command.length();
			int endIndex = lowerCase.length();
			
			String detailString = lowerCase.substring(startIndex, endIndex);
			endIndex = lowerCase.indexOf(Constant.DELIMETER_SEARCH);
			
			searchKey = detailString.substring(0, endIndex);
		}
		
		return searchKey;
	}
}
