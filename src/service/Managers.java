package service;

import java.io.File;

public final class Managers {

    private Managers() {}

    public static TaskManager getDefault() {
        return new HttpTaskManager("https://localhost:8078");
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
