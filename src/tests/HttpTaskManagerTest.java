package tests;

import model.Task;
import net.KVServer;
import org.junit.jupiter.api.*;
import service.HttpTaskManager;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private static KVServer kvServer;
    private static final String url = "http://localhost:8078";

    @BeforeAll
    public static void beforeAll() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        Thread.sleep(2000);
    }

    @AfterAll
    public static void afterAll() {
        kvServer.stop();
    }

    @BeforeEach
    public void beforeEach() {
        taskManager = new HttpTaskManager(url);
        super.beforeEach();
    }

    @AfterEach
    public void afterEach() {
        taskManager.clear();
    }

    @Test
    public void testReturnValue() {
        final int taskId = taskManager.addToList(task1);
        final int epicId = taskManager.addToList(epic1);
        final int subTaskId = taskManager.addToList(subtask1_epic1);
        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subTaskId);
        taskManager.getTaskById(taskId);
        final List<Task> tasksBeforeLoad = taskManager.getAllTasks();
        final List<Task> historyBeforeLoad = taskManager.getHistory();

        taskManager.deleteAllWithoutSave();
        assertEquals(Collections.emptyList(), taskManager.getAllTasks(), "Менеджер не очищен");
        assertEquals(Collections.emptyList(), taskManager.getHistory(), "История не очищена");

        taskManager.loadFromServer(url);

        final List<Task> historyAfterLoad = taskManager.getHistory();
        final Task returnedTask = taskManager.getTaskById(taskId);
        final Task returnedSubTask = taskManager.getSubtaskById(subTaskId);
        final Task returnedEpic = taskManager.getEpicById(epicId);
        final List<Task> tasksAfterLoad = taskManager.getAllTasks();
        final int taskListSize = tasksAfterLoad.size();

        assertNotNull(taskManager, "Менеджер пустой");
        assertNotNull(tasksAfterLoad, "Список задач пуст");
        assertEquals(task1, returnedTask, "Задачи не совпадают");
        assertEquals(subtask1_epic1, returnedSubTask, "Подзадачи не совпадают");
        assertEquals(epic1, returnedEpic, "Эпики не совпадают");
        assertEquals(3, taskListSize, "Не совпадает количество задач");
        assertEquals(tasksBeforeLoad, tasksAfterLoad, "Не совпадает список задач");
        assertEquals(historyBeforeLoad, historyAfterLoad, "История не совпадает");
    }

    @Test
    public void testEmptyListOfTasks() {

        final int taskId = taskManager.addToList(task1);
        taskManager.deleteTask(taskId);

        taskManager.deleteAllWithoutSave();
        assertEquals(Collections.emptyList(), taskManager.getAllTasks(), "Менеджер не очищен");
        assertEquals(Collections.emptyList(), taskManager.getHistory(), "История не очищена");

        taskManager.loadFromServer(url);
        assertEquals(Collections.emptyList(), taskManager.getAllTasks());
    }

    @Test
    public void testEpicWithoutSubtasks() {
        final int epicId = taskManager.addToList(epic1);
        final Task expected = taskManager.getEpicById(epicId);

        List<Task> expectedTaskList = Collections.singletonList(epic1);

        taskManager.deleteAllWithoutSave();
        assertEquals(Collections.emptyList(), taskManager.getAllTasks(), "Менеджер не очищен");
        assertEquals(Collections.emptyList(), taskManager.getHistory(), "История не очищена");

        taskManager.loadFromServer(url);

        assertEquals(expectedTaskList, taskManager.getAllTasks());
        assertEquals(expectedTaskList, taskManager.getHistory());
    }

    @Test
    public void testEmptyHistory() {
        taskManager.addToList(epic1);
        taskManager.addToList(epic2);

        taskManager.deleteAllWithoutSave();
        assertEquals(Collections.emptyList(), taskManager.getAllTasks(), "Менеджер не очищен");
        assertEquals(Collections.emptyList(), taskManager.getHistory(), "История не очищена");

        taskManager.loadFromServer(url);

        assertEquals(Collections.emptyList(), taskManager.getHistory());
    }

}