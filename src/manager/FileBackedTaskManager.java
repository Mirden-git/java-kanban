package manager;

import exceptions.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private final Path path;

    public FileBackedTaskManager(File file) {
        super();

        if (file == null) {
            throw new ManagerSaveException();
        }

        this.file = file.getAbsoluteFile();
        this.path = this.file.toPath();

        try {
            Path parent = path.getParent();

            if (Files.isDirectory(path)) {
                throw new ManagerSaveException();
            }

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(path)) {
                Files.createFile(path);
            }

        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public void save() {

        try (BufferedWriter fileWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
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

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        record FieldsOfTasks(int id, String name, TaskStatus status, String description) {
        }

        try (BufferedReader fileReader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            fileReader.readLine();

            while (fileReader.ready()) {
                String line = fileReader.readLine();

                if (line.isBlank()) {
                    continue;
                }

                String[] splitOfLine = line.split(",");
                FieldsOfTasks f = new FieldsOfTasks(
                        Integer.parseInt(splitOfLine[0]),
                        splitOfLine[2],
                        TaskStatus.valueOf(splitOfLine[3]),
                        splitOfLine[4]
                );

                switch (splitOfLine[1]) {
                    case "SUBTASK": {
                        int epicId = Integer.parseInt(splitOfLine[5]);
                        Subtask taskToRestore = new Subtask(f.id, f.name, f.description, epicId);
                        manager.addSubtask(taskToRestore);
                        manager.getSubtasks().get(f.id).setStatus(f.status);
                        break;
                    }
                    case "EPIC": {
                        Epic taskToRestore = new Epic(f.id, f.name, f.name);
                        manager.addEpic(taskToRestore);
                        manager.getEpics().get(f.id).setStatus(f.status);
                        break;
                    }
                    case "TASK": {
                        Task taskToRestore = new Task(f.id, f.name, f.name);
                        manager.addTask(taskToRestore);
                        manager.getTasks().get(f.id).setStatus(f.status);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения файла");
        }

        return manager;
    }
}
