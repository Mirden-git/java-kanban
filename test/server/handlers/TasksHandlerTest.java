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

class TasksHandlerTest {

    private HttpClient client;
    private static final String BASE_URL = "http://localhost:8080";
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
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
    void shouldReturnEmptyListWhenNoTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertNotNull(tasks);
        assertEquals(0, tasks.length);
    }

    @Test
    void shouldCreateTaskOnPostAndReturn201() throws IOException, InterruptedException {
        String body = """
                {
                  "id": 0,
                  "name": "Test task",
                  "description": "Description",
                  "startTime": "20.11.2025 10:00",
                  "duration": "PT30M"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        // Проверим, что задача появилась в списке
//        HttpRequest getAll = HttpRequest.newBuilder()
//                .uri(URI.create(BASE_URL + "/tasks"))
//                .GET()
//                .build();
//
//        HttpResponse<String> getResponse = client.send(getAll, HttpResponse.BodyHandlers.ofString());
//        Task[] tasks = gson.fromJson(getResponse.body(), Task[].class);
//        assertTrue(tasks.length >= 1);
//        assertTrue(
//                java.util.Arrays.stream(tasks)
//                        .anyMatch(t -> "Test task".equals(t.getName()))
//        );
    }

    @Test
    void shouldReturnTaskById() throws IOException, InterruptedException {
        // Создаём задачу
        String body = """
                {
                  "id": 0,
                  "name": "Task for getById",
                  "description": "Desc",
                  "startTime": "20.11.2025 11:00",
                  "duration": "PT45M"
                }
                """;

        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        client.send(post, HttpResponse.BodyHandlers.ofString());

        // Находим её id через GET /tasks
        HttpRequest getAll = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .GET()
                .build();
        HttpResponse<String> getResp = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        Task[] tasks = gson.fromJson(getResp.body(), Task[].class);

        Task created = java.util.Arrays.stream(tasks)
                .filter(t -> "Task for getById".equals(t.getName()))
                .findFirst()
                .orElseThrow();

        HttpRequest getById = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + created.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(getById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task fromServer = gson.fromJson(response.body(), Task.class);
        assertEquals(created.getId(), fromServer.getId());
        assertEquals("Task for getById", fromServer.getName());
    }

    @Test
    void shouldReturn404ForUnknownTaskId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/999999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteTaskAndReturn200() throws IOException, InterruptedException {
        // Создаём задачу
        String body = """
                {
                  "id": 0,
                  "name": "Task for delete",
                  "description": "Desc",
                  "startTime": "20.11.2025 12:00",
                  "duration": "PT30M"
                }
                """;

        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        client.send(post, HttpResponse.BodyHandlers.ofString());

        // Находим id
        HttpRequest getAll = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .GET()
                .build();
        HttpResponse<String> getResp = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        Task[] tasks = gson.fromJson(getResp.body(), Task[].class);

        Task toDelete = java.util.Arrays.stream(tasks)
                .filter(t -> "Task for delete".equals(t.getName()))
                .findFirst()
                .orElseThrow();

        HttpRequest delete = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + toDelete.getId()))
                .DELETE()
                .build();

        HttpResponse<String> deleteResp = client.send(delete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResp.statusCode());
    }

    @Test
    void shouldReturn406OnTimeIntersection() throws IOException, InterruptedException {
        // Первая задача
        String body1 = """
                {
                  "id": 0,
                  "name": "Task 1",
                  "description": "Desc 1",
                  "startTime": "21.11.2025 10:00",
                  "duration": "PT60M"
                }
                """;
        HttpRequest post1 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body1))
                .header("Content-Type", "application/json")
                .build();
        client.send(post1, HttpResponse.BodyHandlers.ofString());

        // Вторая задача с пересечением по времени
        String body2 = """
                {
                  "id": 0,
                  "name": "Task 2 overlap",
                  "description": "Desc 2",
                  "startTime": "21.11.2025 10:30",
                  "duration": "PT30M"
                }
                """;
        HttpRequest post2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body2))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(post2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }
}