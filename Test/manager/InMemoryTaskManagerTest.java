package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    public static TaskManager taskManager;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();

//        taskManager.addTask("Обычная задача 1", "Описание 1"); // id 1
//        taskManager.addTask("Обычная задача 2", "Описание 2"); // id 2
//        taskManager.addEpic("Эпик 1", "Описание эпика 1"); // id 3
//        taskManager.addSubtask("Подзадача 1", "Описание подзадачи 1", 3); // id 4
//        taskManager.addSubtask("Подзадача 2", "Описание подзадачи 2", 3); // id 5
//        taskManager.addEpic("Эпик 2", "Описание эпика 2"); // id 6
//        taskManager.addSubtask("Подзадача 3", "Описание подзадачи 3", 6); // id 7
    }

    @AfterEach
    public void afterEach() {
        taskManager.clearListOf(TaskType.TASK);
        taskManager.clearListOf(TaskType.SUBTASK);
        taskManager.clearListOf(TaskType.EPIC);
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
        taskManager.addEpic("Эпик 1", "Описание эпика 1"); // id 1
        Epic epic = taskManager.getEpics().get(0);
        Subtask tempSubtask = new Subtask(epic.getId(), "Подзадача", "Описание подзадачи", epic.getId());
        taskManager.addSubtask(tempSubtask); // id 2
        Subtask subtask = taskManager.getSubtasks().get(0);
        assertNotEquals(epic.getId(), subtask.getId());
    }

    @Test
    public void subtaskCannotBeEpicForItself() {
        taskManager.addEpic("Эпик 1", "Описание эпика 1"); // id 1
        Subtask tempSubtask = new Subtask(1, "A", "B", 1);
        taskManager.addSubtask(tempSubtask);
        Subtask subtask = taskManager.getSubtasks().get(0);
        assertNotEquals(subtask.getId(), subtask.getEpicId());
    }
    @Test
    public void differentTaskTypesMayBeAddedAndFoundById() {
        taskManager.addTask("Обычная задача 2", "Описание 2"); // id 1
        taskManager.addEpic("Эпик 1", "Описание эпика 1"); // id 2
        taskManager.addSubtask("Подзадача 1", "Описание подзадачи 1", 2); // id 3
        List<Task> tasks = taskManager.getTasks();
        List<Subtask> subtasks = taskManager.getSubtasks();
        List<Epic> epics = taskManager.getEpics();
        int taskId = tasks.get(0).getId();
        int subtaskId = subtasks.get(0).getId();
        int epicId = epics.get(0).getId();

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
        Task firstTask = new Task(1, "Задача с заданным id", "заданный id = 1");
        taskManager.addTask(firstTask);
        assertTrue(taskManager.getTasks().size() == 2);
    }

    @Test
    public void fieldsOfTasksUnchangedAfterAddingToManager() {
        Task newTask = new Task(1, "Обычная задача 1", "Описание 1");
        taskManager.addTask(newTask);
        Task task = taskManager.getTasks().get(0);
        assertEquals("Обычная задача 1", task.getName());
        assertEquals("Описание 1", task.getDescription());
    }

    @Test
    void historyKeepsOldVersionOfTask() {
        taskManager.addTask("A", "B");
        int id = taskManager.getTasks().get(0).getId();
        Task task = taskManager.getTaskById(id);
        taskManager.getHistoryManager().addToHistory(task);
        taskManager.changeTaskStatus(id, TaskStatus.DONE);
        List<Task> history = taskManager.getHistoryManager().getHistory();
        Task fromHistory = history.get(0);
        assertEquals(TaskStatus.NEW, fromHistory.getStatus());
    }

    @Test
    public void getEpicSubtasksId() {
    }

    @Test
    public void addSubtaskId() {
    }
}