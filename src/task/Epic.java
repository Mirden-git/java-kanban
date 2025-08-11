package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasks = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public List<Integer> getEpicSubtasksId() {
        return subtasks;
    }

    public void addSubtaskId(int subtaskId) {
        this.subtasks.add(subtaskId);
    }
}
