package application;

public enum Command {
	ADD("/add", "/a"),
	DELETE("/delete", "/d"),
	UPDATE("/update", "/u"),
	SEARCH("/search", "/s"),
	FIND("/find", "/f"),
	FROM("/from", "/from"),
	TO("/to", "/t"),
	ON("/on", "/o"),
	BY("/by", "/b"),
	AT("/at", "/@"),
	EVENT("/event", "/e");
	
	private final String _COMMAND_BASIC;
	private final String _COMMAND_ADVANCED;
	
	private Command(String commandBasic, String commandAdvanced) {
		_COMMAND_BASIC = commandBasic;
		_COMMAND_ADVANCED = commandAdvanced;
	}
	
	public boolean isEqual(String otherCommand) {
		boolean isEqual = false;
		
		if (_COMMAND_BASIC.equalsIgnoreCase(otherCommand) ||  
			_COMMAND_ADVANCED.equalsIgnoreCase(otherCommand)) {
			isEqual = true;
		}
		
		return isEqual;
	}
	
	public String getBasicCommand() {
		return _COMMAND_BASIC;
	}
	
	public String getAdvancedCommand() {
		return _COMMAND_ADVANCED;
	}
	
	public static Command extractCrudCommands(String commandLine) {
		String basicCommand = "";
		String advancedCommand = "";
		
		for (Command command : Constant.CRUD_COMMANDS) {
			basicCommand = command.getBasicCommand();
			advancedCommand = command.getAdvancedCommand();
			
			if (commandLine.contains(basicCommand) ||
				commandLine.contains(advancedCommand)) {
				
				return command;
			}
		}
		
		return Command.ADD;
	}
}
