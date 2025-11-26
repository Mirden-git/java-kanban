package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest {

    private HttpClient client;
    private static final String BASE_URL = "http://localhost:8080";
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    void setUp() {
        client = HttpClient.newHttpClient();
        HttpTaskServer.start();
        HttpTaskServer.taskManager.clearListOfTasks();
        HttpTaskServer.taskManager.clearListOfSubtasks();
        HttpTaskServer.taskManager.clearListOfEpics();
    }

    @AfterEach
    void afterEach() {
        HttpTaskServer.stop();
    }

    @Test
    void shouldReturnEmptyPrioritizedListInitially() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());

        Task[] tasks = gson.fromJson(resp.body(), Task[].class);
        assertNotNull(tasks);
        assertEquals(0, tasks.length);
    }

    @Test
    void shouldReturnTasksInPrioritizedEndpoint() throws IOException, InterruptedException {
        String body1 = """
                {
                  "id": 0,
                  "name": "P task 1",
                  "description": "desc1",
                  "startTime": "26.11.2025 09:00",
                  "duration": "PT30M"
                }
                """;
        String body2 = """
                {
                  "id": 0,
                  "name": "P task 2",
                  "description": "desc2",
                  "startTime": "26.11.2025 11:00",
                  "duration": "PT30M"
                }
                """;

        HttpRequest post1 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body1))
                .header("Content-Type", "application/json")
                .build();
        client.send(post1, HttpResponse.BodyHandlers.ofString());

        HttpRequest post2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body2))
                .header("Content-Type", "application/json")
                .build();
        client.send(post2, HttpResponse.BodyHandlers.ofString());

        HttpRequest getPrioritized = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(getPrioritized, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());

        Task[] tasks = gson.fromJson(resp.body(), Task[].class);
        assertNotNull(tasks);
        assertTrue(tasks.length >= 2);
    }
}