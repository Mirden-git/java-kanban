package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    //private static final int MAX_TASKS_IN_HISTORY = 10;
    private final List<Task> historyList = new ArrayList<>();
    private final Map<Integer, Node> listForDeletion = new HashMap<>();



    @Override
    public void addToHistory(Task task) {

        if (task == null) return;

        Task copyOfAnyTask;

        if (task instanceof Epic epic) {
            copyOfAnyTask = new Epic(epic.getId(), epic.getName(), epic.getDescription());
            copyOfAnyTask.setStatus(epic.getStatus());
            List<Integer> epicSubtasksIdList = ((Epic) copyOfAnyTask).getEpicSubtasksId();
            epicSubtasksIdList = List.copyOf(epic.getEpicSubtasksId());
        } else if (task instanceof Subtask subtask) {
            copyOfAnyTask = new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
                    subtask.getEpicId());
            copyOfAnyTask.setStatus(subtask.getStatus());
        } else {
            copyOfAnyTask = new Task(task.getId(), task.getName(), task.getDescription());
            copyOfAnyTask.setStatus(task.getStatus());
        }

//        if (historyList.size() >= MAX_TASKS_IN_HISTORY) {
//            historyList.removeFirst();
//        }

        historyList.add(copyOfAnyTask);
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
        return historyList;
    }
}
