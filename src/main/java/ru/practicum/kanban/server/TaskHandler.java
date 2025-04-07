package ru.practicum.kanban.server;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange, String path) throws IOException {
        String[] stringPath = path.split("/");
        if (stringPath.length == 2 && stringPath[1].equals("tasks")) {
            try {
                List<Task> tasks = taskManager.getAllTasks();
                String responseTask = gson.toJson(tasks);
                sendText(exchange, responseTask, 200);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else if (stringPath.length == 3 && stringPath[1].equals("tasks")) {
            try {
                int taskId = Integer.parseInt(stringPath[2]);
                Task task = taskManager.getTaskById(taskId);
                if (task == null) {
                    sendText(exchange, "Task not found", 404);
                } else {
                    String responseTask = gson.toJson(task);
                    sendText(exchange, responseTask, 200);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Invalid task id format", 400);
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
        if (stringPath.length == 2 && stringPath[1].equals("tasks")) {
            try {
                String createTask = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(createTask, Task.class);
                Task createdTask = taskManager.createTask(task);
                String responseJson = gson.toJson(createdTask);
                sendText(exchange, responseJson, 201);
                sendText(exchange, "Task has been created", 201);
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("пересекается")) {
                    sendText(exchange, "Task time overlap with existing tasks", 406);
                } else {
                    sendText(exchange, "Bad request: " + e.getMessage(), 400);
                }
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else if (stringPath.length == 3 && stringPath[1].equals("tasks")) {
            try {
                int taskId = Integer.parseInt(stringPath[2]);
                String updateTask = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(updateTask, Task.class);
                task.setId(taskId);
                if (taskManager.getTaskById(taskId) == null) {
                    sendText(exchange, "Task not found", 404);
                } else {
                    taskManager.updateTask(task);
                    sendText(exchange, "Task has been updated", 201);
                }
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("пересекается")) {
                    sendText(exchange, "Task time overlap with existing tasks", 406);
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
        if (stringPath.length == 2 && stringPath[1].equals("tasks")) {
            try {
                taskManager.deleteAllTasks();
                sendText(exchange, "All tasks have been deleted", 201);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else if (stringPath.length == 3 && stringPath[1].equals("tasks")) {
            try {
                int taskId = Integer.parseInt(stringPath[2]);
                if (taskManager.getTaskById(taskId) == null) {
                    sendText(exchange, "Task not found", 404);
                } else {
                    taskManager.deleteTaskById(taskId);
                    sendText(exchange, "Task has been deleted", 201);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Invalid task id format: " + e.getMessage(), 400);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else {
            sendText(exchange, "Endpoint not found: " + path, 404);
        }
    }
}
