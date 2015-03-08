package application;

import java.util.Calendar;
import java.util.Date;

public class DateParser {
	public static int calculateDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		return dayOfWeek;
	}
}
