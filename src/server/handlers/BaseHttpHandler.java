package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class BaseHttpHandler {
    protected String request;
    protected String path;
    protected String[] splitPath;
    protected Gson gson;

    protected void prepareHandler(HttpExchange h) {
        request = h.getRequestMethod();
        path = h.getRequestURI().getPath();
        splitPath = path.split("/");
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, 0);
        h.getResponseBody().write("Not Found".getBytes());
        h.close();
    }

    protected void sendHasOverlaps(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, 0);
        h.getResponseBody().write("Not acceptable".getBytes());
        h.close();
    }

    protected void sendOk(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, 0);
        h.close();
    }

    protected Optional<Integer> getIdFromPath(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
