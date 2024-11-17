package ru.practicum.kanban.service;

import org.junit.jupiter.api.Test;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {

    @Test
    void tasksAddedToHistoryManagerRetainPreviousVersionOfTaskAndItsData() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("TaskTestOne", "DescriptionForTaskTestOne",
                1, TaskStatus.NEW);
        taskManager.createTask(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(task1.getName(), history.get(0).getName());
        assertEquals(task1.getDescription(), history.get(0).getDescription());
        assertEquals(task1.getId(), history.get(0).getId());
        assertEquals(task1.getTaskStatus(), history.get(0).getTaskStatus());

    }
}
