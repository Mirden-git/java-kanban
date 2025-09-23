package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyList = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {

        if (task == null) {
            System.out.println("Переданный объект равен null");
            return;
        }

        int taskId = task.getId();
        Task copyOfAnyTask = task.copy();
        Node nodeForHistory = new Node(copyOfAnyTask);

        if (historyList.isEmpty()) {
            head = nodeForHistory;
            tail = nodeForHistory;
            historyList.put(taskId, nodeForHistory);
        } else {
            remove(taskId);
            linkLast(nodeForHistory);
            historyList.put(taskId, nodeForHistory);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(historyList.get(id));
        historyList.remove(id);
    }

    @Override
    public List<Task> getHistoryList() {
        return getTasks();
    }

    private void linkLast(Node node) {

        if (tail != null) tail.setNext(node);

        node.setPrev(tail);
        tail = node;
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node currentNode = head;

        while (currentNode != null) {
            history.add(currentNode.getTask());
            currentNode = currentNode.getNext();
        }

        return history;
    }

    private void removeNode(Node node) {

        if (node == null) return;

        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());

            if (node.getNext() == null) {
                tail = node.getPrev();
            } else {
                node.getNext().setPrev(node.getPrev());
            }
        } else {
            head = node.getNext();

            if (head == null) {
                tail = null;
            } else {
                head.setPrev(null);
            }
        }
    }
}