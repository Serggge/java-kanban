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

    public void printSubtaskList() {
        subtaskList.values().forEach(System.out::println);
    }

    protected void addSubtaskToList(Subtask subtask) {
        subtaskList.put(subtask.getTaskID(), subtask);
        changeStatus();
    }

    private void changeStatus() {
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
