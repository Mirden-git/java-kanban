package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    private static TaskManager taskManager;
    private static HistoryManager history;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
        history = Managers.getDefaultHistory();
    }

    @Test
    public void getDefaultTaskManagerReturnsWorkingInstance() {
        assertNotNull(taskManager);
        taskManager.addTask("A", "B");
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    public void getDefaultHistoryManagerReturnsWorkingInstance() {
        assertNotNull(history);
        Task task = new Task(1, "A", "B");
        history.addToHistory(task);
        assertEquals(1, history.getHistoryList().size());
    }

}