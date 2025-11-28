package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasks = new ArrayList<>();
    private LocalDateTime endTime = getStartTime();

    public Epic(int id, String name, String description, LocalDateTime startTime, Duration duration) {
        super(id, name, description, startTime, duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
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
        Epic copy = new Epic(getId(), getName(), getDescription(), getStartTime(), getDuration());
        copy.setStatus(getStatus());
        copy.setEpicSubtasksId(this.subtasks);
        return copy;
    }

    @Override
    public String toString() {
        return super.getId() + "," +
                TypeOfTask.EPIC + "," +
                super.getName() + "," +
                super.getStatus() + "," +
                super.getDescription() + "," +
                "," +
                super.getStartTime() + "," +
                super.getDuration().toMinutes();
    }
}