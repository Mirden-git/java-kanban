package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    private static TaskManager taskManager;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void possibilityToAddSubtaskId() {
        taskManager.addEpic("Эпик 1", "Описание эпика 1");
        List<Epic> epics = taskManager.getEpics();
        epics.get(0).addSubtaskId(taskManager.nextId());
        assertEquals(1, epics.get(0).getEpicSubtasksId().size());
    }
}