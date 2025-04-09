package ru.practicum.kanban.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.practicum.kanban.adapter.DurationAdapter;
import ru.practicum.kanban.adapter.EpicInstanceCreator;
import ru.practicum.kanban.adapter.LocalDateTimeAdapter;
import ru.practicum.kanban.model.Epic;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public final class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getFileBacked(File file) {
        return new FileBackedTaskManager(getDefaultHistory(), file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(Epic.class, new EpicInstanceCreator());
        return gsonBuilder.create();
    }
}
