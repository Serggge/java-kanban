package model;

import service.TaskType;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

public class Task implements Comparable<Task> {

    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    protected static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final TaskType TASK_TYPE;
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
        TASK_TYPE = TaskType.valueOf(this.getClass()
                                         .getSimpleName()
                                         .toUpperCase());
    }

    public Task(String taskName, String description, String date, String time, int duration) {
        this(taskName, description);
        setDateTime(date, time);
        setDuration(duration);
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getTaskType() {
        return TASK_TYPE;
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

    public int getId() {
        return taskID;
    }

    public void setId(int id) {
        if (this.taskID == 0 && id > 0) {
            this.taskID = id;
        }
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getDateTime() {
        return startTime.get() != null ? startTime.get()
                                                  .format(DATE_TIME_FORMATTER) : "none";
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
          .append(TASK_TYPE)
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
        return String.format("Type:%s ID=%d Name:%s Description:%s Status:%s Start:%s Duration_in_minutes:%s",
                TASK_TYPE, taskID, taskName, description, taskStatus, startTime, duration);
    }

    @Override
    public int compareTo(Task o) {
        return taskID - o.taskID;
    }

}
