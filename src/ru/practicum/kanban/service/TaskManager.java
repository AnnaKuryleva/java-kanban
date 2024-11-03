package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;

import java.util.*;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    private int idCounter = 1;

    public int idGenerator() {
       return idCounter++;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic relatedEpic : epics.values()) {
            relatedEpic.removeSubTasks();
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubTasks();
    }

    public void deleteTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            tasks.remove(id);
        }
    }

    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            subTasks.remove(id);
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubTask(subTask);
            }
        }
    }

    public void deleteEpicById(int id) {
        List<SubTask> listSubTasksByDelete = new ArrayList<>();
        Epic epic = epics.get(id);
        if (epic != null) {
            int epicId = epic.getId();
            subTasks.values().removeIf(subTask -> subTask.getEpicId() == epicId);
            epic.removeSubTasks();
            epics.remove(id);
        }
    }

    public List<SubTask> getAllSubtasksByEpicId(int id) {
        List<SubTask> listSubTasks = new ArrayList<>();
        final Epic epic = epics.get(id);
        if (epic != null) {
            int epicId = epic.getId();
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getEpicId() == epicId) {
                    listSubTasks.add(subTask);
                }
            }
        }
        return listSubTasks;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Task createTask(Task newTask) {
        newTask.setId(idGenerator());
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    public void createSubTask(SubTask newSubTask) {
        int epicId = newSubTask.getEpicId();
        Epic relatedEpic = epics.get(epicId);
        if (relatedEpic != null) {
            newSubTask.setId(idGenerator());
            relatedEpic.addSubTask(newSubTask);
            subTasks.put(newSubTask.getId(), newSubTask);
        }
    }

    public void createEpic(Epic newEpic) {
        newEpic.setId(idGenerator());
        epics.put(newEpic.getId(), newEpic);
    }

    public void updateTask(Task updateTask) {
        Integer idTask = updateTask.getId();
        if (!tasks.containsKey(idTask)) {
            return;
        }
        tasks.replace(idTask, updateTask);
    }

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
        } else {
            return;
        }
        subTasks.put(myUpdateSubTask.getId(), myUpdateSubTask);
    }

    public void updateEpic(Epic updateEpic) {
        final Epic oldEpic = epics.get(updateEpic.getId());
        if (oldEpic != null) {
            oldEpic.setName(updateEpic.getName());
            oldEpic.setDescription(updateEpic.getDescription());
        }
    }
}
