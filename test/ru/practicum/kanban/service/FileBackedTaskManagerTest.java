package ru.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.exceptions.ManagerSaveException;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.model.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private File file;
    FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void setForEachMethod() throws IOException {
        HistoryManager historyManager = Managers.getDefaultHistory();
        file = Files.createTempFile("test", ".csv").toFile();
        fileBackedTaskManager = new FileBackedTaskManager(historyManager, file);
    }

    @Test
    void writeTasksToFileThatIsNotWritable() {
        file.setWritable(false);
        ManagerSaveException exception = assertThrows(ManagerSaveException.class, () -> fileBackedTaskManager.save());
        assertEquals("Error working with the file", exception.getMessage());
    }

    @Test
    void saveAndUploadMultipleTasksToFile() {
        Task taskOne = fileBackedTaskManager.createTask(new Task("TaskTestOne", "DescriptionForTaskTestOne", 1, TaskStatus.NEW));
        Task taskTwo = fileBackedTaskManager.createTask(new Task("TaskTestTwo", "DescriptionForTaskTestTwo", 2, TaskStatus.NEW));
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> loadedTasks = loadedManager.getAllTasks();
        assertEquals(2, loadedTasks.size());
        assertEquals(taskOne, loadedTasks.get(0));
        assertEquals(taskTwo, loadedTasks.get(1));
    }

}
