//@author A0112498B
package application;

import java.util.Date;

public enum Command {
	ADD("add", "-a"),
	DELETE("delete", "-d"),
	UPDATE("update", "-u"),
	SEARCH("search", "-s"),
	COMPLETE("complete", "-c"),
	UNCOMPLETE("uncomplete", "-uc"),
	VIEW("view", "-v"),
	FROM("/from", "-f"),
	TO("/to", "-t"),
	ON("/on", "-o"),
	BY("/by", "-b"),
	CATEGORY("#", "#"),
	PRIORITY_HIGH("/high", "/***"),
	PRIORITY_MEDIUM("/medium", "/**"),
	PRIORITY_LOW("/low", "/*"),
	RECURRING_WEEKLY("/weekly", "-w"),
	RECURRING_MONTHLY("/monthly", "-m"),
	RECURRING_YEARLY("/yearly", "-y"),
	RECURRING_UNTIL("/until", "-u");
	
	private final String _COMMAND_BASIC;
	private final String _COMMAND_ADVANCED;
	
	// -----------------------------------------------------------------------------------------------
	// Constructor
	// -----------------------------------------------------------------------------------------------		
	private Command(String commandBasic, String commandAdvanced) {
		_COMMAND_BASIC = commandBasic;
		_COMMAND_ADVANCED = commandAdvanced;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Get methods
	// -----------------------------------------------------------------------------------------------
	public String getBasicCommand() {
		return _COMMAND_BASIC;
	}
	
	public String getAdvancedCommand() {
		return _COMMAND_ADVANCED;
	}
		
	// -----------------------------------------------------------------------------------------------
	// Public methods
	// -----------------------------------------------------------------------------------------------
	public static Command verifyCrudCommands(String commandLine) {		
		for (Command command : Constant.COMMAND_ACTIONS) {
			
			if (command.hasActionCommand(commandLine)) {				
				return command;
			}
		}		
		return Command.ADD;
	}

	/*
	 * Returns recurring command if: 
	 * 1) has the correct recurring command e.g. /weekly /until
	 */
	public static Command verifyRecurringCommands(String commandLine) {				
		for (Command recurringCommand : Constant.COMMAND_RECURRING) {
			if (recurringCommand.hasValidRecurringCommand(commandLine)) {
				return recurringCommand;
			}
		}
		
		return null;
	}
	
	/*
	 * Returns true if:
	 * 1) commandLine has either /weekly, /monthly or /yearly
	 */
	public static boolean hasRecurringCommands(String commandLine) {			
		boolean hasFound = false;
		
		for (Command recurringCommand : Constant.COMMAND_RECURRING) {
			if (recurringCommand.hasCommand(commandLine)) {
				hasFound = true;
			}
		}
		
		return hasFound;
	}
			
	public static boolean hasCategoryCommand(String commandLine) {
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = Command.CATEGORY.getBasicCommand();
		boolean isCategorised = false;
		int beginIndex = -1;
		int endIndex = -1;
		
		if (lowerCase.contains(basicCmd)) {
			beginIndex = lowerCase.indexOf(basicCmd);
			endIndex = beginIndex + basicCmd.length();
			
			if (lowerCase.charAt(endIndex) != ' ') {
				isCategorised = true;
			}
		}
				
		return isCategorised;
	}
			
	public static boolean hasValidNumOfDateCommands(String commandLine) {
		int count = 0;
		boolean isCorrectNum = false;
		boolean isValidTimed = true;
		
		if (TaskType.isFloatingTask(commandLine)) {
			isCorrectNum = true;
			
			return isCorrectNum;
		}
		
		for (Command command : Constant.COMMAND_DATES) {
			if (command.hasCommand(commandLine)) {
				isValidTimed = !(((command.name().equals(Command.FROM) ||
						 		command.name().equals(Command.TO)) && count >= 2) ||
						 		(!(command.name().equals(Command.FROM) ||
								command.name().equals(Command.TO)) && count >= 1));
				
				count++;
			}
		}
		
		if (count == 1 || (count == 2 && isValidTimed)) {
			isCorrectNum = true;
		}
		
		return isCorrectNum;
	}
	
	public static boolean shouldRetrieveOriginalInput(String commandLine) {		
		boolean shouldRetrieve = false;
		
		boolean hasNoUpdateDelimiter = 
				(commandLine.indexOf(Constant.DELIMITER_UPDATE) == -1);
		
		if (hasNoUpdateDelimiter) {
			shouldRetrieve = true;
		}
		
		return shouldRetrieve;
	}
		
	// -----------------------------------------------------------------------------------------------
	// Private methods
	// -----------------------------------------------------------------------------------------------
	private boolean hasCommand(String commandLine) {
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = _COMMAND_BASIC + " ";
		String advancedCmd = _COMMAND_ADVANCED + " ";
				
		
		return (lowerCase.contains(basicCmd) ||
				lowerCase.contains(advancedCmd));
	}
	
	private boolean hasActionCommand(String commandLine) {
		int startIndex = 0;
		String lowerCase = commandLine.toLowerCase();
		String basicCmd = _COMMAND_BASIC + " ";
		String advancedCmd = _COMMAND_ADVANCED + " ";
		
		return (lowerCase.indexOf(basicCmd) == startIndex ||
			    lowerCase.indexOf(advancedCmd) == startIndex);
	}
	
	private boolean hasValidRecurringCommand(String commandLine) {
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = _COMMAND_BASIC + " " + 
						  Command.RECURRING_UNTIL.getBasicCommand() + " ";
		String advancedCmd = _COMMAND_ADVANCED + " " + 
						  	 Command.RECURRING_UNTIL.getAdvancedCommand() + " ";
		int beginIndex = -1;
		int endIndex = lowerCase.length();
		
		if (lowerCase.contains(basicCmd)) {
			beginIndex = lowerCase.indexOf(basicCmd);
		} else if (lowerCase.contains(advancedCmd)) {
			beginIndex = lowerCase.indexOf(advancedCmd);
		}
				
		boolean hasRecurringCommand = (beginIndex != -1);
		if (hasRecurringCommand) {
			lowerCase = lowerCase.substring(beginIndex, endIndex);
			
			Date untilDate = Main.inputParser.getDateFromString(lowerCase);
			if (untilDate != null) {
				hasRecurringCommand = true;
			} else {
				hasRecurringCommand = false;
			}
		}
		
		return hasRecurringCommand;
	}
}
