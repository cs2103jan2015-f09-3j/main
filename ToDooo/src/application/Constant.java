package application;

import java.text.SimpleDateFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;

public final class Constant {
	public static final int START_ID = 1;	
	public static final String CATEGORY_UNCATEGORISED = "Uncategorised";
	
	// -----------------------------------------------------------------------------------------------
	// XML tags
	// -----------------------------------------------------------------------------------------------	
	// ---------------------------------------------------------
	// List-related details tags
	// ---------------------------------------------------------
	public static final String TAG_FILE = "file";
	public static final String TAG_NEXT_ID = "nextid";
	public static final String TAG_CATEGORIES = "categories";
	public static final String TAG_CATEGORY = "category";
	public static final String TAG_TASKS = "tasks";
	public static final String TAG_TASK = "task";
	public static final String TAG_ATTRIBUTE_ID = "id";
	
	// ---------------------------------------------------------
	// Task-related details tags
	// ---------------------------------------------------------
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
	
	// ---------------------------------------------------------
	// Recurring Task-related details tags
	// ---------------------------------------------------------
	public static final String TAG_RECURRING_TASKS = "recurringTasks";
	public static final String TAG_RECURRING_TASK = "recurringTask";
	public static final String TAG_RECURRING_ID = "recurringTaskId";
	public static final String TAG_RECURRING_STATUS = "recurringTaskStatus";
	public static final String TAG_RECURRING_DATE = "recurDate";
	
	// ---------------------------------------------------------
	// Setting-related details tags
	// ---------------------------------------------------------
	public static final String TAG_SETTING = "setting";	
	public static final String TAG_SETTING_SAVE = "save";
	public static final String TAG_SETTING_CLEAN = "clean";
	
	// -----------------------------------------------------------------------------------------------
	// XML configuration values
	// -----------------------------------------------------------------------------------------------
	public static final String XML_TEXT_NIL = "NIL";
	public static final String XML_OUTPUT_INDENT = "yes";
	public static final String XML_OUTPUT_INDENT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
	public static final String XML_OUTPUT_INDENT_AMOUNT = "2";
	public static final String XML_WHITESPACE_NODE_XPATH = "//text()[normalize-space(.)='']";
	
	// -----------------------------------------------------------------------------------------------
	// XML XPaths
	// -----------------------------------------------------------------------------------------------
	public static final String XML_XPATH_NEXT_ID = "/" + Constant.TAG_FILE + "/" + Constant.TAG_NEXT_ID;
	public static final String XML_XPATH_SETTING_SAVE = "/" + Constant.TAG_SETTING + "/" + Constant.TAG_SETTING_SAVE;
	
	public static final String XML_XPATH_TASKS = "/" + Constant.TAG_FILE + "/" + Constant.TAG_TASKS;	
	public static final String XML_XPATH_TASK = "/" + Constant.TAG_FILE + "/" + Constant.TAG_TASKS + "/" +
			   											Constant.TAG_TASK;
	
	public static final String XML_XPATH_CATEGORIES = "/" + Constant.TAG_FILE + "/" + Constant.TAG_CATEGORIES;	
	public static final String XML_XPATH_CATEGORY = "/" + Constant.TAG_FILE + "/" + Constant.TAG_CATEGORIES + "/" +
			   									    Constant.TAG_CATEGORY;	
	
	public static final String XML_XPATH_SETTING_CLEAN = "/" + Constant.TAG_SETTING + "/" + Constant.TAG_SETTING_CLEAN;
	
	// -----------------------------------------------------------------------------------------------
	// Command arrays
	// -----------------------------------------------------------------------------------------------
	public static final Command[] COMMAND_ACTIONS = { Command.ADD, Command.DELETE, 
													  Command.UPDATE, Command.SEARCH,
													  Command.COMPLETE, Command.UNCOMPLETE,
													  Command.VIEW };
	
	public static final Command[] COMMAND_DATES = { Command.ON, Command.BY, 
													Command.FROM, Command.TO };
	
	public static final Command[] COMMAND_PRIORITIES = { Command.PRIORITY_HIGH, Command.PRIORITY_MEDIUM,
														 Command.PRIORITY_LOW };
	
	public static final Command[] COMMAND_RECURRING = { Command.RECURRING_MONTHLY, Command.RECURRING_WEEKLY,
														Command.RECURRING_YEARLY };
	
