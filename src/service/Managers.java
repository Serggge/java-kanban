package service;

public class Managers<T extends TaskManager> {

    private final T taskManager;

    public Managers(T taskManager) {
        this.taskManager = taskManager;
    }

    public T getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}