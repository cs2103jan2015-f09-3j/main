package application;

public enum Undo {
	ADD(Command.DELETE),
	DELETE(Command.ADD),
	UPDATE(Command.UPDATE);
	
	private final Command _UNDO_COMMAND;
	
	private Undo(Command undoCommand) {
		_UNDO_COMMAND = undoCommand;
	}
	
	public Command getUndoCommand() {
		return _UNDO_COMMAND;
	}
}
