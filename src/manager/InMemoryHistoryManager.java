package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyList = new ArrayList<>();
    private static final int MAX_TASKS_IN_HISTORY = 10;

    @Override
    public void addToHistory(Task task) {

        if (task == null) return;

        Task copyOfAnyTask;

        if (task instanceof Epic epic) {
            copyOfAnyTask = new Epic(epic.getId(), epic.getName(), epic.getDescription());
            copyOfAnyTask.setStatus(epic.getStatus());
        } else if (task instanceof Subtask subtask) {
            copyOfAnyTask = new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
                    subtask.getEpicId());
            copyOfAnyTask.setStatus(subtask.getStatus());
        } else {
            copyOfAnyTask = new Task(task.getId(), task.getName(), task.getDescription());
            copyOfAnyTask.setStatus(task.getStatus());
        }

        if (historyList.size() < MAX_TASKS_IN_HISTORY) {
            historyList.add(copyOfAnyTask);
        } else {
            historyList.removeFirst();
            historyList.add(copyOfAnyTask);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
