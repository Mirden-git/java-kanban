import manager.TaskManager;
import manager.Managers;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.addTask("Задача A", "Описание задачи A");
        int taskAId = taskManager.getIdCount();

        taskManager.addTask("Задача B", "Описание задачи B");
        int taskBId = taskManager.getIdCount();

        taskManager.addEpic("Эпик 1 (с подзадачами)", "Описание эпика 1");
        int epicWithSubsId = taskManager.getIdCount();

        taskManager.addSubtask("Сабтаск 1 эпика 1", "Описание S1", epicWithSubsId);
        int sub1Id = taskManager.getIdCount();

        taskManager.addSubtask("Сабтаск 2 эпика 1", "Описание S2", epicWithSubsId);
        int sub2Id = taskManager.getIdCount();

        taskManager.addSubtask("Сабтаск 3 эпика 1", "Описание S3", epicWithSubsId);
        int sub3Id = taskManager.getIdCount();

        taskManager.addEpic("Эпик 2 (без подзадач)", "Описание эпика 2");
        int epicEmptyId = taskManager.getIdCount();

        // 2) Запросы в разном порядке; после каждого — печать истории
        System.out.println("== Последовательность запросов и история ==");

        taskManager.getTaskById(taskAId);
        printHistory(taskManager, "\nПосле getTask(" + taskAId + ")");

        taskManager.getEpicById(epicWithSubsId);
        printHistory(taskManager, "\nПосле getEpic(" + epicWithSubsId + ")");

        taskManager.getSubtaskById(sub1Id);
        printHistory(taskManager, "\nПосле getSubSubtask(" + sub1Id + ")");

        taskManager.getTaskById(taskAId); // повторный просмотр — в истории не должно быть дубля
        printHistory(taskManager, "\nПосле повторного getTask(" + taskAId + ")");

        taskManager.getEpicById(epicEmptyId);
        printHistory(taskManager, "\nПосле getEpic(" + epicEmptyId + ")");

        taskManager.getSubtaskById(sub1Id); // ещё раз тот же сабтаск — дубликатов быть не должно
        printHistory(taskManager, "\nПосле повторного getSubtask(" + sub1Id + ")");

        taskManager.getSubtaskById(sub2Id);
        printHistory(taskManager, "\nПосле getSubtask(" + sub2Id + ")");

        taskManager.getEpicById(epicWithSubsId); // снова эпик с сабтасками
        printHistory(taskManager, "\nПосле повторного getEpic(" + epicWithSubsId + ")");

        taskManager.getSubtaskById(sub3Id);
        printHistory(taskManager, "\nПосле getSubtask(" + sub3Id + ")");

        // 3) Удаляем задачу, которая есть в истории, и проверяем, что она пропала из истории
        System.out.println("\n== Удаляем задачу A и проверяем историю ==");
        taskManager.deleteTask(taskAId);
        printHistory(taskManager, "После deleteTask(" + taskAId + ")");

        // 4) Удаляем эпик с тремя подзадачами.
        // История должна очиститься от этого эпика и всех его сабтасков.
        System.out.println("\n== Удаляем эпик с тремя подзадачами и проверяем историю ==");
        taskManager.deleteEpic(epicWithSubsId);
        printHistory(taskManager, "После deleteEpic(" + epicWithSubsId + ")");

        System.out.println("\n== Конечные списки ==");
        System.out.println("Задачи: " + taskManager.getTasks());
        System.out.println("Подзадачи: " + taskManager.getSubtasks());
        System.out.println("Эпики: " + taskManager.getEpics());
    }

    private static void printHistory(TaskManager tm, String label) {
        System.out.println(label + " -> " + tm.getHistory());
    }

//        taskManager.addTask("Обычная задача 1", "Описание 1");
//        taskManager.addTask("Обычная задача 2", "Описание 2");
//        taskManager.addEpic("Эпик 1", "Описание эпика 1");
//        taskManager.addSubtask("Подзадача 1", "Описание подзадачи 1", 3);
//        taskManager.addSubtask("Подзадача 2", "Описание подзадачи 2", 3);
//        taskManager.addEpic("Эпик 2", "Описание эпика 2");
//        taskManager.addSubtask("Подзадача 3", "Описание подзадачи 3", 6);
//
//        System.out.println(taskManager.getTasks());
//        System.out.println(taskManager.getSubtasks());
//        System.out.println(taskManager.getEpics());
//        System.out.println(taskManager.getListOfEpicSubtasks(3));
//        System.out.println(taskManager.getListOfEpicSubtasks(6));
//        System.out.println();
//
//        System.out.println("история");
//        taskManager.getTaskById(1);
//        taskManager.getSubtaskById(4);
//        taskManager.getEpicById(3);
//        taskManager.getTaskById(1);
//        taskManager.getSubtaskById(4);
//        taskManager.getEpicById(3);
//        taskManager.getTaskById(1);
//        taskManager.getSubtaskById(4);
//        taskManager.getEpicById(3);
//        taskManager.getTaskById(1);
//        taskManager.getSubtaskById(4);
//        taskManager.getEpicById(3);
//        System.out.println(taskManager.getHistory());
//        System.out.println();
//
//        taskManager.changeTaskStatus(1, TaskStatus.IN_PROGRESS);
//        taskManager.changeTaskStatus(2, TaskStatus.DONE);
//        System.out.println(taskManager.getTasks());
//        System.out.println();
//        taskManager.clearListOfEpics();
//
//        taskManager.changeSubtaskStatus(4, TaskStatus.DONE);
//        taskManager.changeSubtaskStatus(5, TaskStatus.IN_PROGRESS);
//        System.out.println(taskManager.getSubtasks());
//        System.out.println(taskManager.getListOfEpicSubtasks(3));
//        System.out.println(taskManager.getEpics());
//        System.out.println();
//
//        taskManager.changeSubtaskStatus(7, TaskStatus.DONE);
//        System.out.println(taskManager.getListOfEpicSubtasks(6));
//        System.out.println(taskManager.getEpics());
//        System.out.println();
//
//        taskManager.deleteTask(2);
//        taskManager.deleteSubtask(5);
//        System.out.println(taskManager.getListOfEpicSubtasks(3));
//        taskManager.deleteEpic(3);
//        System.out.println(taskManager.getListOfEpicSubtasks(3));
//        System.out.println(taskManager.getEpics());
//        System.out.println();
//
//        taskManager.clearListOfTasks();
//        taskManager.clearListOfSubtasks();
//        taskManager.clearListOfEpics();
    }

