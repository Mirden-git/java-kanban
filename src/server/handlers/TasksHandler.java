package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        prepareHandler(exchange);

        switch (request) {
            case "GET": {
                String text;

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
                System.out.println("body = " + taskFromJson); //todo удалить
                Task task = gson.fromJson(taskFromJson, Task.class);
                System.out.println("Задача " + task); //todo удалить

                boolean isIntersection = taskManager.isTimeIntersectionWithAllTasks(task);

                if (isIntersection) {
                    sendHasOverlaps(exchange);
                    return;
                }

                if (taskManager.getTaskById(task.getId()) == null) {
                    taskManager.addTask(task);
                } else {
                    taskManager.updateTask(task);
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