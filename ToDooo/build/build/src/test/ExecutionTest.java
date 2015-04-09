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
	private static final String TEST_UPDATE_COMMAND = "update 1: Have dinner with Amy /by Monday //friend /*";
	private static final String TEST_UPDATE_COMMAND_NOT_FOUND = "update 10: Have dinner with Amy /by Monday //friend /*";
	public static final String PATH_TEST_FILE = "testFile.xml";
	
	private String originalSavePath;
	
	@Test
	public void test() {
		initTest();
		testAdd();
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
		Main.storage.updateFilePathInSetting(originalSavePath);
	}
	
	private void deleteTestFile() {
		File file = new File(ToDoListTest.PATH_TEST_FILE);
		file.delete();		
	}
}
