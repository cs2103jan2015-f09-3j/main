//@author A0112856E
package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class TaskSorter {
	public enum SortParameter {
		ALPHABETICAL_ORDER, 
		CATEGORY, 
		DATE, 
		PRIORITY, 
		TASKTYPE_FLOATING
	}

	private static SortParameter[] parametersForViewAll = {
			SortParameter.TASKTYPE_FLOATING, 
			SortParameter.DATE, 
			SortParameter.PRIORITY, 
			SortParameter.ALPHABETICAL_ORDER
	};
	
	private static SortParameter[] parametersForViewCategory = {
			SortParameter.CATEGORY,
			SortParameter.DATE, 
			SortParameter.PRIORITY, 
			SortParameter.ALPHABETICAL_ORDER
	};
	
	private static SortParameter[] parametersForViewPriority = {
			SortParameter.PRIORITY, 
			SortParameter.DATE, 
			SortParameter.ALPHABETICAL_ORDER
	};
	
	
	// -----------------------------------------------------------------------------------------------
	// Public methods
	// -----------------------------------------------------------------------------------------------
	public static Comparator<Task> getComparator(SortParameter[] sortParameters) {
		return new TaskComparator(sortParameters);
	}
	
	//@author A0112537M
	public static ArrayList<Task> getTasksSortedByDate(ArrayList<Task> list) {
		return reallocateFloatingTasks(sortByDate(list));
	}
	
	//@author A0112856E
	public static ArrayList<Task> getTasksSortedByCategories(ArrayList<Task> list) {
		Comparator<Task> CategoryComparator = getComparator(parametersForViewCategory);
		Collections.sort(list, CategoryComparator);
		
		return list;
	}
	
	public static ArrayList<Task> getTasksSortedByPriorities(ArrayList<Task> list) {
		Comparator<Task> priorityComparator = getComparator(parametersForViewPriority);
		Collections.sort(list, priorityComparator);
		
		return list;
	}	
	
	
	// -----------------------------------------------------------------------------------------------
	// Private methods
	// -----------------------------------------------------------------------------------------------
	//@author A0112537M
		private static ArrayList<Task> reallocateFloatingTasks(ArrayList<Task> list) {
			ArrayList<Task> floatingList = extractFloatingTasks(list);
			addFloatingTasksBackIntoList(list, floatingList);
			
			return list;
		}
		
		private static ArrayList<Task> extractFloatingTasks(ArrayList<Task> list) {
			Task task = null;
			String taskType = null;
			ArrayList<Task> floatingList = new ArrayList<Task>();
			
			for(int i = 0; i < list.size(); i++) {
				task = list.get(i);
				taskType = task.getTaskType().toString();
				if(taskType.equalsIgnoreCase(TaskType.FLOATING.toString())) {
					floatingList.add(list.get(i));
				} else {
					break;
				}
			}
			
			list.removeAll(floatingList);
			
			return floatingList;
		}

		private static void addFloatingTasksBackIntoList(ArrayList<Task> list, ArrayList<Task> floatingList) {		
			if(list.isEmpty()) {
				// insert the floating task to the top of the list 
				addFloatingTaskToTaskList(list, floatingList);
			} else {
				// insert floating tasks after today's task in order
				insertFloatingTasksInOrder(list, floatingList); 
			}
		}

		private static void insertFloatingTasksInOrder(ArrayList<Task> list,
											   		   ArrayList<Task> floatingList) {
			Task task = null;
			TaskType taskType = null;
			Date date = null;
			Date today = null;
			
			for(int j = 0; j < list.size(); j++) {
				task = list.get(j);
				taskType = task.getTaskType();
				date = task.getStartDate();
				today = DateParser.getTodayDate().getTime();
				int counter = j;
				
				if(!taskType.equals(TaskType.FLOATING) && 
				   DateParser.isAfterDateWithoutTime(date, today)) {
					insertFloatingTaskToTaskList(list, floatingList, counter);
					
					break;
				} else if(taskType.equals(TaskType.FLOATING) || 
						  j == list.size()-1) {
					addFloatingTaskToTaskList(list, floatingList);
					
					break;
				}
			}
		}

		private static void insertFloatingTaskToTaskList(ArrayList<Task> list,
														 ArrayList<Task> floatingList, int counter) {
			for(int k = 0; k < floatingList.size(); k++) {
				list.add(counter, floatingList.get(k));
				counter++;
			}
		}

		private static void addFloatingTaskToTaskList(ArrayList<Task> list,
														 ArrayList<Task> floatingList) {
			for(int k = 0; k < floatingList.size(); k++) {
				list.add(floatingList.get(k));
			}
		}
		
		//@author A0112856E
		private static ArrayList<Task> sortByDate(ArrayList<Task> list) {
			Comparator<Task> comparator = getComparator(parametersForViewAll);
			Collections.sort(list, comparator);
			return list;
		}
		
	// -----------------------------------------------------------------------------------------------
	// TaskComparator
	// -----------------------------------------------------------------------------------------------
	private static class TaskComparator implements Comparator<Task> {
		private SortParameter[] parameters = null;
		
		private TaskComparator(SortParameter[] parameters) {
			this.parameters = parameters;
		}
		
		public int compare(Task taskA, Task taskB) {
			int comparison = 0;
			
			for (SortParameter parameter : parameters) {
				switch (parameter) {
				case ALPHABETICAL_ORDER:
					comparison = taskA.getToDo().compareTo(taskB.getToDo());
					if (comparison != 0) {
						return comparison;
					}
					break;
				case CATEGORY:
					String categoryOfTaskA = taskA.getCategory();
					String categoryOfTaskB = taskB.getCategory();
					comparison = categoryComparison(categoryOfTaskA, categoryOfTaskB);
					if (comparison != 0) {
						return comparison;
					}
				case DATE:
					comparison = dateComparison(taskA, taskB);
					if (comparison != 0) {
						return comparison;
					}
					break;
				case PRIORITY:
					Priority taskAPriority = taskA.getPriority();
					Priority taskBPriority = taskB.getPriority();
					comparison = priorityComparison(taskAPriority, taskBPriority);
					if (comparison != 0) {
						return comparison;
					}
					break;
				case TASKTYPE_FLOATING:
					TaskType taskTypeOfTaskA = taskA.getTaskType();
					TaskType taskTypeOfTaskB = taskB.getTaskType();
					int lengthOfTaskTypeOfTaskA = taskTypeOfTaskA.toString().length();
					int lengthOfTaskTypeOfTaskB = taskTypeOfTaskB.toString().length();
					comparison = lengthOfTaskTypeOfTaskB - lengthOfTaskTypeOfTaskA;
					if (comparison != 0) {
						return comparison;
					}
					break; 
				}
			}
			return comparison;
		}

		private int categoryComparison(String categoryOfTaskA, String categoryOfTaskB) {
			int comparison = 0;
			
			if (!categoryOfTaskA.equals(Constant.CATEGORY_UNCATEGORISED) 
				&& !categoryOfTaskB.equals(Constant.CATEGORY_UNCATEGORISED)) {
				comparison = categoryOfTaskA.compareTo(categoryOfTaskB);		
				
			} else if (categoryOfTaskA.equals(Constant.CATEGORY_UNCATEGORISED) && 
					   !categoryOfTaskB.equals(Constant.CATEGORY_UNCATEGORISED)) {				
				comparison = 1;				
				
			} else if (!categoryOfTaskA.equals(Constant.CATEGORY_UNCATEGORISED) && 
					   categoryOfTaskB.equals(Constant.CATEGORY_UNCATEGORISED)) {
				comparison = -1;
			} else {
				comparison = 0;
			}
			
			return comparison;
		}
		
		private int dateComparison(Task taskA, Task taskB) {
			int comparison = 0;			
			Date dateOfTaskA = null; 
			Date dateOfTaskB = null;
			
			TaskType taskTypeOfTaskA = taskA.getTaskType();
			TaskType taskTypeOfTaskB = taskB.getTaskType();
			
			if (!taskTypeOfTaskA.equals(TaskType.FLOATING) && 
				!taskTypeOfTaskB.equals(TaskType.FLOATING)) {
				
				dateOfTaskA = taskA.getStartDate();
				dateOfTaskB = taskB.getStartDate();
				
				comparison = dateOfTaskA.compareTo(dateOfTaskB);
				
			} else if (taskTypeOfTaskA.equals(TaskType.FLOATING) && 
					   !taskTypeOfTaskB.equals(TaskType.FLOATING)) {
				comparison = 1;
				
			} else if (!taskTypeOfTaskA.equals(TaskType.FLOATING) &&
					   taskTypeOfTaskB.equals(TaskType.FLOATING)) {
				comparison = -1;
			} else {
				comparison = 0;
			}
			
			return comparison;
		}

		private int priorityComparison(Priority taskAPriority, Priority taskBPriority) {
			int comparison = 0;
			int priorityLengthOfTaskA, priorityLengthOfTaskB;
			
			if (!taskAPriority.equals(Priority.NEUTRAL) && 
				!taskBPriority.equals(Priority.NEUTRAL)) {
				
				priorityLengthOfTaskA = taskAPriority.getCommand().
										getAdvancedCommand().length();
				priorityLengthOfTaskB = taskBPriority.getCommand().
										getAdvancedCommand().length();
				
				comparison = priorityLengthOfTaskB - priorityLengthOfTaskA;
				
			} else if (taskAPriority.equals(Priority.NEUTRAL) && 
					   !taskBPriority.equals(Priority.NEUTRAL)) {
				comparison = 1;
			} else if (!taskAPriority.equals(Priority.NEUTRAL) && 
				       taskBPriority.equals(Priority.NEUTRAL)) {
				comparison = -1;
			} else {
				comparison = 0;
			}
			
			return comparison;
		}
	}
}
