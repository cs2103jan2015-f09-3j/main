package application;

import java.io.IOException;
import java.util.Date;

import controller.HeaderController;
import controller.MainController;
import javafx.util.Pair;

public enum Command {
	ADD("add", "-a"),
	DELETE("delete", "-d"),
	UPDATE("update", "-u"),
	SEARCH("search", "-s"),
	FROM("/from", "-f"),
	TO("/to", "-t"),
	ON("/on", "-o"),
	BY("/by", "-b"),
	CATEGORY("//", "//"),
	PRIORITY_HIGH("/high", "/***"),
	PRIORITY_MEDIUM("/medium", "/**"),
	PRIORITY_LOW("/low", "/*"),
	RECURRING_WEEKLY("/weekly", "-w"),
	RECURRING_MONTHLY("/monthly", "-m"),
	RECURRING_YEARLY("/yearly", "-y"),
	RECURRING_UNTIL("/until", "-u"),
	SETTING("/setting", "/setting"),
	GO_BACK("/~", "/~");
	
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
		for (Command command : Constant.COMMAND_ACTIONS) {
			
			if (command.hasActionCommand(commandLine)) {				
				return command;
			}
		}		
		return Command.ADD;
	}

	public static Command verifyPriorityCommands(String commandLine) {
		for (Command command : Constant.COMMAND_PRIORITIES) {
			if (command.hasCommand(commandLine)) {
				return command;
			}
		}
		
		return null;
	}

	public boolean hasCommand(String commandLine) {
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = _COMMAND_BASIC + " ";
		String advancedCmd = _COMMAND_ADVANCED + " ";
				
		
		return (lowerCase.contains(basicCmd) ||
				lowerCase.contains(advancedCmd));
	}
	
	public static Command verifyRecurringCommands(String commandLine) {				
		for (Command recurringCommand : Constant.COMMAND_RECURRING) {
			if (recurringCommand.hasValidRecurringCommand(commandLine)) {
				return recurringCommand;
			}
		}
		
		return null;
	}
	
	public boolean hasValidRecurringCommand(String commandLine) {
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = _COMMAND_BASIC + " " + 
						  Command.RECURRING_UNTIL.getBasicCommand() + " ";
		String advancedCmd = _COMMAND_ADVANCED + " " + 
						  	 Command.RECURRING_UNTIL.getAdvancedCommand() + " ";
		int beginIndex = -1;
		int endIndex = lowerCase.length();
		
		if (lowerCase.contains(basicCmd)) {
			beginIndex = lowerCase.indexOf(basicCmd);
		} else if (lowerCase.contains(advancedCmd)) {
			beginIndex = lowerCase.indexOf(advancedCmd);
		}
				
		boolean hasRecurringCommand = (beginIndex != -1);
		if (hasRecurringCommand) {
			lowerCase = lowerCase.substring(beginIndex, endIndex);
			
			Date untilDate = Main.inputParser.getDateFromString(lowerCase);
			if (untilDate != null) {
				hasRecurringCommand = true;
			} else {
				hasRecurringCommand = false;
			}
		}
		
		return hasRecurringCommand;
	}
	
	public static boolean hasRecurringCommands(String commandLine) {			
		boolean hasFound = false;
		
		for (Command recurringCommand : Constant.COMMAND_RECURRING) {
			if (recurringCommand.hasCommand(commandLine)) {
				hasFound = true;
			}
		}
		
		return hasFound;
	}
		
	
	public static boolean hasCategoryCommand(String commandLine) {
		String lowerCase = commandLine.toLowerCase() + " ";
		String basicCmd = Command.CATEGORY.getBasicCommand();
		boolean isCategorised = false;
		int beginIndex = -1;
		int endIndex = -1;
		
		if (lowerCase.contains(basicCmd)) {
			beginIndex = lowerCase.indexOf(basicCmd);
			endIndex = beginIndex + basicCmd.length();
			
			if (lowerCase.charAt(endIndex) != ' ') {
				isCategorised = true;
			}
		}
				
		return isCategorised;
	}
	
	public boolean hasActionCommand(String commandLine) {
		int startIndex = 0;
		String lowerCase = commandLine.toLowerCase();
		String basicCmd = _COMMAND_BASIC + " ";
		String advancedCmd = _COMMAND_ADVANCED + " ";
		
		return (lowerCase.indexOf(basicCmd) == startIndex ||
			    lowerCase.indexOf(advancedCmd) == startIndex);
	}
	
	public static boolean isValidNumOfDateCommands(String commandLine) {
		int count = 0;
		boolean isCorrectNum = false;
		boolean isValidTimed = true;
		
		if (TaskType.isFloatingTask(commandLine)) {
			isCorrectNum = true;
			
			return isCorrectNum;
		}
		
		for (Command command : Constant.COMMAND_DATES) {
			if (command.hasCommand(commandLine)) {
				isValidTimed = !(((command.name().equals(Command.FROM) ||
						 		command.name().equals(Command.TO)) && count >= 2) ||
						 		(!(command.name().equals(Command.FROM) ||
								command.name().equals(Command.TO)) && count >= 1));
				
				count++;
			}
		}
		
		if (count == 1 || (count == 2 && isValidTimed)) {
			isCorrectNum = true;
		}
		
		return isCorrectNum;
	}
	
	public static String executeUserInput(String userInput, HeaderController headerController, 
			  							  MainController mainController) {
		String systemMsg = "";
		Command commandType = InputParser.getActionFromString(userInput);	

		if (Main.toUpdate && commandType.equals(Command.UPDATE)) {
			userInput = InputParser.removeLineBreaks(userInput);
			systemMsg = executeUpdate(userInput, headerController);
			
			Main.toUpdate = false;
			headerController.textArea.clear();
		} else {
			systemMsg = commandType.executeCommand(userInput, headerController, mainController);
		}
		
		return systemMsg;
	}
		
	private String executeCommand(String userInput, HeaderController headerController, 
								  MainController mainController) {
		String systemMsg = null;
		try {			
			userInput = InputParser.removeLineBreaks(userInput);
			
			switch (this) {
				case ADD :
					systemMsg = Command.executeAdd(userInput);
					headerController.textArea.clear();
					break;
				case UPDATE :
					systemMsg = Command.executeRetrieveOriginalText(userInput, headerController);	
					Main.shouldResetCaret = true;
					break;
				case DELETE :
					systemMsg = Command.executeDelete(userInput);
					headerController.textArea.clear();
					break;
				case SEARCH :
					systemMsg = Command.executeSearch(userInput);
					mainController.executeSearchResult();
					headerController.textArea.clear();
					break;
				case SETTING :
					mainController.executeSetting();
					headerController.textArea.clear();
					break;
				case GO_BACK :
					mainController.executeGoBack();
					headerController.textArea.clear();
					break;
				default :
					// invalid command
					break;
			}
		} catch (IOException exception) {
			
		}
		
		return systemMsg;
	}
	
	private static String executeAdd(String userInput) {
		Task task = new Task(userInput);	
		
		String systemMsg = null;
		if (task.getIsValid()) {
			systemMsg = Main.list.addTaskToList(task);
			
			if (systemMsg.equals(Constant.MSG_ADD_SUCCESS)) {
				Undo undo = new Undo(Command.ADD, task.getId());
				Main.undos.push(undo);				
				Main.redos.clear();
			}
			
		} else {
			systemMsg = Main.systemFeedback;
		}
				
		return systemMsg;
	}
	
	private static String executeDelete(String userInput) {
		String systemMsg= null;
		Task removedTask = Main.list.deleteTaskFromList(userInput);
		
		if (removedTask != null) {
			Undo undo = new Undo(Command.DELETE, removedTask);
			Main.undos.push(undo);			
			Main.redos.clear();
			
			systemMsg = Constant.MSG_DELETE_SUCCESS;
		} else {
			systemMsg = Constant.MSG_ITEM_NOT_FOUND;
		}
		
		return systemMsg;
	}
	
	private static String executeRetrieveOriginalText(String userInput, HeaderController headerController) {
		String systemMsg = null;
		String targetId = InputParser.getTargetIdFromString(userInput);
		Task originalTask = Main.list.getTaskById(targetId);
				
		if (originalTask != null) {		
			headerController.textArea.
			appendText(Constant.DELIMETER_UPDATE + " " + 
					   originalTask.getOriginalText());						
			
			Main.toUpdate = true;
			systemMsg = Constant.MSG_ORIGINAL_RETRIEVED;
		} else {
			systemMsg = Constant.MSG_ORIGINAL_NOT_RETRIEVED;
		}
		
		return systemMsg;
	}
	
	private static String executeUpdate(String userInput, HeaderController headerController) {
		String systemMsg = null;
		
		if (userInput.indexOf(Constant.DELIMETER_UPDATE) == -1) {
			systemMsg = executeRetrieveOriginalText(userInput, headerController);
		} else {
			Pair<Task, String> updatedTasksDetails = Main.list.updateTaskOnList(userInput);
			if (updatedTasksDetails == null) {
				return systemMsg = Main.systemFeedback;
			}
			
			Task originalTask = updatedTasksDetails.getKey();
			String targetId = updatedTasksDetails.getValue();
			
			if (originalTask != null) {
				Undo undo = new Undo(Command.UPDATE, originalTask, targetId);
				Main.undos.push(undo);
				Main.redos.clear();
				
				systemMsg = Constant.MSG_UPDATE_SUCCESS;
			} else {
				systemMsg = Constant.MSG_UPDATE_FAIL;
			}
		}
		
		return systemMsg;
	}
	
	private static String executeSearch(String userInput) {
		String systemMsg = null;
		
		Main.searchResults = Main.list.searchTheList(userInput);
		if (Main.searchResults.isEmpty()) {
			systemMsg = Constant.MSG_NO_RESULTS;
		} else {
			systemMsg = Constant.MSG_SEARCH_SUCCESS.
						replace(Constant.DELIMETER_SEARCH, 
								String.valueOf(Main.searchResults.size()));
		}
		
		return systemMsg;
	}
	
}
