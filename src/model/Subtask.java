package model;

public class Subtask extends Task {

    private final Epic epicTask;

    public Subtask(int taskID, Epic epicTask, String taskName, String description, TaskStatus taskStatus) {
        super(taskID, taskName, description, taskStatus);
        this.epicTask = epicTask;
        epicTask.addSubtaskToList(this);
        epicTask.changeStatus();
    }

    @Override
    public void setStatus(TaskStatus taskStatus) {
        if (this.taskStatus != taskStatus) {
            this.taskStatus = taskStatus;
            epicTask.addSubtaskToList(this);
            epicTask.changeStatus();
        }
    }

}
