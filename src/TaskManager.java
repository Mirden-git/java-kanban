import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    static private final HashMap<Integer, Task> tasks = new HashMap<>();
    static private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    static private final HashMap<Integer, Epic> epics = new HashMap<>();
    static private int idCount;

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public ArrayList<Task> getListOfTasks(HashMap<Integer, Task> tasks) {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getListOfSubtasks(HashMap<Integer, Subtask> subtasks) {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getListOfEpics(HashMap<Integer, Epic> epics) {
        return new ArrayList<>(epics.values());
    }

    public void clearListOfTasks() {
        tasks.clear();
    }

    public void clearListOfSubtasks() {
        subtasks.clear();
    }

    public void clearListOfEpics() {
        epics.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void addTask(Task task) {
        idCount++;
        tasks.put(idCount, task);
    }

    public void addSubtask(Subtask subtask) {
        idCount++;
        subtasks.put(idCount, subtask);
        int epicId = subtask.getEpicId();
        Epic tempEpic = epics.get(epicId);
        tempEpic.addSubtaskId(idCount);
    }

    public void addEpic(Epic epic) {
        idCount++;
        epics.put(idCount, epic);
    }

    public void updateTask(int id, Task task) {
        tasks.replace(id, task);
    }

    public void updateSubtask(int id, Subtask subtask) {
        subtasks.replace(id, subtask);
    }

    public void updateEpic(int id, Epic epic) {
        epics.replace(id, epic);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask tempSubtask = subtasks.get(id);
        int epicId = tempSubtask.getEpicId();

        if (epics.get(epicId) != null) {
            ArrayList<Integer> tempArray = epics.get(epicId).getEpicSubtasksId();
            int idForDeletion = -1;

            for (int i : tempArray) {
                if (i == id) {
                    idForDeletion = tempArray.indexOf(i);
                    break;
                }
            }

            if (idForDeletion >= 0) {
                tempArray.remove(idForDeletion);
            }
        }

        subtasks.remove(id);
    }

    public void deleteEpic(int id) {

        if (epics.containsKey(id)) {

            ArrayList<Integer> idList = epics.get(id).getEpicSubtasksId();

            for (int idItem : idList) {
                subtasks.remove(idItem);
            }

            epics.remove(id);
        }
    }

    public ArrayList<Subtask> getListOfEpicSubtasks(int id) {

        if (epics.containsKey(id)) {
            ArrayList<Subtask> list = new ArrayList<>();
            ArrayList<Integer> idForList = epics.get(id).getEpicSubtasksId();
            Subtask tempTask;

            for (int tempId : idForList) {
                tempTask = subtasks.get(tempId);
                list.add(tempTask);
            }

            return list;
        }
        return null;
    }

    public void changeTaskStatus(int id, TaskStatus newStatus) {
        tasks.get(id).setStatus(newStatus);
    }

    public void changeSubtaskStatus(int id, TaskStatus newStatus) {
        subtasks.get(id).setStatus(newStatus);
        changeEpicStatus(subtasks.get(id).getEpicId());
    }

    private void changeEpicStatus(int id) {

        if (epics.containsKey(id)) {
            ArrayList<Subtask> list = getListOfEpicSubtasks(id);
            boolean allNew = true;
            boolean allDone = true;

            for (Subtask task : list) {

                switch (task.getStatus()) {
                    case IN_PROGRESS -> {
                        allNew = false;
                        allDone = false;
                    }
                    case NEW -> {
                        allNew = allNew && true;
                        allDone = allDone && false;
                    }
                    case DONE -> {
                        allNew = allNew && false;
                        allDone = allDone && true;
                    }
                }

                if (!allNew && !allDone) {
                    break;
                }

            }

            if (allNew) {
                epics.get(id).setStatus(TaskStatus.NEW);
            } else if (allDone) {
                epics.get(id).setStatus(TaskStatus.DONE);
            } else {
                epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}
