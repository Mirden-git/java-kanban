package manager;

import task.Epic;
import task.Subtask;
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

        if (historyList.isEmpty()) {
            head = nodeForHistory;
            tail = nodeForHistory;
            historyList.put(task.getId(), nodeForHistory);
        } else {

            if (historyList.containsValue(nodeForHistory) && historyList.size() > 1) {
                removeNode(nodeForHistory);
                remove(task.getId()); //todo а надо ли?
            } else {
                linkLast(nodeForHistory);
            }

            historyList.put(task.getId(), nodeForHistory);
            tail = nodeForHistory;

        }
    }

    @Override
    public void remove(int id) {
        historyList.remove(id - 1);
    }

    @Override
    public void remove(Set<Integer> allID) {
        for (int id : allID) {
            historyList.remove(id - 1);
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
        node.getPrev().setNext(node.getNext());
    }

}
