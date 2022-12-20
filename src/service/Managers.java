package service;

public class Managers {

    private final TaskManager taskManager;

    public Managers(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
