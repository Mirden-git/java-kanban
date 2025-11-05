package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    private T manager;

    abstract T createManager();

    @BeforeEach
    public void setUp() {
        manager = createManager();
    }

    @AfterEach
    public void afterEach() {
        manager.clearListOfTasks();
        manager.clearListOfSubtasks();
        manager.clearListOfEpics();
    }

    protected LocalDateTime dateTime(int h, int m) {
        return LocalDateTime.of(2025, 1, 1, h, m);
    }

    protected Duration duration(int duration) {
        return Duration.ofMinutes(duration);
    }

    @Test
    public void taskEqualItselfById() {
        Task task1 = new Task(1, "A", "B", dateTime(12, 0), duration(15));
        Task task2 = new Task(1, "C", "D", dateTime(13, 0), duration(20));
        assertEquals(task1, task2);
    }

    @Test
    public void subtaskEqualsItselfById() {
        new Epic(1, "A", "B", null, Duration.ZERO);
        Subtask subtask1 = new Subtask(2, "A", "B", 1, dateTime(12, 0), duration(15));
        Subtask subtask2 = new Subtask(2, "C", "D", 1, dateTime(13, 0), duration(20));
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void epicEqualsItselfById() {
        Epic epic1 = new Epic(1, "A", "B", dateTime(12, 0), duration(15));
        Epic epic2 = new Epic(1, "C", "D", dateTime(13, 0), duration(20));
        assertEquals(epic1, epic2);
    }

    @Test
    public void epicCannotBeInsideItself() {
        manager.addEpic("Эпик 1", "Описание эпика 1", null, Duration.ZERO);
        Epic epic = manager.getEpics().getLast();
        Subtask tempSubtask = new Subtask(epic.getId(), "Подзадача", "Описание", epic.getId(),
                dateTime(12, 0), duration(15));
        manager.addSubtask(tempSubtask);
        Subtask subtask = manager.getSubtasks().getLast();
        assertNotEquals(epic.getId(), subtask.getId());
    }

    @Test
    public void subtaskCannotBeEpicForItself() {
        manager.addEpic("Эпик 1", "Описание эпика 1", null, Duration.ZERO);
        int epicId = manager.getEpics().getLast().getId();
        Subtask tempSubtask =
                new Subtask(epicId, "A", "B", epicId, dateTime(12, 0), duration(15));
        manager.addSubtask(tempSubtask);
        Subtask subtask = manager.getSubtasks().getLast();
        assertNotEquals(subtask.getId(), subtask.getEpicId());
    }

    @Test
    public void differentTaskTypesMayBeAddedAndFoundById() {
        manager.addTask("Обычная задача 2", "Описание 2", dateTime(12, 0), duration(15));
        manager.addEpic("Эпик 1", "Описание эпика 1", null, Duration.ZERO);
        List<Epic> epics = manager.getEpics();
        int epicId = epics.getLast().getId();
        manager.addSubtask("Подзадача 1", "Описание подзадачи 1",
                epicId, dateTime(12, 0), duration(15));
        List<Task> tasks = manager.getTasks();
        List<Subtask> subtasks = manager.getSubtasks();
        int taskId = tasks.getLast().getId();
        int subtaskId = subtasks.getLast().getId();

        assertNotNull(tasks);
        assertNotNull(subtasks);
        assertNotNull(epics);

        assertNotNull(manager.getTaskById(taskId));
        assertNotNull(manager.getSubtaskById(subtaskId));
        assertNotNull(manager.getEpicById(epicId));
    }

    @Test
    public void generatedIdDoesNotConflictWithSettedId() {
        manager.addTask("Обычная задача 1", "Описание 1", dateTime(12, 0), duration(15));
        List<Task> tasks = manager.getTasks();
        int taskId = tasks.getLast().getId();
        Task firstTask = new Task(taskId, "Задача с заданным id", "заданный id = 1",
                dateTime(12, 0), duration(15));
        manager.addTask(firstTask);
        assertEquals(2, manager.getTasks().size());
    }

    @Test
    public void fieldsOfTasksUnchangedAfterAddingToManager() {
        Task newTask = new Task(1, "Обычная задача 1", "Описание 1",
                dateTime(12, 0), duration(15));
        manager.addTask(newTask);
        Task task = manager.getTasks().getLast();
        assertEquals("Обычная задача 1", task.getName());
        assertEquals("Описание 1", task.getDescription());
    }

    @Test
    public void possibilityToGetEpicSubtasksId() {
        manager.addEpic("Эпик 1", "Описание эпика 1", null, Duration.ZERO);
        List<Epic> epics = manager.getEpics();
        int epicId = epics.getLast().getId();
        manager.addSubtask("Подзадача 1", "Описание подзадачи 1", epicId,
                dateTime(12, 0), duration(15));
        assertNotNull(epics.getLast().getEpicSubtasksId());
    }

    @Test
    public void thereAreNoNotActualSubIdsInEpic() {
        manager.addEpic("Эпик 1", "Описание эпика 1", null, Duration.ZERO);
        List<Epic> epics = manager.getEpics();
        int epicId = epics.getLast().getId();
        manager.addSubtask("Подзадача 1", "Описание 1", epicId, dateTime(12, 0), duration(15));
        manager.addSubtask("Подзадача 2", "Описание 2", epicId, dateTime(13, 0), duration(20));
        List<Subtask> subs = manager.getSubtasks();
        int subId = subs.getFirst().getId();
        int epicIndex = epics.indexOf(manager.getEpicById(epicId));
        manager.deleteSubtask(subId);
        assertFalse(epics.get(epicIndex).getEpicSubtasksId().contains(subId));
    }

    @Test
    void epicStatusAllNew() {
        manager.addEpic("Эпик 1", "Описание эпика 1", null, Duration.ZERO);
        Epic e = manager.getEpics().getLast();
        manager.addSubtask(new Subtask(0, "S1", "",
                e.getId(), dateTime(10, 0), Duration.ofMinutes(10)));
        manager.addSubtask(new Subtask(0, "S2", "",
                e.getId(), dateTime(11, 0), Duration.ofMinutes(10)));
        assertEquals(TaskStatus.NEW, e.getStatus());
    }

    @Test
    void epicStatusAllDone() {
        manager.addEpic("Эпик 1", "Описание эпика 1", null, Duration.ZERO);
        Epic e = manager.getEpics().getLast();
        manager.addSubtask(new Subtask(0, "S1", "",
                e.getId(), dateTime(10, 0), Duration.ofMinutes(10)));
        Subtask sub1 = manager.getSubtasks().getLast();
        manager.addSubtask(new Subtask(0, "S2", "",
                e.getId(), dateTime(11, 0), Duration.ofMinutes(10)));
        Subtask sub2 = manager.getSubtasks().getLast();
        manager.changeSubtaskStatus(sub1.getId(), TaskStatus.DONE);
        manager.changeSubtaskStatus(sub2.getId(), TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, e.getStatus());
    }

    @Test
    void epicStatusNewDone() {
        manager.addEpic("Эпик 1", "Описание эпика 1", null, Duration.ZERO);
        Epic e = manager.getEpics().getLast();
        manager.addSubtask(new Subtask(0, "S1", "",
                e.getId(), dateTime(10, 0), Duration.ofMinutes(10)));
        Subtask sub1 = manager.getSubtasks().getLast();
        manager.addSubtask(new Subtask(0, "S2", "",
                e.getId(), dateTime(11, 0), Duration.ofMinutes(10)));
        manager.changeSubtaskStatus(sub1.getId(), TaskStatus.DONE);
        assertEquals(TaskStatus.IN_PROGRESS, e.getStatus());
    }

    @Test
    void epicStatusAllInProgress() {
        manager.addEpic("Эпик 1", "Описание эпика 1", null, Duration.ZERO);
        Epic e = manager.getEpics().getLast();
        manager.addSubtask(new Subtask(0, "S1", "",
                e.getId(), dateTime(10, 0), Duration.ofMinutes(10)));
        Subtask sub1 = manager.getSubtasks().getLast();
        manager.addSubtask(new Subtask(0, "S2", "",
                e.getId(), dateTime(11, 0), Duration.ofMinutes(10)));
        Subtask sub2 = manager.getSubtasks().getLast();
        manager.changeSubtaskStatus(sub1.getId(), TaskStatus.IN_PROGRESS);
        manager.changeSubtaskStatus(sub2.getId(), TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, e.getStatus());
    }

    @Test
    void timeIntersectionTrue() {
        manager.addTask("задача 1", "Описание 1", dateTime(12, 0), duration(60));
        manager.addTask("задача 2", "Описание 2", dateTime(12, 30), duration(60));
        Task b = manager.getTasks().getLast();
        assertTrue(manager.isTimeIntersectionWithAllTasks(b));
    }

    @Test
    void timeIntersectionFalse() {
        manager.addTask("задача 1", "Описание 1", dateTime(13, 0), duration(60));
        manager.addTask("задача 2", "Описание 2", dateTime(12, 0), duration(60));
        Task b = manager.getTasks().getLast();
        assertFalse(manager.isTimeIntersectionWithAllTasks(b));
    }

    @Test
    void prioritizedTasksShouldBeSortedByStartTime() {
        manager.addTask("A", "desc", dateTime(14, 0), duration(10));
        int a = manager.getTasks().getLast().getId();
        manager.addTask("B", "desc", dateTime(10, 0), duration(10));
        int b = manager.getTasks().getLast().getId();
        manager.addTask("C", "desc", dateTime(12, 0), duration(10));
        int c = manager.getTasks().getLast().getId();
        List<Integer> ordered = new ArrayList<>();
        ordered.add(b);
        ordered.add(c);
        ordered.add(a);
        List<Integer> priority = manager.getPrioritizedTasks()
                .stream()
                .map(Task::getId)
                .toList();
        assertEquals(ordered, priority);
    }
}