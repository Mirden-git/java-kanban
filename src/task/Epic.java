package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public List<Integer> getEpicSubtasksId() {
        return subtasksId;
    }

    public void addSubtaskId(int subtaskId) {
        this.subtasksId.add(subtaskId);
    }
}
