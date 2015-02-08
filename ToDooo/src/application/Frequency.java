package application;
public enum Frequency {
	NIL,
	WEEKLY,
	MONTHLY,
	YEARLY;
	
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
