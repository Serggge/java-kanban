package service;

import model.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final Path path;

    public FileBackedTasksManager(File file) {
        path = Path.of(file.getPath());
    }

    public static void main(String[] args) {
        File file = new File("src/resources/backup.csv");
        TaskManager taskManager = new FileBackedTasksManager(file);

        Task goWalk = new Task("Пойти на прогулку", "Минут 15-30 спокойной ходьбы");
        Task cleanRoom = new Task("Прибраться в квартире", "Не забыть пропылесосить!");
        taskManager.addToList(goWalk);
        taskManager.addToList(cleanRoom);
        Epic becomeDeveloper = new Epic("Стать разработчиком",
                "Освоить технологию Java и устроиться на работу по специальности");
        taskManager.addToList(becomeDeveloper);
        Subtask studyJava = new Subtask(becomeDeveloper, "Освоить технологию Java",
                "Пройти курсы по разработке на Java");
        taskManager.addToList(studyJava);
        Subtask findJob = new Subtask(becomeDeveloper,
                "Найти работу по специальности", "Специальность Java developer");
        taskManager.addToList(findJob);
        Epic findSock = new Epic("Найти второй носок после стирки", "Крайне маловероятно");
        taskManager.addToList(findSock);
        studyJava.setStatus(TaskStatus.DONE);
        findJob.setStatus(TaskStatus.DONE);
        taskManager.getTask(1);
        taskManager.getTask(3);
        taskManager.getTask(2);
        taskManager.getTask(4);

        FileBackedTasksManager manager = loadFromFile(file);
        System.out.println("Список простых задач:");
        for (Task task : manager.getTaskList()) {
            System.out.println(task.toString());
        }
        System.out.println("-------------------------------------");
        System.out.println("Список эпиков и их подзадач, если есть:");
        for (Task task : manager.getEpicTaskList()) {
            System.out.println(task.toString());
            Epic epic = (Epic) task;
            for (Task subtask : epic.getSubtaskList()) {
                System.out.println(subtask.toString());
            }
        }
        System.out.println("-------------------------------");
        System.out.println("Отдельно подзадачи:");
        for (Task task : manager.getSubtaskList()) {
            System.out.println(task.toString());
        }
        System.out.println("--------------------------------");
        System.out.println("История просмотров после восстановления:");
        manager.printHistory();
    }

    @Override
    public void addToList(Task task) {
        super.addToList(task);
        save();
    }

    @Override
    public Task getTask(int taskID) {
        Task task = super.getTask(taskID);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int taskID) {
        Epic epic = super.getEpic(taskID);
        save();
        return epic;
    }

    @Override
    public Task getSubtask(int taskID) {
        Task subtask = super.getSubtask(taskID);
        save();
        return subtask;
    }

    private void save() {
        try {
            Files.writeString(path, "id;type;name;status;description;epic\n", StandardCharsets.UTF_8,
                    CREATE, TRUNCATE_EXISTING);
            for (Task task : taskList.values()) {
                Files.writeString(path, task.toString() + "\n", StandardCharsets.UTF_8, APPEND);
            }
            Files.writeString(path, historyToString(historyManager), StandardCharsets.UTF_8, APPEND);
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка при записи в файл", ex);
        }
    }

    private Task fromString(String value) {
        Task task = null;
        String[] elements = value.split(";");
        switch (TaskType.valueOf(elements[1].toUpperCase())) {
            case SUBTASK:
                int epicTaskId = Integer.parseInt(elements[5]);
                task = new Subtask(getEpic(epicTaskId), elements[2], elements[4]);
                break;
            case TASK:
                task = new Task(elements[2], elements[4]);
                break;
            case EPIC:
                task = new Epic(elements[2], elements[4]);
        }
        task.setTaskID(Integer.parseInt(elements[0]));
        task.setStatus(TaskStatus.valueOf(elements[3]));
        return task;
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> taskHistory = manager.getHistory();
        StringBuilder sb = new StringBuilder("\n");
        for (Task task : taskHistory) {
            sb.append(task.getTaskID()).append(";");
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> taskHistory = new ArrayList<>();
        String[] elements = value.split(";");
        for (String taskId : elements) {
            taskHistory.add(Integer.valueOf(taskId));
        }
        return taskHistory;
    }

    private static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        List<String> backUp;
        try {
            backUp = Files.readAllLines(Path.of(file.getAbsolutePath()));
            backUp.remove(0);
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", ex);
        }
        for (String entry : backUp) {
            if (entry.isBlank()) {
                break;
            }
            Task task = manager.fromString(entry);
            manager.taskList.put(task.getTaskID(), task);
        }
        String historyEntry = backUp.get(backUp.size() - 1);
        for (int taskId : historyFromString(historyEntry)) {
            manager.getTask(taskId);
        }
        return manager;
    }
}
