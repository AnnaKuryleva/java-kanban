package ru.practicum.kanban;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.TaskManager;
import ru.practicum.kanban.service.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task taskTestOne = new Task("TaskTestOne", "DescriptionForTaskTestOne", 1, TaskStatus.NEW);
        Task taskTestTwo = new Task("TaskTestTwo", "DescriptionForTaskTestTwo", 2, TaskStatus.IN_PROGRESS);
        taskManager.createTask(taskTestOne);
        taskManager.createTask(taskTestTwo);
        System.out.println();
        System.out.println("Tasks: " + taskManager.getAllTasks());
        System.out.println();
        Epic epicTestOne = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА", 3);
        SubTask subTaskTestOne = new SubTask("SubTaskTestOne", "ОПИСАНИЕ ПЕРВОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", 4, TaskStatus.DONE, 3);
        SubTask subTaskTestFoo = new SubTask("SubTaskTestFoo", "DescriptionSubTaskFooForEpicTestOne", 4, TaskStatus.DONE, 3);
        SubTask subTaskTestTwo = new SubTask("SubTaskTestTwo", "ОПИСАНИЕ ВТОРОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", 5, TaskStatus.IN_PROGRESS, 3);
        Epic epicTestTwo = new Epic("EpicTestTwo", "DescriptionForEpicTestTwo", 6);
        SubTask subTaskTestThree = new SubTask("SubTaskTestThree", "DescriptionSubTaskThreeForEpicTestTwo", 7, TaskStatus.DONE, 6);
        taskManager.createEpic(epicTestOne);
        taskManager.createSubTask(subTaskTestOne);
        taskManager.createSubTask(subTaskTestTwo);
        taskManager.createSubTask(subTaskTestFoo);
        taskManager.createEpic(epicTestTwo);
        taskManager.createSubTask(subTaskTestThree);
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println();
        System.out.println("SubTasks: " + taskManager.getAllSubTasks());
        System.out.println("Получить ru.practicum.kanban.model.Epic по Id: " + taskManager.getEpicById(3));
        epicTestOne.setTaskStatus(TaskStatus.NEW);
        System.out.println(epicTestOne.getTaskStatus());
        taskManager.deleteAllSubTasks();
        taskManager.updateSubTask(subTaskTestOne);
        System.out.println(taskManager.getAllSubtasksByEpicId(3));
        taskManager.deleteSubTaskById(4);
        System.out.println("SubTasks: " + taskManager.getAllSubTasks());
        System.out.println(epicTestOne.getTaskStatus());
        taskManager.deleteEpicById(3);
        System.out.println("SubTasks: " + taskManager.getAllSubTasks());
        System.out.println("Epics: " + taskManager.getAllEpics());

    }
}
