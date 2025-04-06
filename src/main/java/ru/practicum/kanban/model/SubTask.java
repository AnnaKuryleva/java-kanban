package ru.practicum.kanban.model;

import ru.practicum.kanban.service.TaskType;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String description, int id, TaskStatus taskStatus, int epicId) {
        super(name, description, id, taskStatus);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int id, TaskStatus taskStatus, int epicId, Long duration,
                   LocalDateTime startTime) {
        super(name, description, id, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int id, TaskStatus taskStatus, int epicId, Long duration) {
        super(name, description, id, taskStatus, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }


    @Override
    public String toString() {
        return "SubTask [epicId=" + epicId +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id= " + getId() +
                ", taskStatus= " + getTaskStatus() +
                ", duration= " + (duration != null ? duration.toMinutes() + " минут" : 0) +
                ", startTime= " + (startTime != null ? startTime : 0) +
                ']';
    }
}
