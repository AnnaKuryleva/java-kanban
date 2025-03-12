package ru.practicum.kanban.service;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    private File file;

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

    @Test
    void utilityClassReturnsInitializedAndReadyToUseInstancesOfFileBackedTaskManager() throws IOException {
        file = Files.createTempFile("test", ".csv").toFile();
        TaskManager fileBackedTaskManager = Managers.getFileBacked(file);
        assertNotNull(fileBackedTaskManager);
    }
}
