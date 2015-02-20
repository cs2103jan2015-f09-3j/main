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
		return (commandLine.contains(Command.ON.getBasicCommand()) ||
				commandLine.contains(Command.ON.getAdvancedCommand()));
	}

	public static boolean isTimedTask(String commandLine) {
		return ((commandLine.contains(Command.FROM.getBasicCommand()) ||
				 commandLine.contains(Command.FROM.getAdvancedCommand())) &&
				(commandLine.contains(Command.TO.getBasicCommand()) ||
				 commandLine.contains(Command.TO.getAdvancedCommand())));
	}
	
	public static boolean isDatedTask(String commandLine) {
		return (commandLine.contains(Command.BY.getBasicCommand()) ||
				commandLine.contains(Command.BY.getAdvancedCommand()));
	}
	
	public static boolean isFloatingTask(String commandLine) {
		return (!(isEvent(commandLine) && isTimedTask(commandLine) &&
				 isDatedTask(commandLine)));
	}
}
