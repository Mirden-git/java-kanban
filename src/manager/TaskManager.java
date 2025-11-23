package manager;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    int getIdCount();

    int nextId();

    void clearListOfTasks();

    void clearListOfSubtasks();

    void clearListOfEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void addTask(String name, String description, LocalDateTime startTime, Duration duration);

    void addTask(Task task);

    void addSubtask(String name, String description, int epicId, LocalDateTime startTime, Duration duration);

    void addSubtask(Subtask subtask);

    void addEpic(String name, String description, LocalDateTime startTime, Duration duration);

    void addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    List<Subtask> getListOfEpicSubtasks(int id);

    void changeTaskStatus(int id, TaskStatus newStatus);

    void changeSubtaskStatus(int id, TaskStatus newStatus);

    void changeEpicStatus(int id);

    List<Task> getHistory();

    LocalDateTime getEpicStartTime(List<Subtask> list);

    LocalDateTime getEpicEndTime(int id, List<Subtask> list);

    Duration getEpicDuration(List<Subtask> list);

    List<Task> getPrioritizedTasks();

    boolean isTimeIntersection(Task task1, Task task2);

    boolean isTimeIntersectionWithAllTasks(Task task);

    boolean isTaskExists(int id);

    boolean isSubtaskExists(int id);

    boolean isEpicExists(int id);
}