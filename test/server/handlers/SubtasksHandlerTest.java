package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.Epic;
import task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest {

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
    void shouldCreateSubtaskForEpic() throws IOException, InterruptedException {
        HttpTaskServer.taskManager.addEpic("A", "B", null, Duration.ZERO);
        int epicId = HttpTaskServer.taskManager.getEpics().getFirst().getId();

        String body = """
                {
                  "id": 0,
                  "name": "Subtask 1",
                  "description": "sub desc",
                  "epicId": %d,
                  "startTime": "23.11.2025 11:00",
                  "duration": "PT30M"
                }
                """.formatted(epicId);

        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(post, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        HttpRequest getAll = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .GET()
                .build();
        HttpResponse<String> getResp = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        Subtask[] subtasks = gson.fromJson(getResp.body(), Subtask[].class);

        assertTrue(subtasks.length >= 1);
        assertTrue(java.util.Arrays.stream(subtasks)
                .anyMatch(s -> "Subtask 1".equals(s.getName())));
    }

    @Test
    void shouldReturn406OnSubtaskTimeIntersection() throws IOException, InterruptedException {
        HttpTaskServer.taskManager.addEpic("A", "B", null, Duration.ZERO);
        int epicId = HttpTaskServer.taskManager.getEpics().getFirst().getId();

        String body1 = """
                {
                  "id": 0,
                  "name": "Subtask A",
                  "description": "desc A",
                  "epicId": %d,
                  "startTime": "24.11.2025 10:00",
                  "duration": "PT60M"
                }
                """.formatted(epicId);
        HttpRequest post1 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body1))
                .header("Content-Type", "application/json")
                .build();
        client.send(post1, HttpResponse.BodyHandlers.ofString());

        String body2 = """
                {
                  "id": 0,
                  "name": "Subtask B overlap",
                  "description": "desc B",
                  "epicId": %d,
                  "startTime": "24.11.2025 10:30",
                  "duration": "PT30M"
                }
                """.formatted(epicId);
        HttpRequest post2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body2))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(post2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }
}