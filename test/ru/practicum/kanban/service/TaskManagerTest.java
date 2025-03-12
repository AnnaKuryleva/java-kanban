package ru.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.model.TaskStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task taskTestOne;
    protected Task taskTestTwo;
    protected Epic epicTestOne;
    protected SubTask subTaskTestOne;
    protected SubTask subTaskTestTwo;
    protected Epic epicTestTwo;

    @BeforeEach
    abstract void setUp() throws IOException;

    @Test
    void addsTasksOfTaskTypeAndCanFindThemById() {
        taskManager.createTask(taskTestOne);
        assertEquals(taskTestOne, taskManager.getTaskById(taskTestOne.getId()));
    }

    @Test
    void addsTasksOfEpicTypeAndCanFindThemById() {
        taskManager.createEpic(epicTestOne);
        assertEquals(epicTestOne, taskManager.getEpicById(epicTestOne.getId()));
    }

    @Test
    void addsTasksOfSabTaskTypeAndCanFindThemById() {
        taskManager.createEpic(epicTestOne);
        SubTask subTaskOne = new SubTask("SubTaskOne",
                "Description", taskManager.idGenerator(),
                TaskStatus.IN_PROGRESS, epicTestOne.getId());
        taskManager.createSubTask(subTaskOne);
        assertEquals(subTaskOne, taskManager.getSubTaskById(subTaskOne.getId()));
    }

    @Test
    void tasksWithGivenIdAndGeneratedIdDoNotConflictWithinManager() {
        taskManager.createTask(taskTestOne);
        taskManager.createTask(taskTestTwo);
        assertEquals(taskTestOne, taskManager.getTaskById(taskTestOne.getId()));
        assertEquals(taskTestTwo, taskManager.getTaskById(taskTestTwo.getId()));
        assertNotEquals(taskTestOne.getId(), taskTestTwo.getId());
    }

    @Test
    void fieldsOfTaskDoNotChangeWhenAddedToManager() {
        taskManager.createTask(taskTestOne);
        Task taskEquals = taskManager.getTaskById(taskTestOne.getId());
        assertEquals(taskTestOne.getName(), taskEquals.getName());
        assertEquals(taskTestOne.getDescription(), taskEquals.getDescription());
        assertEquals(taskTestOne.getId(), taskEquals.getId());
        assertEquals(taskTestOne.getTaskStatus(), taskEquals.getTaskStatus());
    }

    @Test
    void epicObjectCannotBeAddedToItselfAsSubtask() {
        taskManager.createEpic(epicTestOne);
        taskManager.createSubTask(subTaskTestOne);
        assertTrue(taskManager.getAllSubtasksByEpicId(epicTestOne.getId()).isEmpty());
    }

    @Test
    void statusOfAnEpicIsNewIfAllItsSubtasksAreNew() {
        taskManager.createEpic(epicTestOne);
        taskManager.createSubTask(subTaskTestOne);
        taskManager.createSubTask(subTaskTestTwo);
        assertEquals(TaskStatus.NEW, epicTestOne.getTaskStatus());
    }

    @Test
    void statusOfEpicInProgressIfAtLeastOneOfItsSubtasksHasStatusInProgress() {
        taskManager.createEpic(epicTestOne);
        SubTask subTaskOne = new SubTask("SubTaskOne", "Description",
                taskManager.idGenerator(), TaskStatus.DONE, epicTestOne.getId());
        SubTask subTaskTwo = new SubTask("SubTaskTwo", "Description",
                taskManager.idGenerator(), TaskStatus.IN_PROGRESS, epicTestOne.getId());
        taskManager.createSubTask(subTaskOne);
        taskManager.createSubTask(subTaskTwo);
        assertEquals(TaskStatus.IN_PROGRESS, epicTestOne.getTaskStatus());
    }

    @Test
    void epicHasDoneStatusIfAllItsSubtasksHaveDoneStatus() {
        taskManager.createEpic(epicTestOne);
        SubTask subTaskOne = new SubTask("SubTaskTOne", "Description",
                taskManager.idGenerator(), TaskStatus.DONE, epicTestOne.getId());
        SubTask subTaskTwo = new SubTask("SubTaskTwo", "Description",
                taskManager.idGenerator(), TaskStatus.DONE, epicTestOne.getId());
        taskManager.createSubTask(subTaskOne);
        taskManager.createSubTask(subTaskTwo);
        assertEquals(TaskStatus.DONE, epicTestOne.getTaskStatus());
    }

    @Test
    void impossibleToMakeSubTaskObjectAnEpicForYourself() {
        taskManager.createEpic(epicTestOne);
        taskManager.createSubTask(subTaskTestOne);
        assertTrue(taskManager.getAllSubtasksByEpicId(epicTestOne.getId()).isEmpty());
    }

    @Test
    void instancesOfTaskClassEqualIfIdIsEqual() {
        taskManager.createTask(taskTestOne);
        taskTestOne.setId(1);
        taskManager.createTask(taskTestTwo);
        taskTestTwo.setId(1);
        assertEquals(taskTestOne, taskTestTwo);
    }

    @Test
    void heirsOfTaskClassEqualToEachOtherIfIdIsEqual() {
        taskManager.createTask(epicTestOne);
        epicTestOne.setId(2);
        taskManager.createTask(epicTestTwo);
        epicTestTwo.setId(2);
        assertEquals(epicTestOne, epicTestTwo);
    }

    @Test
    void TaskDataLostIfItsIdHasBeenChangedAfterCreation() {
        taskManager.createTask(taskTestOne);
        taskTestTwo = taskManager.getTaskById(taskTestOne.getId());
        assertEquals(taskTestOne, taskTestTwo);
        taskTestTwo.setId(10);
        assertNull(taskManager.getTaskById(taskTestTwo.getId()));
    }

    @Test
    void endTimeOfTaskEqualToItsStartTimePlusItsDuration() {
        Task task = new Task("TaskTestOne", "Description", taskManager.idGenerator(), TaskStatus.NEW,
                30L, 2025, 3, 3, 10, 0);
        LocalDateTime expectedEndTime
                = LocalDateTime.of(2025, 3, 3, 10,
                30);
        LocalDateTime endTime = task.getEndTime();
        assertEquals(expectedEndTime, endTime);
    }

    @Test
    void getListOfAllCreatedTasksAddedViaCreateTask() {
        taskManager.createTask(taskTestOne);
        taskManager.createTask(taskTestTwo);
        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(taskTestOne));
        assertTrue(tasks.contains(taskTestTwo));
    }

    @Test
    void getListOfAllCreatedSubTasksAddedViaCreateTask() {
        taskManager.createEpic(epicTestOne);
        SubTask subTaskOne = new SubTask("SubTaskOne", "Description",
                taskManager.idGenerator(), TaskStatus.DONE, epicTestOne.getId());
        SubTask subTaskTwo = new SubTask("SubTaskTwo", "Description",
                taskManager.idGenerator(), TaskStatus.DONE, epicTestOne.getId());
        taskManager.createSubTask(subTaskOne);
        taskManager.createSubTask(subTaskTwo);
        List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertEquals(2, subTasks.size());
        assertTrue(subTasks.contains(subTaskOne));
        assertTrue(subTasks.contains(subTaskTwo));
    }

    @Test
    void getListOfAllCreatedEpicsAddedViaCreateTask() {
        taskManager.createEpic(epicTestOne);
        taskManager.createEpic(epicTestTwo);
        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(2, epics.size());
        assertTrue(epics.contains(epicTestOne));
        assertTrue(epics.contains(epicTestTwo));
    }

    @Test
    void deleteAllTasksDeletesAllTasks() {
        taskManager.createTask(taskTestOne);
        taskManager.createTask(taskTestTwo);
        List<Task> tasksBefore = taskManager.getAllTasks();
        assertTrue(tasksBefore.contains(taskTestOne));
        assertTrue(tasksBefore.contains(taskTestTwo));
        taskManager.deleteAllTasks();
        List<Task> tasksAfter = taskManager.getAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(tasksAfter.isEmpty());
    }

    @Test
    void deleteAllSubTasksDeletesAllSubTasks() {
        taskManager.createEpic(epicTestOne);
        SubTask subTaskOne = new SubTask("SubTaskOne", "Description",
                taskManager.idGenerator(), TaskStatus.DONE, epicTestOne.getId());
        SubTask subTaskTwo = new SubTask("SubTaskTwo", "Description",
                taskManager.idGenerator(), TaskStatus.DONE, epicTestOne.getId());
        taskManager.createSubTask(subTaskOne);
        taskManager.createSubTask(subTaskTwo);
        taskManager.deleteAllSubTasks();
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @Test
    void deleteAllEpicsDeletesAllEpics() {
        taskManager.createEpic(epicTestOne);
        taskManager.createEpic(epicTestTwo);
        SubTask subTaskOne = new SubTask("SubTaskOne", "Description",
                taskManager.idGenerator(), TaskStatus.DONE, epicTestOne.getId());
        taskManager.createSubTask(subTaskOne);
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @Test
    void deleteTaskByIdDeletesTask() {
        taskManager.createTask(taskTestOne);
        taskManager.deleteTaskById(taskTestOne.getId());
        assertNull(taskManager.getTaskById(taskTestOne.getId()));
    }

    @Test
    void deleteSubTasByIdDeletesSubTask() {
        taskManager.createEpic(epicTestOne);
        SubTask subTaskOne = new SubTask("SubTaskOne", "Description",
                taskManager.idGenerator(), TaskStatus.DONE, epicTestOne.getId());
        taskManager.createSubTask(subTaskOne);
        List<SubTask> subTasksBefore = taskManager.getAllSubTasks();
        taskManager.deleteSubTaskById(subTaskOne.getId());
        List<SubTask> subTasksAfter = taskManager.getAllSubTasks();
        assertTrue(subTasksBefore.contains(subTaskOne));
        assertFalse(subTasksAfter.contains(subTaskOne));
    }

    @Test
    void deletingEpicByIdDeletesEpicAndItsSubtasks() {
        taskManager.createEpic(epicTestOne);
        SubTask subTaskOne = new SubTask("SubTaskOne", "Description",
                taskManager.idGenerator(), TaskStatus.DONE, epicTestOne.getId());
        taskManager.createSubTask(subTaskOne);
        List<Epic> epicsBefore = taskManager.getAllEpics();
        List<SubTask> subTasksBefore = taskManager.getAllSubtasksByEpicId(epicTestOne.getId());
        assertTrue(epicsBefore.contains(epicTestOne));
        assertTrue(subTasksBefore.contains(subTaskOne));
        taskManager.deleteEpicById(epicTestOne.getId());
        List<Epic> epicsAfter = taskManager.getAllEpics();
        assertFalse(epicsAfter.contains(epicTestOne));
        assertTrue(taskManager.getAllSubtasksByEpicId(epicTestOne.getId()).isEmpty());
    }

    @Test
    void whenUpdatingTaskItsFieldsAreUpdated() {
        taskManager.createTask(taskTestOne);
        taskTestOne.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskTestOne.setName("taskTestOne");
        taskTestOne.setDescription("Description");
        Task updatedTask = new Task("updateTask", "updateDescription", taskTestOne.getId(),
                TaskStatus.DONE);
        taskManager.updateTask(updatedTask);
        Task retrievedTask = taskManager.getTaskById(taskTestOne.getId());
        assertEquals("updateTask", retrievedTask.getName());
        assertEquals("updateDescription", retrievedTask.getDescription());
        assertEquals(TaskStatus.DONE, retrievedTask.getTaskStatus());
    }

    @Test
    void whenUpdatingSubTaskItsFieldsAreUpdated() {
        taskManager.createEpic(epicTestOne);
        SubTask subTask = new SubTask("SubTask", "Description", taskManager.idGenerator(),
                TaskStatus.NEW, epicTestOne.getId());
        taskManager.createSubTask(subTask);
        SubTask updateSubTask = new SubTask("UpdateSubTask", "UpdateDescription", subTask.getId(),
                TaskStatus.IN_PROGRESS, epicTestOne.getId());
        taskManager.updateSubTask(updateSubTask);
        SubTask retrievedSubTask = taskManager.getSubTaskById(subTask.getId());
        assertEquals("UpdateSubTask", retrievedSubTask.getName());
        assertEquals("UpdateDescription", retrievedSubTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, retrievedSubTask.getTaskStatus());
    }

    @Test
    void whenUpdatingEpicItsFieldsAreUpdated() {
        taskManager.createEpic(epicTestOne);
        Epic updatedEpic = new Epic("UpdateEpic", "UpdateDescription", epicTestOne.getId());
        taskManager.updateEpic(updatedEpic);
        Epic retrievedEpic = taskManager.getEpicById(epicTestOne.getId());
        assertEquals("UpdateEpic", retrievedEpic.getName());
        assertEquals("UpdateDescription", retrievedEpic.getDescription());
    }

    @Test
    void ifNewTaskOverlapsInTimeWithThoseAlreadyInPriorityListItIsNotAddedToIt() {
        Task task = new Task("Task", "Description", taskManager.idGenerator(), TaskStatus.NEW,
                60L, 2023, 3, 3, 10, 0);
        taskManager.createTask(task);
        Set<Task> tasks = taskManager.getPrioritizedTasks();
        Task taskOne = new Task("TaskOne", "Description", taskManager.idGenerator(), TaskStatus.NEW,
                60L, 2023, 3, 3, 10, 30);
        taskManager.createTask(taskOne);
        Set<Task> tasksAfter = taskManager.getPrioritizedTasks();
        List<Task> tasksList = new ArrayList<>(tasksAfter);
        assertTrue(tasksList.contains(task), "Приоритетный список содержит task");
        assertEquals(1, tasksList.size(), "В списке должна быть только одна задача");
        assertFalse(tasksList.contains(taskOne), "Приоритетный список не должен содержать taskOne");
    }

    @Test
    void inPriorityListTasksArrangedFromEarlierToLaterImplementationDates() {
        Task task = new Task("Task", "Description", taskManager.idGenerator(), TaskStatus.NEW,
                60L, 2023, 3, 3, 10, 0);
        taskManager.createTask(task);
        Task taskOne = new Task("TaskOne", "Description", taskManager.idGenerator(), TaskStatus.NEW,
                60L, 2023, 3, 4, 10, 0);
        taskManager.createTask(taskOne);
        Set<Task> tasks = taskManager.getPrioritizedTasks();
        List<Task> tasksList = new ArrayList<>(tasks);
        assertEquals(task, tasksList.get(0));
        assertEquals(taskOne, tasksList.get(1));
    }

    @Test
    void getAllTasksReturnsEmptyListIfNoTasksHaveBeenAdded() {
        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void getAllSubTasksReturnsEmptyListIfNoSubTasksHaveBeenAdded() {
        List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertTrue(subTasks.isEmpty());
    }

    @Test
    void getAllEpicsReturnsEmptyListIfNoEpicsHaveBeenAdded() {
        List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.isEmpty());
    }

}
