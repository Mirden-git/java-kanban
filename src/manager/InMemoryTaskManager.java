package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private int idCount;

    public final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    // Этот пустой метод будет добавлен в методы, меняющие состав задач, чтобы в новых менеджерах не переопределять их,
    // дублируя код в некоторой степени. Лишь один этот метод будет переопределяться в новых менеджерах, смотря что и
    // как они будут делать, сейчас мы пишем новый менеджер для сохранения в файл, а завтра что-то ещё, например в БД
    protected void newActions() {
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
        Set<Integer> allIdOfTasks = tasks.keySet();

        for (int id : allIdOfTasks) {
            historyManager.remove(id);
        }

        tasks.clear();
        newActions();
    }

    @Override
    public void clearListOfSubtasks() {
        Set<Integer> allIdOfTasks = subtasks.keySet();

        for (int id : allIdOfTasks) {
            historyManager.remove(id);
        }

        subtasks.clear();

        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                epic.setEpicSubtasksId(new ArrayList<>());
                epic.setStatus(TaskStatus.NEW);
            }
        }

        newActions();
    }

    @Override
    public void clearListOfEpics() {
        Set<Integer> allIdOfTasks = epics.keySet();

        for (int id : allIdOfTasks) {
            historyManager.remove(id);
        }

        epics.clear();
        clearListOfSubtasks();
        newActions();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);

        if (task != null) historyManager.add(task);

        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask sub = subtasks.get(id);

        if (sub != null) historyManager.add(sub);

        return sub;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);

        if (epic != null) historyManager.add(epic);

        return epic;
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
        historyManager.add(tasks.get(id));
        newActions();
    }

    @Override
    public void addSubtask(String name, String description, int epicId) {
        addSubtask(new Subtask(0, name, description, epicId));
    }

    @Override
    public void addSubtask(Subtask subtask) {
        Epic tempEpic = epics.get(subtask.getEpicId());

        if (tempEpic == null) {
            System.out.println("Эпик с id=" + subtask.getEpicId() + " не найден");
            return;
        }

        int id = nextId();
        Subtask toStore = new Subtask(id, subtask.getName(), subtask.getDescription(), subtask.getEpicId());
        subtasks.put(id, toStore);
        tempEpic.addSubtaskId(id);
        changeEpicStatus(tempEpic.getId());
        historyManager.add(subtasks.get(id));
        newActions();
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
        historyManager.add(epics.get(id));
        newActions();
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();

        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        } else System.out.println("в Списке нет задачи с id: " + id);

        newActions();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();

        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
        } else System.out.println("в Списке нет подзадачи с id: " + id);

        newActions();
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();

        if (epics.containsKey(id)) {
            epics.put(id, epic);
        } else System.out.println("в Списке нет эпика с id: " + id);

        newActions();
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        newActions();
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask tempSubtask = subtasks.get(id);

        if (tempSubtask == null) return;

        int epicId = tempSubtask.getEpicId();
        subtasks.remove(id);
        historyManager.remove(id);

        if (epics.get(epicId) != null) {
            List<Integer> tempArray = new ArrayList<>();

            for (int i : epics.get(epicId).getEpicSubtasksId()) {
                if (i != id) {
                    tempArray.add(i);
                }
            }

            epics.get(epicId).setEpicSubtasksId(tempArray);
        }

        changeEpicStatus(epicId);
        newActions();
    }

    @Override
    public void deleteEpic(int id) {

        if (epics.containsKey(id)) {
            List<Integer> idList = epics.get(id).getEpicSubtasksId();

            for (int idItem : idList) {
                subtasks.remove(idItem);
                historyManager.remove(idItem);
            }

            epics.remove(id);
            historyManager.remove(id);
        }

        newActions();
    }

    @Override
    public ArrayList<Subtask> getListOfEpicSubtasks(int id) {

        if (!epics.containsKey(id)) return new ArrayList<>();

        ArrayList<Subtask> list = new ArrayList<>();
        List<Integer> idForList = epics.get(id).getEpicSubtasksId();
        Subtask tempTask;

        for (int tempId : idForList) {
            tempTask = subtasks.get(tempId);

            if (tempTask != null) {
                list.add(tempTask);
            }
        }

        return list;
    }

    @Override
    public void changeTaskStatus(int id, TaskStatus newStatus) {
        Task task = tasks.get(id);

        if (task != null) task.setStatus(newStatus);

        newActions();
    }

    @Override
    public void changeSubtaskStatus(int id, TaskStatus newStatus) {
        Subtask sub = subtasks.get(id);

        if (sub == null) return;

        sub.setStatus(newStatus);
        changeEpicStatus(sub.getEpicId());
        newActions();
    }

    @Override
    public void changeEpicStatus(int id) {

        if (!epics.containsKey(id)) return;

        ArrayList<Subtask> list = getListOfEpicSubtasks(id);
        Epic tempEpic = epics.get(id);

        if (list.isEmpty()) {
            tempEpic.setStatus(TaskStatus.NEW);
            return;
        }

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

        newActions();
    }
}