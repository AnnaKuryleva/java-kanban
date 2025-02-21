package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        int id = task.getId();
        if (historyMap.containsKey(id)) {
            remove(id);
        }
        Node<Task> newNode = new Node<>(task);
        if (tail != null) {
            tail.next = newNode;
            newNode.prev = tail;
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
        node.prev = null;
        node.next = null;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyTasks = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            historyTasks.add(node.task);
            node = node.next;
        }
        return historyTasks;
    }

    private class Node<T> {
        T task;
        Node<T> next;
        Node<T> prev;

        Node(T task) {
            this.task = task;
        }
    }
}
