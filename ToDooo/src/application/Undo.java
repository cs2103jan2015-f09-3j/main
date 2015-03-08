package application;

public class Undo {
	private Command _undoCommand;
	private Task _originalTask;
	private String _targetId;
	
	// to undo delete
	public Undo(Command toUndo, Task originalTask) {
		this(toUndo, originalTask, null);
	}
	
	// to undo add
	public Undo(Command toUndo, String targetId) {
		this(toUndo, null, targetId);
	}
	
	// to undo update
	public Undo(Command toUndo, Task originalTask, String targetId) {
		_undoCommand = getUndoCommand(toUndo);
		_originalTask = originalTask;
		_targetId = targetId;
	}
	
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
	
	public String undoAction() {
		String systemMsg = null;
		
		switch (_undoCommand) {
			case ADD :
				// to undo delete action
				// add _originalTask
				systemMsg = Main.list.AddTaskBackToList(_originalTask);				
				if (systemMsg.equals(Constant.MSG_ADD_SUCCESS)) {
					Undo redo = new Undo(Command.ADD, _originalTask.getId());
					Main.redos.push(redo);
					
					systemMsg = Constant.MSG_UNDO_DELETE_SUCCESS;
				} else {
					systemMsg = Constant.MSG_UNDO_DELETE_FAIL;
				}
				break;
			case DELETE :
				// to undo add action
				// delete using _targetId
				Task removedTask = Main.list.deleteTaskById(_targetId);
				
				if (removedTask != null) {
					Undo redo = new Undo(Command.DELETE, removedTask);
					Main.redos.push(redo);
					
					systemMsg = Constant.MSG_UNDO_ADD_SUCCESS;
				} else {
					systemMsg = Constant.MSG_UNDO_ADD_FAIL;
				}
				break;
			case UPDATE :
				// to undo update action
				// update using _originalTask and _targetId
				Task replacedTask = Main.list.replaceTaskOnList(_originalTask, _targetId);
				
				if (replacedTask != null) {
					Undo redo = new Undo(Command.UPDATE, replacedTask, _originalTask.getId());
					Main.redos.push(redo);
					
					systemMsg = Constant.MSG_UNDO_UPDATE_SUCCESS;
				} else {
					systemMsg = Constant.MSG_UNDO_UPDATE_FAIL;
				}
				break;
			default:
				// no undo command
				// return null
				break;
		}
		
		return systemMsg;
	}
	
	public String redoAction() {
		String systemMsg = null;
		
		switch (_undoCommand) {
			case ADD :
				// to redo add action
				// add _originalTask
				systemMsg = Main.list.AddTaskBackToList(_originalTask);				
				if (systemMsg.equals(Constant.MSG_ADD_SUCCESS)) {
					Undo undo = new Undo(Command.ADD, _originalTask.getId());
					Main.undos.push(undo);
					
					systemMsg = Constant.MSG_REDO_ADD_SUCCESS;
				} else {
					systemMsg = Constant.MSG_REDO_ADD_FAIL;
				}
				break;
			case DELETE :
				// to redo delete action
				// delete using _targetId
				Task removedTask = Main.list.deleteTaskById(_targetId);
				
				if (removedTask != null) {
					Undo undo = new Undo(Command.DELETE, removedTask);
					Main.undos.push(undo);
					
					systemMsg = Constant.MSG_REDO_DELETE_SUCCESS;
				} else {
					systemMsg = Constant.MSG_REDO_DELETE_FAIL;
				}
				break;
			case UPDATE :
				// to redo update action
				// update using _originalTask and _targetId
				Task replacedTask = Main.list.replaceTaskOnList(_originalTask, _targetId);
				
				if (replacedTask != null) {
					Undo undo = new Undo(Command.UPDATE, replacedTask, _originalTask.getId());
					Main.undos.push(undo);
					
					systemMsg = Constant.MSG_REDO_UPDATE_SUCCESS;
				} else {
					systemMsg = Constant.MSG_REDO_UPDATE_FAIL;
				}
				break;
			default:
				// no undo command
				// return null
				break;
		}
		
		return systemMsg;
	}
}

