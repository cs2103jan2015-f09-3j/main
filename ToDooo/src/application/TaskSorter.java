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
	
	public static Comparator<Task> getComparator(SortParameter[] sortParameters) {
		return new TaskComparator(sortParameters);
	}

	private static class TaskComparator implements Comparator<Task> {
		private SortParameter[] parameters;
		
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
			int comparison;
			if (!categoryOfTaskA.equals(Constant.CATEGORY_UNCATEGORISED) 
					&& !categoryOfTaskB.equals(Constant.CATEGORY_UNCATEGORISED)) {
				comparison = categoryOfTaskA.compareTo(categoryOfTaskB);
			} else if (categoryOfTaskA.equals(Constant.CATEGORY_UNCATEGORISED) 
					& !categoryOfTaskB.equals(Constant.CATEGORY_UNCATEGORISED)) {
				comparison = 1;
			} else if (!categoryOfTaskA.equals(Constant.CATEGORY_UNCATEGORISED) 
					&& categoryOfTaskB.equals(Constant.CATEGORY_UNCATEGORISED)) {
				comparison = -1;
			} else {
				comparison = 0;
			}
			return comparison;
		}

		private int dateComparison(Task taskA, Task taskB) {
			int comparison;
			Date dateOfTaskA, dateOfTaskB;
			TaskType taskTypeOfTaskA = taskA.getTaskType();
			TaskType taskTypeOfTaskB = taskB.getTaskType();
			if (!taskTypeOfTaskA.equals(TaskType.FLOATING) 
					&& !taskTypeOfTaskB.equals(TaskType.FLOATING)) {
				dateOfTaskA = taskA.getStartDate();
				dateOfTaskB = taskB.getStartDate();
				comparison = dateOfTaskA.compareTo(dateOfTaskB);
			} else if (taskTypeOfTaskA.equals(TaskType.FLOATING) 
					&& !taskTypeOfTaskB.equals(TaskType.FLOATING)) {
				comparison = 1;
			} else if (!taskTypeOfTaskA.equals(TaskType.FLOATING) 
					&& taskTypeOfTaskB.equals(TaskType.FLOATING)) {
				comparison = -1;
			} else {
				comparison = 0;
			}
			return comparison;
		}

		private int priorityComparison(Priority taskAPriority, Priority taskBPriority) {
			int comparison;
			int priorityLengthOfTaskA, priorityLengthOfTaskB;
			if (!taskAPriority.equals(Priority.NEUTRAL) 
					&& !taskBPriority.equals(Priority.NEUTRAL)) {
				priorityLengthOfTaskA = taskAPriority.getCommand().
						getAdvancedCommand().length();
				priorityLengthOfTaskB = taskBPriority.getCommand().
						getAdvancedCommand().length();
				comparison = priorityLengthOfTaskB - priorityLengthOfTaskA;
			} else if (taskAPriority.equals(Priority.NEUTRAL) 
					&& !taskBPriority.equals(Priority.NEUTRAL)) {
				comparison = 1;
			} else if (!taskAPriority.equals(Priority.NEUTRAL) 
					&& taskBPriority.equals(Priority.NEUTRAL)) {
				comparison = -1;
			} else {
				comparison = 0;
			}
			return comparison;
		}
	}
	
	public static ArrayList<Task> getTasksSortedByDate(ArrayList<Task> list) {
		Comparator<Task> comparator = getComparator(parametersForViewAll);
		Collections.sort(list, comparator);
		return list;
	}
	
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
}
