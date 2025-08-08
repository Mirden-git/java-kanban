import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getEpicSubtasksId() {
        return subtasksId;
    }

    public void addSubtaskId(int subtaskId) {
        this.subtasksId.add(subtaskId);
    }
}
