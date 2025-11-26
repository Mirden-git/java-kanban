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
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

    protected void sendServerError(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(500, 0);
        h.getResponseBody().write("Internal Server Error".getBytes());
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

    protected void handleDelete(HttpExchange h, IntConsumer deleteById) throws IOException {
        Optional<Integer> idOpt = getIdFromPath(h);

        if (splitPath.length == 3 && idOpt.isPresent()) {
            int id = idOpt.get();
            deleteById.accept(id);
            sendText(h, "Задача удалена");
        } else {
            sendNotFound(h);
        }
    }

    protected <T> void handleGet(
            HttpExchange h,
            Supplier<Iterable<T>> allTasks,
            Function<Integer, T> getById
    ) throws IOException {

        String text;

        if (splitPath.length == 2) {
            text = gson.toJson(allTasks.get());
        } else if (splitPath.length == 3 && getIdFromPath(h).isPresent()) {
            int id = getIdFromPath(h).get();
            T entity = getById.apply(id);
            if (entity != null) {
                text = gson.toJson(entity);
            } else {
                sendNotFound(h);
                return;
            }
        } else {
            sendNotFound(h);
            return;
        }

        sendText(h, text);
    }

    protected <T> void handlePost(
            HttpExchange h,
            Class<T> type,
            Predicate<T> hasIntersection,
            Predicate<T> isNewEntity,
            Consumer<T> addEntity,
            Consumer<T> updateEntity
    ) throws IOException {

        String entityFromJson = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        T entity = gson.fromJson(entityFromJson, type);

        if (hasIntersection.test(entity)) {
            sendHasOverlaps(h);
            return;
        }

        if (!isNewEntity.test(entity)) {
            addEntity.accept(entity);
        } else {
            updateEntity.accept(entity);
        }

        sendOk(h);
    }
}
