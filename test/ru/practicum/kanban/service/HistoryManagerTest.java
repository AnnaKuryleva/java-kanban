package ru.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setForEachMethod() {
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    void tasksAddedToHistoryManagerRetainPreviousVersionOfTaskAndItsData() {
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

    @Test
    void tasksReceivedByIdAreIncludedInHistory() {
        Task taskOne = new Task("TaskTestOne", "DescriptionForTaskTestOne",
                1, TaskStatus.NEW);
        Task taskTwo = new Task("TaskTestTwo", "DescriptionForTaskTestTwo",
                2, TaskStatus.IN_PROGRESS);
        taskManager.createTask(taskOne);
        taskManager.createTask(taskTwo);
        Task receiveTaskOne = taskManager.getTaskById(1);
        Task receiveTaskTwo = taskManager.getTaskById(2);
        List<Task> history = historyManager.getHistory();
        assertEquals(receiveTaskOne, history.get(0));
        assertEquals(receiveTaskTwo, history.get(1));
    }

    @Test
    void subtaskIdDeletedFromHistoryMapWhenThisSubtaskDeleted() {
        Epic epicTestOne = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА",
                taskManager.idGenerator());
        taskManager.createEpic(epicTestOne);
        SubTask subTaskTestOne = new SubTask("SubTaskTestOne",
                "ОПИСАНИЕ ПЕРВОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", taskManager.idGenerator(),
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


}