	// -----------------------------------------------------------------------------------------------
	// File paths
	// -----------------------------------------------------------------------------------------------
	public static final String PATH_SETTING = "/setting.xml";
	public static final String PATH_FILE_NAME = "listFile.xml";
	public static final String PATH_DEFAULT = "/listFile.xml";
	public static final String PATH_GET_PROPERTY = "user.dir";
	public static final String PATH_DETAIL = "/view/Detail.fxml";
	public static final String PATH_MAIN = "/view/Main.fxml";
	public static final String PATH_CSS = "application.css";
	
	// -----------------------------------------------------------------------------------------------
	// System messages
	// -----------------------------------------------------------------------------------------------
	public static final String MSG_SAVE_FAIL = "File not saved.";
	public static final String MSG_SAVE_SUCCESS = "File saved.";
	
	public static final String MSG_TO_REDO = "Press alt + y to redo.";
	public static final String MSG_TO_UNDO = "Press alt + z to undo.";
			
	public static final String MSG_ADD_SUCCESS = "Add successful. " + Constant.MSG_TO_UNDO;
	public static final String MSG_ADD_FAIL = "Add failed.";
	public static final String MSG_DELETE_SUCCESS = "Delete successful. " + Constant.MSG_TO_UNDO;
	public static final String MSG_DELETE_FAIL = "Delete failed.";
	public static final String MSG_ITEM_NOT_FOUND = "Item not found.";
	public static final String MSG_ORIGINAL_RETRIEVED = "Original input retrieved.";
	public static final String MSG_ORIGINAL_NOT_RETRIEVED = "Original input cannot be retrieved.";
	public static final String MSG_UPDATE_FAIL = "Update failed.";
	public static final String MSG_UPDATE_SUCCESS = "Update successful. " + Constant.MSG_TO_UNDO;
	public static final String MSG_INVALID_FORMAT = "Invalid format. Unable to extract dates.";
    public static final String MSG_NO_UNTIL_DATE = "Please follow the following format: e.g. /weekly /until 2 November 2015";
    public static final String MSG_INVALID_UNTIL_DATE = "Invalid recurring end date";
	public static final String MSG_INVALID_RECURRING = "Unable to create recurring task. " + 
													   "Only /on and /by tasks can " + 
													   "become recurring tasks.";
		
	public static final String MSG_UNDO_ADD_SUCCESS = "Undo add successful. " + Constant.MSG_TO_REDO;
	public static final String MSG_UNDO_ADD_FAIL = "Undo add failed.";
	public static final String MSG_UNDO_DELETE_SUCCESS = "Undo delete successful. " + Constant.MSG_TO_REDO;
	public static final String MSG_UNDO_DELETE_FAIL = "Undo delete failed.";
	public static final String MSG_UNDO_UPDATE_SUCCESS = "Undo successful. " + Constant.MSG_TO_REDO;
	public static final String MSG_UNDO_UPDATE_FAIL = "Undo failed.";
	public static final String MSG_UNDO_COMPLETE_SUCCESS = "Undo complete successful. " + Constant.MSG_TO_REDO;
	public static final String MSG_UNDO_COMPLETE_FAIL = "Undo complete failed.";
	
	public static final String MSG_REDO_ADD_SUCCESS = "Redo add successful. " + Constant.MSG_TO_UNDO;
	public static final String MSG_REDO_ADD_FAIL = "Redo add failed.";
	public static final String MSG_REDO_DELETE_SUCCESS = "Redo delete successful. " + Constant.MSG_TO_UNDO;
	public static final String MSG_REDO_DELETE_FAIL = "Redo delete failed.";
	public static final String MSG_REDO_UPDATE_SUCCESS = "Redo successful. " + Constant.MSG_TO_UNDO;
	public static final String MSG_REDO_UPDATE_FAIL = "Redo failed.";
	public static final String MSG_REDO_COMPLETE_SUCCESS = "Redo complete successful. " + Constant.MSG_TO_UNDO;
	public static final String MSG_REDO_COMPLETE_FAIL = "Redo complete failed.";
	
	public static final String MSG_NO_UNDO = "There is no undo action to execute.";
	public static final String MSG_NO_REDO = "There is no redo action to execute.";
	
