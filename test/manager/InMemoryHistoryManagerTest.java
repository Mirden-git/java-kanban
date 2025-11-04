package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class InMemoryHistoryManagerTest {

    private static TaskManager taskManager;
    private static HistoryManager history;

    @BeforeEach
    public void beforeEach() {
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
        taskManager.addTask("A", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60));
        int id = taskManager.getTasks().getFirst().getId();
        taskManager.changeTaskStatus(id, TaskStatus.DONE);
        List<Task> history = taskManager.getHistory();
        Task fromHistory = history.getFirst();
        assertEquals(TaskStatus.NEW, fromHistory.getStatus());
    }

    @Test
    public void possibilityToAddTaskToHistory() {
        int initialSize = history.getHistoryList().size();
        history.add(new Task(2, "C", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60)));
        int newSize = history.getHistoryList().size();
        assertEquals(initialSize + 1, newSize);
    }

    @Test
    public void thereAreNoDuplicates() {
        int initialSize = history.getHistoryList().size();
        Task task = new Task(1, "A", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60));
        history.add(task);
        history.add(task);
        int newSize = history.getHistoryList().size();
        assertEquals(newSize, initialSize);
    }

    @Test
    public void deleteHead() {
        history.add(new Task(1, "C", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60)));
        history.add(new Task(2, "C", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60)));
        history.add(new Task(3, "C", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60)));
        history.remove(1);
        boolean isDeleted = history.getHistoryList().stream()
                        .anyMatch(item -> item.getId() == 1);
        assertFalse(isDeleted);
    }

    @Test
    public void deleteMiddle() {
        history.add(new Task(1, "C", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60)));
        history.add(new Task(2, "C", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60)));
        history.add(new Task(3, "C", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60)));
        history.remove(2);
        boolean isDeleted = history.getHistoryList().stream()
                .anyMatch(item -> item.getId() == 2);
        assertFalse(isDeleted);
    }

    @Test
    public void deleteTail() {
        history.add(new Task(1, "C", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60)));
        history.add(new Task(2, "C", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60)));
        history.add(new Task(3, "C", "B",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(60)));
        history.remove(3);
        boolean isDeleted = history.getHistoryList().stream()
                .anyMatch(item -> item.getId() == 3);
        assertFalse(isDeleted);
    }
}