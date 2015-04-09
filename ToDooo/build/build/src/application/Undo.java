package application;

import javafx.util.Pair;

public class Undo {
	private Command _undoCommand;
	private Task _originalTask;
	private String _targetId;
	
	// -----------------------------------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------------------------------
	// to undo delete
	public Undo(Command toUndo, Task originalTask) {
		this(toUndo, originalTask, null);
	}
	
	// to undo add
	public Undo(Command toUndo, String targetId) {
		this(toUndo, null, targetId);
	}
	
	// to undo update / complete
	public Undo(Command toUndo, Task originalTask, String targetId) {
		_undoCommand = getUndoCommand(toUndo);
		_originalTask = originalTask;
		_targetId = targetId;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Get methods
	// -----------------------------------------------------------------------------------------------
	private Command getUndoCommand(Command toUndo) {
		Command command = null;
		
		switch (toUndo) {
			case ADD :
				command = Command.DELETE;
				break;
			case DELETE :
				command = Command.ADD;
				break;
			case UPDATE :
				command = Command.UPDATE;
				break;
			default:
				// no undo command
				// return null
				break;
		}
		
		return command;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Undo-related methods
	// -----------------------------------------------------------------------------------------------
	public static void prepareUndoAdd(Task task) {
		Undo undo = new Undo(Command.ADD, task.getId());
		Main.undos.push(undo);				
		Main.redos.clear();
	}	
	
	public static void prepareUndoDelete(Task removedTask) {
		Undo undo = new Undo(Command.DELETE, removedTask);
		Main.undos.push(undo);			
		Main.redos.clear();
	}
	
	public static void prepareUndoUpdate(Task originalTask, String targetId) {
		Undo undo = new Undo(Command.UPDATE, originalTask, targetId);
		Main.undos.push(undo);
		Main.redos.clear();
	}
	
	public static void prepareUndoComplete(Task completedTask, String targetId) {
		Undo undo = new Undo(Command.COMPLETE, completedTask, targetId);
		Main.undos.push(undo);
		Main.redos.clear();
	}
	
	public String undoAction() {
		String systemMsg = null;
		
		switch (_undoCommand) {
			case ADD :
				// to undo delete action
				// add _originalTask
				systemMsg = undoDelete();
				break;
			case DELETE :
				// to undo add action
				// delete using _targetId
				systemMsg = undoAdd();
				break;
			case UPDATE :
			case COMPLETE:
				// to undo update action
				// update using _originalTask and _targetId
				systemMsg = undoUpdate();
				break;
			default:
				// no undo command
				// return null
				break;
		}
		
		return systemMsg;
	}

	private String undoUpdate() {
		String systemMsg;
		Task replacedTask = Main.list.replaceTaskOnList(_originalTask, _targetId);
		
		if (replacedTask != null) {
			Undo redo = new Undo(Command.UPDATE, replacedTask, _originalTask.getId());
			Main.redos.push(redo);
			
			systemMsg = Constant.MSG_UNDO_UPDATE_SUCCESS;
		} else {
			systemMsg = Constant.MSG_UNDO_UPDATE_FAIL;
		}
		return systemMsg;
	}

	private String undoAdd() {
		String systemMsg;
		Task removedTask = Main.list.deleteTaskById(_targetId);
		
		if (removedTask != null) {
			Undo redo = new Undo(Command.DELETE, removedTask);
			Main.redos.push(redo);
			
			systemMsg = Constant.MSG_UNDO_ADD_SUCCESS;
		} else {
			systemMsg = Constant.MSG_UNDO_ADD_FAIL;
		}
		return systemMsg;
	}

	private String undoDelete() {
		String systemMsg;
		Pair<String, Task> systemMsgWithRemovedTaskPair = 
						   Main.list.AddTaskBackToList(_originalTask, true);		
		
		systemMsg = systemMsgWithRemovedTaskPair.getKey();
		if (systemMsg.equals(Constant.MSG_ADD_SUCCESS)) {
			Task removedTask = systemMsgWithRemovedTaskPair.getValue();
			
			if (removedTask != null) {
				Undo redo = new Undo(Command.UPDATE, removedTask, _originalTask.getId());
				Main.redos.push(redo);
			} else {
				Undo redo = new Undo(Command.ADD, _originalTask.getId());
				Main.redos.push(redo);
			}
			
			systemMsg = Constant.MSG_UNDO_DELETE_SUCCESS;
		} else {
			systemMsg = Constant.MSG_UNDO_DELETE_FAIL;
		}
		return systemMsg;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Redo-related methods
	// -----------------------------------------------------------------------------------------------
	public String redoAction() {
		String systemMsg = null;
		
		switch (_undoCommand) {
			case ADD :
				// to redo add action
				// add _originalTask
				systemMsg = redoAdd();
				break;
			case DELETE :
				// to redo delete action
				// delete using _targetId
				systemMsg = redoDelete();
				break;
			case UPDATE :
			case COMPLETE:
				// to redo update action
				// update using _originalTask and _targetId
				systemMsg = redoUpdate();
				break;
			default:
				// no undo command
				// return null
				break;
		}
		
		return systemMsg;
	}

	private String redoUpdate() {
		String systemMsg;
		Task replacedTask = Main.list.replaceTaskOnList(_originalTask, _targetId);
		
		if (replacedTask != null) {
			Undo undo = new Undo(Command.UPDATE, replacedTask, _originalTask.getId());
			Main.undos.push(undo);
			
			systemMsg = Constant.MSG_REDO_UPDATE_SUCCESS;
		} else {
			systemMsg = Constant.MSG_REDO_UPDATE_FAIL;
		}
		return systemMsg;
	}

	private String redoDelete() {
		String systemMsg;
		Task removedTask = Main.list.deleteTaskById(_targetId);
		
		if (removedTask != null) {
			Undo undo = new Undo(Command.DELETE, removedTask);
			Main.undos.push(undo);
			
			systemMsg = Constant.MSG_REDO_DELETE_SUCCESS;
		} else {
			systemMsg = Constant.MSG_REDO_DELETE_FAIL;
		}
		return systemMsg;
	}

	private String redoAdd() {
		String systemMsg;
		Pair<String, Task> systemMsgWithRemovedTaskPair = 
		   				   Main.list.AddTaskBackToList(_originalTask, true);		

		systemMsg = systemMsgWithRemovedTaskPair.getKey();			
		if (systemMsg.equals(Constant.MSG_ADD_SUCCESS)) {
			Undo undo = new Undo(Command.ADD, _originalTask.getId());
			Main.undos.push(undo);
			
			systemMsg = Constant.MSG_REDO_ADD_SUCCESS;
		} else {
			systemMsg = Constant.MSG_REDO_ADD_FAIL;
		}
		return systemMsg;
	}
}

