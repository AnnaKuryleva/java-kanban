package ru.practicum.kanban.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.exceptions.ManagerSaveException;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.model.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {
    private File file;
    private FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    @Override
    void setUp() throws IOException {
        file = Files.createTempFile("test", ".csv").toFile();
        fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
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
    @AfterEach
    void clean() {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    @Test
    void writeTasksToFileThatIsNotWritable() {
        file.setWritable(false);
        ManagerSaveException exception = assertThrows(ManagerSaveException.class, () -> fileBackedTaskManager.save());
        assertEquals("Error working with the file", exception.getMessage());
    }

    @Test
    void saveAndUploadMultipleTasksToFile() {
        Task taskOne = new Task("TaskTestOne", "Description", 1, TaskStatus.NEW, 30L);
        Task taskTwo = new Task("TaskTestTwo", "Description", 2, TaskStatus.NEW, 60L);
        fileBackedTaskManager.createTask(taskOne);
        fileBackedTaskManager.createTask(taskTwo);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> loadedTasks = loadedManager.getAllTasks();
        assertEquals(2, loadedTasks.size());
        assertEquals(taskOne, loadedTasks.get(0));
        assertEquals(taskTwo, loadedTasks.get(1));
    }
}
