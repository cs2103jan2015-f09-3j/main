//@author A0112498B
package test;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javafx.util.Pair;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import application.Main;
import application.Task;
import application.ToDoList;

public class ToDoListTest {
	private static final String METHOD_NAME_INIT = "initialise";
	private static final String TEST_ADD_COMMAND = "Have dinner with Amy /on Monday //friend /*";
	private static final String TEST_DELETE_COMMAND = "delete 1";
	private static final String TEST_UPDATE_COMMAND = "update 1: Have dinner with Amy /by Monday //friend /*";
	private static final String TEST_UPDATE_COMMAND_NOT_FOUND = "update 1: Have dinner with Amy /by Monday //friend /*";
	public static final String PATH_TEST_FILE = "testFile.xml";
	
	private String originalSavePath;
	
	@Test 
	public void test() {
		initTest();		
		testAddTaskToList();
		testUpdateTaskOnList();
		testDeleteTaskFromList();
		
		cleanTestFromSystem();
	}
	
	public void testAddTaskToList() {		
		Task testTask = new Task(ToDoListTest.TEST_ADD_COMMAND);		
		int expectedSize = Main.list.getTasks().size() + 1;
		
		Main.list.addTaskToList(testTask);
		
		// Check size
		// The new task is only added into the arraylist 
		// when it has received a success message from storage
		// thus it is sufficient to check the size of tasks arraylist 
		int afterSize = Main.list.getTasks().size();
		assertEquals(afterSize, expectedSize);
	}

	/*
	 * Equivalence Partition: EXISTING_TASK, NON_EXISTING_TASK
	 * userInput: "delete d1", "delete d1" (after delete)
	 * Delete Result: DELETED: (size == size + 1) && removedTask != null, 
	 * 				  NOT_FOUND: (size == size) && removedTask == null
	 */
	public void testDeleteTaskFromList() {
		int expectedSize = Main.list.getTasks().size() - 1;		
		Task removedTask = Main.list.deleteTaskFromList(ToDoListTest.TEST_DELETE_COMMAND);
		
		// Check that task is removed
		assertNotNull(removedTask);
		
		// Check size
		int afterSize = Main.list.getTasks().size();
		assertEquals(afterSize, expectedSize);
		
		// Delete the same item again
		expectedSize = afterSize;
		removedTask = Main.list.deleteTaskFromList(ToDoListTest.TEST_DELETE_COMMAND);
		
		// Check that task is not found
		assertNull(removedTask);	
		// Check size
		afterSize = Main.list.getTasks().size();		
		assertEquals(afterSize, expectedSize);
	}

	/*
	 * Equivalence Partition: EXISTING_TASK, NON_EXISTING_TASK
	 * userInput: "update e1: <description>", "update d1: <description>"
	 * Delete Result: UPDATED: (id = e1) && resultPair != null, 
	 * 				  NOT_FOUND: resultPair == null
	 */
	public void testUpdateTaskOnList() {				
		Pair<Task, String> resultPair = 
				Main.list.updateTaskOnList(ToDoListTest.TEST_UPDATE_COMMAND);
		
		// Check that update has been done
		assertNotNull(resultPair);	
				
		//---------------------------------------------------------------------------------
		resultPair = Main.list.updateTaskOnList(ToDoListTest.
					TEST_UPDATE_COMMAND_NOT_FOUND);
		
		// Check that task not found
		assertNotNull(resultPair);	
	}
	
	private void initTest() {
		String methodName = ToDoListTest.METHOD_NAME_INIT;		
		invokeMainPrivateMethod(methodName);		
		
		boolean isTest = true;
		originalSavePath = Main.list.getListFilePath();				
		
		// change save path in setting to 
		// point to the testfile		
		Main.storage.updateFilePathInSetting(ToDoListTest.PATH_TEST_FILE);		
		Main.list = new ToDoList(isTest);
	}
	
	private void cleanTestFromSystem() {
		deleteTestFile();
		Main.storage.updateFilePathInSetting(originalSavePath);
	}
	
	private void deleteTestFile() {
		File file = new File(ToDoListTest.PATH_TEST_FILE);
		file.delete();		
	}
	
	private void invokeMainPrivateMethod(String methodName) {
		Main main = new Main();
		Class mainClass = main.getClass();
		Method method = null;
		
		try {
			method = mainClass.getDeclaredMethod(methodName);
			method.setAccessible(true);
			
			method.invoke(ToDoList.class);
		} catch (NoSuchMethodException | SecurityException |
				IllegalAccessException | IllegalArgumentException | 
				InvocationTargetException e) {
			e.printStackTrace();
		} 
	}

}
