package ru.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setForEachMethod() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addsTasksOfTaskTypeAndCanFindThemById() {
        Task taskTestOne = new Task("TaskTestOne", "DescriptionForTaskTestOne",
                taskManager.idGenerator(), TaskStatus.NEW);
        taskManager.createTask(taskTestOne);
        assertEquals(taskTestOne, taskManager.getTaskById(taskTestOne.getId()));
    }

    @Test
    void addsTasksOfEpicTypeAndCanFindThemById() {
        Epic epicTestOne = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА",
                taskManager.idGenerator());
        taskManager.createEpic(epicTestOne);
        assertEquals(epicTestOne, taskManager.getEpicById(epicTestOne.getId()));
    }

    @Test
    void addsTasksOfSabTaskTypeAndCanFindThemById() {
        Epic epicTestOne = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА",
                taskManager.idGenerator());
        taskManager.createEpic(epicTestOne);
        SubTask subTaskTestOne = new SubTask("SubTaskTestOne",
                "ОПИСАНИЕ ПЕРВОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", taskManager.idGenerator(),
                TaskStatus.IN_PROGRESS, epicTestOne.getId());
        taskManager.createSubTask(subTaskTestOne);
        assertEquals(subTaskTestOne, taskManager.getSubTaskById(subTaskTestOne.getId()));
    }

    @Test
    void tasksWithGivenIdAndGeneratedIdDoNotConflictWithinManager() {
        Task taskTestOne = new Task("TaskTestOne", "DescriptionForTaskTestOne",
                1, TaskStatus.NEW);
        taskManager.createTask(taskTestOne);
        Task taskTestTwo = new Task("TaskTestTwo", "DescriptionForTaskTestTwo",
                taskManager.idGenerator(), TaskStatus.NEW);
        taskManager.createTask(taskTestTwo);
        assertEquals(taskTestOne, taskManager.getTaskById(taskTestOne.getId()));
        assertEquals(taskTestTwo, taskManager.getTaskById(taskTestTwo.getId()));
        assertNotEquals(taskTestOne.getId(), taskTestTwo.getId());
    }

    @Test
    void fieldsOfTaskDoNotChangeWhenAddedToManager() {
        Task taskTestOne = new Task("TaskTestOne", "DescriptionForTaskTestOne",
                1, TaskStatus.NEW);
        taskManager.createTask(taskTestOne);
        Task taskEquals = taskManager.getTaskById(taskTestOne.getId());
        assertEquals(taskTestOne.getName(), taskEquals.getName());
        assertEquals(taskTestOne.getDescription(), taskEquals.getDescription());
        assertEquals(taskTestOne.getId(), taskEquals.getId());
        assertEquals(taskTestOne.getTaskStatus(), taskEquals.getTaskStatus());
    }
}
