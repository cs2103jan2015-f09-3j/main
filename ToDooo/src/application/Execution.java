package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import javafx.util.Pair;
import controller.HeaderController;
import controller.MainController;
import controller.SettingController;

public class Execution {
	public static MainController mainController;
	public static HeaderController headerController;
	public static SettingController settingController;
	
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
		ArrayList<RecurringTask> recurringList;
		Task task;
		RecurringTask recurringTask;
		TaskStatus taskStatus;
		String taskId;
		String recurringId;
		int restartIndex = -1;
		boolean isRecurring;
	
		for(int i = 0; i < taskList.size(); i++) {
			task = taskList.get(i);
			taskId = task.getId();
			taskStatus = task.getStatus();
			isRecurring = task.getIsRecurring();
			
			if(isRecurring == false) {
				if(taskStatus.equals(TaskStatus.COMPLETED)) {
					Main.list.deleteTaskById(taskId);
					i = restartIndex;
				}
			} else {
				recurringList = task.getRecurringTasks();
				for(int j = 0; j < recurringList.size(); j++) {
					recurringTask = recurringList.get(j);
					if(recurringTask.getStatus().equals(TaskStatus.COMPLETED)) {
						recurringId = taskId + "." + recurringTask.getRecurringTaskId();
						Main.list.deleteTaskById(recurringId);
					}
				}
			}
		}
	}
	
	public static String executeUndo() {
		String systemMsg = null;
		
		boolean canUndo = !(Main.undos.isEmpty());
		if (canUndo) {
			Undo undo = Main.undos.pop();
			systemMsg = undo.undoAction();
		} else {
			systemMsg = Constant.MSG_NO_UNDO;
		}
		
		return systemMsg;
	}
	
	public static String executeRedo() {
		String systemMsg = null;
		
		boolean canRedo = !(Main.redos.isEmpty());
		if (canRedo) {
			Undo redo = Main.redos.pop();
			systemMsg = redo.redoAction();
		} else {
			systemMsg = Constant.MSG_NO_REDO;
		}
		
		return systemMsg;
	}
	
	public static void executeSystemMsgTimerTask() {
		ExecutionTimer.SystemMsgTimerTask systemMsgTimerTask = 
				new ExecutionTimer.SystemMsgTimerTask();
		
		Timer timer = systemMsgTimerTask.getTimer();
		timer.schedule(systemMsgTimerTask, Constant.TIMER_SYSTEM_MSG_DURATION);
	}
	
	public static void executeStatusCheckTimerTask() {
		ExecutionTimer.StatusCheckTimerTask statusCheckTimerTask = 
				new ExecutionTimer.StatusCheckTimerTask();
		
		Timer timer = statusCheckTimerTask.getTimer();
		timer.scheduleAtFixedRate(statusCheckTimerTask, 0, 
				Constant.TIMER_UPDATE_STATUS_DURATION);
	}
	
	//@author A0112537M
	public static void executeSysMsgTimerForSavePath() {
		ExecutionTimer.SystemMsgTimerSavePath sysMsgTimerSavePath = 
				new ExecutionTimer.SystemMsgTimerSavePath();
		
		Timer timer = sysMsgTimerSavePath.getTimer();
		timer.schedule(sysMsgTimerSavePath, Constant.TIMER_SYSTEM_MSG_DURATION);
	}
	
	public static void executeSysMsgTimerForClean() {
		ExecutionTimer.SystemMsgTimerCleanSetting systemMsgTimerCleanSetting =
				new ExecutionTimer.SystemMsgTimerCleanSetting();
		
		Timer timer = systemMsgTimerCleanSetting.getTimer();
		timer.schedule(systemMsgTimerCleanSetting, Constant.TIMER_SYSTEM_MSG_DURATION);
	}
	
	public static void executeClearSavePathMsg() {
		settingController.lblSysMsgSettingA.setText(Constant.EMPTY_STRING);	
	}
	
	public static void executeClearCleanSettingMsg() {
		settingController.lblSysMsgSettingB.setText(Constant.EMPTY_STRING);	
	}
	
	//@author A0112498B
	public static void executeUpdateStatus() {
		Main.list.checkAndUpdateStatus();
		mainController.loadListsInTabs();
	}
	
	public static void executeClearSystemMsg() {
		headerController.lblSysMsg.setText(Constant.EMPTY_STRING);	
    	headerController.imgSysMsg.setImage(null);
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
		Pair<Task, String> deleteDetailsPair = 
				Main.list.deleteTaskFromList(userInput);
		Task removedTask = deleteDetailsPair.getKey();
		String removedTaskId = deleteDetailsPair.getValue();
		
		if (removedTask != null) {
			Undo.prepareUndoDelete(removedTask, removedTaskId);
			
			systemMsg = Constant.MSG_DELETE_SUCCESS;
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
			systemMsg = Constant.MSG_ORIGINAL_RETRIEVED;
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
			
			// ----------------------------------------------
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
		Pair<Task, String> completedTaskDetails = 
				Main.list.completeTaskOnList(userInput);
		
		if(completedTaskDetails == null) {
			systemMsg = Constant.MSG_ITEM_NOT_FOUND;
		} else {
			Task originalTask = completedTaskDetails.getKey();
			String targetId = completedTaskDetails.getValue();
			
			if (originalTask != null && targetId != null) {
				Undo.prepareUndoComplete(originalTask, targetId);
				
				systemMsg = Constant.MSG_COMPLETE_SUCCESS.
							replace(Constant.DELIMITER_REPLACE, targetId);
			} else {
				systemMsg = Constant.MSG_ITEM_NOT_FOUND;
			}			
		}
	
		return systemMsg;
	}
		
	private static String executeUncomplete(String userInput) {
		String systemMsg = null;
		Pair<Task, String> completedTaskDetails = 
				Main.list.uncompleteTaskOnList(userInput);
		
		if(completedTaskDetails == null) {
			systemMsg = Constant.MSG_ITEM_NOT_FOUND;
		} else {
			Task originalTask = completedTaskDetails.getKey();
			String targetId = completedTaskDetails.getValue();
			
			if (originalTask != null && targetId != null) {
				Undo.prepareUndoComplete(originalTask, targetId);
				
				systemMsg = Constant.MSG_UNCOMPLETE_SUCCESS.
							replace(Constant.DELIMITER_REPLACE, targetId);
			} else {
				systemMsg = Constant.MSG_ITEM_NOT_FOUND;
			}			
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
