package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    void clearListOfTasks();

    void clearListOfSubtasks();

    void clearListOfEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void addTask(String name, String description);

    void addSubtask(String name, String description, int epicId);

    void addEpic(String name, String description);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    ArrayList<Subtask> getListOfEpicSubtasks(int id);

    void changeTaskStatus(int id, TaskStatus newStatus);

    void changeSubtaskStatus(int id, TaskStatus newStatus);

    void changeEpicStatus(int id);

}
