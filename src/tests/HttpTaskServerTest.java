package tests;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
import model.Task;
import net.HttpTaskServer;
import net.KVServer;
import net.util.CustomGson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.TaskManager;
import service.exceptions.TaskNotFoundException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    String url = "http://localhost:8080/tasks";
    static Gson gson;
    static HttpTaskServer httpTaskServer;
    static KVServer kvServer;
    static HttpClient client;
    static TaskManager manager;

    @BeforeAll
    public static void beforeAll() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        Thread.sleep(1000);
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        client = HttpClient.newHttpClient();
        manager = httpTaskServer.getManager();
        gson = CustomGson.getGson();
    }

    @AfterAll
    public static void afterAll() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @AfterEach
    public void afterEach() {
        manager.clear();
    }

    @Test
    public void testEndpointAddTask() throws IOException, InterruptedException {
        Task newTask = new Task("Task1", "for endpoint AddTask");
        String jsonTask = gson.toJson(newTask);
        URI uri = URI.create(url + "/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .POST(body)
                                         .headers("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int id = Integer.parseInt(response.headers()
                                          .map()
                                          .get("ID")
                                          .get(0));
        newTask.setId(id);
        Task taskInManager = manager.getTaskById(id);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals("Задача создана", response.body(), "Не возвращается тело ответа");
        assertEquals(newTask, taskInManager, "Задачи не совпадают");
    }

    @Test
    public void testEndpointGetTaskById() throws IOException, InterruptedException {
        Task newTask = new Task("Task1", "for endpoint GetTaskById");
        int id = manager.addToList(newTask);
        URI uri = URI.create(url + "/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .GET()
                                         .header("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task returnedTask = gson.fromJson(response.body(), Task.class);

        assertNotNull(returnedTask, "Задача не возвращается");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(newTask, returnedTask, "Задачи не совпадают");
    }

    @Test
    public void testEndpointDeleteTaskById() throws IOException, InterruptedException {
        Task newTask = new Task("Task1", "for endpoint GetTaskById");
        int id = manager.addToList(newTask);
        URI uri = URI.create(url + "/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .DELETE()
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> taskListAfterDeleteTask = manager.getTasks();

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals("Задача удалена", response.body(), "Не возвращается тело ответа");
        assertEquals(Collections.emptyList(), taskListAfterDeleteTask, "Задача не удалена");
        final TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> manager.getTaskById(id));
        assertEquals("Задача не найдена", exception.getMessage(), "Задача не удалена");
    }

    @Test
    public void testEndpointGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "task for endpoint GetTasks");
        Task task2 = new Task("Task2", "task for endpoint GetTasks");
        manager.addToList(task1);
        manager.addToList(task2);
        List<Task> expectedTaskList = manager.getTasks();
        URI uri = URI.create(url + "/task/");
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .GET()
                                         .header("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> returnedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertNotNull(returnedTasks, "Задачи не возвращаются");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(expectedTaskList, returnedTasks, "Список задач не совпадает");
    }

    @Test
    public void testEndpointDeleteAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "task for endpoint DeleteAllTasks");
        Task task2 = new Task("Task2", "task for endpoint DeleteAllTasks");
        manager.addToList(task1);
        manager.addToList(task2);
        URI uri = URI.create(url + "/task/");
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .DELETE()
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> taskListAfterDelete = manager.getAllTasks();

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals("Задачи удалены", response.body(), "Не возвращается тело ответа");
        assertEquals(Collections.emptyList(), taskListAfterDelete, "Задачи не удалены");
    }

    @Test
    public void testEndpointAddEpic() throws IOException, InterruptedException {
        Epic newEpic = new Epic("Epic", "epic for test endpoint AddEpic");
        String jsonEpic = gson.toJson(newEpic);
        URI uri = URI.create(url + "/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                                         .header("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int id = Integer.parseInt(response.headers()
                                          .map()
                                          .get("ID")
                                          .get(0));
        newEpic.setId(id);
        Task epicInManager = manager.getEpicById(id);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals("Эпик создан", response.body(), "Не возвращается тело ответа");
        assertEquals(newEpic, epicInManager, "Эпики не совпадают");
    }

    @Test
    public void testEndpointGetEpicById() throws IOException, InterruptedException {
        Epic newEpic = new Epic("Epic1", "epic for test endpoint GetEpicById");
        int id = manager.addToList(newEpic);
        URI uri = URI.create(url + "/epic/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .GET()
                                         .header("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task returnedEpic = gson.fromJson(response.body(), Epic.class);

        assertNotNull(returnedEpic, "Эпик не возвращается");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(newEpic, returnedEpic, "Эпики не совпадают");
    }

    @Test
    public void testEndpointDeleteEpicById() throws IOException, InterruptedException {
        Task newEpic = new Epic("Epic1", "epic for test endpoint DeleteEpicById");
        int id = manager.addToList(newEpic);
        URI uri = URI.create(url + "/epic/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .DELETE()
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> listEpicsAfterDelete = manager.getEpics();

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals("Эпик удалён", response.body(), "Не возвращается тело ответа");
        assertEquals(Collections.emptyList(), listEpicsAfterDelete, "Эпик не удалён");
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> manager.getEpicById(id));
        assertEquals("Эпик не найден", exception.getMessage(), "Эпик не удалён");
    }

    @Test
    public void testEndpointGetEpics() throws IOException, InterruptedException {
        Task epic1 = new Epic("Epic1", "epic for test endpoint GetEpics");
        Task epic2 = new Epic("Epic2", "epic for test endpoint GetEpics");
        manager.addToList(epic1);
        manager.addToList(epic2);
        List<Task> listOfEpics = manager.getEpics();
        URI uri = URI.create(url + "/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .GET()
                                         .header("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> returnedEpics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertNotNull(returnedEpics, "Эпики не возвращаются");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(listOfEpics, returnedEpics, "Список эпиков не совпадает");
    }

    @Test
    public void testEndpointDeleteAllEpics() throws IOException, InterruptedException {
        Task epic1 = new Epic("Epic1", "epic for test endpoint DeleteAllEpics");
        Task epic2 = new Epic("Epic2", "epic for test endpoint DeleteAllEpics");
        manager.addToList(epic1);
        manager.addToList(epic2);
        URI uri = URI.create(url + "/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .DELETE()
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals("Эпики удалены", response.body(), "Не совпадает тело ответа");
        assertEquals(Collections.emptyList(), manager.getEpics(), "Эпики не удалены");
    }

    @Test
    public void testEndpointAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "epic for test endpoint AddSubTask");
        manager.addToList(epic);
        Subtask newSubtask = new Subtask(epic, "Subtask", "subtask for test endpoint AddSubTask");
        String jsonSubTask = gson.toJson(newSubtask);
        URI uri = URI.create(url + "/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .POST(HttpRequest.BodyPublishers.ofString(jsonSubTask))
                                         .header("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int id = Integer.parseInt(response.headers()
                                          .map()
                                          .get("ID")
                                          .get(0));
        newSubtask.setId(id);
        Task subTaskInManager = manager.getSubtaskById(id);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals("Подзадача создана", response.body(), "Не возвращается тело ответа");
        assertEquals(newSubtask, subTaskInManager, "Подзадачи не совпадают");
    }

    @Test
    public void testEndpointGetSubTaskById() throws IOException, InterruptedException {
        Epic newEpic = new Epic("Epic1", "epic for test endpoint GetSubTaskById");
        manager.addToList(newEpic);
        Subtask newSubtask = new Subtask(newEpic, "Subtask", "subtask for test endpoint GetSubTaskById");
        int id = manager.addToList(newSubtask);
        URI uri = URI.create(url + "/subtask/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .GET()
                                         .header("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task returnedSubTask = gson.fromJson(response.body(), Subtask.class);

        assertNotNull(returnedSubTask, "Подзадача не возвращается");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(newSubtask, returnedSubTask, "Подзадачи не совпадают");
    }

    @Test
    public void testEndpointDeleteSubTaskById() throws IOException, InterruptedException {
        Epic newEpic = new Epic("Epic1", "epic for test endpoint DeleteSubTaskById");
        manager.addToList(newEpic);
        Task newSubtask = new Subtask(newEpic, "Subtask", "subtask for test endpoint DeleteSubTaskById");
        int id = manager.addToList(newSubtask);
        URI uri = URI.create(url + "/subtask/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .DELETE()
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> listSubTasksAfterDelete = manager.getSubTasks();

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals("Подзадача удалена", response.body(), "Не возвращается тело ответа");
        assertEquals(Collections.emptyList(), listSubTasksAfterDelete, "Подзадача не удалена");
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> manager.getSubtaskById(id));
        assertEquals("Подзадача не найдена", exception.getMessage(), "Подзадача не удалена");
    }

    @Test
    public void testEndpointGetSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "epic for test endpoint GetSubTasks");
        manager.addToList(epic);
        Task subTask1 = new Subtask(epic, "SubTask2", "subtask for test endpoint GetSubTasks");
        manager.addToList(subTask1);
        Task subTask2 = new Subtask(epic, "SubTask2", "subtask for test endpoint GetSubTasks");
        manager.addToList(subTask2);
        List<Task> listOfSubTasks = manager.getSubTasks();
        URI uri = URI.create(url + "/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .GET()
                                         .header("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> returnedSubTasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        assertNotNull(returnedSubTasks, "Подзадачи не возвращаются");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(listOfSubTasks, returnedSubTasks, "Список подзадач не совпадает");
    }

    @Test
    public void testEndpointDeleteAllSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "epic for test endpoint DeleteAllSubTasks");
        manager.addToList(epic);
        Task subTask1 = new Subtask(epic, "SubTask1", "subtask for test endpoint DeleteAllSubTasks");
        manager.addToList(subTask1);
        Task subTask2 = new Subtask(epic, "SubTask2", "subtask for test endpoint DeleteAllSubTasks");
        manager.addToList(subTask2);
        URI uri = URI.create(url + "/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .DELETE()
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals("Подзадачи удалены", response.body(), "Не совпадает тело ответа");
        assertEquals(Collections.emptyList(), manager.getSubTasks(), "Подзадачи не удалены");
    }

    @Test
    public void testGetEpicSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "epic for test endpoint GetEpicSubTasks");
        int epicId = manager.addToList(epic);
        Task subTask1 = new Subtask(epic, "SubTask1", "subtask for test endpoint GetEpicSubTasks");
        manager.addToList(subTask1);
        Task subTask2 = new Subtask(epic, "SubTask2", "subtask for test endpoint GetEpicSubTasks");
        manager.addToList(subTask2);
        List<Task> expectedSubTasks = manager.getSubTasks();
        URI uri = URI.create(url + "/subtask/epic/?id=" + epicId);
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .GET()
                                         .header("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> returnedSubTasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        assertNotNull(returnedSubTasks, "Подзадачи не возвращаются");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(expectedSubTasks, returnedSubTasks, "Список подзадач не совпадает");
    }

    @Test
    public void testEndpointGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "for test endpoint GetHistory");
        Task task2 = new Task("Task2", "for test endpoint GetHistory");
        Task task3 = new Task("Task3", "for test endpoint GetHistory");
        int firstTaskId = manager.addToList(task1);
        int secondTaskId = manager.addToList(task2);
        int thirdTaskId = manager.addToList(task3);
        manager.getTaskById(thirdTaskId);
        manager.getTaskById(firstTaskId);
        manager.getTaskById(secondTaskId);
        List<Task> expectedHistory = manager.getHistory();
        URI uri = URI.create(url + "/history");
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .GET()
                                         .header("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> returnedHistory = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertNotNull(returnedHistory, "История не возвращается");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(expectedHistory, returnedHistory, "История не совпадает");
    }

    @Test
    public void testEndpointGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "for test endpoint GetPrioritizedTasks", "01.03.2023", "12:00", 30);
        Task task2 = new Task("Task2", "for test endpoint GetPrioritizedTasks", "05.01.2023", "12:00", 30);
        Task task3 = new Task("Task3", "for test endpoint GetPrioritizedTasks", "08.01.2023", "12:00", 30);
        manager.addToList(task1);
        manager.addToList(task2);
        manager.addToList(task3);
        List<Task> expectedPriority = manager.getPrioritizedTasks();
        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .GET()
                                         .header("Content-Type", "application/json")
                                         .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> returnedPriority = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertNotNull(returnedPriority, "Приоритет не возвращается");
        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(expectedPriority, returnedPriority, "История не совпадает");
    }

}