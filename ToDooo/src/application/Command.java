package application;

import java.util.Date;

public enum Command {
	ADD("add", "/a"),
	DELETE("delete", "/d"),
	UPDATE("update", "/u"),
	SEARCH("search", "/s"),
	FROM("/from", "/f"),
	TO("/to", "/t"),
	ON("/on", "/o"),
	BY("/by", "/b"),
	CATEGORY("//", "//"),
	PRIORITY_HIGH("/high", "/***"),
	PRIORITY_MEDIUM("/medium", "/**"),
	PRIORITY_LOW("/low", "/*"),
	RECURRING_WEEKLY("/weekly", "/w"),
	RECURRING_MONTHLY("/monthly", "/m"),
	RECURRING_YEARLY("/yearly", "/y"),
	RECURRING_UNTIL("/until", "/u"),
	SETTING("/setting", "/setting"),
	GO_BACK("/~", "/~");
	
	private final String _COMMAND_BASIC;
	private final String _COMMAND_ADVANCED;
	
	private Command(String commandBasic, String commandAdvanced) {
		_COMMAND_BASIC = commandBasic;
		_COMMAND_ADVANCED = commandAdvanced;
	}
	
	public String getBasicCommand() {
		return _COMMAND_BASIC;
	}
	
	public String getAdvancedCommand() {
		return _COMMAND_ADVANCED;
	}
	
	public static Command verifyCrudCommands(String commandLine) {
		String lowerCase = commandLine.toLowerCase();
		String basicCommand = "";
		String advancedCommand = "";
		
		for (Command command : Constant.ACTION_COMMANDS) {
			basicCommand = command.getBasicCommand();
			advancedCommand = command.getAdvancedCommand();
			
			if (lowerCase.indexOf(basicCommand) == Constant.START_INDEX ||
				lowerCase.indexOf(advancedCommand) == Constant.START_INDEX) {
				
				return command;
			}
		}
		
		return Command.ADD;
	}

	public static boolean isPrioritised(String commandLine) {
		String lowerCase = commandLine.toLowerCase();
		
		return (lowerCase.contains(Command.PRIORITY_HIGH.getBasicCommand()) ||
				lowerCase.contains(Command.PRIORITY_HIGH.getAdvancedCommand()) ||
				lowerCase.contains(Command.PRIORITY_MEDIUM.getBasicCommand()) ||
				lowerCase.contains(Command.PRIORITY_MEDIUM.getAdvancedCommand()) ||
				lowerCase.contains(Command.PRIORITY_LOW.getBasicCommand()) ||
				lowerCase.contains(Command.PRIORITY_LOW.getAdvancedCommand()));
	}

	public static boolean isRecurred(String commandLine) {
		String lowerCase = commandLine.toLowerCase();
		int beginIndex = -1;
		int endIndex = commandLine.length();
		
		if (lowerCase.contains(Command.RECURRING_WEEKLY.getBasicCommand())) {
			beginIndex = lowerCase.indexOf(Command.RECURRING_WEEKLY.getBasicCommand());
		} else if (lowerCase.contains(Command.RECURRING_WEEKLY.getAdvancedCommand())) {
			beginIndex = lowerCase.indexOf(Command.RECURRING_WEEKLY.getAdvancedCommand());
		} else if (lowerCase.contains(Command.RECURRING_MONTHLY.getBasicCommand())) {
			beginIndex = lowerCase.indexOf(Command.RECURRING_MONTHLY.getBasicCommand());
		} else if (lowerCase.contains(Command.RECURRING_MONTHLY.getAdvancedCommand())) {
			beginIndex = lowerCase.indexOf(Command.RECURRING_MONTHLY.getAdvancedCommand());
		} else if (lowerCase.contains(Command.RECURRING_YEARLY.getBasicCommand())) {
			beginIndex = lowerCase.indexOf(Command.RECURRING_YEARLY.getBasicCommand());
		} else if (lowerCase.contains(Command.RECURRING_YEARLY.getAdvancedCommand())) {
			beginIndex = lowerCase.indexOf(Command.RECURRING_YEARLY.getAdvancedCommand());
		}
		
		boolean hasFirstCommand = (beginIndex != -1);
		if (hasFirstCommand) {		
			lowerCase = lowerCase.substring(beginIndex, endIndex);
						
			if (lowerCase.contains(Command.RECURRING_UNTIL.getBasicCommand()) ||
				lowerCase.contains(Command.RECURRING_UNTIL.getAdvancedCommand())) {

				Date untilDate = Main.inputParser.getDateFromString(lowerCase);
				if (untilDate != null) {
					return true;
				}
			} 
		}
		
		return false;
	}
	
	public static boolean isCategorised(String commandLine) {
		String lowerCase = commandLine.toLowerCase();
		
		return (lowerCase.contains(Command.CATEGORY.getBasicCommand()));
	}
}
