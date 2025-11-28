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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EpicsHandlerTest {

    private HttpClient client;
    private static final String BASE_URL = "http://localhost:8080";
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();

    @BeforeEach
    public void setUp() {
        client = HttpClient.newHttpClient();
        HttpTaskServer.start();
        HttpTaskServer.taskManager.clearListOfTasks();
        HttpTaskServer.taskManager.clearListOfSubtasks();
        HttpTaskServer.taskManager.clearListOfEpics();
    }

    @AfterEach
    public void afterEach() {
        HttpTaskServer.stop();
    }

    @Test
    public void shouldReturnEmptyEpicsListInitially() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertNotNull(epics);
        assertEquals(0, epics.length);
    }

    @Test
    public void shouldCreateEpicOnPost() throws IOException, InterruptedException {
        String body = """
                {
                  "id": 0,
                  "name": "Epic 1",
                  "description": "Epic desc",
                  "startTime": "22.11.2025 11:00",
                  "duration": "PT0M"
                }
                """;
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> postResp = client.send(post, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResp.statusCode());

        HttpRequest getAll = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .GET()
                .build();
        HttpResponse<String> getResp = client.send(getAll, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Epic[] epics = gson.fromJson(getResp.body(), Epic[].class);

        assertTrue(epics.length >= 1);
        assertTrue(Arrays.stream(epics)
                .anyMatch(e -> "Epic 1".equals(e.getName())));
    }

    @Test
    public void shouldReturnEpicById() throws IOException, InterruptedException {
        String body = """
                {
                  "id": 0,
                  "name": "Epic for getById",
                  "description": "Epic desc",
                  "startTime": "22.11.2025 11:00",
                  "duration": "PT0M"
                }
                """;
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> postResp = client.send(post, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, postResp.statusCode());

        HttpRequest getAll = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .GET()
                .build();
        HttpResponse<String> getResp = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        Epic[] epics = gson.fromJson(getResp.body(), Epic[].class);

        Epic created = Arrays.stream(epics)
                .filter(e -> "Epic for getById".equals(e.getName()))
                .findFirst()
                .orElseThrow();

        HttpRequest getById = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics/" + created.getId()))
                .GET()
                .build();
        HttpResponse<String> response = client.send(getById, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic fromServer = gson.fromJson(response.body(), Epic.class);
        assertEquals(created.getId(), fromServer.getId());
    }

    @Test
    public void shouldReturn404ForUnknownEpicId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics/999999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}