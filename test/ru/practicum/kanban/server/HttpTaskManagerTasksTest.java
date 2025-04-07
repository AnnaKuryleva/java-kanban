package ru.practicum.kanban.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.model.TaskStatus;
import ru.practicum.kanban.service.Managers;
import ru.practicum.kanban.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
    private final TaskManager taskManager;
    private final HttpTaskServer taskServer;
    private final Gson gson;
    private final HttpClient client;

    public HttpTaskManagerTasksTest() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        gson = BaseHttpHandler.getGson();
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    public void setUp() {
        taskManager.deleteAllTasks();
        taskServer.start();
    }

    @AfterEach
    public void StopServer() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "DescriptionForTaskTestTwo", taskManager.idGenerator(), TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 3, 2, 20, 0));
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код ответа");
        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Task", tasks.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description", taskManager.idGenerator(), TaskStatus.NEW,
                30L, LocalDateTime.of(2023, 3, 2, 20, 0));
        Task taskOne = new Task("TaskOne", "Description", taskManager.idGenerator(), TaskStatus.DONE,
                60L, LocalDateTime.of(2024, 3, 2, 20, 0));
        String taskJson = gson.toJson(task);
        String taskJsonOne = gson.toJson(taskOne);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код ответа");

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJsonOne))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "Неверный код ответа для второй задачи");


        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Некорректное количество задач");
        assertEquals("Task", tasks.get(0).getName(), "Некорректное имя первой задачи");
        assertEquals("TaskOne", tasks.get(1).getName(), "Некорректное имя второй задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description", taskManager.idGenerator(), TaskStatus.NEW,
                60L, LocalDateTime.of(2021, 3, 2, 20, 0));
        Task task1 = taskManager.createTask(task);
        int taskId = task1.getId();

        URI getUrl = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Неверный код ответа при получении");

        Task retrievedTask = gson.fromJson(getResponse.body(), Task.class);

        assertEquals(taskId, retrievedTask.getId(), "ID полученной задачи не совпадает с ID из списка");
        assertEquals(task.getName(), retrievedTask.getName(), "Имя задачи не совпадает с исходным");
    }

    @Test
    public void testUpDateTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description", 0, TaskStatus.NEW,
                60L, LocalDateTime.of(2021, 3, 2, 20, 0));
        Task createdTask = taskManager.createTask(task);
        int originalId = createdTask.getId();

        List<Task> allTasks = taskManager.getAllTasks();
        assertEquals(1, allTasks.size(), "В списке должно быть ровно одна задача");
        assertEquals(originalId, allTasks.get(0).getId(), "ID первой задачи не совпадает");

        Task taskUpdate = new Task("TaskUpdate", "Description", originalId, TaskStatus.NEW,
                10L, LocalDateTime.of(2021, 3, 2, 20, 0));
        String taskUpdateJson = gson.toJson(taskUpdate);

        URI updateUrl = URI.create("http://localhost:8080/tasks/" + originalId);
        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(updateUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskUpdateJson))
                .build();
        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, updateResponse.statusCode(), "Неверный код ответа при обновлении");

        List<Task> allTasksNew = taskManager.getAllTasks();
        assertFalse(allTasksNew.isEmpty(), "Список задач пуст после обновления");
        assertEquals(1, allTasksNew.size(), "В списке должно быть ровно одна задача");
        Task updatedTaskFromList = allTasksNew.get(0);

        assertEquals(originalId, updatedTaskFromList.getId(), "ID задачи изменился после обновления");
        assertEquals("TaskUpdate", updatedTaskFromList.getName(), "Имя задачи не обновилось");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description", 0, TaskStatus.NEW,
                60L, LocalDateTime.of(2021, 3, 2, 20, 0));
        Task createdTask = taskManager.createTask(task);
        int taskId = createdTask.getId();

        List<Task> allTasks = taskManager.getAllTasks();
        assertEquals(1, allTasks.size(), "В списке должно быть ровно одна задача");
        assertEquals(taskId, allTasks.get(0).getId(), "ID созданной задачи не совпадает");

        URI deleteUrl = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(deleteUrl)
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, deleteResponse.statusCode(), "Неверный код ответа при удалении");

        List<Task> allTasksAfter = taskManager.getAllTasks();
        assertTrue(allTasksAfter.isEmpty(), "Список задач не пуст после удаления");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("TaskOne", "Description1", taskManager.idGenerator(), TaskStatus.NEW,
                60L, LocalDateTime.of(2021, 3, 2, 20, 0));
        Task task2 = new Task("TaskTwo", "Description2", taskManager.idGenerator(), TaskStatus.NEW,
                60L, LocalDateTime.of(2021, 3, 2, 21, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> allTasks = taskManager.getAllTasks();
        int taskId1 = allTasks.get(0).getId();
        int taskId2 = allTasks.get(1).getId();

        taskManager.getTaskById(taskId1);
        taskManager.getTaskById(taskId2);

        URI getUrl = URI.create("http://localhost:8080/history");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Неверный код ответа при получении истории");

        Task[] historyArray = gson.fromJson(getResponse.body(), Task[].class);
        List<Task> retrievedHistory = Arrays.asList(historyArray);

        assertEquals(2, retrievedHistory.size(), "Размер истории не соответствует ожидаемому");
        assertEquals(taskId1, retrievedHistory.get(0).getId(), "ID первой задачи в истории не совпадает");
        assertEquals(taskId2, retrievedHistory.get(1).getId(), "ID второй задачи в истории не совпадает");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("TaskOne", "Description1", taskManager.idGenerator(), TaskStatus.NEW,
                60L, LocalDateTime.of(2021, 3, 2, 20, 0));
        Task task2 = new Task("TaskTwo", "Description2", taskManager.idGenerator(), TaskStatus.NEW,
                60L, LocalDateTime.of(2021, 3, 2, 21, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> allTasks = taskManager.getAllTasks();
        int taskId1 = allTasks.get(0).getId();
        int taskId2 = allTasks.get(1).getId();

        URI getUrl = URI.create("http://localhost:8080/prioritized");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Неверный код ответа при получении приоритетных задач");

        Task[] prioritizedArray = gson.fromJson(getResponse.body(), Task[].class);
        List<Task> retrievedPrioritized = Arrays.asList(prioritizedArray);

        assertEquals(2, retrievedPrioritized.size(), "Размер списка приоритетных задач не соответствует ожидаемому");
        assertEquals(taskId1, retrievedPrioritized.get(0).getId(), "ID первой задачи в приоритете не совпадает");
        assertEquals(taskId2, retrievedPrioritized.get(1).getId(), "ID второй задачи в приоритете не совпадает");
    }

}