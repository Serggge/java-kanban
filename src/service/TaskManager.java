package service;

import model.Task;
import java.util.List;

public interface TaskManager {

    void addToList(Task task);

    void getTask(int taskID);

    void getEpic(int taskID);

    Task getSubtask(int taskID);

    List<Task> getTaskList();

    List<Task> getEpicTaskList();

    List<Task> getSubtaskList();

    void deleteTask(int taskID);

    void deleteTasks();

    void deleteEpicTask(int taskID);

    void deleteAllTasks();

}
