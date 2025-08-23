package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyList = new ArrayList<>();
    private static final int MAX_TASKS_IN_HISTORY = 10;

    @Override
    public void addToHistory(Task task) {

        if (historyList.size() < MAX_TASKS_IN_HISTORY) {
            historyList.add(task);
        } else {
            historyList.removeFirst();
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
