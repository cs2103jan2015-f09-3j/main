package application;
public final class Constant {
	public static final String CATEGORY_UNCATEGORISED = "Uncategorised";
	
	public static final String TAG_FILE = "file";
	public static final String TAG_NEXT_ID = "nextid";
	public static final String TAG_CATEGORIES = "categories";
	public static final String TAG_CATEGORY = "category";
	public static final String TAG_TASKS = "tasks";
	public static final String TAG_TASK = "task";
	
	public static final Command[] CRUD_COMMANDS = { Command.ADD, Command.DELETE, 
													Command.UPDATE, Command.SEARCH };
	
	public static final String PATH_DEFAULT_LIST_FILE = "listFile.xml";
}
