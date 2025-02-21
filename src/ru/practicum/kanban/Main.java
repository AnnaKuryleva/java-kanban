package ru.practicum.kanban;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.*;
import ru.practicum.kanban.model.TaskStatus;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        System.out.println("****** 1. Создание задач");
        System.out.println();
        Task taskTestOne = new Task("TaskTestOne", "DescriptionForTaskTestOne", 1, TaskStatus.NEW);
        Task taskTestTwo = new Task("TaskTestTwo", "DescriptionForTaskTestTwo", 2, TaskStatus.NEW);

        System.out.println("****** 1.1 Добавление задач в менеджер");
        System.out.println();

        taskManager.createTask(taskTestOne);
        taskManager.createTask(taskTestTwo);

        System.out.println("Созданы задачи:");

        System.out.println("Tasks: " + taskManager.getAllTasks());
        System.out.println();

        System.out.println("****** 2. Создание эпиков и подзадач");
        System.out.println();

        Epic epicTestOne = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА", 3);
        SubTask subTaskTestOne = new SubTask("SubTaskTestOne", "ОПИСАНИЕ ПЕРВОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", 4, TaskStatus.DONE, 3);
        SubTask subTaskTestFoo = new SubTask("SubTaskTestFoo - обновление SubTaskTestOne", "ОПИСАНИЕ - ОБНОВЛЕНИЕ ДЛЯ ПЕРВОГО сабТаска", 4, TaskStatus.DONE, 3);
        SubTask subTaskTestTwo = new SubTask("SubTaskTestTwo", "ОПИСАНИЕ ВТОРОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", 5, TaskStatus.DONE, 3);

        Epic epicTestTwo = new Epic("EpicTestTwo", "DescriptionForEpicTestTwo", 6);
        SubTask subTaskTestThree = new SubTask("SubTaskTestThree", "DescriptionSubTaskThreeForEpicTestTwo", 7, TaskStatus.DONE, 6);

        System.out.println("****** 2.1 Добавление эпиков и подзадач в менеджер");
        System.out.println();
        taskManager.createEpic(epicTestOne);
        taskManager.createSubTask(subTaskTestOne);
        taskManager.createSubTask(subTaskTestTwo);
        taskManager.createSubTask(subTaskTestFoo);

        taskManager.createEpic(epicTestTwo);
        taskManager.createSubTask(subTaskTestThree);

        System.out.println("ТЕСТИРОВАНИЕ CSV");

        File file = new File("src/ru/practicum/kanban/service/tasks.csv");
        System.out.println();

        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, file);
        System.out.println();

        System.out.println("****** 1. Создание задач");
        System.out.println();

        manager.createTask(taskTestOne);
        manager.createTask(taskTestTwo);

        manager.createEpic(epicTestOne);

        manager.createSubTask(subTaskTestOne);
        manager.createSubTask(subTaskTestTwo);
        manager.createSubTask(subTaskTestFoo);

        manager.loadFromFile(file);
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

        System.out.println("Созданы эпики и подзадачи:");
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println("SubTasks: " + taskManager.getAllSubTasks());
        System.out.println();

        System.out.println("****** 3. Получение по id");
        System.out.println("Получаем задачи и эпики для истории:");
        System.out.println();

        System.out.println("Получить Task по Id: " + taskManager.getTaskById(1));


        System.out.println("Получить SubTask по Id: " + taskManager.getSubTaskById(4));

        System.out.println("Получить Epic по Id: " + taskManager.getEpicById(3));
        System.out.println("Получить Epic по Id: " + taskManager.getEpicById(6));


        epicTestOne.setTaskStatus(TaskStatus.DONE);
        System.out.println("Обновлен статус эпика: " + epicTestOne.getTaskStatus());


        taskManager.updateSubTask(subTaskTestFoo);
        System.out.println("Обновлена подзадача: " + taskManager.getSubTaskById(4));
        System.out.println();

        System.out.println("4. Вывод всех задач, эпиков и подзадач");
        System.out.println();

        System.out.println("Все задачи:");
        System.out.println("Task:" + taskManager.getAllTasks());

        System.out.println("Все подзадачи:");
        System.out.println("SubTasks: " + taskManager.getAllSubTasks());

        System.out.println("Все эпики:");
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println();

        System.out.println("****** 5. Вывод истории");
        System.out.println();

        System.out.println("История просмотров задач:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();

        System.out.println("****** 6. Вывод всех задач через printAllTasks");
        System.out.println();
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task + "\n");
        }

        System.out.println("Эпики:");
        for (Task epic : taskManager.getAllEpics()) {
            System.out.println(epic);
            for (Task task : taskManager.getAllSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }

        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}