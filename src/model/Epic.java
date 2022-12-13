package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {

    private final Map<Integer, Subtask> subtaskList = new HashMap<>();

    public Epic(int taskID, String taskName, String description) {
        super(taskID, taskName, description, TaskStatus.NEW);
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

    protected void addSubtaskToList(Subtask subtask) {
        if (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
            taskStatus = TaskStatus.IN_PROGRESS;
        }
        subtaskList.put(subtask.getTaskID(), subtask);
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

    protected void changeStatus() {
        boolean isEpicDone = true;
        for (Subtask subtask : subtaskList.values()) {
            if (subtask.getTaskStatus() != TaskStatus.DONE) {
                isEpicDone = false;
                break;
            }
        }
        taskStatus = isEpicDone ? TaskStatus.DONE : TaskStatus.IN_PROGRESS;
    }

}
