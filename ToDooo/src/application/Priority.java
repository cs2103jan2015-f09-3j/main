package application;
public enum Priority {
	NEUTRAL(null),
	LOW(Command.PRIORITY_LOW),
	MEDIUM(Command.PRIORITY_MEDIUM),
	HIGH(Command.PRIORITY_HIGH);
	
	private final Command _COMMAND;
	
	private Priority(Command command) {
		_COMMAND = command;
	}
	
	public Command getCommand() {
		return _COMMAND;
	}

	public static boolean isLow(String commandLine) {
		return (commandLine.contains(Command.PRIORITY_LOW.getBasicCommand()) ||
				commandLine.contains(Command.PRIORITY_LOW.getAdvancedCommand()));
	}
	
	public static boolean isMedium(String commandLine) {
		return (commandLine.contains(Command.PRIORITY_MEDIUM.getBasicCommand()) ||
				commandLine.contains(Command.PRIORITY_MEDIUM.getAdvancedCommand()));
	}
	
	public static boolean isHigh(String commandLine) {
		return (commandLine.contains(Command.PRIORITY_HIGH.getBasicCommand()) ||
				commandLine.contains(Command.PRIORITY_HIGH.getAdvancedCommand()));
	}
}
