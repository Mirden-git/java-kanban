package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private int idCount;
    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime).thenComparingInt(Task::getId);
    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    public final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    protected void newActions() {
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
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
            prioritizedTasks.remove(tasks.get(id));
        }

        tasks.clear();
        newActions();
    }

    @Override
    public void clearListOfSubtasks() {
        Set<Integer> allIdOfTasks = subtasks.keySet();

        for (int id : allIdOfTasks) {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
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
    public void addTask(String name, String description, LocalDateTime startTime, Duration duration) {
        addTask(new Task(0, name, description, startTime, duration));
    }

    @Override
    public void addTask(Task task) {
        int id = nextId();
        Task toStore = new Task(id, task.getName(), task.getDescription(), task.getStartTime(), task.getDuration());
        tasks.put(id, toStore);
        Task newTask = tasks.get(id);
        historyManager.add(newTask);

        if (newTask.getStartTime() != null && !isTimeIntersectionWithAllTasks(newTask)) {
            prioritizedTasks.add(newTask);
        } else {
            System.out.println("Нет времени начала задачи или есть пересечение по времени начала с имеющимися");
        }

        newActions();
    }

    @Override
    public void addSubtask(String name, String description, int epicId, LocalDateTime startTime, Duration duration) {
        addSubtask(new Subtask(0, name, description, epicId, startTime, duration));
    }

    @Override
    public void addSubtask(Subtask subtask) {
        Epic tempEpic = epics.get(subtask.getEpicId());

        if (tempEpic == null) {
            System.out.println("Эпик с id=" + subtask.getEpicId() + " не найден");
            return;
        }

        int id = nextId();
        Subtask toStore = new Subtask(id, subtask.getName(), subtask.getDescription(), subtask.getEpicId(),
                subtask.getStartTime(), subtask.getDuration());
        subtasks.put(id, toStore);
        tempEpic.addSubtaskId(id);
        changeEpicStatus(tempEpic.getId());
        Subtask newSubtask = subtasks.get(id);
        historyManager.add(newSubtask);

        if (newSubtask.getStartTime() != null && !isTimeIntersectionWithAllTasks(newSubtask)) {
            prioritizedTasks.add(newSubtask);
        } else {
            System.out.println("Нет времени начала задачи или есть пересечение по времени начала с имеющимися");
        }

        newActions();
    }

    @Override
    public void addEpic(String name, String description, LocalDateTime startTime, Duration duration) {
        addEpic(new Epic(0, name, description, startTime, duration));
    }

    @Override
    public void addEpic(Epic epic) {
        int id = nextId();
        Epic toStore = new Epic(id, epic.getName(), epic.getDescription(), epic.getStartTime(), epic.getDuration());
        epics.put(id, toStore);
        historyManager.add(epics.get(id));
        newActions();
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();

        if (tasks.containsKey(id) && !isTimeIntersectionWithAllTasks(task)) {
            prioritizedTasks.remove(tasks.get(id));
            tasks.put(id, task);
            prioritizedTasks.add(task);
        } else System.out.println("в Списке нет задачи с id: " + id + " или есть пересечение времени");

        newActions();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();

        if (subtasks.containsKey(id) && !isTimeIntersectionWithAllTasks(subtask)) {
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.put(id, subtask);
            prioritizedTasks.add(subtask);
            changeEpicStatus(subtask.getEpicId());
        } else System.out.println("в Списке нет подзадачи с id: " + id + " или есть пересечение времени");

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
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
        newActions();
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask tempSubtask = subtasks.get(id);

        if (tempSubtask == null) return;

        int epicId = tempSubtask.getEpicId();
        prioritizedTasks.remove(subtasks.get(id));
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
                prioritizedTasks.remove(subtasks.get(id));
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

        return epics.get(id).getEpicSubtasksId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
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

        LocalDateTime startTime = list.stream()
                .min(Comparator.comparing(Subtask::getStartTime))
                .get().getStartTime();

        Duration totalDuration = list.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime endTime = startTime.plus(totalDuration);

        tempEpic.setStartTime(startTime);
        tempEpic.setDuration(totalDuration);
        tempEpic.setEndTime(endTime);

        newActions();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        TreeSet<Task> copy = new TreeSet<>(comparator);
        copy.addAll(prioritizedTasks);
        return copy;
    }

    @Override
    public boolean isTimeIntersection(Task task1, Task task2) {
        return task1.getEndTime().isAfter(task2.getStartTime()) && task2.getEndTime().isAfter(task1.getStartTime());
    }

    @Override
    public boolean isTimeIntersectionWithAllTasks(Task task) {
        boolean result1 = getTasks().stream()
                .filter(item -> item.getId() != task.getId())
                .anyMatch(item -> isTimeIntersection(item, task));
        boolean result2 = getSubtasks().stream()
                .filter(item -> item.getId() != task.getId())
                .anyMatch(item -> isTimeIntersection(item, task));
        return result1 || result2;
    }
}