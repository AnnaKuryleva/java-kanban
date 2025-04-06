package ru.practicum.kanban.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.Managers;
import ru.practicum.kanban.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private final HttpServer server;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        this.gson = Managers.getGson();
        this.server.createContext("/", this::handleTasks);
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public void start() {
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }

    public static Gson getGson() {
        return Managers.getGson();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.server.start();
    }

    private void handleTasks(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        String[] stringPath = path.split("/");
        switch (requestMethod) {
            case "GET":
                if (stringPath.length == 2 && stringPath[1].equals("tasks")) {
                    try {
                        List<Task> tasks = taskManager.getAllTasks();
                        String responseTask = gson.toJson(tasks);
                        sendText(httpExchange, responseTask, 200);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 3 && stringPath[1].equals("tasks")) {
                    try {
                        Task task = taskManager.getTaskById(Integer.parseInt(stringPath[2]));
                        if (task == null) {
                            sendText(httpExchange, "Task not found", 404);
                        } else {
                            String responseTask = gson.toJson(task);
                            sendText(httpExchange, responseTask, 200);
                        }
                    } catch (NumberFormatException e) {
                        sendText(httpExchange, "Invalid task id format", 400);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 2 && stringPath[1].equals("epics")) {
                    try {
                        List<Epic> epics = taskManager.getAllEpics();
                        String responseEpic = gson.toJson(epics);
                        sendText(httpExchange, responseEpic, 200);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 3 && stringPath[1].equals("epics")) {
                    try {
                        Epic epic = taskManager.getEpicById(Integer.parseInt(stringPath[2]));
                        if (epic == null) {
                            sendText(httpExchange, "Epic not found", 404);
                        } else {
                            String responseEpic = gson.toJson(epic);
                            sendText(httpExchange, responseEpic, 200);
                        }
                    } catch (NumberFormatException e) {
                        sendText(httpExchange, "Invalid epic id format", 400);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 2 && stringPath[1].equals("subtasks")) {
                    try {
                        List<SubTask> subTasks = taskManager.getAllSubTasks();
                        String responseSubtask = gson.toJson(subTasks);
                        sendText(httpExchange, responseSubtask, 200);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 3 && stringPath[1].equals("subtasks")) {
                    try {
                        SubTask subTask = taskManager.getSubTaskById(Integer.parseInt(stringPath[2]));
                        if (subTask == null) {
                            sendText(httpExchange, "Subtask not found", 404);
                        } else {
                            String responseSubtask = gson.toJson(subTask);
                            sendText(httpExchange, responseSubtask, 200);
                        }
                    } catch (NumberFormatException e) {
                        sendText(httpExchange, "Invalid subtask id format", 400);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 2 && stringPath[1].equals("history")) {
                    try {
                        List<Task> history = taskManager.getHistory();
                        String responseHistory = gson.toJson(history);
                        sendText(httpExchange, responseHistory, 200);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 2 && stringPath[1].equals("prioritized")) {
                    try {
                        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                        String responsePrioritized = gson.toJson(prioritizedTasks);
                        sendText(httpExchange, responsePrioritized, 200);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }
                } else {
                    sendText(httpExchange, "Endpoint not found: " + path, 404);
                }
                break;
            case "POST":
                if (stringPath.length == 2 && stringPath[1].equals("tasks")) {
                    try {
                        String createTask = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task task = gson.fromJson(createTask, Task.class);
                        Task createdTask = taskManager.createTask(task);
                        String responseJson = gson.toJson(createdTask);
                        sendText(httpExchange, responseJson, 201);
                    } catch (IllegalArgumentException e) {
                        if (e.getMessage().contains("пересекается")) {
                            sendText(httpExchange, "Task time overlap with existing tasks", 406);
                        } else {
                            sendText(httpExchange, "Bad request: " + e.getMessage(), 400);
                        }
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 3 && stringPath[1].equals("tasks")) {
                    try {
                        int taskId = Integer.parseInt(stringPath[2]);
                        String updateTask = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task task = gson.fromJson(updateTask, Task.class);
                        task.setId(taskId);
                        if (taskManager.getTaskById(taskId) == null) {
                            sendText(httpExchange, "Task not found", 404);
                        } else {
                            taskManager.updateTask(task);
                            sendText(httpExchange, "Task has been updated", 201);
                        }
                    } catch (IllegalArgumentException e) {
                        if (e.getMessage().contains("пересекается")) {
                            sendText(httpExchange, "Task time overlap with existing tasks", 406);
                        } else {
                            sendText(httpExchange, "Bad request: " + e.getMessage(), 400);
                        }
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 2 && stringPath[1].equals("epics")) {
                    try {
                        String createEpic = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic = gson.fromJson(createEpic, Epic.class);
                        taskManager.createEpic(epic);
                        sendText(httpExchange, "Epic has been created", 201);
                    } catch (IllegalArgumentException e) {
                        sendText(httpExchange, "Bad request: " + e.getMessage(), 400);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 3 && stringPath[1].equals("epics")) {
                    try {
                        int epicId = Integer.parseInt(stringPath[2]);
                        String updateEpic = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic = gson.fromJson(updateEpic, Epic.class);
                        epic.setId(epicId);
                        if (taskManager.getEpicById(epicId) == null) {
                            sendText(httpExchange, "Epic not found", 404);
                        } else {
                            taskManager.updateEpic(epic);
                            sendText(httpExchange, "Epic has been updated", 201);
                        }
                    } catch (IllegalArgumentException e) {
                        sendText(httpExchange, "Bad request: " + e.getMessage(), 400);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 2 && stringPath[1].equals("subtasks")) {
                    try {
                        String createSubtasks = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        SubTask subTask = gson.fromJson(createSubtasks, SubTask.class);
                        taskManager.createSubTask(subTask);
                        sendText(httpExchange, "Subtask has been created", 201);
                    } catch (IllegalArgumentException e) {
                        if (e.getMessage().contains("пересекается")) {
                            sendText(httpExchange, "Subtask time overlap with existing tasks", 406);
                        } else {
                            sendText(httpExchange, "Bad request: " + e.getMessage(), 400);
                        }
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 3 && stringPath[1].equals("subtasks")) {
                    try {
                        int subTaskId = Integer.parseInt(stringPath[2]);
                        String updateSubTask = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        SubTask subTask = gson.fromJson(updateSubTask, SubTask.class);
                        subTask.setId(subTaskId);
                        if (taskManager.getSubTaskById(subTaskId) == null) {
                            sendText(httpExchange, "Subtask not found", 404);
                        } else {
                            taskManager.updateSubTask(subTask);
                            sendText(httpExchange, "Subtask has been updated", 201);
                        }
                    } catch (IllegalArgumentException e) {
                        if (e.getMessage().contains("пересекается")) {
                            sendText(httpExchange, "Subtask time overlap with existing tasks", 406);
                        } else {
                            sendText(httpExchange, "Bad request: " + e.getMessage(), 400);
                        }
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else {
                    sendText(httpExchange, "Endpoint not found: " + path, 404);
                }
                break;
            case "DELETE":
                if (stringPath.length == 2 && stringPath[1].equals("tasks")) {
                    try {
                        taskManager.deleteAllTasks();
                        sendText(httpExchange, "All tasks have been deleted", 201);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 3 && stringPath[1].equals("tasks")) {
                    try {
                        int taskId = Integer.parseInt(stringPath[2]);
                        if (taskManager.getTaskById(taskId) == null) {
                            sendText(httpExchange, "Task not found", 404);
                        } else {
                            taskManager.deleteTaskById(taskId);
                            sendText(httpExchange, "Task has been deleted", 201);
                        }
                    } catch (NumberFormatException e) {
                        sendText(httpExchange, "Invalid task id format: " + e.getMessage(), 400);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 2 && stringPath[1].equals("epics")) {
                    try {
                        taskManager.deleteAllEpics();
                        sendText(httpExchange, "All epics have been deleted", 201);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 3 && stringPath[1].equals("epics")) {
                    try {
                        int epicId = Integer.parseInt(stringPath[2]);
                        if (taskManager.getEpicById(epicId) == null) {
                            sendText(httpExchange, "Epic not found", 404);
                        } else {
                            taskManager.deleteEpicById(epicId);
                            sendText(httpExchange, "Epic has been deleted", 201);
                        }
                    } catch (NumberFormatException e) {
                        sendText(httpExchange, "Invalid epic id format: " + e.getMessage(), 400);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 2 && stringPath[1].equals("subtasks")) {
                    try {
                        taskManager.deleteAllSubTasks();
                        sendText(httpExchange, "All subtasks have been deleted", 201);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else if (stringPath.length == 3 && stringPath[1].equals("subtasks")) {
                    try {
                        int subTaskId = Integer.parseInt(stringPath[2]);
                        if (taskManager.getSubTaskById(subTaskId) == null) {
                            sendText(httpExchange, "Subtask not found", 404);
                        } else {
                            taskManager.deleteSubTaskById(subTaskId);
                            sendText(httpExchange, "Subtask has been deleted", 201);
                        }
                    } catch (NumberFormatException e) {
                        sendText(httpExchange, "Invalid subtask id format: " + e.getMessage(), 400);
                    } catch (Exception e) {
                        sendText(httpExchange, "Server error: " + e.getMessage(), 500);
                    }

                } else {
                    sendText(httpExchange, "Endpoint not found: " + path, 404);
                }
                break;
            default:
                sendText(httpExchange, "Method not allowed", 405);
                break;
        }
    }

    private void sendText(HttpExchange httpExchange, String response, int statusCode) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(statusCode, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }
}