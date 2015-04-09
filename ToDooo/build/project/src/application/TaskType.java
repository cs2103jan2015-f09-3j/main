package application;
public enum TaskType {
	TIMED, 
	DATED, 
	FLOATING, 
	EVENT,
	INVALID;

	public static TaskType verifyTaskType(String commandLine) {
		if (isEvent(commandLine)) {
			return TaskType.EVENT;
		} else if (isTimedTask(commandLine)) {
			return TaskType.TIMED;
		} else if (isDatedTask(commandLine)) {
			return TaskType.DATED;
		} else if (isFloatingTask(commandLine)) {
			return TaskType.FLOATING;
		} else {
			return TaskType.INVALID;
		}
	}
	
	public static boolean isEvent(String commandLine) {
		String lowerCase = commandLine.toLowerCase();
		String basicCmd = Command.ON.getBasicCommand() + " ";
		String advancedCmd = Command.ON.getAdvancedCommand() + " ";
		
		return (lowerCase.contains(basicCmd) ||
				lowerCase.contains(advancedCmd));
	}

	public static boolean isTimedTask(String commandLine) {
		String lowerCase = commandLine.toLowerCase();
		String basicFromCmd = Command.FROM.getBasicCommand() + " ";
		String advancedFromCmd = Command.FROM.getAdvancedCommand() + " ";
		String basicToCmd = Command.TO.getBasicCommand() + " ";
		String advancedToCmd = Command.TO.getAdvancedCommand() + " ";
				
		return ((lowerCase.contains(basicFromCmd) ||
				 lowerCase.contains(advancedFromCmd)) &&
				(lowerCase.contains(basicToCmd) ||
				 lowerCase.contains(advancedToCmd)));
	}
	
	public static boolean isDatedTask(String commandLine) {
		String lowerCase = commandLine.toLowerCase();
		String basicCmd = Command.BY.getBasicCommand() + " ";
		String advancedCmd = Command.BY.getAdvancedCommand() + " ";
		
		return (lowerCase.contains(basicCmd) ||
				lowerCase.contains(advancedCmd));
	}
	
	public static boolean isFloatingTask(String commandLine) {
		return (!(isEvent(commandLine) && isTimedTask(commandLine) &&
				 isDatedTask(commandLine)));
	}
}
