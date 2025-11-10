package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import task.Epic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private static FileBackedTaskManager manager;
    private File tempFile;

    @TempDir
    private Path path;

    @Override
    protected FileBackedTaskManager createManager() {
        tempFile = path.resolve("test.csv").toFile();
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

    @Test
    void saveEmptyTasks() throws IOException {
        manager.save();
        BufferedReader fileReader = Files.newBufferedReader(tempFile.toPath(), StandardCharsets.UTF_8);
        int count = 0;

        while (fileReader.ready()) {
            fileReader.readLine();
            count++;
        }

        fileReader.close();
        assertEquals(1, count);
    }

    @Test
    void loadEmptyTasks() {
        manager.save();
        FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(manager.getTasks().isEmpty() && manager.getSubtasks().isEmpty() &&
                manager.getEpics().isEmpty());
    }

    @Test
    void saveEpicWithoutSubs() {
        manager.addEpic(new Epic(0, "Эпик 1", "Описание 1", dateTime(12, 0), duration(60)));
        manager.save();
        FileBackedTaskManager.loadFromFile(tempFile);
        assertFalse(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    void historyEmptyAfterLoading() {
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(manager.getHistory().isEmpty());
    }
}