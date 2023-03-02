package net.util;

import com.google.gson.Gson;

import static service.TaskType.EPIC;
import static service.TaskType.TASK;

public enum GsonType {
    TASK_GSON (GsonFactory.createGson(TASK)),
    EPIC_GSON (GsonFactory.createGson(EPIC));

    private final Gson gson;

    private GsonType(Gson gson) {
        this.gson = gson;
    }

    public Gson get() {
        return this.gson;
    }
}