	public static final String MSG_NO_RESULTS = "No match found.";
	public static final String MSG_SEARCH_SUCCESS = "{0} matches found.";
	public static final String MSG_SEARCH_INVALID = "Invalid format. Please use the search attributes and separate with ;;<space>";
	
	public static final String MSG_COMPLETE_SUCCESS = "Successfully marked task as completed.";
	public static final String MSG_COMPLETE_FAIL = "Failed to mark task as completed.";
	public static final String MSG_COMPLETE_INVALID = "Task does not exist.";
	public static final String MSG_UNCOMPLETE_SUCCESS = "Successfully marked task as uncompleted.";
	public static final String MSG_UNCOMPLETE_FAIL = "Failed to mark task as uncompleted.";
	
	public static final String MSG_VIEW_SUCCESS = "Item is found.";
	public static final String MSG_VIEW_FAIL = "Item is not found.";
	
	public static final String MSG_MULTIPLE_DELETE_SUCCESS = "Deleted {0}.";
	public static final String MSG_MULTIPLE_DELETE_FAIL = "Failed to delete {0}";
	
	// -----------------------------------------------------------------------------------------------
	// System messages colors
	// -----------------------------------------------------------------------------------------------
	public static final Color COLOR_ERROR = Color.rgb(255, 0, 51);
	public static final Color COLOR_FEEDBACK = Color.rgb(71, 113, 255);
	public static final Color COLOR_SUCCESS = Color.rgb(135, 183, 99);    
    public static final Color COLOR_PRIORITY_HIGH = Color.rgb(196, 1, 9);
    public static final Color COLOR_PRIORITY_MEDIUM = Color.rgb(248, 135, 46);
    public static final Color COLOR_PRIORITY_LOW = Color.rgb(249, 212, 35);
    public static final Color COLOR_PRIORITY_NEUTRAL = Color.WHITE;
    
	// -----------------------------------------------------------------------------------------------
	// System message keywords
	// -----------------------------------------------------------------------------------------------
    public static final String[] SYS_MSG_KEYWORD_SUCCESS = { "Successfully", "successful", "saved" , "matches found", "is found" };
    public static final String[] SYS_MSG_KEYWORD_ERROR = { "failed", "Failed", "cannot" , "Invalid", "does not exist",
    														"Unable", "not saved", "not found" };
    public static final String SYS_MSG_KEYWORD_SEARCH = "matches found";
    public static final String SYS_MSG_KEYWORD_FILE_SAVED = "saved";
    public static final String SYS_MSG_KEYWORD_FILE_NOT_SAVED = "not saved";
    
    // -----------------------------------------------------------------------------------------------
 	// System message types
 	// -----------------------------------------------------------------------------------------------
    public static final int SYS_MSG_TYPE_SUCCESS = 1;
    public static final int SYS_MSG_TYPE_ERROR = -1;
    public static final int SYS_MSG_TYPE_FEEDBACK = 0;
	
	// -----------------------------------------------------------------------------------------------
	// Delimeters and symbols
	// -----------------------------------------------------------------------------------------------
	public static final String DELIMITER_UPDATE = ":";
	public static final String DELIMITER_SEARCH = ";; ";
	public static final String DELIMITER_REPLACE = "{0}";
	public static final String PREFIX_RECURRING_ID = ".";
	public static final String REGEX_LINE_BREAK = "(\\r|\\n)";
	public static final String REGEX_SPACE = "\\s+";
	
	// -----------------------------------------------------------------------------------------------
	// Date formats
	// -----------------------------------------------------------------------------------------------
	public static final SimpleDateFormat FORMAT_DATE_OUTPUT = new SimpleDateFormat("dd MMMMMMMMM yyyy, EEEEEEEEE");
	public static final SimpleDateFormat FORMAT_TIME_OUTPUT = new SimpleDateFormat("hh:mm a");
	public static final SimpleDateFormat FORMAT_DATE_TIME_OUTPUT = new SimpleDateFormat("dd MMM, hh:mm a");
	public static final SimpleDateFormat FORMAT_DATE_OUTPUT_FOR_TIMED_TASK = new SimpleDateFormat("dd MMM yyyy");
	
