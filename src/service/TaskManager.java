package service;

import model.Epic;
import model.Task;
import java.util.List;

public interface TaskManager {

    int addToList(Task task);

    Task getTask(int taskID);

    Epic getEpic(int taskID);

    Task getSubtask(int taskID);

    List<Task> getTaskList();

    List<Task> getEpicTaskList();

    List<Task> getSubtaskList();

    List<Task> getAllTasks();

    void deleteTask(int taskID);

    void deleteTasks();

    void deleteEpicTask(int taskID);

    void deleteAllTasks();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
