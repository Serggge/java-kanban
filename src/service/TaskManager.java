package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private static int taskID;
    private final Map<Integer, Task> taskList = new HashMap<>();

    public int getNextID() {
        return ++taskID;
    }

    public void addToList(Task task) {
        if (task.getClass() != Subtask.class) {
            taskList.put(task.getTaskID(), task);
        }
    }

    public List<Task> getTaskList() {
        return getListByClass(Task.class);
    }

    public List<Task> getEpicTaskList() {
        return getListByClass(Epic.class);
    }

    public List<Task> getSubtaskList() {
        List<Task> subtaskList = new ArrayList<>();
        for (Task task : taskList.values()) {
            if (task.getClass() == Epic.class) {
                Epic epicTask = (Epic) task;
                subtaskList.addAll(epicTask.getSubtaskList());
            }
        }
        return subtaskList;
    }

    public Task getTaskByID(int taskID) {
        Task result = null;
        if (taskList.containsKey(taskID)) {
            result = taskList.get(taskID);
        } else {
            for (Task task : getEpicTaskList()) {
                Epic epicTask = (Epic) task;
                if (epicTask.getListSubtaskID().contains(taskID)) {
                    return epicTask.getSubtask(taskID);
                }
            }
        }
        return result;
    }

    public void deleteTaskByID(int taskID) {
        if (taskList.containsKey(taskID)) {
            taskList.remove(taskID);
            System.out.printf("Задача с ID=%d удалена.\n", taskID);
            return;
        } else {
            for (Task task : getEpicTaskList()) {
                Epic epicTask = (Epic) task;
                if (epicTask.getListSubtaskID().contains(taskID)) {
                    epicTask.removeSubtaskByID(taskID);
                    System.out.printf("Подзадача с ID=%d удалена.\n", taskID);
                    return;
                }
            }
        }
        System.out.printf("Задачи с ID=%d нет в списках задач\n", taskID);
    }

    public void deleteTasks() {
        deleteTasksByClass(Task.class);
    }

    public void deleteEpicTasks() {
        deleteTasksByClass(Epic.class);
    }

    public void deleteSubtasks() {
        for (Task task : getEpicTaskList()) {
            Epic epicTask = (Epic) task;
            epicTask.removeSubtasks();
        }
    }

    public void printTaskList() {
        printTasksByClass(Task.class);
    }

    public void printEpicTaskList() {
        printTasksByClass(Epic.class);
    }

    public void printSubtaskList() {
        for (Task task : getEpicTaskList()) {
            Epic epicTask = (Epic) task;
            epicTask.printSubtaskList();
        }
    }

    private List<Task> getListByClass(Class<? extends Task> clazz) {
        List<Task> typedList = new ArrayList<>();
        for (Task task : taskList.values()) {
            if (task.getClass() == clazz) {
                typedList.add(task);
            }
        }
        return typedList;
    }

    private void deleteTasksByClass(Class<? extends Task> clazz) {
        for (Task task : taskList.values()) {
            if (task.getClass() == clazz) {
                taskList.remove(task.getTaskID());
            }
        }
    }

    private void printTasksByClass(Class<? extends Task> clazz) {
        for (Task task : taskList.values()) {
            if (task.getClass() == clazz) {
                System.out.println(task);
            }
        }
    }

}
