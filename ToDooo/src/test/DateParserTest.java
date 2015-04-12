//@author A0112537M
package test;

import static org.junit.Assert.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import application.DateParser;

public class DateParserTest {
	private String dateStrA, dateStrB;
	private SimpleDateFormat format;
	private Date dateA, dateB;
	
	@Test
	public void test() throws ParseException {
		initialiseDateValue();
		testCalculateDayOfWeek();
		testCalculateDayOfMonth();
		testCompareDate();
		testIsBeforeDate();
		testIsAfterDate();
	}
	
	public void initialiseDateValue() throws ParseException {
		format = new SimpleDateFormat("MMMM d, yyyy");
		dateStrA = "March 27, 2015";
		dateStrB = "April 04, 2015";
		dateA = format.parse(dateStrA);
		dateB = format.parse(dateStrB);
	}
	
	/*
	 * Equivalence Partition: [1-7]
	 * Possible boundary values: 0, 1, 2, 6, 7, 8
	 * This is a boundary case for an equivalent partition. 
	 */
	public void testCalculateDayOfWeek() {
		int result;
		int expected = 6;
		result = DateParser.calculateDayOfWeek(dateA);
		assertEquals(result, expected);
	}
	
	/*
	 * Equivalence Partition: [1-31]
	 * Possible boundary values: 0, 1, 2, 30, 31, 32
	 * This is not a boundary case for an equivalent partition. 
	 */
	public void testCalculateDayOfMonth() {
		int result;
		int expected = 27;
		result = DateParser.calculateDayOfMonth(dateA);
		assertEquals(result, expected);
	}
	
	/*
	 * Equivalence Partition: [The dates are the same], [The dates are different]
	 */
	public void testCompareDate() {
		boolean result;
		boolean expected = false;
		result = DateParser.compareDate(dateA, dateB);
		assertEquals(result, expected);
	}
	
	/*
	 * Equivalence Partition: [dateA is before dateB], [dateA is not before dateB]
	 */
	public void testIsBeforeDate() {
		boolean result;
		boolean expected = true;
		result = DateParser.isBeforeDate(dateA, dateB);
		assertEquals(result, expected);
	}
	
	/*
	 * Equivalence Partition: [dateB is after dateA], [dateB is not after dateA]
	 */
	public void testIsAfterDate() {
		boolean result;
		boolean expected = true;
		result = DateParser.isAfterDate(dateB, dateA);
		assertEquals(result, expected);
	}
	
	
}
