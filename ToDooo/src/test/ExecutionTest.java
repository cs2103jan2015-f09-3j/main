//@author A0112498B
package test;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import application.Constant;
import application.Execution;
import application.Main;
import application.ToDoList;

public class ExecutionTest {
	private static final String TEST_ADD_COMMAND = "Have dinner with Amy /on Monday //friend /*";
	private static final String TEST_DELETE_COMMAND = "delete 1";
	public static final String PATH_TEST_FILE = "testFile.xml";
	
	private String originalSavePath;
	
	@Test
	public void test() {
		initTest();
		testAdd();
		testUndo();
		testRedo();
		testDelete();
		
		cleanTestFromSystem();
	}
	
	public void testAdd() {
		String methodName = "executeAdd";
		Class[] paramTypes = new Class[] {
				String.class
		};			
		Object[] params = new Object[] { 
				TEST_ADD_COMMAND 		
		};
		
		String expectedResult = Constant.MSG_ADD_SUCCESS;
		String actualResult = invokeMethod(methodName, paramTypes, params);
		
		assertEquals(actualResult, expectedResult);
	}
	
	public void testDelete() {
		// delete existing item
		String methodName = "executeDelete";
		Class[] paramTypes = new Class[] {
				String.class
		};			
		Object[] params = new Object[] { 
				TEST_DELETE_COMMAND 		
		};
		
		String expectedResult = Constant.MSG_DELETE_SUCCESS;
		String actualResult = invokeMethod(methodName, paramTypes, params);
		
		assertEquals(actualResult, expectedResult);
		
		// delete non-existing item
		expectedResult = Constant.MSG_ITEM_NOT_FOUND;
		actualResult = invokeMethod(methodName, paramTypes, params);
		
		assertEquals(actualResult, expectedResult);		
	}
	
	public void testUndo() {
		// undo add
		String actualResult = Execution.executeUndo();
		String expectedResult = Constant.MSG_UNDO_ADD_SUCCESS;
		
		int expectedSize = 0;
		int actualSize = Main.list.getTasks().size();
		
		assertEquals(actualSize, expectedSize);
		assertEquals(actualResult, expectedResult);
		
		// attempt to undo with an empty undo stack
		actualResult = Execution.executeUndo();
		expectedResult = Constant.MSG_NO_UNDO;
		
		assertEquals(actualResult, expectedResult);		
	}
	
	public void testRedo() {
		// redo add
		String actualResult = Execution.executeRedo();
		String expectedResult = Constant.MSG_REDO_ADD_SUCCESS;
				
		int expectedSize = 1;
		int actualSize = Main.list.getTasks().size();
				
		assertEquals(actualSize, expectedSize);
		assertEquals(actualResult, expectedResult);
			
		// attempt to redo with an empty redo stack
		actualResult = Execution.executeRedo();
		expectedResult = Constant.MSG_NO_REDO;
				
		assertEquals(actualResult, expectedResult);		
	}
	
	private String invokeMethod(String methodName, Class[] paramTypes, Object[] params) {
		Execution execution = new Execution();
		Class exeClass = execution.getClass();
		Method method = null;
		
		Object object = null;
		try {
			method = exeClass.getDeclaredMethod(methodName, paramTypes);
			method.setAccessible(true);
			
			object = method.invoke(new Execution(), params);
		} catch (NoSuchMethodException | SecurityException |
				IllegalAccessException | IllegalArgumentException | 
				InvocationTargetException e) {
			e.printStackTrace();
		} 
		
		return String.valueOf(object);
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
	
	private void initTest() {
		String methodName = "initialise";		
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
		Main.storage.updateFilePathInSetting(Constant.PATH_DEFAULT);
	}
	
	private void deleteTestFile() {
		File file = new File(ToDoListTest.PATH_TEST_FILE);
		file.delete();		
	}
}
