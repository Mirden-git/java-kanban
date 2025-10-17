package manager;

import exceptions.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super();

        if (file == null) {
            throw new RuntimeException();
        }

        this.file = file.getAbsoluteFile();
        Path path = this.file.toPath();

        try {
            Path parent = path.getParent();

            if (Files.isDirectory(path)) {
                System.out.println("Передан не файл, а директория");
            }

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(path)) {
                Files.createFile(path);
            }

        } catch (IOException e) {
            System.out.println("Ошибка файла");
        }
    }

    public void save() {

        try (BufferedWriter fileWriter = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            List<String> listOfAllTasks = new ArrayList<>();

            for (Task item : super.getTasks()) {
                listOfAllTasks.add(item.toString());
            }

            for (Task item : super.getSubtasks()) {
                listOfAllTasks.add(item.toString());
            }

            for (Task item : super.getEpics()) {
                listOfAllTasks.add(item.toString());
            }

            fileWriter.write("id,type,name,status,description,epic");
            fileWriter.newLine();

            for (String item : listOfAllTasks) {
                fileWriter.write(item);
                fileWriter.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    @Override
    protected void newActions() {
        save();
    }

    private Task taskFromString(String value) {
        String[] splitOfLine = value.split(",");
        int id = Integer.parseInt(splitOfLine[0]);
        String name = splitOfLine[2];
        TaskStatus status = TaskStatus.valueOf(splitOfLine[3]);
        String description = splitOfLine[4];
        Task task;
        TypeOfTask type = TypeOfTask.valueOf(splitOfLine[1]);

        if (type == TypeOfTask.SUBTASK) {
            int epicId = Integer.parseInt(splitOfLine[5]);
            task = new Subtask(id, name, description, epicId);
        } else if (type == TypeOfTask.EPIC) {
            task = new Epic(id, name, description);
        } else {
            task = new Task(id, name, description);
        }

        task.setStatus(status);
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader fileReader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            fileReader.readLine();

            while (fileReader.ready()) {
                String line = fileReader.readLine();

                if (line.isBlank()) {
                    continue;
                }

                Task task = manager.taskFromString(line);

                if (task.getClass() == Subtask.class) {
                    manager.addSubtask((Subtask) task);
                } else if (task.getClass() == Epic.class) {
                    manager.addEpic((Epic) task);
                } else {
                    manager.addTask(task);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения файла");
        }

        return manager;
    }
}
