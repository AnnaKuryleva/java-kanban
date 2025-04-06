package ru.practicum.kanban.model;

import ru.practicum.kanban.service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus taskStatus;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, int id, TaskStatus taskStatus) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.taskStatus = taskStatus;
    }

    public Task(String name, String description, int id, TaskStatus taskStatus, Long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.taskStatus = taskStatus;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
    }

    public Task(String name, String description, int id, TaskStatus taskStatus, Long duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.taskStatus = taskStatus;
        this.duration = Duration.ofMinutes(duration);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Task [" +
                "name= '" + name + '\'' +
                ", description= '" + description + '\'' +
                ", id= " + id +
                ", taskStatus= " + taskStatus +
                ", duration= " + (duration != null ? duration.toMinutes() + " минут" : 0) +
                ", startTime= " + (startTime != null ? startTime : 0) +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
