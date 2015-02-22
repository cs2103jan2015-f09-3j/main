package application;
public final class Constant {
	public static final int START_INDEX = 0;
	public static final int START_ID = 1;
	
	public static final String CATEGORY_UNCATEGORISED = "Uncategorised";
	
	public static final String TAG_FILE = "file";
	public static final String TAG_NEXT_ID = "nextid";
	public static final String TAG_CATEGORIES = "categories";
	public static final String TAG_CATEGORY = "category";
	public static final String TAG_TASKS = "tasks";
	public static final String TAG_TASK = "task";
	public static final String TAG_ATTRIBUTE_ID = "id";
	
	public static final String TAG_TYPE = "type";
	public static final String TAG_TODO = "todo";
	public static final String TAG_ORIGINAL = "original";
	public static final String TAG_ON = "on";
	public static final String TAG_FROM = "from";
	public static final String TAG_TO = "to";
	public static final String TAG_BY = "by";
	public static final String TAG_RECURRING = "recurring";
	public static final String TAG_REPEAT = "repeat";
	public static final String TAG_PRIORITY = "priority";
	
	public static final String TAG_SETTING = "setting";	
	public static final String TAG_SETTING_SAVE = "save";
	
	public static final String TAG_UNDO_COMMANDS = "commands";
	
	public static final String XML_TEXT_NIL = "NIL";
	public static final String XML_OUTPUT_INDENT = "yes";
	public static final String XML_OUTPUT_INDENT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
	public static final String XML_OUTPUT_INDENT_AMOUNT = "2";
	public static final String XML_WHITESPACE_NODE_XPATH = "//text()[normalize-space(.)='']";
	
	public static final Command[] ACTION_COMMANDS = { Command.ADD, Command.DELETE, 
													  Command.UPDATE, Command.SEARCH,
													  Command.SETTING };
	
	public static final String PATH_SETTING = "setting.xml";
	public static final String PATH_UNDO = "undo.xml";
	
	public static final String MSG_SAVE_FAIL = "File not saved.";
	public static final String MSG_SAVE_SUCCESS = "File saved.";
	
	public static final String MSG_ADD_FAIL = "Add successful.";
	public static final String MSG_ADD_SUCCESS = "Add failed.";
	public static final String MSG_DELETE_SUCCESS = "Delete successful.";
	public static final String MSG_DELETE_FAIL = "Delete failed.";
	public static final String MSG_ITEM_NOT_FOUND = "Item not found.";
	public static final String MSG_ORIGINAL_RETRIEVED = "Original input retrieved.";
	public static final String MSG_ORIGINAL_NOT_RETRIEVED = "Original input cannot be retrieved.";
	public static final String MSG_UPDATE_FAIL = "Update successful.";
	public static final String MSG_UPDATE_SUCCESS = "Update failed.";
	
	public static final String MSG_UNDO_ADD_SUCCESS = "Undo add successful.";
	public static final String MSG_UNDO_ADD_FAIL = "Undo add failed.";
	public static final String MSG_UNDO_DELETE_SUCCESS = "Undo delete successful.";
	public static final String MSG_UNDO_DELETE_FAIL = "Undo delete failed.";
	public static final String MSG_UNDO_UPDATE_SUCCESS = "Undo update successful.";
	public static final String MSG_UNDO_UPDATE_FAIL = "Undo update failed.";
	
	public static final String COMMAND_DELIMETER = ":";
}
