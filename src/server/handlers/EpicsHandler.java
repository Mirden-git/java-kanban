package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
