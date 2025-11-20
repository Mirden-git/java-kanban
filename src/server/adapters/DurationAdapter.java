package server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class DurationAdapter extends TypeAdapter<Duration> {
//    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(duration.toString());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        return Duration.parse(jsonReader.nextString());
    }
}