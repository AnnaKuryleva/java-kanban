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
        HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        assertNotNull(inMemoryHistoryManager);
    }
}
