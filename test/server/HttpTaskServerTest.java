package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {

    private HttpClient client;
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    public void setUp() {
        client = HttpClient.newHttpClient();
        HttpTaskServer.start();
    }

    @AfterEach
    public void afterEach() {
        HttpTaskServer.stop();
    }

    @Test
    public void shouldReturn404OnUnknownEndpoint() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/unknown"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}