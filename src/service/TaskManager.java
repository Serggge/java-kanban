package service;

import model.Task;
import java.util.List;

public interface TaskManager {

    int getNextID();

    void addToList(Task task);

    Task getTask(int taskID);

    Task getEpic(int taskID);

    Task getSubtask(int taskID);

    List<Task> getTaskList();

    List<Task> getEpicTaskList();

    List<Task> getSubtaskList();

    void deleteTask(int taskID);

    void deleteTasks();

    void deleteEpicTasks();

    void deleteSubtasks();

    void deleteAllTasks();

}
