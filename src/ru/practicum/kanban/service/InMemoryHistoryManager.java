package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyList = new LinkedList<>();
    private static final int MAX_SIZE_HISTORY_LIST = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (historyList.size() >= MAX_SIZE_HISTORY_LIST) {
            historyList.removeFirst();
        }
        historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(historyList);
    }
}
