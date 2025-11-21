package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        prepareHandler(exchange);

        switch (request) {
            case "GET": {
                String text;

                if (splitPath.length == 2) {
                    text = gson.toJson(taskManager.getSubtasks());
                } else if (splitPath.length == 3 && getIdFromPath(exchange).isPresent()) {
                    int id = getIdFromPath(exchange).get();
                    Subtask subtask = taskManager.getSubtaskById(id);
                    if (subtask != null) {
                        text = gson.toJson(subtask);
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
                String subtaskFromJson = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("body = " + subtaskFromJson); //todo удалить
                Subtask subtask = gson.fromJson(subtaskFromJson, Subtask.class);
                System.out.println("Задача " + subtask); //todo удалить

                boolean isIntersection = taskManager.isTimeIntersectionWithAllTasks(subtask);

                if (isIntersection) {
                    sendHasOverlaps(exchange);
                    return;
                }

                if (taskManager.getSubtaskById(subtask.getId()) == null) {
                    taskManager.addSubtask(subtask);
                } else {
                    taskManager.updateSubtask(subtask);
                }

                sendOk(exchange);
                break;
            }
            case "DELETE": {
                Optional<Integer> idOpt = getIdFromPath(exchange);

                if (splitPath.length == 3 && idOpt.isPresent()) {
                    boolean isTaskExist = taskManager.getSubtaskById(idOpt.get()) != null;

                    if (isTaskExist) {
                        taskManager.deleteSubtask(idOpt.get());
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