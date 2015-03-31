package application;

import java.util.Calendar;
import java.util.Date;

public class RecurringTask implements Cloneable {
	private String _recurringTaskId;
	private Status _status;
	private Date _recurDate;
	
	public RecurringTask() {
		
	}
	
	public RecurringTask(String recurringTaskId, Date recurDate) {
		_recurringTaskId = recurringTaskId;
		_status = Task.getStatus(recurDate);
		_recurDate = recurDate;
	}
	
	public RecurringTask(String recurringTaskId, Calendar recurDateCalendar) {
		_recurringTaskId = recurringTaskId;
		
		Date recurDate = recurDateCalendar.getTime();
		_status = Task.getStatus(recurDate);
		
		_recurDate = recurDate;
	}
	
	@Override
	public RecurringTask clone() {
		RecurringTask clone = null;
		
		try {
			clone = (RecurringTask) super.clone();
		} catch(CloneNotSupportedException e){
            throw new RuntimeException(e); 
        }
		
		 return clone;
	}

	public String getRecurringTaskId() {
		return _recurringTaskId;
	}

	public void setRecurringTaskId(String recurringTaskId) {
		_recurringTaskId = recurringTaskId;
	}

	public Status getStatus() {
		return _status;
	}

	public void setStatus(Status status) {
		_status = status;
	}

	public Date getRecurDate() {
		return _recurDate;
	}

	public void setRecurDate(Date recurDate) {
		_recurDate = recurDate;
	}
	
	public static boolean isValidRecurringTaskType(TaskType taskType) {
		return (taskType.equals(TaskType.DATED) ||
				taskType.equals(TaskType.EVENT));
	}
}
