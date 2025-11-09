import manager.FileBackedTaskManager;
import manager.TaskManager;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager;

        try {
            taskManager = FileBackedTaskManager.loadFromFile(File.createTempFile("test", "csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}