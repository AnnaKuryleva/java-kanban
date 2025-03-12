package ru.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.model.TaskStatus;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
    @Override
    void setUp() {
        taskManager = Managers.getDefault();
        taskTestOne = new Task("TaskTestOne", "Description", taskManager.idGenerator(), TaskStatus.NEW);
        taskTestTwo = new Task("TaskTestTwo", "DescriptionForTaskTestTwo", taskManager.idGenerator(),
                TaskStatus.NEW);
        epicTestOne = new Epic("EpicTestOne", "Description", taskManager.idGenerator());
        subTaskTestOne = new SubTask("SubTaskTestOne", "Description", taskManager.idGenerator(),
                TaskStatus.IN_PROGRESS, epicTestOne.getId());
        subTaskTestTwo = new SubTask("SubTaskTestTwo", "Description", taskManager.idGenerator(),
                TaskStatus.NEW, epicTestOne.getId());
        epicTestTwo = new Epic("EpicTestTwo", "Description", taskManager.idGenerator());
    }
}
