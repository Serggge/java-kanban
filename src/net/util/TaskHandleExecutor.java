package net.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;
import service.TaskType;
import service.exceptions.TaskCreateFromJsonException;
import service.exceptions.TaskNotFoundException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TaskHandleExecutor {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final TaskManager taskManager;

    public TaskHandleExecutor(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = CustomGson.getGson();
/*        Epic epic = new Epic("Epic", "description");
        taskManager.addToList(epic);
        Task subtask = new Subtask(epic, "Subtask", "desc", "01.02.2023", "04:00", 10);
        taskManager.addToList(subtask);*/
    }

    public Map.Entry<String, Integer> execute(Endpoint endpoint, HttpExchange httpExchange) {
        Map.Entry<String, Integer> answer = null;
        switch (endpoint) {
            case ADD_TASK:
                answer = handleAddTask(httpExchange);
                break;
            case GET_TASK_BY_ID:
                answer = handleGetTaskById(httpExchange);
                break;
            case GET_TASKS_BY_TYPE:
                answer = handleGetTasksByType(httpExchange);
                break;
            case GET_EPIC_SUBTASKS:
                answer = handleGetEpicSubtasks(httpExchange);
                break;
            case DELETE_TASK_BY_ID:
                answer = handleDeleteTaskById(httpExchange);
                break;
            case DELETE_TASKS_BY_TYPE:
                answer = handleDeleteTasksByType(httpExchange);
                break;
            case GET_HISTORY:
                answer = handleGetHistory(httpExchange);
            case GET_PRIORITIZED_TASKS:
                answer = handleGetPrioritizedTasks(httpExchange);
            case UNKNOWN:
                answer = Map.entry("такого эндпоинта не существует", 404);
        }
        if (answer != null && answer.getValue().equals(200)) {
            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "application/json");
        }
        return answer;
    }

    private Map.Entry<String, Integer> handleAddTask(HttpExchange httpExchange) {
        Map.Entry<String, Integer> answer = Map.entry("По данному запросу ничего не найдено", 404);
        List<String> contentTypeValues = httpExchange.getRequestHeaders()
                                                     .get("Content-type");
        if (contentTypeValues == null || !contentTypeValues.contains("application/json")) {
            return Map.entry("Ожидаются данные в формате json", 400);
        }
        try {
            String jsonString = new String(httpExchange.getRequestBody()
                                                       .readAllBytes(), DEFAULT_CHARSET);
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String taskType = jsonObject.get("TASK_TYPE")
                                        .getAsString();
            Task task = null;
            switch (TaskType.valueOf(taskType.toUpperCase())) {
                case TASK:
                    task = gson.fromJson(jsonString, Task.class);
                    answer = Map.entry("Задача создана", 200);
                    break;
                case SUBTASK:
                    task = gson.fromJson(jsonString, Subtask.class);
                    answer = Map.entry("Подзадача создана", 200);
                    break;
                case EPIC:
                    task = gson.fromJson(jsonString, Epic.class);
                    answer = Map.entry("Эпик создан", 200);
            }
            taskManager.addToList(task);
        } catch (Exception exception) {
            answer = Map.entry("Некорректный api запрос", 400);
            throw new TaskCreateFromJsonException("Ошибка при создании задачи из Json", exception);
        } finally {
            return answer;
        }
    }

    private Map.Entry<String, Integer> handleGetTaskById(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI()
                                       .getQuery();
            int id = Integer.parseInt(query.split("=")[1]);
            String path = httpExchange.getRequestURI()
                                      .getPath();
            TaskType taskType = TaskType.valueOf(path.split("/")[2].toUpperCase());
            Task task = null;
            switch (taskType) {
                case TASK:
                    task = taskManager.getTaskById(id);
                    break;
                case SUBTASK:
                    task = taskManager.getSubtaskById(id);
                    break;
                case EPIC:
                    task = taskManager.getEpicById(id);
            }
            String jsonString = gson.toJson(task);
            return Map.entry(jsonString, 200);
        } catch (TaskNotFoundException e) {
            return Map.entry(e.getMessage(), 404);
        } catch (Exception e) {
            return Map.entry("Некорректный запрос", 400);
        }
    }

    private Map.Entry<String, Integer> handleGetEpicSubtasks(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI()
                                       .getQuery();
            int id = Integer.parseInt(query.split("=")[1]);
            List<Task> subTaskList = taskManager.getEpicSubTasks(id);
            String jsonString = gson.toJson(subTaskList);
            return Map.entry(jsonString, 200);
        } catch (TaskNotFoundException e) {
            return Map.entry(e.getMessage(), 404);
        }
    }

    private Map.Entry<String, Integer> handleDeleteTaskById(HttpExchange httpExchange) {
        try {
            String query = httpExchange.getRequestURI()
                                       .getQuery();
            int id = Integer.parseInt(query.split("=")[1]);
            taskManager.deleteTask(id);
            return Map.entry("Задача удалена", 200);
        } catch (TaskNotFoundException e) {
            return Map.entry(e.getMessage(), 404);
        }
    }

    private Map.Entry<String, Integer> handleGetTasksByType(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI()
                                  .getPath();
        TaskType taskType = TaskType.valueOf(path.split("=")[2].toUpperCase());
        List<Task> taskListByType = null;
        switch (taskType) {
            case TASK:
                taskListByType = taskManager.getTasks();
                break;
            case SUBTASK:
                taskListByType = taskManager.getSubTasks();
                break;
            case EPIC:
                taskListByType = taskManager.getEpics();
        }
        String jsonString = gson.toJson(taskListByType, new TypeToken<List<Task>>() {
        }.getType());
        return Map.entry(jsonString, 200);
    }

    private Map.Entry<String, Integer> handleDeleteTasksByType(HttpExchange httpExchange) {
        String path = httpExchange.getRequestURI()
                                  .getPath();
        TaskType taskType = TaskType.valueOf(path.split("=")[2].toUpperCase());
        switch (taskType) {
            case TASK:
                taskManager.deleteTasks();
                break;
            case SUBTASK:
                taskManager.deleteSubTasks();
                break;
            case EPIC:
                taskManager.deleteEpics();
        }
        return Map.entry("Задачи удалены", 200);
    }

    private Map.Entry<String, Integer> handleGetPrioritizedTasks(HttpExchange httpExchange) {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String jsonString = gson.toJson(prioritizedTasks, new TypeToken<List<Task>>() {
        }.getType());
        return Map.entry(jsonString, 200);
    }

    private Map.Entry<String, Integer> handleGetHistory(HttpExchange httpExchange) {
        String jsonString = gson.toJson(taskManager.getHistory(), new TypeToken<List<Task>>() {
        }.getType());
        return Map.entry(jsonString, 200);
    }

}
