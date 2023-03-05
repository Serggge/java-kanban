package service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
import model.Task;
import net.KVTaskClient;
import net.util.CustomGson;
import service.exceptions.HttpServerException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private static final File FILE = new File("src/resources/backup.csv");
    private final String url;
    private static KVTaskClient client;
    private final Gson gson = CustomGson.getGson();

    public HttpTaskManager(String url) {
        super(FILE);
        this.url = url;
        try {
            createClient(url);
            loadFromServer(url);
        } catch (HttpServerException e) {
            TaskManager taskManager = loadFromFile(FILE);
            taskManager.getAllTasks()
                       .forEach(this::addToList);
            taskManager.getHistory()
                       .forEach(historyManager::add);
        }
    }

    @Override
    protected void save() {
        super.save();
        if (client == null) {
            createClient(url);
        } else {
            try {
                String taskListJson = gson.toJson(getTasks(), new TypeToken<List<Task>>() {
                }.getType());
                String subTaskListJson = gson.toJson(getSubTasks(), new TypeToken<List<Subtask>>() {
                }.getType());
                String epicListJson = gson.toJson(getEpics(), new TypeToken<List<Subtask>>() {
                }.getType());
                String historyJson = gson.toJson(historyManager.getHistory(), new TypeToken<List<Epic>>() {
                }.getType());
                client.put("Tasks", taskListJson);
                client.put("Subtasks", subTaskListJson);
                client.put("Epics", epicListJson);
                client.put("History", historyJson);
            } catch (IOException | InterruptedException e) {
                throw new HttpServerException("Ошибка при сериализации на KVServer", e);
            }
        }
    }

    public void loadFromServer(String url) {
        try {
            String tasksJson = client.load("Tasks");
            String subTasksJson = client.load("Subtasks");
            String epicsJson = client.load("Epics");
            String historyJson = client.load("History");
            List<Task> taskList = gson.fromJson(tasksJson, new TypeToken<List<Task>>() {
            }.getType());
            List<Task> subTaskList = gson.fromJson(subTasksJson, new TypeToken<List<Subtask>>() {
            }.getType());
            List<Task> epicList = gson.fromJson(epicsJson, new TypeToken<List<Epic>>() {
            }.getType());
            List<Task> history = gson.fromJson(historyJson, new TypeToken<List<Task>>() {
            }.getType());
            if (taskList == null) {
                throw new HttpServerException("Не удалось восстановить задачи из KVServer");
            } else {
                taskList.forEach(this::addToList);
                epicList.forEach(this::addToList);
                subTaskList.forEach(this::addToList);
                history.forEach(task -> getAnyTask(task.getId()));
            }
        } catch (IOException | InterruptedException e) {
            throw new HttpServerException("Ошибка при подключении к KVServer", e);
        }
    }

    private void createClient(String url) {
        client = new KVTaskClient(url);
    }

}
