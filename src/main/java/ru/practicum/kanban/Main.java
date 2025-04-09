package ru.practicum.kanban;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.*;
import ru.practicum.kanban.model.TaskStatus;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        System.out.println("****** 1. Creating Tasks");
        System.out.println();
        Task taskTestOne = new Task("TaskTestOne", "DescriptionForTaskTestOne", 1, TaskStatus.NEW,
                120L);
        Task taskTestTwo = new Task("TaskTestTwo", "DescriptionForTaskTestTwo", 2, TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 3, 2, 20, 0));
        Task taskTestFoo = new Task("TaskTestFoo", "DescriptionForTaskTestTwo", 3, TaskStatus.NEW,
                30L, LocalDateTime.of(2023, 3, 2, 20, 0));


        System.out.println("****** 1.1 Adding Tasks to the manager");
        System.out.println();

        taskManager.createTask(taskTestOne);
        taskManager.createTask(taskTestTwo);
        taskManager.createTask(taskTestFoo);


        System.out.println("Tasks created: ");

        System.out.println("Tasks: " + taskManager.getAllTasks());
        System.out.println();

        System.out.println("****** 2. Create Epics and SabTasks");
        System.out.println();

        Epic epicTestOne = new Epic("EpicTestOne", "DescriptionForEpicTestOne", 4);
        Epic epicTestTwo = new Epic("EpicTestTwo", "DescriptionForEpicTestTwo", 5);
        SubTask subTaskTestOne = new SubTask("SubTaskTestOne", "DescriptionOfTheFirstSubtaskForFirstEpic",
                6, TaskStatus.IN_PROGRESS, 4, 30L,
                LocalDateTime.of(2023, 3, 2, 21, 0));
        SubTask subTaskTestTwo = new SubTask("SubTaskTestTwo", "DescriptionOfTheSecondSubtaskForFirstEpic",
                7, TaskStatus.DONE, 4, 30L,
                LocalDateTime.of(2023, 3, 2, 23, 0));
        SubTask subTaskTestThree = new SubTask("SubTaskTestThree", "DescriptionOfTheThreeSubTaskForSecondEpic",
                8, TaskStatus.DONE, 5, 20L,
                LocalDateTime.of(2023, 3, 3, 15, 0));

        Epic epicTestThree = new Epic("EpicTestTree", "DescriptionForEpicTestTree", 9);

        System.out.println("****** 2.1 Adding Epics and Subtasks to the manager");
        System.out.println();
        taskManager.createEpic(epicTestOne);
        taskManager.createEpic(epicTestTwo);
        taskManager.createSubTask(subTaskTestOne);
        taskManager.createSubTask(subTaskTestTwo);

        taskManager.updateSubTask(new SubTask("SubTaskTestFoo - update SubTaskTestOne", "Description - " +
                "updateForTheFirstSubtask", 6, TaskStatus.IN_PROGRESS, 4, 60L,
                LocalDateTime.of(2023, 3, 2, 21, 0)));

        taskManager.createSubTask(subTaskTestThree);
        taskManager.createEpic(epicTestThree);
        System.out.println();

        System.out.println("Epics and Subtasks created: ");
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println("SubTasks: " + taskManager.getAllSubTasks());
        System.out.println();

        System.out.println("****** 3. Get by id");
        System.out.println("Get Tasks and Epics for the story: ");
        System.out.println();

        System.out.println("Get Task по Id: " + taskManager.getTaskById(1));


        System.out.println("Get SubTask по Id: " + taskManager.getSubTaskById(6));

        System.out.println("Get Epic по Id: " + taskManager.getEpicById(4));
        System.out.println("Get Epic по Id: " + taskManager.getEpicById(9));


        System.out.println("Subtask updated: " + taskManager.getSubTaskById(6));
        System.out.println();

        System.out.println("4. Display all Tasks, Epics and Subtasks");
        System.out.println();

        System.out.println("All Tasks: ");
        System.out.println("Task:" + taskManager.getAllTasks());

        System.out.println("All Subtasks: ");
        System.out.println("SubTasks: " + taskManager.getAllSubTasks());

        System.out.println("All Epics: ");
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println();

        System.out.println("****** 5. History output");
        System.out.println();

        System.out.println("Task view history: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();

        System.out.println("****** 6. Output all tasks via printAllTasks");
        System.out.println();
        printAllTasks(taskManager);
        System.out.println(taskManager.getPrioritizedTasks());

        System.out.println("ТЕСТИРОВАНИЕ CSV");

        File file = new File("src/main/resources/tasks.csv");
        TaskManager manager = Managers.getFileBacked(file);
        System.out.println();

        System.out.println("****** 1. Создание задач");
        System.out.println();

        manager.createTask(taskTestOne);
        manager.createTask(taskTestTwo);
        manager.createTask(taskTestFoo);

        manager.createEpic(epicTestOne);
        manager.createEpic(epicTestTwo);
        manager.createSubTask(subTaskTestOne);
        manager.createSubTask(subTaskTestTwo);

        manager.createSubTask(subTaskTestThree);
        manager.createEpic(epicTestThree);


        FileBackedTaskManager.loadFromFile(file);
        System.out.println();

        List<Task> tasks = manager.getAllTasks();
        List<Epic> epics = manager.getAllEpics();
        List<SubTask> subTasks = manager.getAllSubTasks();

        System.out.println("Прочитать Task из файла:");
        tasks.stream().forEach(System.out::println);

        System.out.println("Прочитать Epic из файла:");
        epics.stream().forEach(System.out::println);

        System.out.println("Прочитать Subtask из файла:");
        subTasks.stream().forEach(System.out::println);

        System.out.println("Задачи в памяти после загрузки: " + manager.getAllTasks());
        System.out.println("Эпики в памяти после загрузки: " + manager.getAllEpics());
        System.out.println("Подзадачи в памяти после загрузки: " + manager.getAllSubTasks());


        System.out.println("ТЕСТИРОВАНИЕ CSV");

    }

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Tasks:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task + "\n");
        }

        System.out.println("Epics:");
        for (Task epic : taskManager.getAllEpics()) {
            System.out.println(epic);
            for (Task task : taskManager.getAllSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }

        System.out.println("Subtasks:");
        for (Task subtask : taskManager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}