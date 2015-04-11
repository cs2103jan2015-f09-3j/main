//@author A0112498B
package application;

import java.util.Date;

public enum Status {
	ERROR,
	COMPLETED,
	ONGOING,
	OVERDUE,
	DELETED;
	
	public static Status getTaskStatus(Date endDate) {
		Status status = Status.OVERDUE;		
		
		if (endDate == null) { // floating task
			return Status.ONGOING;
		} else {
			Date todayDate = new Date();
			boolean isOngoing = DateParser.isBeforeDate(todayDate, endDate);
			
			if (isOngoing) {
				status = Status.ONGOING;
			}
		}
		
		return status;
	}
}
