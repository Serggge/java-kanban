package model;

import java.util.HashMap;
import java.util.HashSet;

public class Epic extends Task {

    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();

    public Epic(String taskName, String description) {
        super(taskName, description, TaskStatus.NEW);
    }

    public HashSet<Subtask> getSubtaskList() {
        return new HashSet<>(subtaskList.values());
    }

    protected void addSubtaskToList(Subtask subtask) {
            subtaskList.put(subtask.getTaskID(), subtask);
    }

    protected void removeSubtaskFromList(int taskID) {
        if (subtaskList.containsKey(taskID)) {
            subtaskList.remove(taskID);
        } else {
            System.out.printf("Не удалось удалить подзадачу с ID=%d. " +
                    "Указанный ID в списке подзадач не найден\n", taskID);
        }
    }

    public HashSet<Integer> getListSubtaskID() {
        return new HashSet<>(subtaskList.keySet());
    }

}
