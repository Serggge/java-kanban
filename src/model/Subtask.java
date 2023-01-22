package model;

public class Subtask extends Task {

    private final Epic epicTask;

    public Subtask(Epic epicTask, String taskName, String description) {
        super(taskName, description);
        this.epicTask = epicTask;
    }

    @Override
    public void setTaskID(int taskID) {
        super.setTaskID(taskID);
        epicTask.addSubtaskToList(this);
    }

    @Override
    public void setStatus(TaskStatus taskStatus) {
        if (this.taskStatus != taskStatus) {
            this.taskStatus = taskStatus;
            epicTask.addSubtaskToList(this);
            epicTask.changeStatus();
        }
    }

    @Override
    public String toString() {
        return super.toString() + epicTask.getTaskID();
    }

}
