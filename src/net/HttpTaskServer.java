package net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.util.Endpoint;
import net.util.TaskHandleExecutor;
import service.Managers;
import service.TaskManager;
import service.exceptions.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer httpServer;
    private final TaskHandleExecutor taskHandlerExecutor;


    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.start();
/*        Type dateTimeType = new TypeToken<Supplier<LocalDateTime>>() {
        }.getType();
        Type durationType = new TypeToken<Supplier<Duration>>() {
        }.getType();
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Task1", "description");
        Task task2 = new Task("Task2", "description", "28.02.2023", "04:00", 10);
        Task epic = new Epic("Epic", "description");
        Task subtask = new Subtask((Epic) epic, "Subtask", "desc", "01.02.2023", "04:00", 10);
        int first = taskManager.addToList(task1);
        int second = taskManager.addToList(task2);
        int third = taskManager.addToList(epic);
        int fourth = taskManager.addToList(subtask);
        taskManager.getTask(third);
        taskManager.getTask(second);
        taskManager.getTask(first);
        taskManager.getTask(fourth);
        Type token = new TypeToken<List<Task>>() {
        }.getType();
        Gson gson =
        String jsonString = gson.toJson(taskManager.getHistory(), token);
        String jsonString = gson.toJson(subtask);
        System.out.println(jsonString);
        Task task = gson.fromJson(jsonString, Task.class);
        System.out.println(task);*/
    }

    public HttpTaskServer() {
/*        taskManager = Managers.getHttpTaskManager();
        gson = CustomGson.getGson();*/
        taskHandlerExecutor = new TaskHandleExecutor(Managers.getDefault());
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(8080), 0);
            httpServer.createContext("/tasks", new TasksHandler());
            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        } catch (IOException e) {
            throw new HttpServerException("Сбой при попытке создать Http-сервер", e);
        }
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    private Endpoint getEndpoint(String path, String query, String method) {
        Endpoint endpoint = Endpoint.UNKNOWN;
        if (query == null) {
            switch (method) {
                case "GET": {
                    if (Pattern.matches("^/tasks/?$", path)) {
                        endpoint = Endpoint.GET_PRIORITIZED_TASKS;
                    } else if (Pattern.matches("^/tasks/history/?$", path)) {
                        endpoint = Endpoint.GET_HISTORY;
                    } else if (Pattern.matches("^/tasks/(task|subtask|epic)/?$", path)) {
                        endpoint = Endpoint.GET_TASKS_BY_TYPE;
                    }
                }
                break;
                case "POST": {
                    if (Pattern.matches("^/tasks/?$", path)) {
                        endpoint = Endpoint.ADD_TASK;
                    }
                }
                break;
                case "DELETE": {
                    if (Pattern.matches("^/tasks/(task|subtask|epic)/?$", path)) {
                        endpoint = Endpoint.DELETE_TASKS_BY_TYPE;
                    }
                }
            }
        } else {
            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks/(task|subtask|epic)/$", path)) {
                        endpoint = Endpoint.GET_TASK_BY_ID;
                    } else if (Pattern.matches("^/tasks/subtask/epic/$", path)) {
                        endpoint = Endpoint.GET_EPIC_SUBTASKS;
                    }
                    break;
                case "POST":
                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/(task|subtask|epic)/$", path)) {
                        endpoint = Endpoint.DELETE_TASK_BY_ID;
                    }
            }
        }
        return endpoint;
    }

    private class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) {
            String path = httpExchange.getRequestURI()
                                      .getPath()
                                      .toLowerCase();
            String query = httpExchange.getRequestURI()
                                       .getQuery();
            String method = httpExchange.getRequestMethod();
            Endpoint endpoint = getEndpoint(path, query, method);
            Map.Entry<String, Integer> answer = taskHandlerExecutor.execute(endpoint, httpExchange);
            writeResponse(httpExchange, answer);

/*            switch (endpoint) {
                case GET_HISTORY:
                    handleGetHistory(httpExchange);
                    break;
                case ADD_TASK:
                    handleAddTask(httpExchange);
                    break;
                case UNKNOWN:
                    writeResponse(httpExchange, "такого эндпоинта не существует", 404);
            }*/
        }
    }

/*    private void handleAddTask(HttpExchange httpExchange) {
        List<String> contentTypeValues = httpExchange.getRequestHeaders().get("Content-type");
        if (contentTypeValues == null || !contentTypeValues.contains("application/json")) {
            writeResponse(httpExchange, "Ожидаются данные в формате json", 400);
            return;
        }
        try {
            String jsonString = new String(httpExchange.getRequestBody().readAllBytes());
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String taskType = jsonObject.get("TASK_TYPE").getAsString();
            Task task = null;
            switch (TaskType.valueOf(taskType.toUpperCase())) {
                case TASK:
                    task = gson.fromJson(jsonString, Task.class);
                    writeResponse(httpExchange, "Задача создана", 200);
                    break;
                case SUBTASK:
                    task = gson.fromJson(jsonString, Subtask.class);
                    writeResponse(httpExchange, "Подзадача создана", 200);
                    break;
                case EPIC:
                    task = gson.fromJson(jsonString, Epic.class);
                    writeResponse(httpExchange, "Эпик создан", 200);
            }
            taskManager.addToList(task);
        } catch (Exception exception) {
            writeResponse(httpExchange, "Некорректный api запрос", 400);
            throw new TaskCreateFromJsonException("Ошибка при создании задачи из Json", exception);
        }
    }

    private void handleGetHistory(HttpExchange httpExchange) {
        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");
        try {
            httpExchange.sendResponseHeaders(200, 0);
            Gson gson = new Gson();
            String jsonString = gson.toJson(taskManager.getHistory());
        } catch (IOException e) {
            throw new ServerResponseException("Произошла ошибка при формировании ответа клиенту", e);
        }
    }*/

    //private void writeResponse(HttpExchange httpExchange, String responseString, int responseCode) {
    private void writeResponse(HttpExchange httpExchange, Map.Entry<String, Integer> answer) {
        String responseString = answer.getKey();
        int responseCode = answer.getValue();
        byte[] byteData = responseString.getBytes(DEFAULT_CHARSET);
        try {
            httpExchange.sendResponseHeaders(responseCode, byteData.length);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(byteData);
            outputStream.close();
        } catch (IOException e) {
            throw new ServerResponseException("Произошла ошибка при формировании ответа клиенту", e);
        }
        httpExchange.close();
    }

}
