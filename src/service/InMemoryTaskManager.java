package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private static int taskID;
    private final Map<Integer, Task> taskList = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int getNextID() {
        return ++taskID;
    }

    @Override
    public void addToList(Task task) {
        if (task.getClass() != Subtask.class) {
            taskList.put(task.getTaskID(), task);
        }
    }

    @Override
    public Task getTask(int taskID) {
        checkRequestForError(taskID, Task.class);
        Task resultTask = taskList.get(taskID);
        historyManager.add(resultTask);
        return resultTask;
    }

    @Override
    public Task getEpic(int taskID) {
        checkRequestForError(taskID, Epic.class);
        Task resultTask = taskList.get(taskID);
        historyManager.add(resultTask);
        return resultTask;
    }

    @Override
    public Task getSubtask(int taskID) {
        for (Task task : getEpicTaskList()) {
            Epic epicTask = (Epic) task;
            if (epicTask.getListSubtaskID().contains(taskID)) {
                Task resultTask = epicTask.getSubtask(taskID);
                historyManager.add(resultTask);
                return resultTask;
            }
        }
        if (taskList.containsKey(taskID)) {
            throw new RuntimeException("Ошибка соответствия класса. Запрошен класс: " + Subtask.class +
                    ". Задача с ID=" + taskID + " соответствует классу: " + taskList.get(taskID).getClass());
        }
        throw new RuntimeException("Подзадача с ID=" + taskID + " в списках не найдена.");
    }

    @Override
    public List<Task> getTaskList() {
        return getListByClass(Task.class);
    }

    @Override
    public List<Task> getEpicTaskList() {
        return getListByClass(Epic.class);
    }

    @Override
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

    @Override
    public void deleteTask(int taskID) {
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
        throw new RuntimeException("Задача с ID=" + taskID + " в списках не найдена.");
    }

    @Override
    public void deleteTasks() {
        deleteTasksByClass(Task.class);
    }

    @Override
    public void deleteEpicTasks() {
        deleteTasksByClass(Epic.class);
    }

    @Override
    public void deleteSubtasks() {
        for (Task task : getEpicTaskList()) {
            Epic epicTask = (Epic) task;
            epicTask.removeSubtasks();
        }
    }

    @Override
    public void deleteAllTasks() {
        deleteTasks();
        deleteEpicTasks();
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

    public void printHistory() {
        historyManager.getHistory().forEach(System.out::println);
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

    private void checkRequestForError(int taskID, Class<? extends Task> clazz) {
        if (!taskList.containsKey(taskID)) {
            throw new RuntimeException("Задача с ID=" + taskID + " в списках не найдена.");
        }
        if (taskList.get(taskID).getClass() != clazz) {
            throw new RuntimeException("Ошибка соответствия класса. Запрошен класс: " + clazz +
                    ". Задача с ID=" + taskID + " соответствует классу: " + taskList.get(taskID).getClass());
        }
    }

}
