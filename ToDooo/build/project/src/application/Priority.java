//@author A0112498B
package application;
public enum Priority {
	NEUTRAL(null),
	LOW(Command.PRIORITY_LOW),
	MEDIUM(Command.PRIORITY_MEDIUM),
	HIGH(Command.PRIORITY_HIGH);
	
	private final Command _COMMAND;

	// -----------------------------------------------------------------------------------------------
	// Constructor
	// -----------------------------------------------------------------------------------------------		
	private Priority(Command command) {
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
	public static boolean isLow(String commandLine) {
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = Command.PRIORITY_LOW.getBasicCommand() + " ";
		String advancedCmd = Command.PRIORITY_LOW.getAdvancedCommand() + " ";
		
		return (lowerCase.contains(basicCmd) ||
				lowerCase.contains(advancedCmd));
	}
	
	public static boolean isMedium(String commandLine) {
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = Command.PRIORITY_MEDIUM.getBasicCommand() + " ";
		String advancedCmd = Command.PRIORITY_MEDIUM.getAdvancedCommand() + " ";
		
		return (lowerCase.contains(basicCmd) ||
				lowerCase.contains(advancedCmd));
	}
	
	public static boolean isHigh(String commandLine) {
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = Command.PRIORITY_HIGH.getBasicCommand() + " ";
		String advancedCmd = Command.PRIORITY_HIGH.getAdvancedCommand() + " ";
		
		return (lowerCase.contains(basicCmd) ||
				lowerCase.contains(advancedCmd));
	}
}
