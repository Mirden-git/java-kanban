package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private static FileBackedTaskManager manager;
    private File tempFile;

    @TempDir
    Path path;

    @Override
    protected FileBackedTaskManager createManager() {
        tempFile = path.resolve("testtasks.csv").toFile();
        return new FileBackedTaskManager(tempFile);
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

    @Test
    void fileSizeNotZeroAfterTaskAdded() throws IOException {
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        manager.addTask("Обычная задача 1", "Описание 1", dateTime(12, 0), duration(60));
        assertNotEquals(0, Files.size(tempFile.toPath()));
    }

    @Test
    void tasksAreNotEmptyAfterLoadFromFile() {
        manager = new FileBackedTaskManager(tempFile);
        boolean isNull = manager.getTasks().isEmpty();
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        manager.addTask("Обычная задача 1", "Описание 1", dateTime(12, 0), duration(60));
        boolean isFilled = manager.getTasks().isEmpty();
        assertNotEquals(isNull, isFilled);
    }
}