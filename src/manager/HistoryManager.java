package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    final int MAX_TASKS_IN_HISTORY = 10;

    void addToHistory(Task task);

    List<Task> getHistory();

}
