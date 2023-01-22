package service;

import model.Task;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> browsingHistory = new HashMap<>();
    private final Node head = new Node(null);
    private final Node tail = new Node(null);

    protected InMemoryHistoryManager() {
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public void add(Task task) {
        int taskID = task.getTaskID();
        if (browsingHistory.containsKey(taskID)) {
            remove(taskID);
        }
        browsingHistory.put(taskID, linkLast(task));
    }

    @Override
    public void remove(int taskID) {
        if (browsingHistory.containsKey(taskID)) {
            if (browsingHistory.size() == 1) {
                browsingHistory.clear();
                head.next = tail;
                tail.prev = head;
            } else {
                removeNode(browsingHistory.get(taskID));
                browsingHistory.remove(taskID);
            }
        } else {
            throw new RuntimeException("Попытка удалить из истории просмотров несуществующей " +
                    "задачи с ID = " + taskID);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node node = head.next;
        while (node.hasNext()) {
            taskList.add(node.task);
            node = node.next;
        }
        return taskList;
    }

    private Node linkLast(Task task) {
        Node newView = new Node(task);
        Node last = tail.prev;
        last.next = newView;
        newView.prev = last;
        newView.next = tail;
        tail.prev = newView;
        return newView;
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    private static class Node {

        Node next;
        Node prev;
        final Task task;

        Node(Task task) {
            this.task = task;
        }

        boolean hasNext() {
            return next != null;
        }
    }

}
