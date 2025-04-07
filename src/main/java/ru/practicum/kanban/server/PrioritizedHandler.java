package ru.practicum.kanban.server;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange, String path) throws IOException {
        String[] stringPath = path.split("/");
        if (stringPath.length == 2 && stringPath[1].equals("prioritized")) {
            try {
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String responsePrioritized = gson.toJson(prioritizedTasks);
                sendText(exchange, responsePrioritized, 200);
            } catch (Exception e) {
                sendText(exchange, "Server error: " + e.getMessage(), 500);
            }
        } else {
            sendText(exchange, "Endpoint not found: " + path, 404);
        }
    }
}
