package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private static int taskID;
    private static final HashMap<Integer, Task> taskList = new HashMap<>();
    private static final HashMap<Integer, Epic> epicTaskList = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtaskList = new HashMap<>();

    private TaskManager() {

    }

    public static void createTask(Task task) {
        taskID++;
        task.setTaskID(taskID);
        if (task.getClass() == Task.class) {
            taskList.put(task.getTaskID(), task);
        } else if (task.getClass() == Epic.class) {
            Epic epicTask = (Epic) task;
            epicTaskList.put(task.getTaskID(), epicTask);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            subtask.getParentTask().getSubtaskList().put(subtask.getTaskID(), subtask);
            subtaskList.put(task.getTaskID(), subtask);
            checkEpicTaskStatus(subtask.getParentTask());
        }
    }

    public static void updateTask(Task task) {
        if (task.getClass() == Task.class) {
            taskList.put(task.getTaskID(), task);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            subtask.getParentTask().getSubtaskList().put(subtask.getTaskID(), subtask);
            subtaskList.put(task.getTaskID(), subtask);
            checkEpicTaskStatus(subtask.getParentTask());
        } else if (task.getClass() == Epic.class) {
            Epic epicTask = (Epic) task;
            checkEpicTaskStatus(epicTask);
        }
    }

    private static void checkEpicTaskStatus(Epic epicTask) {
        HashMap<Integer, Subtask> taskList = epicTask.getSubtaskList();
        if (taskList.isEmpty()) {
            epicTask.setTaskStatus(TaskStatus.NEW);
            epicTaskList.put(epicTask.getTaskID(), epicTask);
        } else {
            boolean isFirstValue = true;
            TaskStatus firstSubtaskStatus = null;
            for (Subtask subtask : taskList.values()) {
                if (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                    epicTask.setTaskStatus(TaskStatus.IN_PROGRESS);
                    epicTaskList.put(epicTask.getTaskID(), epicTask);
                    return;
                }
                if (isFirstValue) {
                    firstSubtaskStatus = subtask.getTaskStatus();
                    isFirstValue = false;
                    continue;
                }
                if (subtask.getTaskStatus() != firstSubtaskStatus) {
                    epicTask.setTaskStatus(TaskStatus.IN_PROGRESS);
                    epicTaskList.put(epicTask.getTaskID(), epicTask);
                    return;
                }
            }
            switch (firstSubtaskStatus) {
                case NEW:
                    epicTask.setTaskStatus(TaskStatus.NEW);
                    break;
                case DONE:
                    epicTask.setTaskStatus(TaskStatus.DONE);
            }
            epicTaskList.put(epicTask.getTaskID(), epicTask);
        }
    }

    public static ArrayList<Task> getTaskList() {
        if (taskList.isEmpty()) {
            System.out.println("Список задач пуст.");
            return new ArrayList<>();
        }
        return new ArrayList<>(taskList.values());
    }

    public static ArrayList<Epic> getEpicTaskList() {
        if (epicTaskList.isEmpty()) {
            System.out.println("Список Эпиков пуст.");
            return new ArrayList<>();
        }
        return new ArrayList<>(epicTaskList.values());
    }

    public static ArrayList<Subtask> getSubtaskList() {
        if (subtaskList.isEmpty()) {
            System.out.println("Список подзадач пуст.");
            return new ArrayList<>();
        }
        return new ArrayList<>(subtaskList.values());
    }

    public static void deleteTasks() {
        taskList.clear();
    }

    public static void deleteEpicTasks() {
        for (Epic epicTask : epicTaskList.values()) {
            for (Subtask subtask : epicTask.getSubtaskList().values()) {
                subtaskList.remove(subtask.getTaskID());
            }
        }
        epicTaskList.clear();
    }

    public static void deleteSubtasks() {
        for (Subtask subtask : subtaskList.values()) {
            subtask.getParentTask().getSubtaskList().remove(subtask.getTaskID());
        }
        subtaskList.clear();
    }

    public static void deleteEpicTaskByID(int taskID) {
        for (Integer subtaskID : epicTaskList.get(taskID).getSubtaskList().keySet()) {
            subtaskList.remove(subtaskID);
        }
        epicTaskList.remove(taskID);
        System.out.printf("Эпик с ID=%d со всеми подзадачами удалён\n", taskID);
    }

    public static Task getTaskByID(int id) {
        Task result = null;
        if (taskList.containsKey(id)) {
            result = taskList.get(id);
        } else if (epicTaskList.containsKey(id)) {
            result = epicTaskList.get(id);
        } else if (subtaskList.containsKey(id)) {
            result = subtaskList.get(id);
        }
        return result;
    }

    public static void deleteTaskByID(int id) {
        if (taskList.containsKey(id)) {
            taskList.remove(id);
            System.out.printf("Задача с ID=%d удалена\n", id);
        } else if (epicTaskList.containsKey(id)) {
            Epic epicTask = epicTaskList.get(id);
            HashMap<Integer, Subtask> subtaskList = epicTask.getSubtaskList();
            for (Integer subtaskID : subtaskList.keySet()) {
                subtaskList.remove(subtaskID);
            }
            epicTaskList.remove(id);
            System.out.printf("Эпик с ID=%d удалён\n", id);
        } else if (subtaskList.containsKey(id)) {
            Epic epicTask = subtaskList.get(id).getParentTask();
            epicTask.getSubtaskList().remove(id);
            subtaskList.remove(id);
            System.out.printf("Подзадача с ID=%d удалена\n", id);
            checkEpicTaskStatus(epicTask);
        } else {
            System.out.printf("Задачи с ID=%d нет в списках задач\n", id);
        }
    }

    public static ArrayList<Subtask> getTaskListOfEpic(Epic epicTask) {
        return new ArrayList<>(epicTask.getSubtaskList().values());
    }

    public static ArrayList<Subtask> getTaskListOfEpicByID(int id) {
        ArrayList<Subtask> subtaskList = null;
        if (epicTaskList.containsKey(id)) {
            subtaskList = new ArrayList<>(epicTaskList.get(id).getSubtaskList().values());
        } else {
            System.out.printf("Эпик с ID=%d в списке не найден\n", id);
        }
        return subtaskList;
    }

    public static void printTaskList() {
        if (taskList.isEmpty()) {
            System.out.println("Список задач пуст.");
        } else {
            taskList.values().forEach(System.out::println);
        }
    }

    public static void printSubtaskList() {
        if (subtaskList.isEmpty()) {
            System.out.println("Список подзадач пуст.");
        } else {
            subtaskList.values().forEach(System.out::println);
        }
    }

    public static void printEpicTaskList() {
        if (epicTaskList.isEmpty()) {
            System.out.println("Список Эпиков пуст.");
        } else {
            epicTaskList.values().forEach(System.out::println);
        }
    }

    public static void printSubtaskListOfEpic(Epic epic) {
        epic.getSubtaskList().values().forEach(System.out::println);
    }

}
