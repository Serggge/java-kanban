package service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import model.Task;
import net.KVTaskClient;
import net.util.CustomGson;
import service.exceptions.HttpServerException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

public class HttpTaskManager extends FileBackedTasksManager {

    private static final File FILE = new File("src/resources/backup.csv");
    private KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url) {
        super(FILE);
        gson = CustomGson.getGson();
        try {
            client = new KVTaskClient(url);
            loadFromServer();
        } catch (HttpServerException | IOException | InterruptedException e) {
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
        String taskListJson = gson.toJson(taskList.values(), new TypeToken<List<Task>>() {
        }.getType());
        String historyJson = gson.toJson(historyManager.getHistory(), new TypeToken<List<Task>>() {
        }.getType());
        try {
            client.put("Tasks", taskListJson);
            client.put("History", historyJson);
        } catch (IOException | InterruptedException e) {
            throw new HttpServerException("Ошибка при сериализации на KVServer", e);
        }
    }

    public void loadFromServer() {
        try {
            String tasksJson = client.load("Tasks");
            String historyJson = client.load("History");
            List<Task> taskList = gson.fromJson(tasksJson, new TypeToken<TreeSet<Task>>() {
            }.getType());
            List<Task> history = gson.fromJson(historyJson, new TypeToken<TreeSet<Task>>() {
            }.getType());
            taskList.forEach(this::addToList);
            history.forEach(historyManager::add);
        } catch (IOException | InterruptedException e) {
            throw new HttpServerException("Ошибка при десериализации на KVServer", e);
        }
    }
}
