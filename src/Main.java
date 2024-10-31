

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task taskTestOne = new Task("TaskTestOne", "DescriptionForTaskTestOne", 1, Task.TaskStatus.NEW);
        Task taskTestTwo = new Task("TaskTestTwo", "DescriptionForTaskTestTwo", 2, Task.TaskStatus.IN_PROGRESS);
        taskManager.createTask(taskTestOne);
        taskManager.createTask(taskTestTwo);
        System.out.println();
        System.out.println("Tasks: " + taskManager.getAllTasks());
        System.out.println();
        Epic epicTestOne = new Epic("EpicTestOne", "DescriptionForEpicTestOne", 3, Task.TaskStatus.NEW);
        SubTask subTaskTestOne = new SubTask("SubTaskTestOne", "DescriptionSubTaskOneForEpicTestOne", 4, Task.TaskStatus.IN_PROGRESS, 3);
        SubTask subTaskTestTwo = new SubTask("SubTaskTestTwo", "DescriptionSubTaskTwoForEpicTestOne", 5, Task.TaskStatus.DONE, 3);
        Epic epicTestTwo = new Epic("EpicTestTwo", "DescriptionForEpicTestTwo", 6, Task.TaskStatus.DONE);
        SubTask subTaskTestThree = new SubTask("SubTaskTestThree", "DescriptionSubTaskThreeForEpicTestTwo", 7, Task.TaskStatus.DONE, 6);
        taskManager.createEpic(epicTestOne);
        taskManager.createSubTask(subTaskTestOne);
        taskManager.createSubTask(subTaskTestTwo);
        taskManager.createEpic(epicTestTwo);
        taskManager.createSubTask(subTaskTestThree);
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println();
        System.out.println("SubTasks: " + taskManager.getAllSubTasks());
        System.out.println("Получить Epic по Id: " + taskManager.getEpicById(3));
    }
}
