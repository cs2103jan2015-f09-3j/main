package application;
public enum Frequency {
	NIL(null),
	WEEKLY(Command.RECURRING_WEEKLY),
	MONTHLY(Command.RECURRING_MONTHLY),
	YEARLY(Command.RECURRING_YEARLY);
	
	private final Command _COMMAND;
	
	private Frequency(Command command) {
		_COMMAND = command;
	}
	
	public Command getCommand() {
		return _COMMAND;
	}

	public static boolean isWeekly(String commandLine) {
		return (commandLine.contains(Command.RECURRING_WEEKLY.getBasicCommand()) ||
				commandLine.contains(Command.RECURRING_WEEKLY.getAdvancedCommand()));
	}
	
	public static boolean isMonthly(String commandLine) {
		return (commandLine.contains(Command.RECURRING_MONTHLY.getBasicCommand()) ||
				commandLine.contains(Command.RECURRING_MONTHLY.getAdvancedCommand()));
	}
	
	public static boolean isYearly(String commandLine) {
		return (commandLine.contains(Command.RECURRING_YEARLY.getBasicCommand()) ||
				commandLine.contains(Command.RECURRING_YEARLY.getAdvancedCommand()));
	}
}
