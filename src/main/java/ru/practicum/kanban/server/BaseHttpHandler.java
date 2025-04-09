package ru.practicum.kanban.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.kanban.service.Managers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {
    protected final Gson gson;

    public BaseHttpHandler() {
        this.gson = Managers.getGson();
    }

    public static Gson getGson() {
        return Managers.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET":
                processGet(exchange, path);
                break;
            case "POST":
                processPost(exchange, path);
                break;
            case "DELETE":
                processDelete(exchange, path);
                break;
            default:
                sendText(exchange, "Method not allowed", 405);
        }
    }

    protected void processGet(HttpExchange exchange, String path) throws IOException {
        sendText(exchange, "Method not allowed", 405);
    }

    protected void processPost(HttpExchange exchange, String path) throws IOException {
        sendText(exchange, "Method not allowed", 405);
    }

    protected void processDelete(HttpExchange exchange, String path) throws IOException {
        sendText(exchange, "Method not allowed", 405);
    }

    protected void sendText(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }
}
