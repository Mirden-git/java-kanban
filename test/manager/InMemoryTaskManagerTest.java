package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private static TaskManager taskManager;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @AfterEach
    public void afterEach() {
        taskManager.clearListOfTasks();
        taskManager.clearListOfSubtasks();
        taskManager.clearListOfEpics();
    }

    @Test
    public void taskEqualItselfById() {
        Task task1 = new Task(1, "A","B");
        Task task2 = new Task(1, "C","D");
        assertEquals(task1, task2);
    }

    @Test
    public void subtaskEqualsItselfById() {
        new Epic(1,"A", "B");
        Subtask subtask1 = new Subtask(2, "A","B", 1);
        Subtask subtask2 = new Subtask(2, "C","D", 1);
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void epicEqualsItselfById() {
        Epic epic1 = new Epic(1, "A", "B");
        Epic epic2 = new Epic(1, "C", "D");
        assertEquals(epic1, epic2);
    }

    @Test
    public void epicCannotBeInsideItself() {
        taskManager.addEpic("Эпик 1", "Описание эпика 1");
        Epic epic = taskManager.getEpics().getLast();
        Subtask tempSubtask = new Subtask(epic.getId(), "Подзадача", "Описание", epic.getId());
        taskManager.addSubtask(tempSubtask);
        Subtask subtask = taskManager.getSubtasks().getLast();
        assertNotEquals(epic.getId(), subtask.getId());
    }

    @Test
    public void subtaskCannotBeEpicForItself() {
        taskManager.addEpic("Эпик 1", "Описание эпика 1");
        int epicId = taskManager.getEpics().getLast().getId();
        Subtask tempSubtask = new Subtask(epicId, "A", "B", epicId);
        taskManager.addSubtask(tempSubtask);
        Subtask subtask = taskManager.getSubtasks().getLast();
        assertNotEquals(subtask.getId(), subtask.getEpicId());
    }
    @Test
    public void differentTaskTypesMayBeAddedAndFoundById() {
        taskManager.addTask("Обычная задача 2", "Описание 2");
        taskManager.addEpic("Эпик 1", "Описание эпика 1");
        List<Epic> epics = taskManager.getEpics();
        int epicId = epics.getLast().getId();
        taskManager.addSubtask("Подзадача 1", "Описание подзадачи 1", epicId);
        List<Task> tasks = taskManager.getTasks();
        List<Subtask> subtasks = taskManager.getSubtasks();
        int taskId = tasks.getLast().getId();
        int subtaskId = subtasks.getLast().getId();

        assertNotNull(tasks);
        assertNotNull(subtasks);
        assertNotNull(epics);

        assertNotNull(taskManager.getTaskById(taskId));
        assertNotNull(taskManager.getSubtaskById(subtaskId));
        assertNotNull(taskManager.getEpicById(epicId));
    }

    @Test
    public void generatedIdDoesNotConflictWithSettedId() {
        taskManager.addTask("Обычная задача 1", "Описание 1");
        List<Task> tasks = taskManager.getTasks();
        int taskId = tasks.getLast().getId();
        Task firstTask = new Task(taskId, "Задача с заданным id", "заданный id = 1");
        taskManager.addTask(firstTask);
        assertEquals(2, taskManager.getTasks().size());
    }

    @Test
    public void fieldsOfTasksUnchangedAfterAddingToManager() {
        Task newTask = new Task(1, "Обычная задача 1", "Описание 1");
        taskManager.addTask(newTask);
        Task task = taskManager.getTasks().getLast();
        assertEquals("Обычная задача 1", task.getName());
        assertEquals("Описание 1", task.getDescription());
    }

    @Test
    public void possibilityToGetEpicSubtasksId() {
        taskManager.addEpic("Эпик 1", "Описание эпика 1");
        List<Epic> epics = taskManager.getEpics();
        int epicId = epics.getLast().getId();
        taskManager.addSubtask("Подзадача 1", "Описание подзадачи 1", epicId);
        assertNotNull(epics.getLast().getEpicSubtasksId());
    }

    @Test
    public void thereAreNoNotActualSubIdsInEpic() {
        taskManager.addEpic("Эпик 1", "Описание эпика 1");
        List<Epic> epics = taskManager.getEpics();
        int epicId = epics.getLast().getId();
        taskManager.addSubtask("Подзадача 1", "Описание подазадачи 1", epicId);
        taskManager.addSubtask("Подзадача 2", "Описание подазадачи 2", epicId);
        List<Subtask> subs = taskManager.getSubtasks();
        int subId = subs.getFirst().getId();
        int epicIndex = epics.indexOf(taskManager.getEpicById(epicId));
        taskManager.deleteSubtask(subId);
        assertFalse(epics.get(epicIndex).getEpicSubtasksId().contains(subId));
    }
}