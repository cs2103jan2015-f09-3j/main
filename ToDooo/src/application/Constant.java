package application;
public final class Constant {
	public static final String CATEGORY_UNCATEGORISED = "Uncategorised";
	
	public static final String TAG_FILE = "file";
	public static final String TAG_CATEGORIES = "categories";
	public static final String TAG_CATEGORY = "category";
	public static final String TAG_TASKS = "tasks";
	public static final String TAG_TASK = "task";
	
	public static final String TAG_SETTING = "setting";			
	public static final String TAG_SETTING_NEXT_ID = "nextid";
	public static final String TAG_SETTING_SAVE = "save";
	
	public static final Command[] CRUD_COMMANDS = { Command.ADD, Command.DELETE, 
													Command.UPDATE, Command.SEARCH };
	
	public static final String PATH_SETTING = "setting.xml";
	
	public static final String MSG_SAVE_FAIL = "File not saved.";
	public static final String MSG_SAVE_SUCCESS = "File saved.";
}
