package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {

    private static int taskID;
    private final Map<Integer, Task> taskList = new HashMap<>();
    private final Map<Integer, Task> epicTaskList = new HashMap<>();
    private final Map<Integer, Task> subtaskList = new HashMap<>();

    public int getNextID() {
        return ++taskID;
    }

    public void addToList(Task task) {
        if (task.getClass() == Task.class) {
            taskList.put(task.getTaskID(), task);
        } else if (task.getClass() == Epic.class) {
            epicTaskList.put(task.getTaskID(), task);
        } else if (task.getClass() == Subtask.class) {
            subtaskList.put(task.getTaskID(), task);
        }
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskList.values());
    }

    public ArrayList<Task> getEpicTaskList() {
        return new ArrayList<>(epicTaskList.values());
    }

    public Task getTaskByID(int taskID) {
        Task result = null;
        if (taskList.containsKey(taskID)) {
            result = taskList.get(taskID);
        } else if (epicTaskList.containsKey(taskID)) {
            result = epicTaskList.get(taskID);
        } else if (subtaskList.containsKey(taskID)) {
            result = subtaskList.get(taskID);
        }
        return result;
    }

    public void deleteTaskByID(int taskID) {
        if (taskList.containsKey(taskID)) {
            taskList.remove(taskID);
            System.out.printf("Задача с ID=%d удалена\n", taskID);
        } else if (epicTaskList.containsKey(taskID)) {
            epicTaskList.remove(taskID);
            System.out.printf("Эпик с ID=%d удалён\n", taskID);
        } else if (subtaskList.containsKey(taskID)) {
            subtaskList.remove(taskID);
            System.out.printf("Подзадача с ID=%d удалена\n", taskID);
        } else {
            System.out.printf("Задачи с ID=%d нет в списках задач\n", taskID);
        }
    }

    public void deleteTasks() {
        taskList.clear();
    }

    public void deleteEpicTasks() {
        subtaskList.clear();
        epicTaskList.clear();
    }

    public void deleteSubtasks() {
        subtaskList.clear();
    }

    public void printTaskList() {
        if (taskList.isEmpty()) {
            System.out.println("Список задач пуст.");
        } else {
            taskList.values().forEach(System.out::println);
        }
    }

    public void printEpicTaskList() {
        if (epicTaskList.isEmpty()) {
            System.out.println("Список Эпиков пуст.");
        } else {
            epicTaskList.values().forEach(System.out::println);
        }
    }

}
