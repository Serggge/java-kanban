package model;

public class Subtask extends Task {

    public Subtask(int taskID, Epic epicTask, String taskName, String description, TaskStatus taskStatus) {
        super(taskID, taskName, description, taskStatus);
        epicTask.addSubtaskToList(this);
        if (taskStatus == TaskStatus.DONE) {
            epicTask.changeStatus();
        }
    }

}
