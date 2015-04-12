//@author A0112498B
package application;

import java.util.Calendar;
import java.util.Date;

public class RecurringTask implements Cloneable {
	private String _recurringTaskId;
	private TaskStatus _status;
	private Date _recurDate;
	
	// -----------------------------------------------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------------------------------------------	
	public RecurringTask() {
		/*
		 * attributes to be filled up by XmlManager
		 */
	}
	
	public RecurringTask(String recurringTaskId, Date recurDate) {
		_recurringTaskId = recurringTaskId;
		_status = TaskStatus.getTaskStatus(recurDate);
		_recurDate = recurDate;
	}
	
	public RecurringTask(String recurringTaskId, Calendar recurDateCalendar) {
		_recurringTaskId = recurringTaskId;
		
		Date recurDate = recurDateCalendar.getTime();
		_status = TaskStatus.getTaskStatus(recurDate);
		
		_recurDate = recurDate;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Get methods
	// -----------------------------------------------------------------------------------------------
	public String getRecurringTaskId() {
		return _recurringTaskId;
	}

	public TaskStatus getStatus() {
		return _status;
	}

	public Date getRecurDate() {
		return _recurDate;
	}
	
	// -----------------------------------------------------------------------------------------------
	// Set methods
	// -----------------------------------------------------------------------------------------------
	public void setRecurringTaskId(String recurringTaskId) {
		_recurringTaskId = recurringTaskId;
	}

	public void setStatus(TaskStatus status) {
		_status = status;
	}

	public void setRecurDate(Date recurDate) {
		_recurDate = recurDate;
	}

	// -----------------------------------------------------------------------------------------------
	// Public methods
	// -----------------------------------------------------------------------------------------------		
	public static boolean isValidRecurringTaskType(TaskType taskType) {
		return (taskType.equals(TaskType.DATED) ||
				taskType.equals(TaskType.EVENT));
	}
		
	@Override
	public RecurringTask clone() {
		RecurringTask clone = null;
		
		try {
			clone = (RecurringTask) super.clone();
		} catch(CloneNotSupportedException exception){
            throw new RuntimeException(exception); 
        }
		
		 return clone;
	}
}
