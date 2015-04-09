package application;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateParser {
	public static Calendar createCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTime(date);
		
		return calendar;
	}
	
	public static int calculateDayOfWeek(Date date) {
		Calendar calendar = DateParser.createCalendar(date);
		int dayOfWeek = calculateDayOfWeek(calendar);
		
		return dayOfWeek;
	}
	
	public static int calculateDayOfWeek(Calendar calendar) {
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		return dayOfWeek;
	}	
	
	public static int calculateDayOfMonth(Date date) {
		Calendar calendar = DateParser.createCalendar(date);
		int dayOfMonth = calculateDayOfMonth(calendar);
		
		return dayOfMonth;
	}
	
	public static int calculateDayOfMonth(Calendar calendar) {
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		
		return dayOfMonth;
	}
	
	public static int calculateMonth(Date date) {
		Calendar calendar = DateParser.createCalendar(date);
		int month = calculateMonth(calendar);
		
		return month;
	}
	
	public static int calculateMonth(Calendar calendar) {
		int month = calendar.get(Calendar.MONTH);
		
		return month;
	}
	
	public static boolean compareDate(Date dateA, Date dateB) {
		Calendar calendarA = Calendar.getInstance();
		Calendar calendarB = Calendar.getInstance();
		calendarA.setTime(dateA);
		calendarB.setTime(dateB);
		boolean sameDay = calendarA.get(Calendar.YEAR) == calendarB.get(Calendar.YEAR) &&
				calendarA.get(Calendar.DAY_OF_YEAR) == calendarB.get(Calendar.DAY_OF_YEAR);
		
		return sameDay;
	}
	
	public static boolean isAfterDateWithoutTime(Date dateA, Date dateB) {
		Calendar calendarA = Calendar.getInstance();
		Calendar calendarB = Calendar.getInstance();
		calendarA.setTime(dateA);
		calendarB.setTime(dateB);
		boolean isAfterDay = calendarA.get(Calendar.YEAR) >= calendarB.get(Calendar.YEAR) &&
				calendarA.get(Calendar.DAY_OF_YEAR) > calendarB.get(Calendar.DAY_OF_YEAR);
		
		return isAfterDay;
	}
	
	public static boolean isBeforeDate(Date thisDate, Date thatDate) {
		Calendar calendarA = DateParser.createCalendar(thisDate);
		Calendar calendarB = DateParser.createCalendar(thatDate);
				
		return isBeforeDate(calendarA, calendarB);
	}
	
	public static boolean isBeforeDate(Calendar thisCalendar, Calendar thatCalendar) {				
		return thisCalendar.before(thatCalendar);
	}
	
	public static boolean isAfterDate(Date thisDate, Date thatDate) {
		return DateParser.isBeforeDate(thatDate, thisDate);
	}
	
	public static boolean isAfterDate(Calendar thisCalendar, Calendar thatCalendar) {
		return DateParser.isBeforeDate(thatCalendar, thisCalendar);
	}
	
	public static boolean hasMatchedDateOnly(Date thisDate, Date thatDate) {
		Calendar calendarA = DateParser.createCalendar(thisDate);
		Calendar calendarB = DateParser.createCalendar(thatDate);
		
		return DateParser.hasMatchedDateOnly(calendarA, calendarB);
	}
	
	public static boolean hasMatchedDateOnly(Calendar thisCalendar, Calendar thatCalendar) {
		return (thisCalendar.get(Calendar.ERA) == thatCalendar.get(Calendar.ERA) &&
				thisCalendar.get(Calendar.YEAR) == thatCalendar.get(Calendar.YEAR) &&
				thisCalendar.get(Calendar.DAY_OF_YEAR) == thatCalendar.get(Calendar.DAY_OF_YEAR));
	}
	
	public static Calendar getTodayDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		
		calendar.set(Calendar.HOUR_OF_DAY, 0); 
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
	    
	    return calendar;
	}
}