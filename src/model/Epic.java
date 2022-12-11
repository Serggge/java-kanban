package model;

import java.util.HashMap;

public class Epic extends Task {

    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();

    public Epic(String taskName, String description) {
        super(taskName, description, TaskStatus.NEW);
    }

    public HashMap<Integer, Subtask> getSubtaskList() {
        return subtaskList;
    }

}
