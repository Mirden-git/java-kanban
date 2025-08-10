import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Обычная задача 1", "Описание 1");
        Task task2 = new Task("Обычная задача 2", "Описание 2");
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 3);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", 3);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", 6);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);

        System.out.println(taskManager.getTasks().toString());
        System.out.println(taskManager.getSubtasks().toString());
        System.out.println(taskManager.getEpics().toString());
        System.out.println(taskManager.getListOfEpicSubtasks(3));
        System.out.println(taskManager.getListOfEpicSubtasks(6));
        System.out.println();

        taskManager.changeTaskStatus(1, TaskStatus.IN_PROGRESS);
        taskManager.changeTaskStatus(2, TaskStatus.DONE);
        System.out.println(taskManager.getTasks().toString());
        System.out.println();
        taskManager.clearListOfEpics();

        taskManager.changeSubtaskStatus(4, TaskStatus.DONE);
        taskManager.changeSubtaskStatus(5, TaskStatus.IN_PROGRESS);
        System.out.println(taskManager.getSubtasks().toString());
        System.out.println(taskManager.getListOfEpicSubtasks(3));
        System.out.println(taskManager.getEpics().toString());
        System.out.println();

        taskManager.changeSubtaskStatus(7, TaskStatus.DONE);
        System.out.println(taskManager.getListOfEpicSubtasks(6));
        System.out.println(taskManager.getEpics().toString());
        System.out.println();

        taskManager.deleteTask(2);
        taskManager.deleteSubtask(5);
        System.out.println(taskManager.getListOfEpicSubtasks(3));
        taskManager.deleteEpic(3);
        System.out.println(taskManager.getListOfEpicSubtasks(3));
        System.out.println(taskManager.getEpics().toString());
        System.out.println();

        Task task3 = new Task("123", "asd");
        taskManager.updateTask(1, task3);
        System.out.println(taskManager.getTasks().toString());

        Subtask subtask4 = new Subtask("123", "asd", 3);
        taskManager.updateSubtask(1, subtask4);
        System.out.println(taskManager.getSubtasks().toString());

        Epic epic3 = new Epic("123", "asd");
        taskManager.updateTask(3, epic3);
        System.out.println(taskManager.getEpics().toString());

        taskManager.clearListOfTasks();
        taskManager.clearListOfEpics();
        taskManager.clearListOfSubtasks();
    }
}
