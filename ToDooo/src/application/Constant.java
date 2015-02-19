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
	public static final String TAG_ATTRIBUTE_ID = "task";
	
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
	
	public static final String XML_TEXT_NIL = "NIL";
	public static final String XML_INDENT_YES = "yes";
	
	public static final Command[] CRUD_COMMANDS = { Command.ADD, Command.DELETE, 
													Command.UPDATE, Command.SEARCH };
	
	public static final String PATH_SETTING = "setting.xml";
	
	public static final String MSG_SAVE_FAIL = "File not saved.";
	public static final String MSG_SAVE_SUCCESS = "File saved.";
}
