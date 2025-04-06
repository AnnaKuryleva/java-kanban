package ru.practicum.kanban.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.TaskStatus;
import ru.practicum.kanban.service.Managers;
import ru.practicum.kanban.service.TaskManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HttpSubTaskManagerTasksTest {
    private final TaskManager taskManager;
    private final HttpTaskServer taskServer;
    private final Gson gson;
    private final HttpClient client;

    public HttpSubTaskManagerTasksTest() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    public void setUp() {
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubTasks();
        taskServer.start();
    }

    @AfterEach
    public void stopServer() {
        taskServer.stop();
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description", taskManager.idGenerator());
        taskManager.createEpic(epic);
        int epicId = taskManager.getAllEpics().get(0).getId();

        SubTask subTask = new SubTask("SubTask", "DescriptionOfTheFirstSubtaskForFirstEpic",
                taskManager.idGenerator(), TaskStatus.IN_PROGRESS, epicId, 30L, LocalDateTime.of(2021,
                3, 2, 21, 0));
        String subTaskJson = gson.toJson(subTask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код ответа при создании");
        assertEquals("Subtask has been created", response.body(), "Неверный ответ сервера");

        List<SubTask> allSubTasks = taskManager.getAllSubTasks();
        assertEquals(1, allSubTasks.size(), "В списке должна быть ровно одна подзадача");
        assertEquals("SubTask", allSubTasks.get(0).getName(), "Имя подзадачи не совпадает");
        assertEquals(epicId, allSubTasks.get(0).getEpicId(), "EpicId подзадачи не совпадает");
    }

    @Test
    public void testUpdateSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description", taskManager.idGenerator());
        taskManager.createEpic(epic);
        int epicId = taskManager.getAllEpics().get(0).getId();

        SubTask subTask = new SubTask("SubTask", "Description",
                taskManager.idGenerator(), TaskStatus.IN_PROGRESS, epicId, 30L, LocalDateTime.of(2023,
                3, 2, 21, 0));
        taskManager.createSubTask(subTask);

        List<SubTask> allSubTasks = taskManager.getAllSubTasks();
        assertEquals(1, allSubTasks.size(), "В списке должна быть одна подзадача");
        int originalId = allSubTasks.get(0).getId();

        SubTask subTaskUpdate = new SubTask("SubTaskUpdate", "Description",
                taskManager.idGenerator(), TaskStatus.IN_PROGRESS,epicId,
                120L, LocalDateTime.of(2023, 3, 2, 21, 0));
        subTaskUpdate.setId(originalId);
        String subTaskUpdateJson = gson.toJson(subTaskUpdate);

        URI updateUrl = URI.create("http://localhost:8080/subtasks/" + originalId);
        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(updateUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskUpdateJson))
                .build();
        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, updateResponse.statusCode(), "Неверный код ответа при обновлении");
        assertEquals("Subtask has been updated", updateResponse.body(), "Неверный ответ сервера");

        List<SubTask> allSubTasksNew = taskManager.getAllSubTasks();
        assertEquals(1, allSubTasksNew.size(), "В списке должна быть ровно одна подзадача");
        SubTask updatedSubTaskFromList = allSubTasksNew.get(0);

        assertEquals(originalId, updatedSubTaskFromList.getId(), "ID подзадачи изменился после обновления");
        assertEquals("SubTaskUpdate", updatedSubTaskFromList.getName(), "Имя подзадачи не обновилось");
    }

    @Test
    public void testDeleteSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description", taskManager.idGenerator());
        taskManager.createEpic(epic);
        int epicId = taskManager.getAllEpics().get(0).getId();

        SubTask subTask = new SubTask("SubTask", "Description",
                1, TaskStatus.IN_PROGRESS, epicId, 30L, LocalDateTime.of(2023, 3, 2,
                21, 0));
        taskManager.createSubTask(subTask);

        List<SubTask> allSubTasks = taskManager.getAllSubTasks();
        assertEquals(1, allSubTasks.size(), "В списке должна быть одна подзадача");
        int originalId = allSubTasks.get(0).getId();

        URI deleteUrl = URI.create("http://localhost:8080/subtasks/" + originalId);
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(deleteUrl)
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, deleteResponse.statusCode(), "Неверный код ответа при удалении");
        assertEquals("Subtask has been deleted", deleteResponse.body(), "Неверный ответ сервера");

        List<SubTask> allSubTasksAfter = taskManager.getAllSubTasks();
        assertTrue(allSubTasksAfter.isEmpty(), "Список подзадач не пуст после удаления");
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description", taskManager.idGenerator());
        taskManager.createEpic(epic);
        List<Epic> allEpics = taskManager.getAllEpics();
        assertFalse(allEpics.isEmpty(), "Список эпиков пуст после создания");
        int epicId = allEpics.get(0).getId();

        SubTask subTask = new SubTask("SubTask", "Description",
                1, TaskStatus.IN_PROGRESS, epicId, 30L, LocalDateTime.of(2023, 3,
                2, 21, 0));
        taskManager.createSubTask(subTask);

        List<SubTask> allSubTasks = taskManager.getAllSubTasks();
        assertFalse(allSubTasks.isEmpty(), "Список подзадач пуст после создания");
        int subTaskId = allSubTasks.get(0).getId();

        URI getUrl = URI.create("http://localhost:8080/subtasks/" + subTaskId);
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Неверный код ответа при получении");

        SubTask retrievedSubTask = gson.fromJson(getResponse.body(), SubTask.class);

        assertEquals(subTaskId, retrievedSubTask.getId(), "ID полученной подзадачи не совпадает с ID из списка");
        assertEquals(subTask.getName(), retrievedSubTask.getName(), "Имя подзадачи не совпадает с исходным");
        assertEquals(epicId, retrievedSubTask.getEpicId(), "EpicId подзадачи не совпадает с исходным");
    }
}