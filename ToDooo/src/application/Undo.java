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
	
	private String undoAction() {
		String systemMsg = null;
		
		switch (_undoCommand) {
			case ADD :
				// to undo delete action
				// add _originalTask
				break;
			case DELETE :
				// to undo add action
				// delete using _targetId
				break;
			case UPDATE :
				// to undo update action
				// update using _originalTask and _targetId
				break;
			default:
				// no undo command
				// return null
				break;
		}
		
		return systemMsg;
	}
}

