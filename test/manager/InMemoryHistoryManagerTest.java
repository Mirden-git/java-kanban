package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {

    private static TaskManager taskManager;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @AfterEach
    public void afterEach() {
        taskManager.clearListOfTasks();
        taskManager.clearListOfSubtasks();
        taskManager.clearListOfEpics();
    }

    @Test
    public void tasksAddedToHistoryAreUnchanged() {
        taskManager.addTask("A", "B");
        int id = taskManager.getTasks().getFirst().getId();
        taskManager.changeTaskStatus(id, TaskStatus.DONE);
        List<Task> history = taskManager.getHistory();
        Task fromHistory = history.getFirst();
        assertEquals(TaskStatus.NEW, fromHistory.getStatus());
    }
}