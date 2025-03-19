package ru.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;
    private Task taskOne;
    private Task taskTwo;
    private Task taskThree;
    private Epic epicTestOne;

    @BeforeEach
    void setForEachMethod() {
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager(historyManager);
        taskOne = new Task("TaskTestOne", "Description", 1, TaskStatus.NEW);
        taskTwo = new Task("TaskTestTwo", "Description", 2, TaskStatus.NEW);
        taskThree = new Task("TaskTestThree", "Description", 3, TaskStatus.NEW);
        epicTestOne = new Epic("EpicTestOne", "Description", taskManager.idGenerator());

    }

    @Test
    void tasksAddedToHistoryManagerRetainPreviousVersionOfTaskAndItsData() {
        taskManager.createTask(taskOne);
        historyManager.add(taskOne);
        List<Task> history = historyManager.getHistory();
        assertEquals(taskOne.getName(), history.get(0).getName());
        assertEquals(taskOne.getDescription(), history.get(0).getDescription());
        assertEquals(taskOne.getId(), history.get(0).getId());
        assertEquals(taskOne.getTaskStatus(), history.get(0).getTaskStatus());
    }

    @Test
    void tasksReceivedByIdAreIncludedInHistory() {
        taskManager.createTask(taskOne);
        taskManager.createTask(taskTwo);
        Task receiveTaskOne = taskManager.getTaskById(taskOne.getId());
        Task receiveTaskTwo = taskManager.getTaskById(taskTwo.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(receiveTaskOne, history.get(0));
        assertEquals(receiveTaskTwo, history.get(1));
    }

    @Test
    void subtaskIdDeletedFromHistoryMapWhenThisSubtaskDeleted() {
        taskManager.createEpic(epicTestOne);
        SubTask subTaskTestOne = new SubTask("SubTaskTestOne", "Description", taskManager.idGenerator(),
                TaskStatus.IN_PROGRESS, epicTestOne.getId());
        taskManager.createSubTask(subTaskTestOne);
        taskManager.getEpicById(epicTestOne.getId());
        taskManager.getSubTaskById(subTaskTestOne.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(epicTestOne, subTaskTestOne), history);
        historyManager.remove(subTaskTestOne.getId());
        historyManager.remove(epicTestOne.getId());
        List<Task> historyNew = historyManager.getHistory();
        assertEquals(List.of(), historyNew);
    }

    @Test
    void getHistoryReturnsEmptyListIfNoTasksAdded() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void duplicateTaskMovesToEndAndRemovesPrevious() {
        historyManager.add(taskOne);
        historyManager.add(taskTwo);
        historyManager.add(taskOne);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(taskTwo, history.get(0));
        assertEquals(taskOne, history.get(1));
    }

    @Test
    void IfRemovedFromBeginningOfHistoryLinksUpdatedCorrectly() {
        historyManager.add(taskOne);
        historyManager.add(taskTwo);
        historyManager.add(taskThree);
        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(taskTwo, history.get(0));
        assertEquals(taskThree, history.get(1));
    }

    @Test
    void IfRemovedFromMiddleOfHistoryLinksUpdatedCorrectly() {
        historyManager.add(taskOne);
        historyManager.add(taskTwo);
        historyManager.add(taskThree);
        historyManager.remove(2);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(taskOne, history.get(0));
        assertEquals(taskThree, history.get(1));
    }

    @Test
    void IfRemovedFromEndOfHistoryLinksUpdatedCorrectly() {
        historyManager.add(taskOne);
        historyManager.add(taskTwo);
        historyManager.add(taskThree);
        historyManager.remove(3);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(taskOne, history.get(0));
        assertEquals(taskTwo, history.get(1));
    }
}