package service;

import model.*;
import service.exceptions.IntersectionTimeException;
import service.exceptions.TaskNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected static int nextId;
    protected final Map<Integer, Task> taskList;
    protected final TreeSet<Task> sortedByPriority;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        taskList = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        sortedByPriority = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime() == null) {
                return 1;
            } else {
                return task1.getStartTime()
                            .compareTo(task2.getStartTime());
            }
        });
    }

    @Override
    public int addToList(Task task) {
        if (task.getTaskID() == 0) {
            task.setTaskID(getNextId());
        }
        taskList.put(task.getTaskID(), task);
        if (task.getClass() != Epic.class) {
            checkForIntersections(task);
            //Добавил в класс TaskManagerTest (в конец) ещё один тест для проверки приоритета:
            // имя метода checkingCorrectSortingWhenTaskStartTimeChanges()
            //с закоментированной ниже строкой удаления из трисета тест не проходит, потому оставляю её
            sortedByPriority.remove(task);
            sortedByPriority.add(task);
        }
        return nextId;
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
            throw new TaskNotFoundException("Задача не найдена");
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
            throw new TaskNotFoundException("Задача не найдена");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Task getSubtask(int taskID) {
        for (Task task : getEpicTaskList()) {
            Epic epicTask = (Epic) task;
            if (epicTask.getListSubtaskID()
                        .contains(taskID)) {
                Task result = epicTask.getSubtask(taskID);
                historyManager.add(result);
                return result;
            }
        }
        throw new TaskNotFoundException("Задача не найдена.");
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
    public List<Task> getAllTasks() {
        return taskList.values()
                       .stream()
                       .sorted(Comparator.naturalOrder())
                       .collect(Collectors.toList());
    }

    @Override
    public void deleteTask(int taskID) {
        if (taskList.containsKey(taskID)) {
            if (taskList.get(taskID)
                        .getClass() == Epic.class) {
                deleteEpicTask(taskID);
                return;
            }
            taskList.remove(taskID);
            historyManager.remove(taskID);
            return;
        } else {
            for (Task task : getEpicTaskList()) {
                Epic epicTask = (Epic) task;
                if (epicTask.getListSubtaskID()
                            .contains(taskID)) {
                    epicTask.removeSubtaskByID(taskID);
                    historyManager.remove(taskID);
                    return;
                }
            }
        }
        throw new TaskNotFoundException("Задача не найдена");
    }

    @Override
    public void deleteEpicTask(int taskID) {
        if (!taskList.containsKey(taskID)) {
            throw new TaskNotFoundException("Задача не найдена");
        }
        Task task = taskList.get(taskID);
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            for (Task subtask : epic.getSubtaskList()) {
                historyManager.remove(subtask.getTaskID());
                taskList.remove(subtask.getTaskID());
            }
            historyManager.remove(taskID);
            taskList.remove(taskID);
        } else {
            throw new TaskNotFoundException("Задача не найдена");
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedByPriority);
    }

    public void printHistory() {
        historyManager.getHistory()
                      .forEach(System.out::println);
    }

    protected int getNextId() {
        return ++nextId;
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

    protected void checkForIntersections(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        for (Task checkedTask : sortedByPriority) {
            if (checkedTask.getStartTime() == null) {
                continue;
            }
            if (task.getStartTime()
                    .isAfter(checkedTask.getStartTime()) && task.getStartTime()
                                                                .isBefore(checkedTask.getEndTime())) {
                throw new IntersectionTimeException(String.format(
                        "Созданная задача с имеет пересечение по времени с задачей ID=%d по времени начала",
                        checkedTask.getTaskID()));
            } else if (task.getEndTime()
                           .isAfter(checkedTask.getStartTime()) && task.getEndTime()
                                                                       .isBefore(checkedTask.getEndTime())) {
                throw new IntersectionTimeException(String.format(
                        "Созданная задача с имеет пересечение по времени с задачей ID=%d по времени завершения",
                        checkedTask.getTaskID()));
                //добавил в класс TaskManagerTest (в конец) метод testIntersectionWhenNewTaskConsumesExistingOne()
                //если убрать этот блок, то исключение не кидается
            } else if (task.getStartTime()
                           .isBefore(checkedTask.getStartTime()) && task.getEndTime()
                                                                        .isAfter(checkedTask.getEndTime())) {
                throw new IntersectionTimeException(String.format(
                        "Созданная задача имеет пересечение по времени с задачей ID=%d по включению в диапазон",
                        checkedTask.getTaskID()));
            }
        }
    }

}
