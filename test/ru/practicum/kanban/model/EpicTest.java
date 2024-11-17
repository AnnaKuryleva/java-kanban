package ru.practicum.kanban.model;

import org.junit.jupiter.api.Test;
import ru.practicum.kanban.service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    void epicObjectCannotBeAddedToItselfAsSubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА", 3);
        taskManager.createEpic(epic);
        SubTask epic1 = new SubTask("SubTaskTestOne", "ОПИСАНИЕ ПЕРВОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", epic.getId(), TaskStatus.IN_PROGRESS,epic.getId() );
        taskManager.createSubTask(epic1);
        assertTrue(taskManager.getAllSubtasksByEpicId(epic.getId()).isEmpty());
    }
}
