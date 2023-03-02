package tests;

import model.Task;
import service.TaskManager;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskType;
import service.exceptions.IntersectionTimeException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    public T taskManager;
    public Task task1;
    public Task task2;
    public Epic epic1;
    public Subtask subtask1_epic1;
    public Subtask subtask2_epic1;
    public Epic epic2;
    public Subtask subtask1_epic2;
    public Subtask subtask2_epic2;

    @BeforeEach
    public void beforeEach() {
        task1 = new Task("task1", "task1");
        task2 = new Task("task2", "task2");
        epic1 = new Epic("epic1", "epic1");
        subtask1_epic1 = new Subtask(epic1, "subtask1_epic1", "subtask1_epic1");
        subtask2_epic1 = new Subtask(epic1, "subtask2_epic1", "subtask2_epic1");
        epic2 = new Epic("epic2", "epic2");
        subtask1_epic2 = new Subtask(epic2, "subtask1_epic2", "subtask1_epic2");
        subtask2_epic2 = new Subtask(epic2, "subtask2_epic2", "subtask2_epic2");

    }

    @Test
    public void testAddToList() {
        final int taskId = taskManager.addToList(task1);
        final Task savedTask = taskManager.getAnyTask(taskId);
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void testGetTaskById() {
        final int taskId = taskManager.addToList(task1);
        final Task savedTask = taskManager.getAnyTask(taskId);
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void testGetEpicById() {
        final int taskId = taskManager.addToList(epic1);
        final Task savedTask = taskManager.getAnyTask(taskId);
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(epic1, savedTask, "Задачи не совпадают.");
        assertEquals(epic1, tasks.get(0), "Задачи не совпадают.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void testGetSubtaskById() {
        final int epicId = taskManager.addToList(epic1);
        final int subtaskId = taskManager.addToList(subtask1_epic1);
        final Task savedTask = taskManager.getAnyTask(subtaskId);
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(subtask1_epic1, savedTask, "Задачи не совпадают.");
        assertEquals(subtask1_epic1, tasks.get(1), "Задачи не совпадают.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void testGetAllTasksOfManager() {
        final int epicId = taskManager.addToList(epic1);
        final int firstSubtaskId = taskManager.addToList(subtask1_epic1);
        final int secondSubtaskId = taskManager.addToList(subtask2_epic1);
        final Task savedTask = taskManager.getAnyTask(epicId);
        final List<Task> tasks = taskManager.getAllTasks();
        final List<Task> expectedTasks = List.of(epic1, subtask1_epic1, subtask2_epic1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(epic1, savedTask, "Задачи не совпадают.");
        assertEquals(expectedTasks, tasks, "Задачи не совпадают.");
        assertEquals(epic1, tasks.get(0), "Задачи не совпадают.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void testGetEpicTaskList() {
        final int epicId = taskManager.addToList(epic1);
        final int epicId2 = taskManager.addToList(epic2);
        final Task savedEpic = taskManager.getAnyTask(epicId);
        List<Task> epics = taskManager.getTaskListByType(TaskType.EPIC);
        List<Task> expectedEpicList = List.of(epic1, epic2);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");
        assertEquals(expectedEpicList, epics, "Задачи не совпадают.");
        assertEquals(epic1, epics.get(0), "Задачи не совпадают.");
        assertEquals(2, epics.size(), "Неверное количество задач.");
    }

    @Test
    public void testGetSubtaskList() {
        final int epicId = taskManager.addToList(epic1);
        final int firstSubtaskId = taskManager.addToList(subtask1_epic1);
        final int secondSubtaskId = taskManager.addToList(subtask2_epic1);
        final Task savedSubtask = taskManager.getAnyTask(firstSubtaskId);
        List<Task> subtaskList = taskManager.getTaskListByType(TaskType.SUBTASK);
        List<Task> expectedSubtasks = List.of(subtask1_epic1, subtask2_epic1);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertNotNull(subtaskList, "Задачи на возвращаются.");
        assertEquals(subtask1_epic1, savedSubtask, "Задачи не совпадают.");
        assertEquals(expectedSubtasks, subtaskList, "Задачи не совпадают.");
        assertEquals(List.of(subtask1_epic1, subtask2_epic1), subtaskList, "Задачи не совпадают.");
        assertEquals(2, subtaskList.size(), "Неверное количество задач.");
    }

    @Test
    public void testDeleteTaskById() {
        final int firstTaskId = taskManager.addToList(task1);
        final int secondTaskId = taskManager.addToList(task2);
        final Task savedTask = taskManager.getAnyTask(firstTaskId);
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        taskManager.deleteTask(firstTaskId);
        tasks.remove(task1);
        final List<Task> expectedSubtasks = List.of(task2);
        assertEquals(task2, tasks.get(0), "Задачи не совпадают.");
        assertEquals(expectedSubtasks, taskManager.getAllTasks(), "Задачи не совпадают.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void testDeleteOnlyTaskType() {
        final int firstTaskId = taskManager.addToList(task1);
        final int secondTaskId = taskManager.addToList(task2);
        final int epicId = taskManager.addToList(epic1);
        final Task savedEpic = taskManager.getAnyTask(epicId);
        taskManager.deleteTasksByType(TaskType.TASK);
        final List<Task> tasks = taskManager.getAllTasks();
        final List<Task> expectedEpic = List.of(savedEpic);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");
        assertEquals(expectedEpic, tasks, "Задачи не совпадают.");
        assertEquals(savedEpic, tasks.get(0), "Задачи не совпадают.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void testDeleteEpicById() {
        final int epicId = taskManager.addToList(epic1);
        final int subtaskId = taskManager.addToList(subtask1_epic1);
        final Task savedEpic = taskManager.getAnyTask(epicId);
        final List<Task> tasks = taskManager.getAllTasks();
        final List<Task> expectedEpic = List.of(epic1, subtask1_epic1);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");
        assertEquals(expectedEpic, tasks, "Задачи не совпадают.");
        assertEquals(savedEpic, tasks.get(0), "Задачи не совпадают.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

        taskManager.deleteEpic(epicId);
        final List<Task> expected = Collections.emptyList();
        assertEquals(expected, taskManager.getAllTasks());
    }

    @Test
    public void testDeleteAllTasks() {
        taskManager.addToList(task1);
        taskManager.addToList(epic1);
        taskManager.addToList(subtask1_epic1);
        taskManager.clear();

        assertEquals(Collections.emptyList(), taskManager.getAllTasks());
    }

    @Test
    public void checkingCorrectSortingWhenTaskStartTimeChanges() {
        final Task earlyStartTask = new Task("Created in 1990", "1990",
                "01.01.1990", "12:00", 10);
        final Task lateStartTask = new Task("Created in 2010", "2010",
                "01.01.2010", "12:00", 10);
        final int earlyTaskId = taskManager.addToList(earlyStartTask);
        final int lateTaskId = taskManager.addToList(lateStartTask);
        final List<Task> priorityBeforeChange = taskManager.getPrioritizedTasks();
        Task expectedTask = taskManager.getAnyTask(earlyTaskId);
        String taskDateTime = expectedTask.getDateTime();

        assertEquals("01.01.1990 12:00", taskDateTime , "Время задач не совпадает");
        assertEquals(expectedTask, priorityBeforeChange.get(0), "Приоритет по времени определён неверно");

        earlyStartTask.setCurrentDateTime();
        taskManager.addToList(earlyStartTask);
        final List<Task> priorityAfterChange = taskManager.getPrioritizedTasks();
        expectedTask = taskManager.getAnyTask(lateTaskId);
        taskDateTime = expectedTask.getDateTime();
        assertEquals("01.01.2010 12:00", taskDateTime, "Время задач не совпадает");
        assertEquals(expectedTask, priorityAfterChange.get(0), "Приоритет по времени определён неверно");
    }

    @Test
    public void testIntersectionWhenNewTaskConsumesExistingOne() {
        final Task firstTask = new Task("Existing", "2023",
                "14.02.2023", "12:00", 10);
        final Task secondTask = new Task("Comes", "2023",
                "10.02.2023", "12:00", 60 * 24 * 10);
        taskManager.addToList(firstTask);

         final IntersectionTimeException exception = assertThrows(IntersectionTimeException.class,
                 () -> taskManager.addToList(secondTask));

         assertEquals("Созданная задача имеет пересечение по времени с задачей ID="
                 + firstTask.getId() + " по включению в диапазон", exception.getMessage());
    }

}