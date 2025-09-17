package manager;

import task.Task;

import java.util.List;
import java.util.Set;

public interface HistoryManager {

    void addToHistory(Task task);

    void remove(int id);

    void remove(Set<Integer> allID);

    List<Task> getHistoryList();

    void linkLast(Node node);

    List<Task> getTasks();

    void removeNode(Node node);

}
