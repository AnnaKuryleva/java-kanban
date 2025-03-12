package ru.practicum.kanban.service;

import java.io.File;

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

}
