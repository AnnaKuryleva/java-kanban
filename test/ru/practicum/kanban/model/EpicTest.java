package ru.practicum.kanban.model;

import org.junit.jupiter.api.Test;
import ru.practicum.kanban.service.Managers;
import ru.practicum.kanban.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    void epicObjectCannotBeAddedToItselfAsSubtask() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА", 3);
        taskManager.createEpic(epic);
        SubTask epic1 = new SubTask("SubTaskTestOne", "ОПИСАНИЕ ПЕРВОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", epic.getId(), TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createSubTask(epic1);
        assertTrue(taskManager.getAllSubtasksByEpicId(epic.getId()).isEmpty());
    }

    @Test
    void statusOfAnEpicIsNewIfAllItsSubtasksAreNew() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА", taskManager.idGenerator());
        taskManager.createEpic(epic);
        SubTask subTaskOne = new SubTask("SubTaskTestOne", "ОПИСАНИЕ ПЕРВОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", taskManager.idGenerator(), TaskStatus.NEW, epic.getId());
        SubTask subTaskTwo = new SubTask("SubTaskTestTwo", "ОПИСАНИЕ ВТОРОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", taskManager.idGenerator(), TaskStatus.NEW, epic.getId());
        taskManager.createSubTask(subTaskOne);
        taskManager.createSubTask(subTaskTwo);
        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    void statusOfEpicInProgressIfAtLeastOneOfItsSubtasksHasStatusInProgress() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА", 3);
        SubTask subTaskOne = new SubTask("SubTaskTestOne", "ОПИСАНИЕ ПЕРВОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", taskManager.idGenerator(), TaskStatus.DONE, epic.getId());
        SubTask subTaskTwo = new SubTask("SubTaskTestTwo", "ОПИСАНИЕ ВТОРОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", taskManager.idGenerator(), TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTaskOne);
        taskManager.createSubTask(subTaskTwo);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    void epicHasDoneStatusIfAllItsSubtasksHaveDoneStatus() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА", taskManager.idGenerator());
        SubTask subTaskOne = new SubTask("SubTaskTestOne", "ОПИСАНИЕ ПЕРВОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", taskManager.idGenerator(), TaskStatus.DONE, epic.getId());
        SubTask subTaskTwo = new SubTask("SubTaskTestTwo", "ОПИСАНИЕ ВТОРОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", taskManager.idGenerator(), TaskStatus.DONE, epic.getId());
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTaskOne);
        taskManager.createSubTask(subTaskTwo);
        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }
}