	// -----------------------------------------------------------------------------------------------
	// Keyboard shortcuts configuration values
	// -----------------------------------------------------------------------------------------------
	public static final KeyCombination SHORTCUT_UNDO = new KeyCodeCombination(KeyCode.Z, KeyCombination.ALT_DOWN);
	public static final KeyCombination SHORTCUT_REDO = new KeyCodeCombination(KeyCode.Y, KeyCombination.ALT_DOWN);
		
	public static final KeyCombination SHORTCUT_PAGE_DOWN = new KeyCodeCombination(KeyCode.PAGE_DOWN, KeyCombination.ALT_DOWN);
	public static final KeyCombination SHORTCUT_TA_UNFOCUSED_PAGE_DOWN = new KeyCodeCombination(KeyCode.PAGE_DOWN);
	public static final KeyCombination SHORTCUT_PAGE_UP = new KeyCodeCombination(KeyCode.PAGE_UP, KeyCombination.ALT_DOWN);	
	public static final KeyCombination SHORTCUT_TA_UNFOCUSED_PAGE_UP = new KeyCodeCombination(KeyCode.PAGE_UP);
	
	public static final KeyCombination SHORTCUT_GO_BACK = new KeyCodeCombination(KeyCode.BACK_QUOTE, KeyCombination.ALT_DOWN);	
	public static final KeyCombination SHORTCUT_OPEN_FILE_DIALOG = new KeyCodeCombination(KeyCode.F10);
	public static final KeyCombination SHORTCUT_TAB_ALL = new KeyCodeCombination(KeyCode.F1);
	public static final KeyCombination SHORTCUT_TAB_CATEGORY = new KeyCodeCombination(KeyCode.F2);
	public static final KeyCombination SHORTCUT_TAB_PRIORITY = new KeyCodeCombination(KeyCode.F3);
	public static final KeyCombination SHORTCUT_SETTING = new KeyCodeCombination(KeyCode.F4);
	public static final KeyCombination SHORTCUT_TUTORIAL = new KeyCodeCombination(KeyCode.F12);
	public static final KeyCombination SHORTCUT_DETAIL = new KeyCodeCombination(KeyCode.F11);
	
	// -----------------------------------------------------------------------------------------------
	// TabPane configuration values
	// -----------------------------------------------------------------------------------------------
    public static final String TAB_NAME_ALL = "all";  
    public static final String TAB_NAME_CATEGORY = "category";
    public static final String TAB_NAME_PRIORITY= "priority";
    public static final int TAB_INDEX_ALL = 0;
    public static final int TAB_INDEX_CATEGORY = 1;
    public static final int TAB_INDEX_PRIORITY = 2;
	
	// -----------------------------------------------------------------------------------------------
	// CSS Class name
	// -----------------------------------------------------------------------------------------------    
    public static final String CSS_CLASS_ACTION_COMMANDS = "actionCommands";
    public static final String CSS_CLASS_DATE_COMMANDS = "dateCommands";
    public static final String CSS_CLASS_CATEGORY = "category";
    public static final String CSS_CLASS_PRIORITY = "priority";
    public static final String CSS_CLASS_RECURRING_COMMANDS = "recurringCommands";
    public static final String CSS_CLASS_SEARCH_ATTRIBUTES = "searchAttributes";
    public static final String CSS_CLASS_ANCHORPANE_DETAIL = "aPaneDetail";
    public static final String CSS_CLASS_BORDERPANE_ALL = "bPaneAll";
    public static final String CSS_CLASS_BORDERPANE_OVERDUE = "bPaneOverdue";
    public static final String CSS_CLASS_BORDERPANE_COMPLETED = "bPaneCompleted";
    public static final String CSS_CLASS_BORDERPANE_SEARCH_RESULT = "bPaneSearchResult";
    public static final String CSS_CLASS_PANE_HORIZONTAL_BAR = "paneHBar";
    public static final String CSS_CLASS_LABEL_TITLE_TODAY = "labelTodayTitle";
    public static final String CSS_CLASS_LABEL_TITLE_OVERDUE = "labelOverdueTitle";
    public static final String CSS_CLASS_LABEL_TITLE_DATE = "labelDateTitle";
    public static final String CSS_CLASS_LABEL_ID = "labelId";
    public static final String CSS_CLASS_LABEL_DESCRIPTION = "labelDesc";
    public static final String CSS_CLASS_LABEL_CATEGORY = "labelCategory";
    public static final String CSS_CLASS_LABEL_BEFORE_TIME = "labelBeforeTime";
    public static final String CSS_CLASS_LABEL_DATETIME = "labelDateTime";
    public static final String CSS_CLASS_LABEL_SYSTEM_MSG = "labelSysMsg";
    public static final String CSS_CLASS_IMAGEVIEW_ICON_IMAGE = "iconImage";
    public static final String CSS_CLASS_LINE_PRIORITY_BAR = "priorityBar";
    public static final String CSS_CLASS_LINE_HORIZONTAL_BAR = "hBar";
    public static final String CSS_CLASS_TEXTAREA_DETAIL = "txtAreaDesc";
    	
