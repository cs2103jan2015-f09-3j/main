package application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

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

		return targetId.toUpperCase();
	}
	
	public static String getTargetIdFromUpdateString(String userInput) {
		int endIndex = userInput.indexOf(Constant.COMMAND_DELIMETER);
		String targetId = getTargetId(userInput, endIndex);
		
		return targetId.toUpperCase();
	}
	
	private static String getTargetId(String userInput, int endIndex) {
		String targetId = null;
		
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

}
