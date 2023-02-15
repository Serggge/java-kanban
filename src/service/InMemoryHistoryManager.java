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
        Node newNode = new Node(task);
        if (browsingHistory.containsKey(taskID)) {
            removeNode(browsingHistory.get(taskID));
        }
        linkLast(newNode);
        browsingHistory.put(taskID, newNode);
    }

    @Override
    public void remove(int taskID) {
        if (!browsingHistory.containsKey(taskID)) {
            return;
        }
        removeNode(browsingHistory.get(taskID));
        browsingHistory.remove(taskID);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node node = head;
        while (node != null) {
            taskList.add(node.task);
            node = node.next;
        }
        return taskList;
    }

    private void linkLast(Node newNode) { //void
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
    }

    private void removeNode(Node node) {
        if (node == head) {
            if (head == tail) {
                head = null;
                tail = null;
            } else {
                Node nextNode = head.next;
                nextNode.prev = null;
                head = nextNode;
            }
        } else if (node == tail) {
            Node prevNode = tail.prev;
            prevNode.next = null;
            tail = prevNode;
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
        final Task task;

        Node(Task task) {
            this.task = task;
        }

    }

}
