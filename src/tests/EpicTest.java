package tests;

import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    public Epic epic;
    public Subtask firstSubtask;
    public Subtask secondSubtask;

    @BeforeEach
    public void beforeEach() {
        epic = new Epic("task1", "task2");
        firstSubtask = new Subtask(epic, "First", "First");
        firstSubtask.setTaskID(1);
        secondSubtask = new Subtask(epic, "Second", "Second");
        secondSubtask.setTaskID(2);
    }

    @AfterEach
    public void afterEach() {
        epic.removeSubtasks();
    }

    @Test
    public void testEpicStatusWithoutSubtasks() {
        Epic epic = new Epic("1", "2");

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    public void testEpicStatusWhenAllSubtasksNew() {
        firstSubtask.setStatus(TaskStatus.NEW);
        secondSubtask.setStatus(TaskStatus.NEW);

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    public void testEpicStatusWhenAllSubtasksDone() {
        firstSubtask.setStatus(TaskStatus.DONE);
        secondSubtask.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }

    @Test
    public void testEpicStatusWhenOneSubtaskIsNewAndOtherIsDone() {
        firstSubtask.setStatus(TaskStatus.NEW);
        secondSubtask.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    public void testEpicStatusWhenSubtasksInProgress() {
        firstSubtask.setStatus(TaskStatus.IN_PROGRESS);
        secondSubtask.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());
    }

}