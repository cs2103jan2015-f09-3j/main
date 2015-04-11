//@author A0112498B
package application;

import java.util.Date;

public enum TaskStatus {
	ERROR,
	COMPLETED,
	ONGOING,
	OVERDUE,
	DELETED;
	
	public static TaskStatus getTaskStatus(Date endDate) {
		TaskStatus status = TaskStatus.OVERDUE;		
		
		if (endDate == null) { // floating task
			return TaskStatus.ONGOING;
		} else {
			Date todayDate = new Date();
			boolean isOngoing = DateParser.isBeforeDate(todayDate, endDate);
			
			if (isOngoing) {
				status = TaskStatus.ONGOING;
			}
		}
		
		return status;
	}
}
