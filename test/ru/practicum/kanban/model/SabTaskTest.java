package ru.practicum.kanban.model;

import org.junit.jupiter.api.Test;

import ru.practicum.kanban.service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SabTaskTest {

    @Test
    void impossibleToMakeSubTaskObjectAnEpicForYourself() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА", 3);
        taskManager.createEpic(epic);
        SubTask epic1 = new SubTask("SubTaskTestOne", "ОПИСАНИЕ ПЕРВОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА",
                3, TaskStatus.IN_PROGRESS,3);
        taskManager.createSubTask(epic1);
        assertTrue(taskManager.getAllSubtasksByEpicId(epic.getId()).isEmpty());
    }
}
