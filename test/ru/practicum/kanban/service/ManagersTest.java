package ru.practicum.kanban.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    @Test
    void utilityClassReturnsInitializedAndReadyToUseInstancesOfTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
    }

    @Test
    void utilityClassReturnsInitializedAndReadyToUseInstancesOfInMemoryHistoryManager() {
        InMemoryHistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        assertNotNull(inMemoryHistoryManager);
    }
}
