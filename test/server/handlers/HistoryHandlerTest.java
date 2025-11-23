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

class HistoryHandlerTest {

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
    }

    @AfterEach
    void afterEach() {
        HttpTaskServer.stop();
    }

    @Test
    void shouldReturnHistoryAfterAccessingTask() throws IOException, InterruptedException {
        // создаём задачу
        String body = """
                {
                  "id": 0,
                  "name": "Task for history",
                  "description": "desc",
                  "startTime": "25.11.2025 10:00",
                  "duration": "PT30M"
                }
                """;

        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        client.send(post, HttpResponse.BodyHandlers.ofString());

        // найдём её id
        HttpRequest getAll = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .GET()
                .build();
        HttpResponse<String> getResp = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        Task[] tasks = gson.fromJson(getResp.body(), Task[].class);
        Task created = java.util.Arrays.stream(tasks)
                .filter(t -> "Task for history".equals(t.getName()))
                .findFirst()
                .orElseThrow();

        // обращаемся к задаче по id (чтобы точно попасть в history)
        HttpRequest getById = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + created.getId()))
                .GET()
                .build();
        client.send(getById, HttpResponse.BodyHandlers.ofString());

        // теперь проверяем /history
        HttpRequest historyReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/history"))
                .GET()
                .build();
        HttpResponse<String> historyResp = client.send(historyReq, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, historyResp.statusCode());
        Task[] history = gson.fromJson(historyResp.body(), Task[].class);
        assertNotNull(history);
        assertTrue(history.length >= 1);
    }
}
