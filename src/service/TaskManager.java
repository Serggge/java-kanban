package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TaskManager {

    private static int taskID;
    private final HashMap<Integer, Task> taskList = new HashMap<>();
    private final HashMap<Integer, Epic> epicTaskList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();

    public void createTask(Task task) {
        taskID++;
        task.setTaskID(taskID);
        if (task.getClass() == Task.class) {
            taskList.put(task.getTaskID(), task);
        } else if (task.getClass() == Epic.class) {
            Epic epicTask = (Epic) task;
            epicTaskList.put(epicTask.getTaskID(), epicTask);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            subtask.addToEpic();
            subtaskList.put(subtask.getTaskID(), subtask);
            checkEpicTaskStatus(subtask.getParentTask());
        }
    }

    public void updateTask(Task task) {
        if (task.getClass() == Task.class) {
            taskList.put(task.getTaskID(), task);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            subtask.addToEpic();
            subtaskList.put(subtask.getTaskID(), subtask);
            checkEpicTaskStatus(subtask.getParentTask());
        } else if (task.getClass() == Epic.class) {
            Epic epicTask = (Epic) task;
            epicTaskList.put(epicTask.getTaskID(), epicTask);
            checkEpicTaskStatus(epicTask);
        }
    }

    private void checkEpicTaskStatus(Epic epicTask) {
        HashSet<Subtask> subtaskList = epicTask.getSubtaskList();
        if (subtaskList.isEmpty()) {
            epicTask.setTaskStatus(TaskStatus.NEW);
            epicTaskList.put(epicTask.getTaskID(), epicTask);
        } else {
            boolean isFirstValue = true;
            TaskStatus firstSubtaskStatus = null;
            for (Subtask subtask : subtaskList) {
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

    public ArrayList<Task> getTaskList() {
        if (taskList.isEmpty()) {
            System.out.println("Список задач пуст.");
            return new ArrayList<>();
        }
        return new ArrayList<>(taskList.values());
    }

    public ArrayList<Epic> getEpicTaskList() {
        if (epicTaskList.isEmpty()) {
            System.out.println("Список Эпиков пуст.");
            return new ArrayList<>();
        }
        return new ArrayList<>(epicTaskList.values());
    }

    public ArrayList<Subtask> getSubtaskList() {
        if (subtaskList.isEmpty()) {
            System.out.println("Список подзадач пуст.");
            return new ArrayList<>();
        }
        return new ArrayList<>(subtaskList.values());
    }

    public void deleteTasks() {
        taskList.clear();
    }

    public void deleteEpicTasks() {
        for (Epic epicTask : epicTaskList.values()) {
            for (Subtask subtask : epicTask.getSubtaskList()) {
                subtaskList.remove(subtask.getTaskID());
            }
        }
        epicTaskList.clear();
    }

    public void deleteSubtasks() {
        HashSet<Epic> epicTasks = new HashSet<>();
        for (Subtask subtask : subtaskList.values()) {
            epicTasks.add(subtask.getParentTask());
            subtask.removeFromEpic();
        }
        for (Epic epicTask : epicTasks) {
            checkEpicTaskStatus(epicTask);
        }
        subtaskList.clear();
    }

    public void deleteEpicTaskByID(int taskID) {
        if (!epicTaskList.containsKey(taskID)) {
            System.out.printf("Не удалось удалить Эпик с ID=%d. Указанный ID в списке не найден\n", taskID);
            return;
        }
        for (Integer subtaskID : epicTaskList.get(taskID).getListSubtaskID()) {
            if (subtaskList.containsKey(subtaskID)) {
                subtaskList.remove(subtaskID);
            } else {
                System.out.printf("При попытке удалить Эпик с ID=%d возникла ошибка удаления связанной" +
                        " подзадачи с ID=%d. Подзадача с таким ID в списке не найдена\n", taskID, subtaskID);
            }
        }
        epicTaskList.remove(taskID);
        System.out.printf("Эпик с ID=%d со всеми подзадачами удалён\n", taskID);
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
        if (result == null) {
            System.out.printf("Задача с ID=%d не найдена\n", taskID);
        }
        return result;
    }

    public void deleteTaskByID(int taskID) {
        if (taskList.containsKey(taskID)) {
            taskList.remove(taskID);
            System.out.printf("Задача с ID=%d удалена\n", taskID);
        } else if (epicTaskList.containsKey(taskID)) {
            Epic epicTask = epicTaskList.get(taskID);
            HashSet<Integer> listID = epicTask.getListSubtaskID();
            for (Integer subtaskID : listID) {
                subtaskList.remove(subtaskID);
            }
            epicTaskList.remove(taskID);
            System.out.printf("Эпик с ID=%d удалён\n", taskID);
        } else if (subtaskList.containsKey(taskID)) {
            Subtask subtask = subtaskList.get(taskID);
            subtask.removeFromEpic();
            checkEpicTaskStatus(subtask.getParentTask());
            subtaskList.remove(taskID);
            System.out.printf("Подзадача с ID=%d удалена\n", taskID);
        } else {
            System.out.printf("Задачи с ID=%d нет в списках задач\n", taskID);
        }
    }

    public ArrayList<Subtask> getTaskListOfEpic(Epic epicTask) {//вернуть пустую коллекцию, если она пустая
        return new ArrayList<>(epicTask.getSubtaskList());
    }

    public ArrayList<Subtask> getSubtaskListOfEpicByID(int taskID) {
        ArrayList<Subtask> subtaskList;
        if (epicTaskList.containsKey(taskID)) {
            Epic epicTask = epicTaskList.get(taskID);
            subtaskList = new ArrayList<>(epicTask.getSubtaskList());
        } else {
            System.out.printf("Эпик с ID=%d в списке не найден\n", taskID);
            subtaskList = new ArrayList<>();
        }
        return subtaskList;
    }

    public void printTaskList() {
        if (taskList.isEmpty()) {
            System.out.println("Список задач пуст.");
        } else {
            taskList.values().forEach(System.out::println);
        }
    }

    public void printSubtaskList() {
        if (subtaskList.isEmpty()) {
            System.out.println("Список подзадач пуст.");
        } else {
            subtaskList.values().forEach(System.out::println);
        }
    }

    public void printEpicTaskList() {
        if (epicTaskList.isEmpty()) {
            System.out.println("Список Эпиков пуст.");
        } else {
            epicTaskList.values().forEach(System.out::println);
        }
    }

    public void printSubtaskListOfEpic(Epic epic) {
        epic.getSubtaskList().forEach(System.out::println);
    }

}
