package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    List<Task> getAllTasks();

    List<SubTask> getAllSubTasks();

    List<Epic> getAllEpics();

    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();

    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    void deleteEpicById(int id);

    List<SubTask> getAllSubtasksByEpicId(int id);

    Task getTaskById(int id);//yes

    SubTask getSubTaskById(int id);//yes

    Epic getEpicById(int id);//yes

    Task createTask(Task newTask);

    void createSubTask(SubTask newSubTask);

    void createEpic(Epic newEpic);

    void updateTask(Task updateTask);

    void updateSubTask(SubTask myUpdateSubTask);

    void updateEpic(Epic updateEpic);

    List<Task> getHistory();

    int idGenerator();

    Set<Task> getPrioritizedTasks();

}
