package service;

import model.*;
import service.exceptions.IntersectionTimeException;
import service.exceptions.TaskNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> taskList;
    protected final TreeSet<Task> tasksByPriority;
    protected final HistoryManager historyManager;
    private static int nextId;

    public InMemoryTaskManager() {
        taskList = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        tasksByPriority = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime() == null) {
                return 1;
            } else if (task2.getStartTime() == null) {
                return -1;
            } else {
                return task1.getStartTime()
                            .compareTo(task2.getStartTime());
            }
        });
    }

    @Override
    public int addToList(Task task) {
        if (task.getId() == 0) {
            task.setId(getNextId());
        }
        taskList.put(task.getId(), task);
        if (task.getTaskType() != TaskType.EPIC) {
            checkForIntersections(task);
            tasksByPriority.remove(task);
            tasksByPriority.add(task);
        }
        return task.getId();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = getAnyTask(id);
        if (task != null && task.getTaskType() == TaskType.TASK) {
            return task;
        } else {
            throw new TaskNotFoundException("Задача не найдена");
        }
    }

    @Override
    public Task getSubtaskById(int id) {
        Task task = getAnyTask(id);
        if (task != null && task.getTaskType() == TaskType.SUBTASK) {
            return task;
        } else {
            throw new TaskNotFoundException("Подзадача не найдена");
        }
    }

    @Override
    public Task getEpicById(int id) {
        Task task = getAnyTask(id);
        if (task != null && task.getTaskType() == TaskType.EPIC) {
            return task;
        } else {
            throw new TaskNotFoundException("Эпик не найден");
        }
    }

    @Override
    public List<Task> getTasks() {
        return getTaskListByType(TaskType.TASK);
    }

    @Override
    public List<Task> getSubTasks() {
        return getTaskListByType(TaskType.SUBTASK);
    }

    @Override
    public List<Task> getEpics() {
        return getTaskListByType(TaskType.EPIC);
    }

    @Override
    public List<Task> getEpicSubTasks(int id) {
        if (taskList.containsKey(id) && taskList.get(id) instanceof Epic) {
            Epic epic = (Epic) taskList.get(id);
            return epic.getSubtasks();
        } else {
            throw new TaskNotFoundException("Эпик не найден");
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return taskList.values()
                       .stream()
                       .sorted(Comparator.naturalOrder())
                       .collect(Collectors.toList());
    }

    @Override
    public void deleteTask(int id) {
        if (taskList.containsKey(id) && taskList.get(id)
                                                .getTaskType() == TaskType.TASK) {
            tasksByPriority.remove(taskList.get(id));
            historyManager.remove(id);
            taskList.remove(id);
        } else {
            throw new TaskNotFoundException("Задача не найдена");
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (taskList.containsKey(id) && taskList.get(id) instanceof Epic) {
            Epic epic = (Epic) taskList.get(id);
            for (Task task : epic.getSubtasks()) {
                deleteSubtask(task.getId());
            }
            historyManager.remove(id);
            taskList.remove(id);
        } else {
            throw new TaskNotFoundException("Эпик не найден");
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (taskList.containsKey(id) && taskList.get(id) instanceof Subtask) {
            Subtask subtask = (Subtask) taskList.get(id);
            subtask.getEpic()
                   .deleteSubtaskByID(id);
            tasksByPriority.remove(subtask);
            historyManager.remove(id);
            taskList.remove(id);
        } else {
            throw new TaskNotFoundException("Подзадача не найдена");
        }
    }

    @Override
    public void deleteTasks() {
        deleteTasksByType(TaskType.TASK);
    }

    @Override
    public void deleteSubTasks() {
        deleteTasksByType(TaskType.SUBTASK);
    }

    @Override
    public void deleteEpics() {
        deleteTasksByType(TaskType.EPIC);
    }

    @Override
    public void clear() {
        tasksByPriority.clear();
        historyManager.clear();
        taskList.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(tasksByPriority);
    }

    protected int getNextId() {
        while (taskList.containsKey(nextId) || nextId == 0) {
            nextId++;
        }
        return nextId++;
    }

    protected void checkForIntersections(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        for (Task checkedTask : tasksByPriority) {
            if (checkedTask.getStartTime() == null) {
                continue;
            }
            if (task.getStartTime()
                    .isAfter(checkedTask.getStartTime()) && task.getStartTime()
                                                                .isBefore(checkedTask.getEndTime())) {
                throw new IntersectionTimeException(String.format(
                        "Созданная задача с имеет пересечение по времени с задачей ID=%d по времени начала",
                        checkedTask.getId()));
            } else if (task.getEndTime()
                           .isAfter(checkedTask.getStartTime()) && task.getEndTime()
                                                                       .isBefore(checkedTask.getEndTime())) {
                throw new IntersectionTimeException(String.format(
                        "Созданная задача с имеет пересечение по времени с задачей ID=%d по времени завершения",
                        checkedTask.getId()));
            } else if (task.getStartTime()
                           .isBefore(checkedTask.getStartTime()) && task.getEndTime()
                                                                        .isAfter(checkedTask.getEndTime())) {
                throw new IntersectionTimeException(String.format(
                        "Созданная задача имеет пересечение по времени с задачей ID=%d по включению в диапазон",
                        checkedTask.getId()));
            }
        }
    }


    protected Task getAnyTask(int id) {
        if (taskList.containsKey(id)) {
            Task task = taskList.get(id);
            historyManager.add(task);
            return task;
        } else {
            return null;
        }
    }


    private List<Task> getTaskListByType(TaskType taskType) {
        return taskList.values()
                       .stream()
                       .filter(task -> task.getTaskType() == taskType)
                       .collect(Collectors.toList());
    }

    private void deleteTasksByType(TaskType taskType) {
        List<Integer> listId = taskList.values()
                                       .stream()
                                       .filter(task -> task.getTaskType() == taskType)
                                       .map(Task::getId)
                                       .collect(Collectors.toList());
        switch (taskType) {
            case TASK:
                listId.forEach(this::deleteTask);
                break;
            case SUBTASK:
                listId.forEach(this::deleteSubtask);
                break;
            case EPIC:
                listId.forEach(this::deleteEpic);
        }
    }

    public void deleteAllWithoutSave() {
        taskList.clear();
        historyManager.clear();
    }

}
