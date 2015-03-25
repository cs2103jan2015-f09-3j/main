package application;

import java.text.SimpleDateFormat;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;

public final class Constant {
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
	public static final String TAG_REPEAT_DAY = "repeatDay";
	public static final String TAG_REPEAT_UNTIL = "repeatUntil";
	public static final String TAG_PRIORITY = "priority";
	public static final String TAG_STATUS = "status";
	public static final String TAG_END_DATE = "endDate";
	
	public static final String TAG_RECURRING_TASKS = "recurringTasks";
	public static final String TAG_RECURRING_TASK = "recurringTask";
	public static final String TAG_RECURRING_ID = "recurringTaskId";
	public static final String TAG_RECURRING_STATUS = "recurringTaskStatus";
	public static final String TAG_RECURRING_DATE = "recurDate";
	
	public static final String TAG_SETTING = "setting";	
	public static final String TAG_SETTING_SAVE = "save";
	
	public static final String TAG_UNDO_COMMANDS = "commands";
	
	public static final String XML_TEXT_NIL = "NIL";
	public static final String XML_OUTPUT_INDENT = "yes";
	public static final String XML_OUTPUT_INDENT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
	public static final String XML_OUTPUT_INDENT_AMOUNT = "2";
	public static final String XML_WHITESPACE_NODE_XPATH = "//text()[normalize-space(.)='']";
	
	public static final Command[] COMMAND_ACTIONS = { Command.ADD, Command.DELETE, 
													  Command.UPDATE, Command.SEARCH,
													  Command.SETTING, Command.GO_BACK };
	
	public static final Command[] COMMAND_DATES = { Command.ON, Command.BY, 
													Command.FROM, Command.TO };
	
	public static final Command[] COMMAND_PRIORITIES = { Command.PRIORITY_HIGH, Command.PRIORITY_MEDIUM,
														 Command.PRIORITY_LOW };
	
	public static final Command[] COMMAND_RECURRING = { Command.RECURRING_MONTHLY, Command.RECURRING_WEEKLY,
														Command.RECURRING_YEARLY };
	
	public static final String PATH_SETTING = "setting.xml";
	public static final String PATH_FILE_NAME = "listFile.xml";
	public static final String PATH_UNDO = "undo.xml";
	public static final String PATH_GET_PROPERTY = "user.dir";
	
	public static final String MSG_SAVE_FAIL = "File not saved.";
	public static final String MSG_SAVE_SUCCESS = "File saved.";
	
	public static final String MSG_ADD_SUCCESS = "Add successful.";
	public static final String MSG_ADD_FAIL = "Add failed.";
	public static final String MSG_DELETE_SUCCESS = "Delete successful.";
	public static final String MSG_DELETE_FAIL = "Delete failed.";
	public static final String MSG_ITEM_NOT_FOUND = "Item not found.";
	public static final String MSG_ORIGINAL_RETRIEVED = "Original input retrieved.";
	public static final String MSG_ORIGINAL_NOT_RETRIEVED = "Original input cannot be retrieved.";
	public static final String MSG_UPDATE_FAIL = "Update failed.";
	public static final String MSG_UPDATE_SUCCESS = "Update successful.";
	public static final String MSG_INVALID_FORMAT = "Invalid format. Unable to extract dates.";
    public static final String MSG_NO_UNTIL_DATE = "Please follow the following format: e.g. /weekly /until 2 November 2015";
	public static final String MSG_INVALID_RECURRING = "Unable to create recurring task. " + 
													   "Only /on and /by tasks can " + 
													   "become recurring tasks.";
	
	public static final String MSG_UNDO_ADD_SUCCESS = "Undo add successful.";
	public static final String MSG_UNDO_ADD_FAIL = "Undo add failed.";
	public static final String MSG_UNDO_DELETE_SUCCESS = "Undo delete successful.";
	public static final String MSG_UNDO_DELETE_FAIL = "Undo delete failed.";
	public static final String MSG_UNDO_UPDATE_SUCCESS = "Undo update successful.";
	public static final String MSG_UNDO_UPDATE_FAIL = "Undo update failed.";
		
