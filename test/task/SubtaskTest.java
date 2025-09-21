package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    public static TaskManager taskManager;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void possibilityToChangeEpicIdOfSubtask() {
        taskManager.addEpic("A", "B");
        int id = taskManager.getEpics().getFirst().getId();
        taskManager.addSubtask("Подзадача","Описание", id);
        int newId = taskManager.nextId();
        Subtask lastAddedSubtask = taskManager.getSubtasks().getLast();
        lastAddedSubtask.setEpicId(newId);
        assertNotEquals(id, lastAddedSubtask.getEpicId());
    }
}