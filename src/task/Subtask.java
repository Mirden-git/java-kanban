package task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String name, String description, int epicId) {
        super(id, name, description);
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
        Subtask copy = new Subtask(getId(), getName(), getDescription(), getEpicId());
        copy.setStatus(getStatus());
        return copy;
    }

    @Override
    public String toString() {
        return "\nSubtask{" +
                "id='" + super.getId() + "'" +
                ", name='" + super.getName() + "'" +
                ", description='" + super.getDescription() + "'" +
                ", status=" + super.getStatus() +
                ", epicId=" + epicId +
                "}";
    }
}
