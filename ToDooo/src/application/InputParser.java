//@author A0112498B
package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javafx.util.Pair;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class InputParser {
	private static InputParser _inputParser;
	private Parser _parser;
	
	// -----------------------------------------------------------------------------------------------
	// Constructor
	// -----------------------------------------------------------------------------------------------		
	private InputParser() {
		_parser = new Parser();
	}
	
	// -----------------------------------------------------------------------------------------------
	// Get methods
	// -----------------------------------------------------------------------------------------------	
	public static InputParser getInstance() {
		if (_inputParser == null) {
			_inputParser = new InputParser();
		}
		
		return _inputParser;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Public methods
	// -----------------------------------------------------------------------------------------------	
	public static Command getActionFromString(String userInput) {
		return Command.verifyCrudCommands(userInput);
	}
	
	public static TaskType getTaskTypeFromString(String userInput) {
		return TaskType.verifyTaskType(userInput.toLowerCase());
	}
	
	public Date getDateFromString(String dateString) {
		List<Date> dates = getDatesFromString(dateString);
		
		if (dates != null) {
			return dates.get(0);
		} else {
			return null;
		}
	}
	
	public List<Date> getDatesFromString(String userInput) {
		String checkString = userInput.toLowerCase() + " ";
		String basicCmd = Command.TO.getBasicCommand();
		String advancedCmd = Command.TO.getAdvancedCommand();
		int endIndex = checkString.length();
		int startIndex = -1;
		
		List<DateGroup> groups = _parser.parse(checkString);
		
		if (groups.isEmpty()) {
			return null;
		}			
		else {
			List<Date> dates = groups.get(0).getDates();
			
			// check for /to command
			if (checkString.contains(basicCmd)) {
				startIndex = checkString.indexOf(basicCmd) + 
							 basicCmd.length();				
			} else if (checkString.contains(advancedCmd)) {
				startIndex = checkString.indexOf(advancedCmd) + 
							 basicCmd.length();		
			}
			
			// extract /to date
			if (startIndex != -1) {
				checkString = checkString.substring(startIndex, endIndex);
				List<Date> toDates = getDatesFromString(checkString);
				if (toDates != null) {
					dates.add(toDates.get(0));
				}
			} 
			
			return dates;
		}
	}
	
	public Date getSearchDateFromString(String userInput) {
		String checkString = userInput.toLowerCase() + " ";
		
		List<DateGroup> groups = _parser.parse(checkString);
		
		if (groups.isEmpty()) {
			return null;
		}			
		else {
			List<Date> dates = groups.get(0).getDates();
			
			return dates.get(0);
		}		
	}
	
	public static String getCategoryFromString(String userInput) {
		boolean isCategorised = Command.hasCategoryCommand(userInput);
		
		if (isCategorised) {
			String basicCmd = Command.CATEGORY.getBasicCommand();
			
			int commandIndex = userInput.indexOf(basicCmd);
			String concatString = userInput.substring(commandIndex + 
								  basicCmd.length(), 
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
	
	public static String[] getTargetIdsFromString(String userInput) {
		int endIndex = userInput.length();
		String targetIdsString = getTargetId(userInput, endIndex);
		String[] targetIds = targetIdsString.split(Constant.REGEX_SPACE);
		
		if (targetIds != null) {
			for (String id : targetIds) {
				id.toUpperCase();
			}
			return targetIds;
		} else {
			return targetIds;
		}
	}
	
	public static String getTargetIdFromUpdateString(String userInput) {
		String targetId = null;
		
		int endIndex = userInput.indexOf(Constant.DELIMITER_UPDATE + " ");
		
		if (endIndex == -1) {
			int minLength = 2;
			int idIndex = 1;
			
			String[] words = userInput.split(" ");
			
			if (words.length == minLength) {
				targetId = words[idIndex];
			}
		} else {
			targetId = getTargetId(userInput, endIndex);
		}
						
		if (targetId != null) {
			return targetId.toUpperCase();
		} else {
			return targetId;
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
		String lowerCase = userInput.toLowerCase() + " ";
		String basicCmd = Command.RECURRING_UNTIL.getBasicCommand() + " ";
		String advancedCmd = Command.RECURRING_UNTIL.getAdvancedCommand() + " "; 
		int beginIndex = -1;
		int endIndex = userInput.length();
		
		if (lowerCase.contains(basicCmd)) {
			beginIndex = lowerCase.indexOf(basicCmd);
		} else if (lowerCase.contains(advancedCmd)) {
			beginIndex = lowerCase.indexOf(advancedCmd);
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
			String lowerCase = toDoString.toLowerCase() + " ";
			int endIndex = -1;

			String basicCommand = recurringCommand.getBasicCommand();
			String advancedCommand = recurringCommand.getAdvancedCommand();

			if (lowerCase.contains(basicCommand)) {
				endIndex = lowerCase.indexOf(basicCommand);
			} else if (lowerCase.contains(advancedCommand)) {
				endIndex = lowerCase.indexOf(advancedCommand);
			}

			toDoString = extractFromString(toDoString, endIndex);
		}
		return toDoString;
	}
	
	public static String removePriorityFromString(String toDoString, Priority priority) {
		boolean isPrioritised = (!priority.equals(Priority.NEUTRAL));
		
		if (isPrioritised) {
			Command priorityCommand = priority.getCommand();
			int endIndex = priorityCommand.getIndexOfCommand(toDoString);
			
			toDoString = extractFromString(toDoString, endIndex);
		}
		return toDoString;
	}
	
	public static String removeCategoryFromString(String toDoString, String category) {
		boolean isCategorised = (!category.equals(Constant.CATEGORY_UNCATEGORISED));
		String lowerCase = toDoString.toLowerCase() + " ";
		
		if (isCategorised) {
			String categoryCommand = Command.CATEGORY.getBasicCommand() + 
									 category.toLowerCase();
			int endIndex = lowerCase.indexOf(categoryCommand);			
			
			toDoString = toDoString.substring(0, endIndex);
		}
		return toDoString;
	}
	
	/*
	 * returns a list of search attribute pairs consisting
	 * of the attribute to search by and the key to match with
	 */
	public static ArrayList<Pair<SearchAttribute, String>> 
		getSearchAttributePairFromString(String userInput) {
		
		ArrayList<Pair<SearchAttribute, String>> attributePairs = 
				new ArrayList<Pair<SearchAttribute, String>>();
		String searchKey = null;

		ArrayList<SearchAttribute> attributes = SearchAttribute
				.getSearchAttributes(userInput);

		for (SearchAttribute attribute : attributes) {
			searchKey = InputParser
					.getSearchKeyFromString(userInput, attribute);

			if (searchKey != null) {
				attributePairs.
				add(new Pair<SearchAttribute, String>(attribute, searchKey));
			}
		}

		return attributePairs;
	}
	
	public static String verifyAndCorrectSearchString(String userInput) {
		userInput = InputParser.removeLineBreaks(userInput);
		userInput = userInput + " ";
		
		int expectedPosition = userInput.length() - 2;
		int actualPosition = userInput.lastIndexOf(Constant.DELIMITER_SEARCH);
		
		if (actualPosition != expectedPosition) {
			userInput += Constant.DELIMITER_SEARCH;
		}
		
		return userInput;
	}
	
	public static String removeLineBreaks(String userInput) {
		userInput = userInput.trim().
					replaceAll(Constant.REGEX_LINE_BREAK, "");
		
		return userInput;
	}
	
	/*
	 * Get the task id of the task which carries the
	 * recurring child task item
	 */
	public static String getTaskIdFromRecurringId(String targetId) {
		int prefixIndex = targetId.
		   		  indexOf(Constant.PREFIX_RECURRING_ID);
		
		String taskId = targetId.substring(0, prefixIndex);
		
		return taskId;
	}
	
	public static String getChildIdFromRecurringId(String targetId) {
		int prefixIndex = targetId.
		   		  indexOf(Constant.PREFIX_RECURRING_ID);
		int startIndex = prefixIndex + 1;
		int endIndex = targetId.length();
		
		String recurringTaskId = targetId.substring(startIndex, endIndex);	
		
		return recurringTaskId;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Private methods
	// -----------------------------------------------------------------------------------------------	
	private static String extractFromString(String toDoString, int endIndex) {
		boolean canSubstring = (endIndex != -1);
		if (canSubstring) {
			toDoString = toDoString.substring(0, endIndex);
		}
		
		return toDoString;
	}
	
	private static String getTargetId(String userInput, int endIndex) {
		String targetId = null;
		
		try {
			Command command = getActionFromString(userInput);
			String basicCommand = command.getBasicCommand();
			String advancedCommand = command.getAdvancedCommand();
			int beginIndex = -1;
			
			if (userInput.contains(basicCommand)) {
				beginIndex = basicCommand.length() + 1;
				targetId = userInput.substring(beginIndex, endIndex);
			} else if (userInput.contains(advancedCommand)) {
				beginIndex = advancedCommand.length() + 1;
				targetId = userInput.substring(beginIndex, endIndex);
			}
		} catch (StringIndexOutOfBoundsException exception) {
			return targetId;
		}
				
		return targetId.toUpperCase();
	}
	
	private static String removeUpdateFromString(String userInput, String id) {
		if (id == null) {
			return userInput;
		}
		
		String toDoString = userInput;	
		String lowerCase = userInput.toLowerCase() + " ";	
		String updateBasicCmd = Command.UPDATE.getBasicCommand();
		String updateAdvancedCmd = Command.UPDATE.getAdvancedCommand();
		
		String updateBasicString = updateBasicCmd + " " +
								   id.toLowerCase() + 
								   Constant.DELIMITER_UPDATE;
		String updateAdvancedString = updateAdvancedCmd + " " +
								      id.toLowerCase() + 
								      Constant.DELIMITER_UPDATE;
		
		if ((lowerCase.contains(updateBasicString) &&
			 lowerCase.indexOf(updateBasicString) == 0) ||
			(lowerCase.contains(updateAdvancedString) &&
			 lowerCase.indexOf(updateAdvancedString) == 0)) {
			
			int delimeterIndex = userInput.indexOf(Constant.DELIMITER_UPDATE);			
			int startIndex = delimeterIndex + 2;
			int endIndex = userInput.length();
					
			if (startIndex < endIndex) {
				toDoString = userInput.substring(startIndex, endIndex);
			} else {
				toDoString = "-";
			}			
		}
		
		return toDoString;
	}

	private static String removeAddFromString(String userInput) {
		String lowerCase = userInput.toLowerCase() + " ";
		String addBasicCmd = Command.ADD.getBasicCommand() + " ";
		String addAdvancedCmd = Command.ADD.getAdvancedCommand() + " ";
		int lengthOfBasicAddCommand = addBasicCmd.length();
		int lengthOfAdvancedAddCommand = addAdvancedCmd.length();
		
		String toDoString = userInput;
		
		if (lowerCase.contains(addBasicCmd) &&
			lowerCase.indexOf(addBasicCmd) == 0){
			
			toDoString = userInput.substring(lengthOfBasicAddCommand, 
						 userInput.length()).trim();
			
		} else if (lowerCase.contains(addAdvancedCmd) &&
				   lowerCase.indexOf(addAdvancedCmd) == 0) {
			
			toDoString = userInput.substring(lengthOfAdvancedAddCommand, 
						 userInput.length()).trim();
		}
		return toDoString;
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
			endIndex = detailString.indexOf(Constant.DELIMITER_SEARCH);
			
			searchKey = detailString.substring(0, endIndex);
		}
		
		return searchKey;
	}
}
