package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

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

    @Test
    public void epicCannotBeInsideItself() {
        Epic epic = taskManager.getEpicById(3);
        taskManager.addSubtask(epic.getName(), epic.getDescription(), 3);
        Subtask subtask = taskManager.getSubtaskById(8);
        subtask.setEpicId(3);
        Assertions.assertNotEquals(epic, subtask);
    }

    @Test
    public void differentTaskTypesMayBeAdded() {
        assertNotNull(taskManager.getTasks());
        assertNotNull(taskManager.getSubtasks());
        assertNotNull(taskManager.getEpics());
    }

    @Test
    public void differentTaskTypesMayBeFoundById() {
        assertNotNull(taskManager.getTaskById(1));
        assertNotNull(taskManager.getSubtaskById(4));
        assertNotNull(taskManager.getEpicById(3));
    }

    @Test
    public void generatedIdDoesNotConflictWithSettedId() {
        Task firstTask = new Task(1, "Задача с заданным id", "заданный id = 1");
        taskManager.getTasks().add(firstTask);
        taskManager.addTask("Обычная задача 1", "Описание 1");
        assertTrue(taskManager.getTasks().size() == 3);
    }

    @Test
    public void fieldsOfTasksUnchangedAfterAddingToManager() {}

}