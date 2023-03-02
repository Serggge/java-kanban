package model;

import service.exceptions.TaskNotFoundException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Supplier;

public class Epic extends Task {

    private final Map<Integer, Subtask> subtaskList;
    private final Supplier<LocalDateTime> endTime;

    public Epic(String taskName, String description) {
        super(taskName, description, LocalDate.now()
                                              .format(DATE_FORMATTER), LocalTime.now()
                                                                                .format(TIME_FORMATTER), 0);
        subtaskList = new HashMap<>();
        startTime = () -> subtaskList.values()
                                     .stream()
                                     .map(Task::getStartTime)
                                     .filter(Objects::nonNull)
                                     .min(Comparator.naturalOrder())
                                     .orElse(null);
        endTime = () -> subtaskList.values()
                                   .stream()
                                   .map(Task::getEndTime)
                                   .filter(Objects::nonNull)
                                   .max(Comparator.naturalOrder())
                                   .orElse(null);
        duration = () -> startTime.get() != null ? Duration.between(startTime.get(), endTime.get()) : null;

    }

    public List<Task> getSubtasks() {
        return new ArrayList<>(subtaskList.values());
    }

    public List<Integer> getListSubtaskID() {
        return new ArrayList<>(subtaskList.keySet());
    }

    public Task getSubtask(int id) {
        return subtaskList.get(id);
    }

    public void printSubtaskList() {
        subtaskList.values()
                   .forEach(System.out::println);
    }

    public void removeSubtasks() {
        subtaskList.clear();
        taskStatus = TaskStatus.NEW;
    }

    public void deleteSubtaskByID(int id) {
        if (subtaskList.containsKey(id)) {
            subtaskList.remove(id);
            if (subtaskList.isEmpty()) {
                taskStatus = TaskStatus.NEW;
            } else {
                changeStatus();
            }
        } else {
            throw new TaskNotFoundException("Подзадача не найдена");
        }
    }

    @Override
    public void setStatus(TaskStatus taskStatus) {
        changeStatus();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime != null ? endTime.get() : null;
    }

    protected void addSubtaskToList(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
    }

    protected void changeStatus() {
        boolean isFirstTask = true;
        TaskStatus firstSubtaskStatus = null;
        for (Subtask subtask : subtaskList.values()) {
            if (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                taskStatus = TaskStatus.IN_PROGRESS;
                return;
            }
            if (isFirstTask) {
                firstSubtaskStatus = subtask.getTaskStatus();
                isFirstTask = false;
                continue;
            }
            if (subtask.getTaskStatus() != firstSubtaskStatus) {
                taskStatus = TaskStatus.IN_PROGRESS;
                return;
            }
            taskStatus = firstSubtaskStatus;
        }
    }

}
