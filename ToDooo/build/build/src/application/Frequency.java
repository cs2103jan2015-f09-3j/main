//@author A0112498B
package application;
public enum Frequency {
	NIL(null),
	WEEKLY(Command.RECURRING_WEEKLY),
	MONTHLY(Command.RECURRING_MONTHLY),
	YEARLY(Command.RECURRING_YEARLY);
	
	private final Command _COMMAND;

	// -----------------------------------------------------------------------------------------------
	// Constructor
	// -----------------------------------------------------------------------------------------------			
	private Frequency(Command command) {
		_COMMAND = command;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Get methods
	// -----------------------------------------------------------------------------------------------
	public Command getCommand() {
		return _COMMAND;
	}

	// -----------------------------------------------------------------------------------------------
	// Public methods
	// -----------------------------------------------------------------------------------------------		
	public static Frequency getFrequency(Command recurringCommand) {
		boolean hasFound = false;
		Frequency matchedFrequency = null;
		
		for (Frequency frequency : Frequency.values()) {
			Command frequencyCommand = frequency.getCommand();
			if (frequencyCommand == null) {
				continue;
			}
			
			hasFound = (frequencyCommand.equals(recurringCommand));
			if (hasFound) {
				matchedFrequency = frequency;
				break;
			}
		}
		
		return matchedFrequency;
	}
	
	public static boolean isWeekly(String commandLine) {		
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = Command.RECURRING_WEEKLY.getBasicCommand() + " ";
		String advancedCmd = Command.RECURRING_WEEKLY.getAdvancedCommand() + " ";
		
		return (lowerCase.contains(basicCmd) ||
				lowerCase.contains(advancedCmd));
	}
	
	public static boolean isMonthly(String commandLine) {
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = Command.RECURRING_MONTHLY.getBasicCommand() + " ";
		String advancedCmd = Command.RECURRING_MONTHLY.getAdvancedCommand() + " ";
		
		return (lowerCase.contains(basicCmd) ||
				lowerCase.contains(advancedCmd));
	}
	
	public static boolean isYearly(String commandLine) {
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = Command.RECURRING_YEARLY.getBasicCommand() + " ";
		String advancedCmd = Command.RECURRING_YEARLY.getAdvancedCommand() + " ";
		
		return (lowerCase.contains(basicCmd) ||
				lowerCase.contains(advancedCmd));
	}
}
