package model;

public class Subtask extends Task {

    private final Epic parentTask;

    public Subtask(Epic parentTask, String taskName, String description, TaskStatus taskStatus) {
        super(taskName, description, taskStatus);
        this.parentTask = parentTask;
    }

    public Subtask(int taskID, Epic parentTask, String taskName, String description, TaskStatus taskStatus) {
        this(parentTask, taskName, description, taskStatus);
        this.taskID = taskID;
    }

    public Epic getParentTask() {
        return parentTask;
    }

    public void addToEpic() {
        parentTask.addSubtaskToList(this);
    }

    public void removeFromEpic() {
        parentTask.removeSubtaskFromList(taskID);
    }

}
