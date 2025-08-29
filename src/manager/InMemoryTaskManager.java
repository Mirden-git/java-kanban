package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private int idCount;

    public final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int getIdCount() {
        return idCount;
    }

    @Override
    public int nextId() {

        do {
            idCount++;
        } while (tasks.containsKey(idCount) || subtasks.containsKey(idCount) || epics.containsKey(idCount));

        return idCount;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistoryList();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearListOfTasks() {
        tasks.clear();
    }

    @Override
    public void clearListOfSubtasks() {
        subtasks.clear();
    }

    @Override
    public void clearListOfEpics() {
        epics.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.addToHistory(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.addToHistory(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.addToHistory(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void addTask(String name, String description) {
        addTask(new Task(0, name, description));
    }

    @Override
    public void addTask(Task task) {
        int id = nextId();
        Task toStore = new Task(id, task.getName(), task.getDescription());
        tasks.put(id, toStore);
    }

    @Override
    public void addSubtask(String name, String description, int epicId) {
        addSubtask(new Subtask(0, name, description, epicId));
    }

    @Override
    public void addSubtask(Subtask subtask) {
        int id = nextId();
        Subtask toStore = new Subtask(id, subtask.getName(), subtask.getDescription(), subtask.getEpicId());
        subtasks.put(id, toStore);

        Epic tempEpic = epics.get(subtask.getEpicId());

        if (tempEpic != null) {
            tempEpic.addSubtaskId(id);
            changeEpicStatus(tempEpic.getId());
        } else {
            System.out.println("Эпик с id=" + subtask.getEpicId() + " не найден");
        }
    }

    @Override
    public void addEpic(String name, String description) {
        addEpic(new Epic(0, name, description));
    }

    @Override
    public void addEpic(Epic epic) {
        int id = nextId();
        Epic toStore = new Epic(id, epic.getName(), epic.getDescription());
        epics.put(id, toStore);
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();

        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        } else System.out.println("в Списке нет задачи с id: " + id);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();

        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
        } else System.out.println("в Списке нет подзадачи с id: " + id);
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();

        if (epics.containsKey(id)) {
            epics.put(id, epic);
        } else System.out.println("в Списке нет эпика с id: " + id);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
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

    @Override
    public void deleteEpic(int id) {

        if (epics.containsKey(id)) {
            List<Integer> idList = epics.get(id).getEpicSubtasksId();

            for (int idItem : idList) {
                subtasks.remove(idItem);
            }

            epics.remove(id);
        }
    }

    @Override
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

    @Override
    public void changeTaskStatus(int id, TaskStatus newStatus) {
        tasks.get(id).setStatus(newStatus);
    }

    @Override
    public void changeSubtaskStatus(int id, TaskStatus newStatus) {
        subtasks.get(id).setStatus(newStatus);
        changeEpicStatus(subtasks.get(id).getEpicId());
    }

    @Override
    public void changeEpicStatus(int id) {

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
