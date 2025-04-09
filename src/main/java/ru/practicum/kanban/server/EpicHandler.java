package ru.practicum.kanban.server;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange, String path) throws IOException {
        String[] stringPath = path.split("/");
        if (stringPath.length == 2 && stringPath[1].equals("epics")) {
            try {
                List<Epic> epics = taskManager.getAllEpics();
                String responseEpic = gson.toJson(epics);
                sendText(exchange, responseEpic, 200);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else if (stringPath.length == 3 && stringPath[1].equals("epics")) {
            try {
                int epicId = Integer.parseInt(stringPath[2]);
                Epic epic = taskManager.getEpicById(epicId);
                if (epic == null) {
                    sendText(exchange, "Epic not found", 404);
                } else {
                    String responseEpic = gson.toJson(epic);
                    sendText(exchange, responseEpic, 200);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Invalid epic id format", 400);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else {
            sendText(exchange, "Endpoint not found: " + path, 404);
        }
    }

    @Override
    protected void processPost(HttpExchange exchange, String path) throws IOException {
        String[] stringPath = path.split("/");
        if (stringPath.length == 2 && stringPath[1].equals("epics")) {
            try {
                String createEpic = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(createEpic, Epic.class);
                taskManager.createEpic(epic);
                sendText(exchange, "Epic has been created", 201);
            } catch (IllegalArgumentException e) {
                sendText(exchange, "Bad request: " + e.getMessage(), 400);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else if (stringPath.length == 3 && stringPath[1].equals("epics")) {
            try {
                int epicId = Integer.parseInt(stringPath[2]);
                String updateEpic = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(updateEpic, Epic.class);
                epic.setId(epicId);
                if (taskManager.getEpicById(epicId) == null) {
                    sendText(exchange, "Epic not found", 404);
                } else {
                    taskManager.updateEpic(epic);
                    sendText(exchange, "Epic has been updated", 201);
                }
            } catch (IllegalArgumentException e) {
                sendText(exchange, "Bad request: " + e.getMessage(), 400);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else {
            sendText(exchange, "Endpoint not found: " + path, 404);
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange, String path) throws IOException {
        String[] stringPath = path.split("/");
        if (stringPath.length == 2 && stringPath[1].equals("epics")) {
            try {
                taskManager.deleteAllEpics();
                sendText(exchange, "All epics have been deleted", 201);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else if (stringPath.length == 3 && stringPath[1].equals("epics")) {
            try {
                int epicId = Integer.parseInt(stringPath[2]);
                if (taskManager.getEpicById(epicId) == null) {
                    sendText(exchange, "Epic not found", 404);
                } else {
                    taskManager.deleteEpicById(epicId);
                    sendText(exchange, "Epic has been deleted", 201);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Invalid epic id format: " + e.getMessage(), 400);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else {
            sendText(exchange, "Endpoint not found: " + path, 404);
        }
    }
}
