package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {

    private final Map<Integer, Subtask> subtaskList = new HashMap<>();

    public Epic(String taskName, String description) {
        super(taskName, description);
    }

    public List<Task> getSubtaskList() {
        return new ArrayList<>(subtaskList.values());
    }

    public List<Integer> getListSubtaskID() {
        return new ArrayList<>(subtaskList.keySet());
    }

    public Task getSubtask(int subtaskID) {
        return subtaskList.get(subtaskID);
    }
    
    public void printSubtaskList() {
        subtaskList.values().forEach(System.out::println);
    }

    public void removeSubtasks() {
        subtaskList.clear();
        taskStatus = TaskStatus.NEW;
    }

    public void removeSubtaskByID(int taskID) {
        subtaskList.remove(taskID);
        if (subtaskList.isEmpty()) {
            taskStatus = TaskStatus.NEW;
        } else {
            changeStatus();
        }
    }

    @Override
    public void setStatus(TaskStatus taskStatus) {
        changeStatus();
    }

    protected void addSubtaskToList(Subtask subtask) {
        subtaskList.put(subtask.getTaskID(), subtask);
    }

    protected void changeStatus() {
        boolean isFirstTask = true;
        TaskStatus firstSubtaskStatus = null;
        for (Subtask subtask : subtaskList.values()) {
            if (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                taskStatus = TaskStatus.IN_PROGRESS;
                return;
            }
            if (isFirstTask) {
                firstSubtaskStatus = subtask.getTaskStatus();
                isFirstTask = false;
                continue;
            }
            if (subtask.getTaskStatus() != firstSubtaskStatus) {
                taskStatus = TaskStatus.IN_PROGRESS;
                return;
            }
            taskStatus = firstSubtaskStatus;
        }
    }

}
