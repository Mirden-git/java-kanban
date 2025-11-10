package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    public static TaskManager taskManager;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void possibilityToChangeEpicIdOfSubtask() {
        taskManager.addEpic("A", "B", null, Duration.ZERO);
        int id = taskManager.getEpics().getFirst().getId();
        taskManager.addSubtask("Подзадача","Описание", id,
                LocalDateTime.of(2025, 1, 1, 14, 0), Duration.ofMinutes(60));
        int newId = taskManager.nextId();
        Subtask lastAddedSubtask = taskManager.getSubtasks().getLast();
        lastAddedSubtask.setEpicId(newId);
        assertNotEquals(id, lastAddedSubtask.getEpicId());
    }

    @Test
    public void linkedEpicExists() {
        taskManager.addEpic("A", "B", null, Duration.ZERO);
        int id = taskManager.getEpics().getFirst().getId();
        taskManager.addSubtask("Подзадача","Описание", id,
                LocalDateTime.of(2025, 1, 1, 14, 0), Duration.ofMinutes(60));
        Subtask lastAddedSubtask = taskManager.getSubtasks().getLast();
        assertEquals(id, lastAddedSubtask.getEpicId());
    }
}