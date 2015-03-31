package application;

import java.util.ArrayList;

public enum SearchAttribute {
	ID("id="),
	DESCRIPTION("des="),
	DATE("date="),
	CATEGORY("cat="),
	PRIORITY("pri=");
	
	private final String _COMMAND;
	
	// -----------------------------------------------------------------------------------------------
	// Constructor
	// -----------------------------------------------------------------------------------------------	
	private SearchAttribute(String command) {
		_COMMAND = command;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Get methods
	// -----------------------------------------------------------------------------------------------
	public String getCommand() {
		return _COMMAND;
	}

	// -----------------------------------------------------------------------------------------------
	// Public methods
	// -----------------------------------------------------------------------------------------------		
	public static ArrayList<SearchAttribute> getSearchAttributes(String commandLine) {
		String lowerCase = commandLine.toLowerCase();
		ArrayList<SearchAttribute> attributes = new ArrayList<SearchAttribute>();
		
		for (SearchAttribute attribute : SearchAttribute.values()) {
			if (lowerCase.contains(attribute.getCommand())) {
				attributes.add(attribute);
			}
		}
		
		return attributes;
	}
}
