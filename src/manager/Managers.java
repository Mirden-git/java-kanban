package manager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

//    public static TaskManager getFileDefault() {
//        return new FileBackedTaskManager(Paths.get("d:/Tasks/tasks.csv"));
//    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}