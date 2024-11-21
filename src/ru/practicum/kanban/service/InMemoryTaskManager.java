package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager;

    private int idCounter = 1;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public int idGenerator() {
        return idCounter++;
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
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic relatedEpic : epics.values()) {
            relatedEpic.removeSubTasks();
        }
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubTasks();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.get(id);
        tasks.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubTask(id);
            }
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer keySubTask : epic.getSubTasks().keySet()) {
                subTasks.remove(keySubTask);
            }
            epic.removeSubTasks();
            epics.remove(id);
        }
    }

    @Override
    public List<SubTask> getAllSubtasksByEpicId(int id) {
        List<SubTask> listSubTasks = new ArrayList<>();
        final Epic epic = epics.get(id);
        if (epic != null) {
            Map<Integer, SubTask> subTaskMap = epic.getSubTasks();
            for (SubTask subTask : subTaskMap.values()) {
                if (subTask != null) {
                    listSubTasks.add(subTask);
                }
            }
        }
        return listSubTasks;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        addTaskToHistoryList(task);
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
        newTask.setId(idGenerator());
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public void createSubTask(SubTask newSubTask) {
        int epicId = newSubTask.getEpicId();
        if (epicId == newSubTask.getId()) {
            return;
        }
        Epic relatedEpic = epics.get(epicId);
        if (relatedEpic != null) {
            relatedEpic.addSubTask(newSubTask);
            subTasks.put(newSubTask.getId(), newSubTask);
        }
    }

    @Override
    public void createEpic(Epic newEpic) {
        epics.put(newEpic.getId(), newEpic);
    }

    @Override
    public void updateTask(Task updateTask) {
        Integer idTask = updateTask.getId();
        if (!tasks.containsKey(idTask)) {
            return;
        }
        tasks.replace(idTask, updateTask);
    }

    @Override
    public void updateSubTask(SubTask myUpdateSubTask) {
        int subTaskId = myUpdateSubTask.getId();
        if (!subTasks.containsKey(subTaskId)) {
            return;
        }
        SubTask subTask = subTasks.get(subTaskId);
        int epicId = myUpdateSubTask.getEpicId();
        Epic relatedEpic = epics.get(epicId);
        if (relatedEpic != null && subTask.getEpicId() == epicId) {
            relatedEpic.addSubTask(myUpdateSubTask);
            subTasks.put(myUpdateSubTask.getId(), myUpdateSubTask);
        }
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        final Epic oldEpic = epics.get(updateEpic.getId());
        if (oldEpic != null) {
            oldEpic.setName(updateEpic.getName());
            oldEpic.setDescription(updateEpic.getDescription());
        }
    }

    public void addTaskToHistoryList(Task task) {
        historyManager.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(historyManager.getHistory());
    }
}