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
        save();
        return id;
    }

    @Override
    public Task getAnyTask(int id) {
        Task task = super.getAnyTask(id);
        save();
        return task;
    }

    @Override
    public Task getEpicById(int id) {
        Task epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Task getSubtaskById(int id) {
        Task subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
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
                                                       .orElseThrow().toUpperCase());
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

    private static List<Integer> historyFromString(String value) {
        return value.lines()
                    .flatMap(line -> Arrays.stream(line.split(";")))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try (Stream<String> stream = Files.lines(file.toPath())) {
            List<String> tasks = stream.skip(1)
                                       .collect(Collectors.toList());
            tasks.stream()
                 .takeWhile(line -> !line.isBlank())
                 .map(manager::fromString)
                 .forEach(manager::addToList);
            tasks.get(tasks.size() - 1)
                 .lines()
                 .flatMap(line -> Arrays.stream(line.split(";")))
                 .mapToInt(Integer::parseInt)
                 .forEach(manager::getAnyTask);
        } catch (IOException ex) {
            throw new ManagerLoadException("Ошибка при загрузке из файла", ex);
        }
        return manager;
    }

}