	// -----------------------------------------------------------------------------------------------
	// View rendering configuration values
	// -----------------------------------------------------------------------------------------------
    public static final String TITLE_TODAY = "TODAY";
    public static final String TITLE_OVERDUE = "PAST TASKS";    
    public static final String TITLE_SETTING_DIR = "Choose File Location";    
    
    public static final String VIEW_NAME_SEARCH_RESULT= "searchResult";
    
    public static final String STR_BEFORE_DATE_BY = "by";
    public static final String STR_BEFORE_DATE_ON = "on";
    public static final String STR_BEFORE_DATE_FROM = "From";
    public static final String STR_BEFORE_DATE_TO = "  to";
    
	// -----------------------------------------------------------------------------------------------
	// Image links
	// -----------------------------------------------------------------------------------------------
    public static final String ICON_EVENT = "images/eventIcon.png";
    public static final String ICON_FLOATING = "images/floatingIcon.png";
    public static final String ICON_TIMED = "images/timedIcon.png";
    public static final String ICON_DATED = "images/datedIcon.png";
    public static final String ICON_SUCCESS = "images/successIcon.png";
    public static final String ICON_ERROR = "images/errorIcon.png";
    public static final String ICON_FEEDBACK = "images/feedbackIcon.png";
    public static final String IMAGE_TUTORIAL = "/images/Tutorial.png";
    public static final String IMAGE_LOGO = "images/logo.png";

	// -----------------------------------------------------------------------------------------------
	// Position values
	// -----------------------------------------------------------------------------------------------
    public static final double POSITION_OFFSET_X_POPUP = 15;
    public static final double POSITION_OFFSET_Y_POPUP = 2;
    public static final double POSITION_OFFSET_VERTICAL = 0.2;
    public static final double POSITION_START_X_HORIZONTAL_BAR = 4.5;
    public static final double POSITION_START_Y_HORIZONTAL_BAR = 0.5;
    public static final double POSITION_END_X_HORIZONTAL_BAR = 760;
    public static final double POSITION_END_Y_HORIZONTAL_BAR = 0;
    public static final double POSITION_START_Y_PRIORITY_BAR = 18;

	// -----------------------------------------------------------------------------------------------
	// Size values
	// -----------------------------------------------------------------------------------------------
    public static final double HEIGHT_STAGE = 655;
    public static final double WIDTH_STAGE = 805;
    
    public static final double WIDTH_MAX_LABEL_FLOATING = 650;
    public static final double WIDTH_MAX_LABEL_EVENT_ALL = 550;
    public static final double WIDTH_MAX_LABEL_TIMED_ALL = 430;
    public static final double WIDTH_MAX_LABEL_EVENT_CAT = 500;
    public static final double WIDTH_MAX_LABEL_TIMED_CAT = 380;
    
    public static final double FIT_HEIGHT_ICON = 20;
    
	// -----------------------------------------------------------------------------------------------
	// Misc
	// -----------------------------------------------------------------------------------------------   
    public static final String BOOLEAN_STRING_TRUE = "Yes";
    public static final String BOOLEAN_STRING_FALSE = "No";
    
    public static final String EMPTY_STRING = "";
    public static final String EMPTY_DATE = "NONE";
    
    public static final int TIMER_SYSTEM_MSG_DURATION = 2500;
    public static final long TIMER_UPDATE_STATUS_DURATION = 60000;   
    
    public static final int MAX_NUM_OF_DATES = 2;
    public static final int MAX_NUM_OF_FROM_TO_DATES = 2;
    public static final int MAX_NUM_OF_DATE_IN_COMMAND = 1;
}
