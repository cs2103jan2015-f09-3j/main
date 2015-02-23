package application;

public enum Command {
	ADD("add", "-a"),
	DELETE("delete", "-d"),
	UPDATE("update", "-u"),
	SEARCH("search", "-s"),
	FROM("from", "-f"),
	TO("to", "-t"),
	ON("on", "-o"),
	BY("by", "-b"),
	CATEGORY("//", "//"),
	PRIORITY_HIGH("/high", "/***"),
	PRIORITY_MEDIUM("/medium", "/**"),
	PRIORITY_LOW("/low", "/*"),
	RECURRING_WEEKLY("/weekly", "-w"),
	RECURRING_MONTHLY("/monthly", "-m"),
	RECURRING_YEARLY("/yearly", "-y"),
	SETTING("/setting", "/setting"),
	GOBACK("/~", "/~");
	
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
		String basicCommand = "";
		String advancedCommand = "";
		
		for (Command command : Constant.ACTION_COMMANDS) {
			basicCommand = command.getBasicCommand();
			advancedCommand = command.getAdvancedCommand();
			
			if (commandLine.indexOf(basicCommand) == Constant.START_INDEX ||
					commandLine.indexOf(advancedCommand) == Constant.START_INDEX) {
				
				return command;
			}
		}
		
		return Command.ADD;
	}

	public static boolean isPrioritised(String commandLine) {
		return (commandLine.contains(Command.PRIORITY_HIGH.getBasicCommand()) ||
			    commandLine.contains(Command.PRIORITY_HIGH.getAdvancedCommand()) ||
			    commandLine.contains(Command.PRIORITY_MEDIUM.getBasicCommand()) ||
			    commandLine.contains(Command.PRIORITY_MEDIUM.getAdvancedCommand()) ||
			    commandLine.contains(Command.PRIORITY_LOW.getBasicCommand()) ||
			    commandLine.contains(Command.PRIORITY_LOW.getAdvancedCommand()));
	}

	public static boolean isRecurred(String commandLine) {
		return (commandLine.contains(Command.RECURRING_WEEKLY.getBasicCommand()) ||
			    commandLine.contains(Command.RECURRING_WEEKLY.getAdvancedCommand()) ||
			    commandLine.contains(Command.RECURRING_MONTHLY.getBasicCommand()) ||
			    commandLine.contains(Command.RECURRING_MONTHLY.getAdvancedCommand()) ||
			    commandLine.contains(Command.RECURRING_YEARLY.getBasicCommand()) ||
			    commandLine.contains(Command.RECURRING_YEARLY.getAdvancedCommand()));
	}
	
	public static boolean isCategorised(String commandLine) {
		return (commandLine.contains(Command.CATEGORY.getBasicCommand()));
	}
}
