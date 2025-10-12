package manager;

import exceptions.ManagerSaveException;
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
    private final Path fileName;

    public FileBackedTaskManager(Path fileName) {
        super();
        this.fileName = fileName;

        if (Files.notExists(fileName)) {

            try {
                Files.createFile(fileName);
            } catch (IOException e) {
                System.out.printf("Ошибка создания файла %s", fileName);
            }
        }

        loadFromFile(fileName.toFile());
    }

    public void save() {

        try (BufferedWriter fileWriter = Files.newBufferedWriter(fileName, StandardCharsets.UTF_8,
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
            throw new ManagerSaveException(e);
        }
    }

    @Override
    protected void newActions() {
        save();
    }

    private static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath());

        try (BufferedReader fileReader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            fileReader.readLine();

            while (fileReader.ready()) {

                if (fileReader.readLine().isBlank()) {
                    continue;
                }

                String line = fileReader.readLine();
                String[] splitOfLine = line.split(",");

                switch (splitOfLine[1]) {
                    case "TASK": {
                        int id = Integer.parseInt(splitOfLine[0]);
                        String name = splitOfLine[2];
                        TaskStatus status;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения файла");
        }

        return manager;
    }
}
