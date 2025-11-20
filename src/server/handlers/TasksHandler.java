package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        String type = splitPath[1];
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        switch (type) {
            case "tasks": {
                switch (request) {
                    case "GET": {
                        String text = "";

                        if (splitPath.length == 2) {
                            text = gson.toJson(taskManager.getTasks());
                        } else if (splitPath.length == 3 && getIdFromPath(exchange).isPresent()) {
                            int id = getIdFromPath(exchange).get();
                            Task task = taskManager.getTaskById(id);
                            if (task != null) {
                                text = gson.toJson(task);
                            } else {
                                sendNotFound(exchange);
                                return;
                            }
                        } else {
                            sendNotFound(exchange);
                            return;
                        }
                        sendText(exchange, text);
                        break;
                    }
                    case "POST": {
                        String taskFromJson = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        System.out.println("body = " + taskFromJson);
                        Task task = gson.fromJson(taskFromJson, Task.class);
                        System.out.println("Задача " + task);
                        taskManager.addTask(task);
                        sendText(exchange, "Задача добавлена");
                        break;
                    }
                    default: {
                    }
                }
                break;
            }
            case "subtasks": {
                break;
            }
            case "epics": {
                break;
            }
            default: {
            }
        }
    }

    private Optional<Integer> getIdFromPath(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
