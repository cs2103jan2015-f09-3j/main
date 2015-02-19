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
	public static Command getActionFromString(String commandLine) {
		return Command.extractCrudCommands(commandLine);
	}
	
	public List<Date> getDatesFromString(String userInput) {
		List<DateGroup> groups = _parser.parse(userInput);
		List<Date> dates = groups.get(0).getDates();
		
		return dates;
	}
	
	public static String getCategoryFromString(String userInput) {
		boolean isCategorised = userInput.contains(Command.CATEGORY.getBasicCommand()) &&
								!Command.isPrioritised(userInput);
		
		if (isCategorised) {
			int commandIndex = userInput.indexOf(Command.CATEGORY.getBasicCommand());
			String concatString = userInput.substring(commandIndex + 1, userInput.length());
			
			return concatString.substring(0, concatString.indexOf(" "));
		}
		
		return Constant.CATEGORY_UNCATEGORISED;
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
		} else if (Priority.isHigh(userInput)) {
			return Priority.HIGH;
		} else {
			return Priority.NEUTRAL;
		}
	}
		
	// Private Methods
	private static String getDateInfo(String userInput, String taskCommand) {
		int startIndexOfCommand = userInput.indexOf(taskCommand);
		int lengthOfCommand = taskCommand.length();
		int beginIndex = 0;
		
		String inputStartsWithWord = userInput.substring(startIndexOfCommand);
		String wordRemovedFromInput = inputStartsWithWord.substring(lengthOfCommand, inputStartsWithWord.length());
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
	
	private static boolean isValidDate(String dateInfo) {
		Date date = convertToDate(dateInfo);
		if (date.equals("null")) {
			return false;
		}
		return true;
	}
	
	private static Date convertToDate(String inputDate) {
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
}
