package manager;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import task.Task;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerExceptionTest {

    private static FileBackedTaskManager manager;
    private File file;

    @TempDir
    Path path;

    private FileBackedTaskManager createManager() {
        file = path.resolve("test.csv").toFile();
        return new FileBackedTaskManager(file);
    }

    @BeforeEach
    public void beforeEach() {
        manager = createManager();
    }

    @AfterEach
    public void afterEach() {
        manager.clearListOfTasks();
        manager.clearListOfSubtasks();
        manager.clearListOfEpics();
    }

    private Task task(String name) {
        return new Task(0, name, "desc",
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(15));
    }

    @Test
    void saveValidFileAssertDoesNotThrow() {
        assertDoesNotThrow(() -> {
            manager.addTask(task("A"));
            manager.addTask(task("B"));
        });
    }

    @Test
    void loadValidFileAssertDoesNotThrow() {
        manager.addTask(task("A"));
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    void saveToNotExistingFileAssertThrowsManagerSaveException() {
        File notFile = path.resolve("test").toFile();
        notFile.mkdir();
        FileBackedTaskManager emptyManager = new FileBackedTaskManager(notFile);
        assertThrows(ManagerSaveException.class, emptyManager::save);
    }
}
