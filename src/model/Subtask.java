package model;

public class Subtask extends Task {

    private final Epic epicTask;

    public Subtask(Epic epicTask, String taskName, String description) {
        super(taskName, description);
        this.epicTask = epicTask;
    }

    public Subtask(Epic epicTask, String taskName, String description, String date, String time, int duration) {
        super(taskName, description, date, time, duration);
        this.epicTask = epicTask;
    }

    public Epic getEpic() {
        return epicTask;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
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
    public String getStringForSave() {
        return super.getStringForSave() + epicTask.getId();
    }

    @Override
    public String toString() {
        return super.toString() + " EpicId=" + epicTask.getId();
    }

}
