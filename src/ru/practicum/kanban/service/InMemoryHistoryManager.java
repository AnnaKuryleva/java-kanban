package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    Node<Task> head;
    Node<Task> tail;

    @Override
    public void add(Task task) {
        int id = task.getId();
        if (historyMap.containsKey(id)) {
            remove(id);
        }
        Node<Task> newNode = new Node<>(task);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        historyMap.put(id, newNode);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyMap.get(id);
        if (node == null) {
            return;
        }
        historyMap.remove(id);
        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;
        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }
    }

    @Override
    public List<Task> getHistoryTasks() {
        List<Task> historyTasks = new ArrayList<>();
        for (Node<Task> node : historyMap.values()) {
            historyTasks.add(node.task);
        }
        return historyTasks;
    }

    private class Node<Task> {
        Task task;
        Node<Task> next;
        Node<Task> prev;

        Node(Task task) {
            this.task = task;
        }
    }
}
