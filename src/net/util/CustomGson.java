package net.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.Task;
import model.TaskStatus;
import service.TaskType;
import service.exceptions.DeserializationException;
import service.exceptions.SerializationException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

public class CustomGson {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Supplier<LocalDateTime>>() {
                                                      }.getType(), new DateTimeAdapter())
                                                      .registerTypeAdapter(new TypeToken<Supplier<Duration>>() {
                                                      }.getType(), new DurationAdapter())
                                                      .registerTypeAdapter(TaskStatus.class, new TaskStatusAdapter())
                                                      .registerTypeAdapter(TaskType.class, new TaskTypeAdapter())
                                                      .registerTypeAdapter(new TypeToken<Map<Integer, Task>>() {
                                                      }.getType(), new MapAdapter())
                                                      .create();

    private CustomGson() {

    }

    public static Gson getGson() {
        return gson;
    }

    private static class MapAdapter extends TypeAdapter<Map<Integer, Task>> {

        @Override
        public void write(JsonWriter jsonWriter, Map<Integer, Task> map) throws IOException {
            StringBuilder sb = new StringBuilder();
            Set<Integer> listId = map.keySet();
            for (int id : listId) {
                sb.append(id).append(";");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            jsonWriter.value(sb.toString());
        }

        @Override
        public Map<Integer, Task> read(JsonReader jsonReader) throws IOException {
            Map<Integer, Task> map = new HashMap<>();
            String[] arrayId = jsonReader.nextString().split(";");
            for (String id : arrayId) {
                if (!id.isEmpty()) {
                    map.put(Integer.parseInt(id), new Task("", ""));
                }
            }
            return map;
        }
    }

    private static class TaskStatusAdapter extends TypeAdapter<TaskStatus> {

        @Override
        public void write(JsonWriter jsonWriter, TaskStatus taskStatus) throws IOException {
            jsonWriter.value(taskStatus.toString());
        }

        @Override
        public TaskStatus read(JsonReader jsonReader) throws IOException {
            return TaskStatus.valueOf(jsonReader.nextString());
        }
    }

    private static class TaskTypeAdapter extends TypeAdapter<TaskType> {

        @Override
        public void write(JsonWriter jsonWriter, TaskType taskType) throws IOException {
            jsonWriter.value(taskType.toString());
        }

        @Override
        public TaskType read(JsonReader jsonReader) throws IOException {
            return TaskType.valueOf(jsonReader.nextString());
        }
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
