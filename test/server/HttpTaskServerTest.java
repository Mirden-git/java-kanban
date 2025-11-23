package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    @BeforeEach
    void setUp() {
        new HttpTaskServer();
        HttpTaskServer.start();

    }

    @AfterEach
    void afterEach() {
        HttpTaskServer.stop();
    }


}