package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.Epic;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        prepareHandler(exchange);

        switch (request) {
            case "GET": {
                String text;

                switch (splitPath.length) {
                    case 2: {
                        text = gson.toJson(taskManager.getEpics());
                        sendText(exchange, text);
                        break;
                    }
                    case 3: {
                        if (getIdFromPath(exchange).isPresent()) {
                            int id = getIdFromPath(exchange).get();
                            Epic epic = taskManager.getEpicById(id);

                            if (epic != null) {
                                text = gson.toJson(epic);
                                sendText(exchange, text);
                            }
                        }

                        break;
                    }
                    case 4: {
                        if (splitPath[3].equals("subtasks".toLowerCase()) && getIdFromPath(exchange).isPresent()) {
                            int id = getIdFromPath(exchange).get();
                            text = gson.toJson(taskManager.getListOfEpicSubtasks(id));
                            sendText(exchange, text);
                        }

                        break;
                    }
                    default: {
                        sendNotFound(exchange);
                    }
                }
            }
            case "POST": {
                String epicFromJson = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("body = " + epicFromJson); //todo удалить
                Epic epic = gson.fromJson(epicFromJson, Epic.class);
                System.out.println("Задача " + epic); //todo удалить

                boolean isIntersection = taskManager.isTimeIntersectionWithAllTasks(epic);

                if (isIntersection) {
                    sendHasOverlaps(exchange);
                    return;
                }

                if (taskManager.getEpicById(epic.getId()) == null) {
                    taskManager.addEpic(epic);
                } else {
                    taskManager.updateEpic(epic);
                }

                sendOk(exchange);
                break;
            }
            case "DELETE": {
                Optional<Integer> idOpt = getIdFromPath(exchange);

                if (splitPath.length == 3 && idOpt.isPresent()) {
                    boolean isTaskExist = taskManager.getTaskById(idOpt.get()) != null;

                    if (isTaskExist) {
                        taskManager.deleteTask(idOpt.get());
                        sendText(exchange, "Задача удалена");
                    } else {
                        sendNotFound(exchange);
                    }
                }

                break;
            }
            default: {
                sendNotFound(exchange);
            }
        }
    }
}
