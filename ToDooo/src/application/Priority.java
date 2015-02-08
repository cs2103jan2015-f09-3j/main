package application;
public enum Priority {
	NEUTRAL,
	LOW,
	MEDIUM,
	HIGH;
	
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
