package tests;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    File file = new File("src/resources/backupTest.csv");

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager(file);
        super.beforeEach();
    }

    @Test
    public void testEmptyListOfTasks() {
        final int taskId = taskManager.addToList(task1);

        taskManager.deleteTask(taskId);
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(new ArrayList<Task>(), manager.getAllTasks());
    }

    @Test
    public void testEpicWithoutSubtasks() {
        final int epicId = taskManager.addToList(epic1);
        final Task expected = taskManager.getTask(epicId);
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(file);
        List<Task> expectedTaskList = Collections.singletonList(epic1);

        assertEquals(expectedTaskList, manager.getAllTasks());
        assertEquals(expectedTaskList, manager.getHistory());
    }

    @Test
    public void testEmptyHistory() {
        taskManager.addToList(epic1);
        taskManager.addToList(epic2);
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(new ArrayList<Task>(), manager.getHistory());
    }

}