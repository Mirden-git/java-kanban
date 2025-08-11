import manager.TaskManager;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.addTask("Обычная задача 1", "Описание 1");
        taskManager.addTask("Обычная задача 2", "Описание 2");
        taskManager.addEpic("Эпик 1", "Описание эпика 1");
        taskManager.addSubtask("Подзадача 1", "Описание подзадачи 1", 3);
        taskManager.addSubtask("Подзадача 2", "Описание подзадачи 2", 3);
        taskManager.addEpic("Эпик 2", "Описание эпика 2");
        taskManager.addSubtask("Подзадача 3", "Описание подзадачи 3", 6);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getListOfEpicSubtasks(3));
        System.out.println(taskManager.getListOfEpicSubtasks(6));
        System.out.println();

        taskManager.changeTaskStatus(1, TaskStatus.IN_PROGRESS);
        taskManager.changeTaskStatus(2, TaskStatus.DONE);
        System.out.println(taskManager.getTasks());
        System.out.println();
        taskManager.clearListOfEpics();

        taskManager.changeSubtaskStatus(4, TaskStatus.DONE);
        taskManager.changeSubtaskStatus(5, TaskStatus.IN_PROGRESS);
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getListOfEpicSubtasks(3));
        System.out.println(taskManager.getEpics());
        System.out.println();

        taskManager.changeSubtaskStatus(7, TaskStatus.DONE);
        System.out.println(taskManager.getListOfEpicSubtasks(6));
        System.out.println(taskManager.getEpics());
        System.out.println();

        taskManager.deleteTask(2);
        taskManager.deleteSubtask(5);
        System.out.println(taskManager.getListOfEpicSubtasks(3));
        taskManager.deleteEpic(3);
        System.out.println(taskManager.getListOfEpicSubtasks(3));
        System.out.println(taskManager.getEpics());
        System.out.println();

        taskManager.clearListOfTasks();
        taskManager.clearListOfEpics();
        taskManager.clearListOfSubtasks();
    }
}
