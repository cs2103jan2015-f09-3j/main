//@author A0112856E
package test;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import application.Command;
import application.InputParser;
import application.Priority;
import application.TaskType;

import com.joestelmach.natty.Parser;

public class InputParserTest {
	private static final String CATEGORY = "revision";
	private static final String TASK_ID = "1";
	private static final String DESCRIPTION = "finish exam revision /by 20 April /high #revision";
	private static final String USER_INPUT_DATED_TASK = "add finish exam revision /by 20 April /high #revision";
	
	@Test
	public void test() {
		testGetActionFromString();
		testGetTaskTypeFromString();
		testGetCategoryFromString();
		testGetPriorityFromString();
		testRemoveActionFromString();
	}
	
	public void testGetActionFromString() {
		Command expectedResult = Command.ADD;
		Command actualResult = InputParser.getActionFromString(USER_INPUT_DATED_TASK);
		assertEquals(actualResult, expectedResult);
	}

	public void testGetTaskTypeFromString() {
		TaskType expectedResult = TaskType.DATED;
		TaskType actualResult = InputParser.getTaskTypeFromString(USER_INPUT_DATED_TASK);
		assertEquals(actualResult, expectedResult);
	}

	public void testGetCategoryFromString() {
		String expectedResult = CATEGORY;
		String actualResult = InputParser.getCategoryFromString(USER_INPUT_DATED_TASK);
		assertEquals(actualResult, expectedResult);
	}

	public void testGetPriorityFromString() {
		Priority expectedResult = Priority.HIGH;
		Priority actualResult = InputParser.getPriorityFromString(USER_INPUT_DATED_TASK);
		assertEquals(actualResult, expectedResult);
	}

	public void testRemoveActionFromString() {
		String expectedResult = DESCRIPTION;
		String actualResult = InputParser.removeActionFromString(USER_INPUT_DATED_TASK, TASK_ID);
		assertEquals(actualResult, expectedResult);
	}
}
