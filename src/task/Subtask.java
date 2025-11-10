package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String name, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(id, name, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public Subtask copy() {
        Subtask copy = new Subtask(getId(), getName(), getDescription(), getEpicId(), getStartTime(), getDuration());
        copy.setStatus(getStatus());
        return copy;
    }

    @Override
    public String toString() {
        return super.getId() + "," +
                TypeOfTask.SUBTASK + "," +
                super.getName() + "," +
                super.getStatus() + "," +
                super.getDescription() + "," +
                epicId + "," +
                super.getStartTime() + "," +
                super.getDuration().toMinutes();
    }
}