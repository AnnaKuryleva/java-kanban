package ru.practicum.kanban.model;

import ru.practicum.kanban.service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {

    private final Map<Integer, SubTask> subTasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
        this.subTasks = new HashMap<>();
        this.startTime = null;
        this.duration = Duration.ZERO;
        this.endTime = null;
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

    private void updateEpicTimeFields() {
        if (!subTasks.isEmpty()) {
            startTime = subTasks.values().stream().filter(subTask -> subTask.getStartTime() != null)
                    .min(Comparator.comparing(SubTask::getStartTime))
                    .map(SubTask::getStartTime).orElse(null);
            endTime = subTasks.values().stream().filter(subTask -> subTask.getEndTime() != null)
                    .max(Comparator.comparing(SubTask::getEndTime))
                    .map(SubTask::getEndTime).orElse(null);

            if (startTime != null && endTime != null) {
                duration = Duration.between(startTime, endTime);
            } else {
                duration = Duration.ZERO;
            }

        } else {
            startTime = null;
            duration = Duration.ZERO;
            endTime = null;
        }
    }

    public void addSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus();
        updateEpicTimeFields();
    }

    public void removeSubTask(int subTaskId) {
        subTasks.remove(subTaskId);
        updateEpicStatus();
        updateEpicTimeFields();
    }

    public void removeSubTasks() {
        subTasks.clear();
        updateEpicStatus();
        updateEpicTimeFields();
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
                ", duration=" + (duration != null ? duration.toMinutes() + " минут" : 0) +
                ", startTime= " + (startTime != null ? startTime : 0) +
                ", endTime= " + (endTime != null ? endTime : 0) +
                ']';
    }

}