	public static final String MSG_REDO_ADD_SUCCESS = "Redo add successful.";
	public static final String MSG_REDO_ADD_FAIL = "Redo add failed.";
	public static final String MSG_REDO_DELETE_SUCCESS = "Redo delete successful.";
	public static final String MSG_REDO_DELETE_FAIL = "Redo delete failed.";
	public static final String MSG_REDO_UPDATE_SUCCESS = "Redo update successful.";
	public static final String MSG_REDO_UPDATE_FAIL = "Redo update failed.";
	
	public static final String MSG_NO_UNDO = "There is no undo action to execute.";
	public static final String MSG_NO_REDO = "There is no redo action to execute.";
	
	public static final String MSG_NO_RESULTS = "No match found.";
	public static final String MSG_SEARCH_SUCCESS = "{0} matches found.";
	
	public static final String DELIMETER_UPDATE = ":";
	public static final String DELIMETER_SEARCH = ";; ";
	public static final String DELIMETER_REPLACE = "{0}";
	public static final String PREFIX_RECURRING_ID = "R";
	public static final String REGEX_LINE_BREAK = "(\\r|\\n)";

	public static final String COLOUR_ERROR = "#FF0033";
	public static final String COLOUR_FEEDBACK = "#4771FF";
	public static final String COLOUR_SUCCESS = "#9ACC77";
	
	public static final SimpleDateFormat DATEOUTPUT = new SimpleDateFormat("dd MMMMMMMMM yyyy, EEEEEEEEE");
	public static final SimpleDateFormat TIMEOUTPUT = new SimpleDateFormat("hh:mm a");
	public static final SimpleDateFormat DATEOUTPUT_FOR_TIMEDTASK = new SimpleDateFormat("dd MMM yyyy");
	
	public static final KeyCombination SHORTCUT_UNDO = new KeyCodeCombination(KeyCode.Z, KeyCombination.ALT_DOWN);
	public static final KeyCombination SHORTCUT_REDO = new KeyCodeCombination(KeyCode.Y, KeyCombination.ALT_DOWN);
	public static final KeyCombination SHORTCUT_OPEN_FILE_DIALOG = new KeyCodeCombination(KeyCode.F10);
	public static final KeyCombination SHORTCUT_TAB_ALL = new KeyCodeCombination(KeyCode.F1);
	public static final KeyCombination SHORTCUT_TAB_CATEGORY = new KeyCodeCombination(KeyCode.F2);
	public static final KeyCombination SHORTCUT_TAB_PRIORITY = new KeyCodeCombination(KeyCode.F3);
	
    public static final String TAB_NAME_ALL = "all";  
    public static final String TAB_NAME_CATEGORY = "category";
    public static final String TAB_NAME_PRIORITY= "priority";
    public static final int TAB_INDEX_ALL = 0;
    public static final int TAB_INDEX_CATEGORY = 1;
    public static final int TAB_INDEX_PRIORITY = 2;
    
    public static final String VIEW_NAME_SEARCH_RESULT= "searchResult";
    public static final int TIMER_SYSTEM_MSG_DURATION = 2500;
    
    public static final String CSS_CLASS_ACTION_COMMANDS = "actionCommands";
    public static final String CSS_CLASS_DATE_COMMANDS = "dateCommands";
    public static final String CSS_CLASS_CATEGORY = "category";
    public static final String CSS_CLASS_PRIORITY = "priority";
    public static final String CSS_CLASS_RECURRING_COMMANDS = "recurringCommands";
    public static final String CSS_CLASS_SEARCH_ATTRIBUTES = "searchAttributes";
    
    public static final String TODAY_TITLE = "TODAY";
    public static final String OVERDUE_TITLE = "OVERDUE";
    
    public static final String EVENT_ICON = "images/eventIcon.png";
    public static final String FLOATING_ICON = "images/floatingIcon.png";
    public static final String TIMED_ICON = "images/timedIcon.png";
    public static final String DATED_ICON = "images/datedIcon.png";
    
    public static final Color HIGH_PRIORITY = Color.rgb(196, 1, 9);
    public static final Color MEDIUM_PRIORITY = Color.rgb(248, 135, 46);
    public static final Color LOW_PRIORITY = Color.rgb(249, 212, 35);
    public static final Color NEUTRAL_PRIORITY = Color.WHITE;
    
    public static final String STR_BEFORE_DATE_BY = "by";
    public static final String STR_BEFORE_DATE_ON = "on";
    public static final String STR_BEFORE_DATE_FROM = "from";
    public static final String STR_BEFORE_DATE_TO = "to";
    

}
