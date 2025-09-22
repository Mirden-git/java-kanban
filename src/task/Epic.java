
package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasks = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public List<Integer> getEpicSubtasksId() {
        return new ArrayList<>(subtasks);
    }

    public void setEpicSubtasksId(List<Integer> ids) {
        this.subtasks = new ArrayList<>(ids);
    }

    public void addSubtaskId(int subtaskId) {
        this.subtasks.add(subtaskId);
    }

    @Override
    public Epic copy() {
        Epic copy = new Epic(getId(), getName(), getDescription());
        copy.setStatus(getStatus());
        copy.setEpicSubtasksId(this.subtasks);
        return copy;
    }

    @Override
    public String toString() {
        return "\nEpic{" +
                "id='" + super.getId() + "'" +
                ", name='" + super.getName() + "'" +
                ", description='" + super.getDescription() + "'" +
                ", status=" + super.getStatus() +
                "}";
    }
}
