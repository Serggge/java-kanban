package service;

import model.Task;
import java.util.List;

public interface TaskManager {

    int addToList(Task task);

    Task getTaskById(int id);

    Task getSubtaskById(int id);

    Task getEpicById(int id);

    List<Task> getTasks();

    List<Task> getSubTasks();

    List<Task> getEpics();

    List<Task> getEpicSubTasks(int id);

    List<Task> getAllTasks();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    void deleteTasks();

    void deleteSubTasks();

    void deleteEpics();

    void clear();

    Task getAnyTask(int id);

    List<Task> getTaskListByType(TaskType taskType);

    void deleteTasksByType(TaskType taskType);

}
