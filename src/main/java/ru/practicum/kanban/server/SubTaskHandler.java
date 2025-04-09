package ru.practicum.kanban.server;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange, String path) throws IOException {
        String[] stringPath = path.split("/");
        if (stringPath.length == 2 && stringPath[1].equals("subtasks")) {
            try {
                List<SubTask> subTasks = taskManager.getAllSubTasks();
                String responseSubtask = gson.toJson(subTasks);
                sendText(exchange, responseSubtask, 200);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else if (stringPath.length == 3 && stringPath[1].equals("subtasks")) {
            try {
                int subTaskId = Integer.parseInt(stringPath[2]);
                SubTask subTask = taskManager.getSubTaskById(subTaskId);
                if (subTask == null) {
                    sendText(exchange, "Subtask not found", 404);
                } else {
                    String responseSubtask = gson.toJson(subTask);
                    sendText(exchange, responseSubtask, 200);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Invalid subtask id format", 400);
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
        if (stringPath.length == 2 && stringPath[1].equals("subtasks")) {
            try {
                String createSubtasks = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                SubTask subTask = gson.fromJson(createSubtasks, SubTask.class);
                taskManager.createSubTask(subTask);
                sendText(exchange, "Subtask has been created", 201);
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("пересекается")) {
                    sendText(exchange, "Subtask time overlap with existing tasks", 406);
                } else {
                    sendText(exchange, "Bad request: " + e.getMessage(), 400);
                }
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else if (stringPath.length == 3 && stringPath[1].equals("subtasks")) {
            try {
                int subTaskId = Integer.parseInt(stringPath[2]);
                String updateSubTask = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                SubTask subTask = gson.fromJson(updateSubTask, SubTask.class);
                subTask.setId(subTaskId);
                if (taskManager.getSubTaskById(subTaskId) == null) {
                    sendText(exchange, "Subtask not found", 404);
                } else {
                    taskManager.updateSubTask(subTask);
                    sendText(exchange, "Subtask has been updated", 201);
                }
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("пересекается")) {
                    sendText(exchange, "Subtask time overlap with existing tasks", 406);
                } else {
                    sendText(exchange, "Bad request: " + e.getMessage(), 400);
                }
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
        if (stringPath.length == 2 && stringPath[1].equals("subtasks")) {
            try {
                taskManager.deleteAllSubTasks();
                sendText(exchange, "All subtasks have been deleted", 201);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else if (stringPath.length == 3 && stringPath[1].equals("subtasks")) {
            try {
                int subTaskId = Integer.parseInt(stringPath[2]);
                if (taskManager.getSubTaskById(subTaskId) == null) {
                    sendText(exchange, "Subtask not found", 404);
                } else {
                    taskManager.deleteSubTaskById(subTaskId);
                    sendText(exchange, "Subtask has been deleted", 201);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Invalid subtask id format: " + e.getMessage(), 400);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else {
            sendText(exchange, "Endpoint not found: " + path, 404);
        }
    }
}