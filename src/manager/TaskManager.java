package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int idCount;

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Epic> getEpics() {
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

    public void addTask(String name, String description) {
        idCount++;
        Task task = new Task(idCount, name, description);
        tasks.put(idCount, task);
    }

    public void addSubtask(String name, String description, int epicId) {
        idCount++;
        Subtask subtask = new Subtask(idCount, name, description);
        subtasks.put(idCount, subtask);
        Epic tempEpic = epics.get(epicId);
        tempEpic.addSubtaskId(idCount);
    }

    public void addEpic(String name, String description) {
        idCount++;
        Epic epic = new Epic(idCount, name, description);
        epics.put(idCount, epic);
    }

    public void updateTask(Task task) {
        int id = task.getId();

        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        } else System.out.println("в Списке нет задачи с id: " + id);
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();

        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
        } else System.out.println("в Списке нет подзадачи с id: " + id);
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();

        if (epics.containsKey(id)) {
            epics.put(id, epic);
        } else System.out.println("в Списке нет эпика с id: " + id);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask tempSubtask = subtasks.get(id);
        int epicId = tempSubtask.getEpicId();

        if (epics.get(epicId) != null) {
            List<Integer> tempArray = epics.get(epicId).getEpicSubtasksId();
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

            List<Integer> idList = epics.get(id).getEpicSubtasksId();

            for (int idItem : idList) {
                subtasks.remove(idItem);
            }

            epics.remove(id);
        }
    }

    public ArrayList<Subtask> getListOfEpicSubtasks(int id) {

        if (epics.containsKey(id)) {
            ArrayList<Subtask> list = new ArrayList<>();
            List<Integer> idForList = epics.get(id).getEpicSubtasksId();
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
            Epic tempEpic = epics.get(id);

            for (Subtask task : list) {

                switch (task.getStatus()) {
                    case IN_PROGRESS -> {
                        allNew = false;
                        allDone = false;
                    }
                    case NEW -> {
                        allNew = allNew && true;
                        allDone = false;
                    }
                    case DONE -> {
                        allNew = false;
                        allDone = allDone && true;
                    }
                }

                if (!allNew && !allDone) {
                    tempEpic.setStatus(TaskStatus.IN_PROGRESS);
                    return;
                }
            }

            if (allNew) {
                tempEpic.setStatus(TaskStatus.NEW);
            } else if (allDone) {
                tempEpic.setStatus(TaskStatus.DONE);
            }
        }
    }
}
