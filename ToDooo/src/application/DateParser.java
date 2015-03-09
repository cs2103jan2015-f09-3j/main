package application;

import java.util.Calendar;
import java.util.Date;
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
}
