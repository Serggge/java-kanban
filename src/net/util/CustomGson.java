package net.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import service.exceptions.DeserializationException;
import service.exceptions.SerializationException;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public class CustomGson {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Supplier<LocalDateTime>>() {
                                               }.getType(), new DateTimeAdapter())
                                               .registerTypeAdapter(new TypeToken<Supplier<Duration>>() {
                                               }.getType(), new DurationAdapter())
                                               .create();

    private CustomGson() {

    }

    public static Gson getGson() {
        return gson;
    }
    private static class DateTimeAdapter extends TypeAdapter<Supplier<LocalDateTime>> {

        @Override
        public void write(JsonWriter jsonWriter, Supplier<LocalDateTime> localDateTimeSupplier) {
            try {
                if (localDateTimeSupplier == null || localDateTimeSupplier.get() == null) {
                    jsonWriter.value("");
                } else {
                    jsonWriter.value(localDateTimeSupplier.get()
                                                          .format(DATE_TIME_FORMATTER));
                }
            } catch (IOException e) {
                throw new SerializationException("Ошибка при сериализации Task/Subtask", e);
            }
        }

        @Override
        public Supplier<LocalDateTime> read(JsonReader jsonReader) {
            try {
                String dateTimeString = jsonReader.nextString();
                if (dateTimeString.isBlank()) {
                    return null;
                } else {
                    return () -> LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
                }
            } catch (IOException e) {
                throw new DeserializationException("Ошибка при десериализации Task/Subtask");
            }
        }
    }

    private static class DurationAdapter extends TypeAdapter<Supplier<Duration>> {

        @Override
        public void write(JsonWriter jsonWriter, Supplier<Duration> durationSupplier) {
            try {
                if (durationSupplier == null || durationSupplier.get() == null) {
                    jsonWriter.value(0);
                } else {
                    jsonWriter.value(durationSupplier.get()
                                                     .toMinutes());
                }
            } catch (IOException e) {
                throw new SerializationException("Ошибка при сериализации Task/Subtask", e);
            }
        }

        @Override
        public Supplier<Duration> read(JsonReader jsonReader) {
            try {
                long duration = jsonReader.nextLong();
                if (duration > 0) {
                    return () -> Duration.ofMinutes(duration);
                } else {
                    return null;
                }
            } catch (IOException e) {
                throw new DeserializationException("Ошибка при десериализации Task/Subtask");
            }
        }
    }
}
