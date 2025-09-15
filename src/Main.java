import manager.TaskManager;
import manager.Managers;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.addTask("Задача A", "Описание задачи A");
        int taskA = taskManager.getIdCount();

        taskManager.addTask("Задача B", "Описание задачи B");
        int taskB = taskManager.getIdCount();

        taskManager.addEpic("Эпик 1 (с подзадачами)", "Описание эпика 1");
        int epicWithSubs = taskManager.getIdCount();

        taskManager.addSubtask("Сабтаск 1 эпика 1", "Описание S1", epicWithSubs);
        int s1 = taskManager.getIdCount();

        taskManager.addSubtask("Сабтаск 2 эпика 1", "Описание S2", epicWithSubs);
        int s2 = taskManager.getIdCount();

        taskManager.addSubtask("Сабтаск 3 эпика 1", "Описание S3", epicWithSubs);
        int s3 = taskManager.getIdCount();

        taskManager.addEpic("Эпик 2 (без подзадач)", "Описание эпика 2");
        int epicEmpty = taskManager.getIdCount();

        // 2) Запросы в разном порядке; после каждого — печать истории
        System.out.println("== Последовательность запросов и история ==");

        taskManager.getTaskById(taskA);
        printHistory(taskManager, "После getTask(" + taskA + ")");

        taskManager.getEpicById(epicWithSubs);
        printHistory(taskManager, "После getEpic(" + epicWithSubs + ")");

        taskManager.getSubtaskById(s1);
        printHistory(taskManager, "После getSubtask(" + s1 + ")");

        taskManager.getTaskById(taskA); // повторный просмотр — в истории не должно быть дубля
        printHistory(taskManager, "После повторного getTask(" + taskA + ")");

        taskManager.getEpicById(epicEmpty);
        printHistory(taskManager, "После getEpic(" + epicEmpty + ")");

        taskManager.getSubtaskById(s1); // ещё раз тот же сабтаск — дубликатов быть не должно
        printHistory(taskManager, "После повторного getSubtask(" + s1 + ")");

        taskManager.getSubtaskById(s2);
        printHistory(taskManager, "После getSubtask(" + s2 + ")");

        taskManager.getEpicById(epicWithSubs); // снова эпик с сабтасками
        printHistory(taskManager, "После повторного getEpic(" + epicWithSubs + ")");

        taskManager.getSubtaskById(s3);
        printHistory(taskManager, "После getSubtask(" + s3 + ")");

        // 3) Удаляем задачу, которая есть в истории, и проверяем, что она пропала из истории
        System.out.println("\n== Удаляем задачу A и проверяем историю ==");
        taskManager.deleteTask(taskA);
        printHistory(taskManager, "После deleteTask(" + taskA + ")");

        // 4) Удаляем эпик с тремя подзадачами.
        // История должна очиститься от этого эпика и всех его сабтасков.
        System.out.println("\n== Удаляем эпик с тремя подзадачами и проверяем историю ==");
        taskManager.deleteEpic(epicWithSubs);
        printHistory(taskManager, "После deleteEpic(" + epicWithSubs + ")");

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

