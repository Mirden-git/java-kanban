package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    public static TaskManager taskManager;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void tasksAddedToHistoryAreUnchanged() {
        taskManager.addTask("A", "B");
        int id = taskManager.getTasks().get(0).getId();
        Task task = taskManager.getTaskById(id);
        taskManager.getHistoryManager().addToHistory(task);
        taskManager.changeTaskStatus(id, TaskStatus.DONE);
        List<Task> history = taskManager.getHistoryManager().getHistory();
        Task fromHistory = history.get(0);
        assertEquals(TaskStatus.NEW, fromHistory.getStatus());
    }

}