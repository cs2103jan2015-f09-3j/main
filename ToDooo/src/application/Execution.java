package application;

import java.io.IOException;
import java.util.ArrayList;
import javafx.util.Pair;
import controller.HeaderController;
import controller.MainController;

public class Execution {
	public static MainController mainController;
	public static HeaderController headerController;
	
	//@author A0112498B
	// -----------------------------------------------------------------------------------------------
	// Public methods
	// -----------------------------------------------------------------------------------------------
	public static String executeUserInput(String userInput) {
		String systemMsg = "";
		Command commandType = InputParser.getActionFromString(userInput);
		systemMsg = executeCommand(userInput, commandType);
		
		return systemMsg;
	}
	
	public static void executeCleanCompletedTasks() {
		ArrayList<Task> taskList = Main.list.getTasks();
		Task task;
		String taskId;
		String taskStatus;
	
		for(int i = 0; i < taskList.size(); i++) {
			task = taskList.get(i);
			taskId = task.getId();
			taskStatus = task.getStatus().toString();
			
			if(taskStatus.equalsIgnoreCase(TaskStatus.COMPLETED.toString())) {
				Main.list.deleteTaskById(taskId);
			}
		}
	}

	// -----------------------------------------------------------------------------------------------
	// Private methods
	// -----------------------------------------------------------------------------------------------
	private static String executeCommand(String userInput, Command commandType) {
		String systemMsg = null;

		userInput = InputParser.removeLineBreaks(userInput);

		switch (commandType) {
		case ADD :
			systemMsg = Execution.executeAdd(userInput);
			headerController.textArea.clear();
			break;
		case UPDATE :
			systemMsg = Execution.executeUpdate(userInput);
			Main.shouldResetCaret = true;
			break;
		case DELETE :
			systemMsg = Execution.executeDelete(userInput);
			headerController.textArea.clear();
			break;
		case SEARCH :
			systemMsg = Execution.executeSearch(userInput);
			displaySearchResult(systemMsg);
			headerController.textArea.clear();
			break;
		case COMPLETE :
			systemMsg = Execution.executeComplete(userInput);
			headerController.textArea.clear();
			break;
		case UNCOMPLETE :
			systemMsg = Execution.executeUncomplete(userInput);
			headerController.textArea.clear();
			break;
		case VIEW :
			systemMsg = Execution.executeView(userInput);
			displayDetail(systemMsg);
			headerController.textArea.clear();
			break ;
		default:
			// invalid command
			break;
		}

		return systemMsg;
	}
	
