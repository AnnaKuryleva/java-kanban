package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager;
    protected final Set<Task> priorityTasksList = new TreeSet<>(
            Comparator.comparing(Task::getStartTime));

    private int idCounter = 1;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public int idGenerator() {
        return idCounter++;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(priorityTasksList);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(task -> {
            priorityTasksList.remove(task);
            historyManager.remove(task.getId());
        });
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.values().forEach(subTask -> {
            priorityTasksList.remove(subTask);
            historyManager.remove(subTask.getId());
        });
        subTasks.clear();
        epics.values().forEach(Epic::removeSubTasks);
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(epic -> {
            historyManager.remove(epic.getId());
        });
        epics.clear();
        deleteAllSubTasks();
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (tasks.get(id) == null) {
            priorityTasksList.remove(task);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubTask(id);
                priorityTasksList.remove(subTask);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.getSubTasks().values().forEach(subTask -> {
                subTasks.remove(subTask.getId());
            });
            epic.removeSubTasks();
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    public List<SubTask> getAllSubtasksByEpicId(int id) {
        final Epic epic = epics.get(id);
        if (epic != null) {
            return epic.getSubTasks().values().stream()
                    .filter(subTask -> subTask != null)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            addTaskToHistoryList(task);
        }
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        addTaskToHistoryList(subTask);
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        addTaskToHistoryList(epic);
        return epic;
    }

    @Override
    public Task createTask(Task newTask) {
        if (newTask.getStartTime() == null) {
            newTask.setId(idGenerator());
            tasks.put(newTask.getId(), newTask);
            return newTask;
        }
        if (findIntersectionTasks(newTask)) {
            throw new IllegalArgumentException("Задача пересекается с другой задачей по времени выполнения");
        }
        newTask.setId(idGenerator());
        tasks.put(newTask.getId(), newTask);
        priorityTasksList.add(newTask);
        return newTask;
    }

    @Override
    public void createSubTask(SubTask newSubTask) {
        int epicId = newSubTask.getEpicId();
        if (epicId == newSubTask.getId()) {
            return;
        }
        Epic relatedEpic = epics.get(epicId);
        if (relatedEpic == null) {
            return;
        }
        if (newSubTask.getStartTime() == null) {
            relatedEpic.addSubTask(newSubTask);
            newSubTask.setId(idGenerator());
            subTasks.put(newSubTask.getId(), newSubTask);
            return;
        }
        if (findIntersectionTasks(newSubTask)) {
            throw new IllegalArgumentException("Подзадача пересекается с другой задачей по времени выполнения");
        }
        relatedEpic.addSubTask(newSubTask);
        newSubTask.setId(idGenerator());
        subTasks.put(newSubTask.getId(), newSubTask);
        priorityTasksList.add(newSubTask);
    }

    @Override
    public void createEpic(Epic newEpic) {
        newEpic.setId(idGenerator());
        epics.put(newEpic.getId(), newEpic);
    }

    @Override
    public void updateTask(Task updateTask) {
        Integer idTask = updateTask.getId();
        if (!tasks.containsKey(idTask)) {
            return;
        }
        Task oldTask = tasks.get(idTask);
        priorityTasksList.remove(oldTask);
        if (updateTask.getStartTime() == null) {
            tasks.replace(idTask, updateTask);
            return;
        }
        if (findIntersectionTasks(updateTask)) {
            priorityTasksList.add(oldTask);
            throw new IllegalArgumentException("Задача пересекается с другой задачей по времени выполнения.");
        }
        tasks.replace(idTask, updateTask);
        priorityTasksList.add(updateTask);
    }

    @Override
    public void updateSubTask(SubTask myUpdateSubTask) {
        int subTaskId = myUpdateSubTask.getId();
        if (!subTasks.containsKey(subTaskId)) {
            return;
        }
        SubTask oldSubTask = subTasks.get(subTaskId);
        int epicId = myUpdateSubTask.getEpicId();
        Epic relatedEpic = epics.get(epicId);
        if (relatedEpic == null || oldSubTask.getEpicId() != epicId) {
            return;
        }
        priorityTasksList.remove(oldSubTask);
        if (myUpdateSubTask.getStartTime() == null) {
            relatedEpic.addSubTask(myUpdateSubTask);
            subTasks.replace(myUpdateSubTask.getId(), myUpdateSubTask);
            return;
        }
        if (findIntersectionTasks(myUpdateSubTask)) {
            priorityTasksList.add(oldSubTask);
            throw new IllegalArgumentException("Подзадача пересекается с другой задачей по времени выполнения.");
        }
        relatedEpic.addSubTask(myUpdateSubTask);
        subTasks.replace(myUpdateSubTask.getId(), myUpdateSubTask);
        priorityTasksList.add(myUpdateSubTask);
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        final Epic oldEpic = epics.get(updateEpic.getId());
        if (oldEpic != null) {
            oldEpic.setName(updateEpic.getName());
            oldEpic.setDescription(updateEpic.getDescription());
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    public void addTaskToHistoryList(Task task) {
        historyManager.add(task);
    }

    public boolean findIntersectionTasks(Task task) {
        LocalDateTime startTimeThis = task.getStartTime();
        LocalDateTime endTimeThis = task.getEndTime();

        return priorityTasksList.stream()
                .anyMatch(other -> {
                    LocalDateTime startTimeOther = other.getStartTime();
                    LocalDateTime endTimeOther = other.getEndTime();
                    return (startTimeThis.isAfter(startTimeOther) && startTimeThis.isBefore(endTimeOther)) ||
                            (startTimeOther.isAfter(startTimeThis) && startTimeOther.isBefore(endTimeThis));
                });
    }
}