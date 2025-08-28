package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

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

}