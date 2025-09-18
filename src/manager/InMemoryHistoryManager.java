package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyList = new HashMap<>();
    private Node head;
    private Node tail;


    @Override
    public void addToHistory(Task task) {

        if (task == null) return;

        Node nodeForHistory = new Node(task);
        int taskId = task.getId();

        if (historyList.isEmpty()) {
            head = nodeForHistory;
            tail = nodeForHistory;
            historyList.put(taskId, nodeForHistory);
        } else {

            if (historyList.containsKey(taskId) && historyList.size() > 1) {
                removeNode(historyList.get(taskId));
            } else if (!historyList.containsKey(taskId)) {
                linkLast(nodeForHistory);
            }

            historyList.put(taskId, nodeForHistory);
            tail = nodeForHistory;

        }
    }

    @Override
    public void remove(int id) {
        historyList.remove(id);
    }

    @Override
    public void remove(Set<Integer> allID) {
        for (int id : allID) {
            historyList.remove(id);
        }
    }

    @Override
    public List<Task> getHistoryList() {
        return getTasks();
    }

    @Override
    public void linkLast(Node node) {
        tail.setNext(node);
        node.setPrev(tail);
    }

    @Override
    public List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        history.add(head.getTask());
        Node currentNode = head;

        while (currentNode != null) {
            history.add(currentNode.getTask());
            currentNode = currentNode.getNext();
        }

        return history;
    }

    @Override
    public void removeNode(Node node) {

        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
            remove(node.getTask().getId());
        } else {
            node.getNext().setPrev(null);
            head = node.getNext();
            node.setNext(null);
            remove(node.getTask().getId());
        }
    }
}
