package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    protected static int taskID;
    protected final Map<Integer, Task> taskList = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void addToList(Task task) {
        if (task.getTaskID() == 0) {
            task.setTaskID(getNextID());
        }
            taskList.put(task.getTaskID(), task);
    }

    @Override
    public Task getTask(int taskID) {
        Task result;
        if (taskList.containsKey(taskID)) {
            result = taskList.get(taskID);
            historyManager.add(result);
        } else {
            result = getSubtask(taskID);
        }
        if (result == null) {
            throw new RuntimeException("При вызове метода getTask(int taskID) возникла ошибка: задача " +
                    "с указанным ID не найдена");
        }
        return result;
    }

    @Override
    public Epic getEpic(int taskID) {
        Epic epic = null;
        Task task = taskList.get(taskID);
        if (task instanceof Epic) {
            epic = (Epic) task;
        }
        if (epic == null) {
            throw new RuntimeException("Эпик с ID = " + taskID + " не найден");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Task getSubtask(int taskID) {
        for (Task task : getEpicTaskList()) {
            Epic epicTask = (Epic) task;
            if (epicTask.getListSubtaskID().contains(taskID)) {
                Task result = epicTask.getSubtask(taskID);
                historyManager.add(result);
                return result;
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
            if (taskList.get(taskID).getClass() == Epic.class) {
                deleteEpicTask(taskID);
                return;
            }
            taskList.remove(taskID);
            historyManager.remove(taskID);
            return;
        } else {
            for (Task task : getEpicTaskList()) {
                Epic epicTask = (Epic) task;
                if (epicTask.getListSubtaskID().contains(taskID)) {
                    epicTask.removeSubtaskByID(taskID);
                    historyManager.remove(taskID);
                    return;
                }
            }
        }
        throw new RuntimeException("Задача с ID=" + taskID + " в списках не найдена.");
    }

    @Override
    public void deleteEpicTask(int taskID) {
        if (!taskList.containsKey(taskID)) {
            throw new RuntimeException("Ошибка при вызове метода deleteEpicTask(int taskID) - " +
                    "задача с указанным ID в списке не найдена");
        }
        Task task = taskList.get(taskID);
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            for (Task subtask : epic.getSubtaskList()) {
                historyManager.remove(subtask.getTaskID());
            }
            historyManager.remove(taskID);
            taskList.remove(taskID);
        } else {
            throw new RuntimeException("Ошибка при вызове метода deleteEpicTask(int taskID) - " +
                    "указанный ID задачи принадлежит не Эпику");
        }
    }

    @Override
    public void deleteTasks() {
        for (Task task : getTaskList()) {
            historyManager.remove(task.getTaskID());
            taskList.remove(task.getTaskID());
        }
    }

    @Override
    public void deleteAllTasks() {
        deleteTasks();
        for (Task task : getEpicTaskList()) {
            deleteEpicTask(task.getTaskID());
        }
    }

    public void printHistory() {
        historyManager.getHistory().forEach(System.out::println);
    }

    protected int getNextID() {
        return ++taskID;
    }

    protected List<Task> getListByClass(Class<? extends Task> clazz) {
        List<Task> typedList = new ArrayList<>();
        for (Task task : taskList.values()) {
            if (task.getClass() == clazz) {
                typedList.add(task);
            }
        }
        return typedList;
    }

}
