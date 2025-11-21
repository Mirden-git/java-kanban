package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import task.Task;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            prepareHandler(exchange);

            switch (request) {
                case "GET": {
                    handleGet(exchange, taskManager::getTasks, taskManager::getTaskById);
                    break;
                }
                case "POST": {
                    handlePost(
                            exchange,
                            Task.class,
                            taskManager::isTimeIntersectionWithAllTasks,
                            task -> taskManager.getTaskById(task.getId()) == null,
                            taskManager::addTask,
                            taskManager::updateTask
                    );
                    break;
                }
                case "DELETE": {
                    handleDelete(exchange, taskManager::deleteTask);
                    break;
                }
                default: {
                    sendNotFound(exchange);
                }
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }
}