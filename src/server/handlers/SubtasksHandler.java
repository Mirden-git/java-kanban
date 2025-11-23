package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            prepareHandler(exchange);

            switch (request) {
                case "GET": {
                    handleGet(exchange, taskManager::getSubtasks, taskManager::getSubtaskById);
                    break;
                }
                case "POST": {
                    handlePost(
                            exchange,
                            Subtask.class,
                            taskManager::isTimeIntersectionWithAllTasks,
                            subtask -> taskManager.isSubtaskExists(subtask.getId()),
                            taskManager::addSubtask,
                            taskManager::updateSubtask
                    );
                    break;
                }
                case "DELETE": {
                    handleDelete(exchange, taskManager::deleteSubtask);
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