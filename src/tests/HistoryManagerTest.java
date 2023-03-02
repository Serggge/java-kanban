package tests;

import model.Task;
import service.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager historyManager;
    Task task1;
    Task task2;
    Task task3;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("1", "1");
        task1.setId(1);
        task2 = new Task("2", "2");
        task2.setId(2);
        task3 = new Task("3", "3");
        task3.setId(3);
    }

    @Test
    public void testForEmptyHistory() {

        assertEquals(new ArrayList<Task>(), historyManager.getHistory());
    }

    @Test
    public void testHistoryWhenDuplicatedTasks() {
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        assertEquals(List.of(task2, task1), historyManager.getHistory());
    }

    @Test
    public void testHistoryWhenTaskRemovedAtHead() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);

        assertEquals(List.of(task2, task3), historyManager.getHistory());
    }

    @Test
    public void testHistoryWhenTaskRemovedAtTail() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(3);

        assertEquals(List.of(task1, task2), historyManager.getHistory());
    }

    @Test
    public void testHistoryWhenTaskRemovedInMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);

        assertEquals(List.of(task1, task3), historyManager.getHistory());
    }

}