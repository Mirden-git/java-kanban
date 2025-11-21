package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
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
                    handlePost(
                            exchange,
                            Epic.class,
                            epic -> false,
                            epic -> taskManager.getEpicById(epic.getId()) == null,
                            taskManager::addEpic,
                            taskManager::updateEpic
                    );
                    break;
                }
                case "DELETE": {
                    handleDelete(exchange, taskManager::deleteEpic);
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