package model;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

public class Task implements Comparable<Task> {

    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    protected static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    protected String taskName;
    protected String description;
    protected int taskID;
    protected TaskStatus taskStatus;
    protected Supplier<Duration> duration;
    protected Supplier<LocalDateTime> startTime;

    public Task(String taskName, String description) {
        this.taskName = taskName;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
    }

    public Task(String taskName, String description, String date, String time, int duration) {
        this(taskName, description);
        setDateTime(date, time);
        setDuration(duration);
    }


    public LocalDateTime getStartTime() {
        return startTime != null ? startTime.get() : null;
    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.get()
                                            .plus(duration.get()) : null;
    }

    public Duration getDuration() {
        return duration != null ? duration.get() : null;
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

    public String getDateTime() {
        return startTime.get() != null ? startTime.get().format(DATE_TIME_FORMATTER) : "none";
    }

    public void setDateTime(String date, String time) {
        startTime = () -> LocalDateTime.of(LocalDate.parse(date, DATE_FORMATTER),
                LocalTime.parse(time, TIME_FORMATTER));
    }

    public void setCurrentDateTime() {
        startTime = LocalDateTime::now;
    }

    public void setDuration(int duration) {
        this.duration = () -> Duration.ofMinutes(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskID == task.taskID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskID);
    }

    public String getStringForSave() {
        StringBuilder sb = new StringBuilder();
        sb.append(taskID)
          .append(";")
          .append(getClass().getSimpleName())
          .append(";")
          .append(taskName)
          .append(";")
          .append(taskStatus)
          .append(";")
          .append(description)
          .append(";");
        if (this.getStartTime() != null) {
            sb.append(startTime.get()
                               .toLocalDate()
                               .format(DATE_FORMATTER))
              .append(";");
            sb.append(startTime.get()
                               .toLocalTime()
                               .format(TIME_FORMATTER))
              .append(";");
            if (this.getDuration() != null) {
                sb.append(duration.get()
                                  .toMinutes())
                  .append(";");
            }
        } else {
            sb.append(";;;");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        String startTime = this.getStartTime() == null ? "none" : this.getStartTime()
                                                                      .format(DATE_TIME_FORMATTER);
        String duration = this.getDuration() == null ? "none" : String.valueOf(this.getDuration()
                                                                                   .toMinutes());
        return String.format("ID=%d Name:%s Description:%s Type:%s Status:%s Start:%s Duration_in_minutes:%s", taskID,
                taskName, description, getClass().getSimpleName(), taskStatus, startTime, duration);
    }

    @Override
    public int compareTo(Task o) {
        return taskID - o.taskID;
    }

}
