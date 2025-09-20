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
                remove(taskId);
            }
//            else if (!historyList.containsKey(taskId)) {
//            }

            linkLast(nodeForHistory);
            historyList.put(taskId, nodeForHistory);
            //tail = nodeForHistory;

        }
    }

    @Override
    public void remove(int id) {
        if (historyList.containsKey(id)) {
            removeNode(historyList.get(id));// todo ssdfsdfsdf
            historyList.remove(id);
        }
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
        tail = node;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
//        history.add(head.getTask());
        Node currentNode = head;

        while (currentNode != null) {
            history.add(currentNode.getTask());
            currentNode = currentNode.getNext();
        }

        return history;
    }

    @Override
    public void removeNode(Node node) {

        if (node.equals(head)) {
            node.getNext().setPrev(null);
            head = node.getNext();
            node.setNext(null);
        } else if (node.equals(tail)) {
            node.getPrev().setNext(null);
            tail = node.getPrev();
            node.setPrev(null);
        } else if (node.getPrev() != null && node.getNext() != null) {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
            node.setPrev(null);
            node.setNext(null);
        }
    }
}