	private static String executeAdd(String userInput) {
		Task task = new Task(userInput);	
		
		String systemMsg = null;
		if (task.getIsValid()) {
			systemMsg = Main.list.addTaskToList(task);
			
			if (systemMsg.equals(Constant.MSG_ADD_SUCCESS)) {
				Undo.prepareUndoAdd(task);
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
			Undo.prepareUndoDelete(removedTask);
			
			systemMsg = Constant.MSG_DELETE_SUCCESS.
						replace(Constant.DELIMITER_REPLACE, 
								removedTask.getId());
		} else {
			systemMsg = Constant.MSG_ITEM_NOT_FOUND;
		}
		
		return systemMsg;
	}
	
	private static String executeRetrieveOriginalText(String userInput) {
		
		String systemMsg = null;
		String targetId = InputParser.getTargetIdFromString(userInput);
		Task originalTask = Main.list.getTaskById(targetId);
				
		if (originalTask != null) {		
			headerController.textArea.
			appendText(Constant.DELIMITER_UPDATE + " " + 
					   originalTask.getOriginalText());						
			
			Main.toUpdate = true;
			systemMsg = Constant.MSG_ORIGINAL_RETRIEVED.
						replace(Constant.DELIMITER_REPLACE, targetId);
		} else {
			systemMsg = Constant.MSG_ORIGINAL_NOT_RETRIEVED;
		}
		
		return systemMsg;
	}
	
	private static String executeUpdate(String userInput) {
		String systemMsg = null;
		
		if (Command.shouldRetrieveOriginalInput(userInput)) {
			systemMsg = executeRetrieveOriginalText(userInput);
		} else {
			Pair<Task, String> updatedTasksDetails = 
					Main.list.updateTaskOnList(userInput);
			
			if (updatedTasksDetails == null) {
				return systemMsg = Main.systemFeedback;
			}
			
			Task originalTask = updatedTasksDetails.getKey();
			String targetId = updatedTasksDetails.getValue();
			
			if (originalTask != null) {
				Undo.prepareUndoUpdate(originalTask, targetId);
				
				systemMsg = Constant.MSG_UPDATE_SUCCESS;
				headerController.textArea.clear();
			} else {
				systemMsg = Constant.MSG_UPDATE_FAIL;
			}
		}
		
		return systemMsg;
	}

	private static String executeSearch(String userInput) {
		String systemMsg = null;
		
		Pair<ArrayList<Task>, String> searchResultsPair = 
				Main.list.searchTheList(userInput);
		Main.searchResults = searchResultsPair.getKey();
		systemMsg = searchResultsPair.getValue();		
		
		if (Main.searchResults.isEmpty() && systemMsg == null) {
			systemMsg = Constant.MSG_NO_RESULTS;
		} else if (systemMsg != null) {
			systemMsg = Constant.MSG_SEARCH_INVALID;
		} else {
			systemMsg = Constant.MSG_SEARCH_SUCCESS.
						replace(Constant.DELIMITER_REPLACE, 
								String.valueOf(Main.searchResults.size()));
		}
		
		return systemMsg;
	}
	
	//@author A0112856E
	private static String executeComplete(String userInput) {
		String systemMsg = null;
		
		Pair<Task, String> toCompleteTask = Main.list.completeTaskOnList(userInput);
		
		Task completedTask = toCompleteTask.getKey();
		String targetId = toCompleteTask.getValue();
		
		if (completedTask != null) {
			Undo.prepareUndoComplete(completedTask, targetId);
			
			systemMsg = Constant.MSG_COMPLETE_SUCCESS.
					replace(Constant.DELIMITER_REPLACE, targetId);
		} else {
			systemMsg = Constant.MSG_ITEM_NOT_FOUND;
		}

		return systemMsg;
	}
	
	private static String executeUncomplete(String userInput) {
		String systemMsg = null;
		
		Pair<Task, String> toUncompleteTask = Main.list.uncompleteTaskOnList(userInput);
		
		Task uncompletedTask = toUncompleteTask.getKey();
		String targetId = toUncompleteTask.getValue();
		
		if (uncompletedTask != null) {
			Undo.prepareUndoComplete(uncompletedTask, targetId);
			
			systemMsg = Constant.MSG_UNCOMPLETE_SUCCESS.
					replace(Constant.DELIMITER_REPLACE, targetId);
		} else {
			systemMsg = Constant.MSG_ITEM_NOT_FOUND;
		}

		return systemMsg;
	}
	
	//@author A0112537M
	private static String executeView(String userInput) {
		String systemMsg = null;
		Task selectedTask = Main.list.selectTaskFromList(userInput);
		
		if(selectedTask != null) {
			systemMsg = Constant.MSG_VIEW_SUCCESS;
		} else {
			systemMsg = Constant.MSG_VIEW_FAIL;
		}
		
		return systemMsg;
	}
	
	private static void displayDetail(String systemMsg) {
		if(systemMsg.equalsIgnoreCase(Constant.MSG_VIEW_SUCCESS)) {
			try {
				mainController.viewDetails(Main.list.getSelectedTask());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void displaySearchResult(String systemMsg) {
		if(systemMsg.contains(Constant.SYS_MSG_KEYWORD_SEARCH)) {
			mainController.executeSearchResult();
		}
	}
}
