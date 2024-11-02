package ru.practicum.kanban.model;

import ru.practicum.kanban.service.TaskStatus;

import java.util.HashMap;
import java.util.Map;

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
            super.setTaskStatus(TaskStatus.NEW);
            return;
        }
        boolean allTasksClosed = true;
        for (SubTask subTask : getSubTasks().values()) {
            if (subTask.getTaskStatus() != TaskStatus.DONE) {
                allTasksClosed = false;
                break;
            }
        }

        if (allTasksClosed) {
            super.setTaskStatus(TaskStatus.DONE);
        } else {
            super.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void addSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus();
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask.getId(), subTask);
        updateEpicStatus();
    }

    public void removeSubTasks() {
        subTasks.clear();
        updateEpicStatus();
    }

    @Override
    public String toString() {
        return " Epic {" +
                "subTasks=" + subTasks +
                ", " + super.toString() +
                '}';
    }
}
