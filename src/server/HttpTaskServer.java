package server;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.handlers.EpicsHandler;
import server.handlers.HistoryHandler;
import server.handlers.PrioritizedHandler;
import server.handlers.SubtasksHandler;
import server.handlers.TasksHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final TaskManager taskManager = Managers.getDefault();
    private static HttpServer httpServer;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler(taskManager));
            httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
            httpServer.createContext("/epics", new EpicsHandler(taskManager));
            httpServer.createContext("/history", new HistoryHandler(taskManager));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
            httpServer.start();
            System.out.println("Сервер запущен на " + PORT + " порту");
        } catch (IOException e) {
            throw new RuntimeException(e); //todo отработать исключение
        }
    }

    public static void stop() {
        if (httpServer != null) {
            httpServer.stop(10); //todo определиться с остановкой сервера
        }
    }
}
