package model;

import java.util.Objects;

public class Task {

    protected String taskName;
    protected String description;
    protected int taskID;
    protected TaskStatus taskStatus;

    public Task(String taskName, String description) {
        this.taskName = taskName;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        if (this.taskID == 0 && taskID > 0) {
            this.taskID = taskID;
        }
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskID == task.taskID && Objects.equals(taskName, task.taskName) &&
                Objects.equals(description, task.description) && taskStatus == task.taskStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, taskID, taskStatus);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", taskID=" + taskID +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
