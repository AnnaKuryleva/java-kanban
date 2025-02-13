package ru.practicum.kanban;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.*;
import ru.practicum.kanban.model.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        System.out.println("1. Создание задач");
        System.out.println();
        Task taskTestOne = new Task("TaskTestOne", "DescriptionForTaskTestOne", 1, TaskStatus.NEW);
        Task taskTestTwo = new Task("TaskTestTwo", "DescriptionForTaskTestTwo", 2, TaskStatus.NEW);
        System.out.println();

        System.out.println("////// 1.A.Добавление задач в менеджер");

        taskManager.createTask(taskTestOne);
        taskManager.createTask(taskTestTwo);
        System.out.println();

        System.out.println("Созданы задачи:");
        System.out.println(STR."Tasks: " + taskManager.getAllTasks());

        System.out.println();

        System.out.println("////// 2. Создание эпиков и подзадач");
        Epic epicTestOne = new Epic("EpicTestOne", "ОПИСАНИЕ-ДЛЯ-ПЕРВОГО ЭПИКА", 3);
        SubTask subTaskTestOne = new SubTask("SubTaskTestOne", "ОПИСАНИЕ ПЕРВОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", 4, TaskStatus.DONE, 3);
        SubTask subTaskTestFoo = new SubTask("SubTaskTestFoo - обновление SubTaskTestOne", "ОПИСАНИЕ - ОБНОВЛЕНИЕ ДЛЯ ПЕРВОГО сабТаска", 4, TaskStatus.DONE, 3);
        SubTask subTaskTestTwo = new SubTask("SubTaskTestTwo", "ОПИСАНИЕ ВТОРОГО сабТаска - ДЛЯ ПЕРВОГО ЭПИКА", 5, TaskStatus.DONE, 3);

        Epic epicTestTwo = new Epic("EpicTestTwo", "DescriptionForEpicTestTwo", 6);
        SubTask subTaskTestThree = new SubTask("SubTaskTestThree", "DescriptionSubTaskThreeForEpicTestTwo", 7, TaskStatus.DONE, 6);

        System.out.println();

        System.out.println("////// 2.A. Добавление эпиков и подзадач в менеджер");
        taskManager.createEpic(epicTestOne);
        taskManager.createSubTask(subTaskTestOne);
        taskManager.createSubTask(subTaskTestTwo);
        taskManager.createSubTask(subTaskTestFoo);

        System.out.println();

        taskManager.createEpic(epicTestTwo);
        taskManager.createSubTask(subTaskTestThree);

        System.out.println();

        System.out.println("Созданы эпики и подзадачи:");
        System.out.println(STR."Epics: " + taskManager.getAllEpics());
        System.out.println(STR."SubTasks: " + taskManager.getAllSubTasks());
        System.out.println();

        System.out.println();

        System.out.println("3. Получение по id");
        System.out.println("получаем задачи и эпики для истории:");

        System.out.println("получаем задачи:");
        System.out.println(STR."Получить Task по Id: " + taskManager.getTaskById(1));

        System.out.println("получаем подзадачи:");
        System.out.println(STR."Получить SubTask по Id: " + taskManager.getSubTaskById(4));

        System.out.println("получаем эпики :");
        System.out.println(STR."Получить Epic по Id: " + taskManager.getEpicById(3));
        System.out.println(STR."Получить Epic по Id: " + taskManager.getEpicById(6));
        System.out.println(STR."Получить Epic по Id: " + taskManager.getEpicById(3));
        System.out.println(STR."Получить Epic по Id: " + taskManager.getEpicById(6));

        System.out.println();
        epicTestOne.setTaskStatus(TaskStatus.DONE);
        System.out.println(STR."Обновлен статус эпика: " + epicTestOne.getTaskStatus());

        System.out.println();

        taskManager.updateSubTask(subTaskTestFoo);
        System.out.println(STR."Обновлена подзадача: " + taskManager.getSubTaskById(4));

        System.out.println();

        System.out.println("////// 4. Вывод всех задач, эпиков и подзадач");
        System.out.println("Все задачи:");
        System.out.println(STR."Task: " + taskManager.getAllTasks());

        System.out.println("Все подзадачи:");
        System.out.println(STR."SubTasks: " + taskManager.getAllSubTasks());

        System.out.println("Все эпики:");
        System.out.println(STR."Epics: " + taskManager.getAllEpics());

        System.out.println();

        System.out.println("////// 5. Вывод истории");
        System.out.println("История просмотров задач:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("////// 6. Вывод всех задач через printAllTasks:");
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Task task : manager.getAllSubtasksByEpicId(epic.getId())) {
                System.out.println(STR."--> " + task);
            }
        }

        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}