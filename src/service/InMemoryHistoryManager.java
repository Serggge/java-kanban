package service;

import model.Task;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> browsingHistory = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        int taskID = task.getTaskID();
        if (browsingHistory.containsKey(taskID)) {
            remove(taskID);
        }
        browsingHistory.put(task.getTaskID(), linkLast(task));
    }

    @Override
    public void remove(int taskID) {
        if (browsingHistory.containsKey(taskID)) {
            if (browsingHistory.size() == 1) {
                browsingHistory.clear();
                head = tail = null;
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
        if (browsingHistory.isEmpty()) {
            return taskList;
        }
        Node node = head;
        while (node.hasNext()) {
            taskList.add(node.task);
            node = node.next;
        }
        taskList.add(tail.task);
        return taskList;
    }

    private Node linkLast(Task task) {
        Node newView = new Node(task);
        if (head == null) {
            head = newView;
        } else {
            tail.next = newView;
            newView.prev = tail;
        }
        tail = newView;
        return newView;
    }

    private void removeNode(Node oldNode) {
        if (oldNode == head) {
            oldNode.next.prev = null;
            head = oldNode.next;
        } else if (oldNode == tail) {
            oldNode.prev.next = null;
            tail = oldNode.prev;
        } else {
            oldNode.prev.next = oldNode.next;
            oldNode.next.prev = oldNode.prev;
        }
    }

    private static class Node {

        Node next;
        Node prev;
        Task task;

        Node(Task task) {
            this.task = task;
        }

        boolean hasNext() {
            return next != null;
        }
    }

}
