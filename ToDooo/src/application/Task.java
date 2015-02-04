package application;
import java.util.Date;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

public class Task {
	private int _id;
	private TaskType _taskType;
	private String _toDo;
	private String _originalText;
	private Date _date;
	private Date _from;
	private Date _to;
	private Date _by;
	private String _category;
	private boolean _isRecurring;
	private Frequency _repeat;
	private Priority _priority;
	
	public Task(String userInput, TaskType taskType) {
		// incomplete
	}
	
	private String generateToDoString(String userInput, TaskType taskType) {
		// remove /add or /a (remember to check if exist because can add
		// without using the 2 keywords.
		
		switch(taskType) {
			case EVENT :
				// remove /event
				// remove the slash in /on OR /from & /to 
				break;
			case TIMED : 
				// remove the slash in /from & /to
				break;
			case DATED :
				// remove the slash in /on
				break;
			default :
				// refactoring advice is to always have default
				// but I dont want to do anything if it's not these 3 cases.
				break;
		}
		
		return ""; // return the processed string.
	}
}
