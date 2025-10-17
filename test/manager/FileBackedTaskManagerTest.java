package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private static TaskManager taskManager;
    private File tempFile;

    @TempDir
    Path path;

    @BeforeEach
    public void beforeEach() {
        tempFile = path.resolve("testtasks.csv").toFile();
    }

    @AfterEach
    public void afterEach() {
        taskManager.clearListOfTasks();
        taskManager.clearListOfSubtasks();
        taskManager.clearListOfEpics();
    }

    @Test
    void fileSizeNotZeroAfterTaskAdded() throws IOException {
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        taskManager.addTask("Обычная задача 1", "Описание 1");
        assertNotEquals(0, Files.size(tempFile.toPath()));
    }

    @Test
    void tasksAreNotEmptyAfterLoadFromFile() {
        taskManager = new FileBackedTaskManager(tempFile);
        boolean isNull = taskManager.getTasks().isEmpty();
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        taskManager.addTask("Обычная задача 1", "Описание 1");
        boolean isFilled = taskManager.getTasks().isEmpty();
        assertNotEquals(isNull, isFilled);
    }
}