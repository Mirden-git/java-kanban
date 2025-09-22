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
    private static HistoryManager history;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
        history = Managers.getDefaultHistory();
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
        Task task = taskManager.getTaskById(id);
        history.addToHistory(task);
        taskManager.changeTaskStatus(id, TaskStatus.DONE);
        List<Task> history = taskManager.getHistory();
        Task fromHistory = history.getFirst();
        assertEquals(TaskStatus.NEW, fromHistory.getStatus());
    }

}