package application;

import java.util.ArrayList;

public enum SearchAttribute {
	ID("id="),
	DESCRIPTION("des="),
	DATE("date"),
	CATEGORY("cat="),
	PRIORITY("pri=");
	
	private final String _COMMAND;
	
	private SearchAttribute(String command) {
		_COMMAND = command;
	}
	
	public String getCommand() {
		return _COMMAND;
	}
	
	public static ArrayList<SearchAttribute> getSearchAttributes(String commandLine) {
		String lowerCase = commandLine.toLowerCase();
		ArrayList<SearchAttribute> attributes = new ArrayList<SearchAttribute>();
		
		for (SearchAttribute attribute : SearchAttribute.values()) {
			if (lowerCase.contains(attribute._COMMAND)) {
				attributes.add(attribute);
			}
		}
		
		return attributes;
	}
}
