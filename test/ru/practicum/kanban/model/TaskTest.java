package ru.practicum.kanban.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.service.Managers;
import ru.practicum.kanban.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private TaskManager taskManager;

    @BeforeEach
    void setForEachMethod() {
        taskManager = Managers.getDefault();
    }

    @Test
    void instancesOfTaskClassEqualIfIdIsEqual() {
        Task task1 = new Task("TaskTestOne", "DescriptionForTaskTestOne", 1, TaskStatus.NEW);
        taskManager.createTask(task1);
        task1.setId(1);
        Task task2 = new Task("TaskTestTwo", "DescriptionForTaskTestTwo", 1, TaskStatus.DONE);
        taskManager.createTask(task2);
        task2.setId(1);
        assertEquals(task1, task2);
    }

    @Test
    void heirsOfTaskClassEqualToEachOtherIfIdIsEqual() {
        Epic epic1 = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА", 2);
        taskManager.createEpic(epic1);
        epic1.setId(2);
        Epic epic2 = new Epic("EpicTestTwo", "ОПИСАНИЕ-ДЛЯ-ВТОРОГО ЭПИКА", 2);
        taskManager.createEpic(epic2);
        epic1.setId(2);
        assertEquals(epic1, epic2);
    }

    @Test
    void TaskDataLostIfItsIdHasBeenChangedAfterCreation() {
        Task task = new Task("TaskTestOne", "DescriptionForTaskTestOne", 1, TaskStatus.NEW);
        taskManager.createTask(task);
        Task task1 = taskManager.getTaskById(task.getId());
        assertEquals(task, task1);
        task1.setId(10);
        Task task2 = taskManager.getTaskById(task1.getId());
        assertNull(task2);

    }
}

