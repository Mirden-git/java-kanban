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
        HttpRequest historyReq1 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/history"))
                .GET()
                .build();
        HttpResponse<String> historyResp1 = client.send(historyReq1, HttpResponse.BodyHandlers.ofString());
        Task[] history = gson.fromJson(historyResp1.body(), Task[].class);
        int initialLength = history.length;

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

        HttpRequest historyReq2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/history"))
                .GET()
                .build();
        HttpResponse<String> historyResp2 = client.send(historyReq2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, historyResp2.statusCode());
        Task[] history2 = gson.fromJson(historyResp2.body(), Task[].class);
        assertNotNull(history2);
        assertEquals(history2.length, initialLength + 1);
    }
}