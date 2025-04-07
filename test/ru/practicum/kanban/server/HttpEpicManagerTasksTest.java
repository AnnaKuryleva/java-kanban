package ru.practicum.kanban.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.service.Managers;
import ru.practicum.kanban.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpEpicManagerTasksTest {
    private final TaskManager taskManager;
    private final HttpTaskServer taskServer;
    private final Gson gson;
    private final HttpClient client;

    public HttpEpicManagerTasksTest() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        gson = BaseHttpHandler.getGson();
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    public void setUp() {
        taskManager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void StopServer() {
        taskServer.stop();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description", taskManager.idGenerator());
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код ответа при создании");
        assertEquals("Epic has been created", response.body(), "Неверный ответ сервера");

        List<Epic> allEpics = taskManager.getAllEpics();
        assertEquals(1, allEpics.size(), "В списке должен быть ровно один эпик");
        assertEquals("Epic", allEpics.get(0).getName(), "Имя эпика не совпадает");
    }

    @Test
    public void testUpdateEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description", taskManager.idGenerator());
        taskManager.createEpic(epic);

        List<Epic> allEpics = taskManager.getAllEpics();
        assertEquals(1, allEpics.size(), "В списке один эпик");
        int originalId = allEpics.get(0).getId();

        Epic epicUpdate = new Epic("EpicUpdate", "Description", originalId);
        String epicUpdateJson = gson.toJson(epicUpdate);

        URI updateUrl = URI.create("http://localhost:8080/epics/" + originalId);
        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(updateUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicUpdateJson))
                .build();
        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, updateResponse.statusCode(), "Неверный код ответа при обновлении");

        List<Epic> allEpicsNew = taskManager.getAllEpics();
        assertEquals(1, allEpicsNew.size(), "В списке должен быть ровно один эпик");
        Epic updatedEpicFromList = allEpicsNew.get(0);

        assertEquals(originalId, updatedEpicFromList.getId(), "ID эпика изменился после обновления");
        assertEquals("EpicUpdate", updatedEpicFromList.getName(), "Имя эпика не обновилось");
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description", taskManager.idGenerator());
        taskManager.createEpic(epic);

        List<Epic> allEpics = taskManager.getAllEpics();
        assertEquals(1, allEpics.size(), "В списке один эпик");
        int originalId = allEpics.get(0).getId();
        assertEquals(1, allEpics.size(), "В списке должен быть ровно один эпик");
        assertEquals(originalId, allEpics.get(0).getId(), "ID созданного эпика не совпадает");

        URI deleteUrl = URI.create("http://localhost:8080/epics/" + originalId);
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(deleteUrl)
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, deleteResponse.statusCode(), "Неверный код ответа при удалении");

        List<Epic> allEpicsAfter = taskManager.getAllEpics();
        assertTrue(allEpicsAfter.isEmpty(), "Список эпиков не пуст после удаления");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description", taskManager.idGenerator());
        taskManager.createEpic(epic);

        List<Epic> allEpics = taskManager.getAllEpics();
        assertFalse(allEpics.isEmpty(), "Список эпиков пуст после создания");
        int epicId = allEpics.get(0).getId();

        URI getUrl = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Неверный код ответа при получении");

        Epic retrievedEpic = gson.fromJson(getResponse.body(), Epic.class);

        assertEquals(epicId, retrievedEpic.getId(), "ID полученного эпика не совпадает с ID из списка");
        assertEquals(epic.getName(), retrievedEpic.getName(), "Имя эпика не совпадает с исходным");
    }
}
