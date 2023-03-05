package service;

import model.*;
import service.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final Path path;

    public FileBackedTasksManager(File file) {
        path = Path.of(file.getPath());
    }

    @Override
    public int addToList(Task task) {
        int id = super.addToList(task);
        this.save();
        return id;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        this.save();
        return task;
    }

    @Override
    public Task getSubtaskById(int id) {
        Task task = super.getSubtaskById(id);
        this.save();
        return task;
    }

    @Override
    public Task getEpicById(int id) {
        Task task = super.getEpicById(id);
        this.save();
        return task;
    }

    @Override
    public List<Task> getEpicSubTasks(int id) {
        List<Task> subTasks = super.getEpicSubTasks(id);
        this.save();
        return subTasks;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        this.save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        this.save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        this.save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        this.save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        this.save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        this.save();
    }

    @Override
    public void clear() {
        super.clear();
        this.save();
    }

    public static TaskManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try (Stream<String> stream = Files.lines(file.toPath())) {
            List<String> tasks = stream.skip(1)
                                       .collect(Collectors.toList());
            if (tasks.isEmpty()) {
                return manager;
            } else {
                tasks.stream()
                     .takeWhile(line -> !line.isBlank())
                     .map(manager::fromString)
                     .forEach(manager::addToList);
            }
            String historyString = tasks.get(tasks.size() - 1);
            if (historyString.isEmpty()) {
                return manager;
            } else {
                historyString.lines()
                             .flatMap(line -> Arrays.stream(line.split(";")))
                             .mapToInt(Integer::parseInt)
                             .forEach(manager::getAnyTask);
            }
        } catch (IOException ex) {
            throw new ManagerLoadException("Ошибка при загрузке из файла", ex);
        }
        return manager;
    }

    protected void save() {
        try {
            Files.writeString(path, "id;type;name;status;description;date;time;duration;epic;\n",
                    StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING);
            Set<Task> sortedSet = new TreeSet<>(taskList.values());
            for (Task task : sortedSet) {
                Files.writeString(path, task.getStringForSave() + "\n", StandardCharsets.UTF_8, APPEND);
            }
            Files.writeString(path, "\n" + historyToString(historyManager), StandardCharsets.UTF_8, APPEND);
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка при записи в файл", ex);
        }
    }

    private Task fromString(String value) {
        int maxNumParams = 9;
        try {
            List<Optional<String>> params = Arrays.stream(value.split(";"))
                                                  .map(elem -> elem.isEmpty() ? null : elem)
                                                  .map(Optional::ofNullable)
                                                  .collect(Collectors.toList());
            while (params.size() < maxNumParams) {
                params.add(Optional.empty());
            }
            Task task;
            int taskId = Integer.parseInt(params.get(0)
                                                .orElseThrow());
            TaskType taskType = TaskType.valueOf(params.get(1)
                                                       .orElseThrow()
                                                       .toUpperCase());
            String taskName = params.get(2)
                                    .orElseThrow();
            TaskStatus taskStatus = TaskStatus.valueOf(params.get(3)
                                                             .orElseThrow());
            String description = params.get(4)
                                       .orElseThrow();
            Optional<String> date = params.get(5);
            Optional<String> time = params.get(6);
            int duration = params.get(7)
                                 .isPresent() ? Integer.parseInt(params.get(7)
                                                                       .get()) : 0;
            Optional<String> epicId = params.get(8);
            switch (taskType) {
                case TASK:
                    task = date.isEmpty() ? new Task(taskName, description) : new Task(taskName, description,
                            date.get(), time.orElseThrow(), duration);
                    break;
                case EPIC:
                    task = new Epic(taskName, description);
                    break;
                case SUBTASK:
                    Epic epic = (Epic) taskList.get(Integer.parseInt(epicId.orElseThrow()));
                    task = date.isEmpty() ? new Subtask(epic, taskName, description) : new Subtask(epic, taskName,
                            description, date.get(), time.orElseThrow(), duration);
                    break;
                default:
                    throw new TaskCreateFromFileException(
                            "Непредвиденная ошибка при попытке создать задачу" + " в методе fromString(String value)");
            }
            task.setId(taskId);
            task.setStatus(taskStatus);
            return task;
        } catch (Exception ex) {
            throw new TaskCreateFromFileException(
                    "Непредвиденная ошибка при попытке создать задачу" + " в методе fromString(String value)", ex);
        }
    }

    private static String historyToString(HistoryManager manager) {
        return manager.getHistory()
                      .stream()
                      .map(task -> String.valueOf(task.getId()))
                      .collect(Collectors.joining(";"));
    }

}
