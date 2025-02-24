package ru.practicum.kanban.model;

import ru.practicum.kanban.service.TaskType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Epic extends Task {

    private final Map<Integer, SubTask> subTasks;

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
        this.subTasks = new HashMap<>();
    }

    public Map<Integer, SubTask> getSubTasks() {
        return new HashMap<>(subTasks);
    }

    @Override
    public final void setTaskStatus(TaskStatus taskStatus) {
    }

    private void updateEpicStatus() {
        if (getSubTasks().isEmpty()) {
            setTaskStatus(TaskStatus.NEW);
            return;
        }
        boolean allTasksNew = true;
        boolean allTasksDone = true;
        for (SubTask subTask : getSubTasks().values()) {
            if (subTask.getTaskStatus() != TaskStatus.DONE) {
                allTasksDone = false;
            }
            if (subTask.getTaskStatus() != TaskStatus.NEW) {
                allTasksNew = false;
            }
        }
        if (allTasksDone) {
            super.setTaskStatus(TaskStatus.DONE);
        } else if (allTasksNew) {
            super.setTaskStatus(TaskStatus.NEW);
        } else {
            super.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void addSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus();
    }

    public void removeSubTask(int subTaskId) {
        subTasks.remove(subTaskId);
        updateEpicStatus();
    }

    public void removeSubTasks() {
        subTasks.clear();
        updateEpicStatus();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        Set<Integer> subTaskIds = subTasks.keySet();
        return "Epic [" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", taskStatus=" + getTaskStatus() +
                ", subTaskIds=" + subTaskIds +
                ']';
    }
}
