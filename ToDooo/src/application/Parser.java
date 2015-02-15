package application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Parser {
	// Public Methods	
	public static Command getActionFromString(String commandLine) {
		return Command.extractCrudCommands(commandLine);
	}
	
	public static Date[] getDatesFromString(String userInput, TaskType taskType) {
		Date[] dates = new Date[2];
		String basicCommand = null; 
		String advancedCommand = null;
		String dateInfo = null;
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
				} else if (userInput.contains(advancedCommand)) {
					dateInfo = getDateInfo(userInput, advancedCommand);
				}
				
				isValidDate = isValidDate(dateInfo);					
				if (isValidDate) {
					dates[beginIndex] = convertToDate(dateInfo);
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
