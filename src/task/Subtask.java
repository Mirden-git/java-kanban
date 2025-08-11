package task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String name, String description) {
        super(id, name, description);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + super.getName() + "'" +
                ", description='" + super.getDescription() + "'" +
                ", status=" + super.getStatus() +
                ", epicId=" + epicId +
                "}";
    }
}
