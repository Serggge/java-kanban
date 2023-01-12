package service;

import model.Task;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> browsingHistory = new HashMap<>();
    private Node head = new Node(null);
    private Node last;

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
                head = last = null;
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
        if (!head.hasNext()) {
            return taskList;
        }
        Node node = head.next;
        while (node.hasNext()) {
            taskList.add(node.task);
            node = node.next;
        }
        taskList.add(last.task);
        return taskList;
    }

    private Node linkLast(Task task) {
        Node newView = new Node(task);
        if (head.next == null) {
            newView.prev = head;
            head.next = newView;
        } else {
            newView.prev = last;
            last.next = newView;
        }
        last = newView;
        return newView;
    }

    private void removeNode(Node node) {
        if (node == head.next) {
            head.next = node.next;
        } else if (node == last) {
            last = node.prev;
            last.next = null;
        } else {
            Node prevNode = node.prev;
            Node nextNode = node.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
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
