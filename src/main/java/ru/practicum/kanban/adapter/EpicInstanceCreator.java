package ru.practicum.kanban.adapter;

import com.google.gson.InstanceCreator;
import ru.practicum.kanban.model.Epic;

import java.lang.reflect.Type;

public class EpicInstanceCreator implements InstanceCreator<Epic> {
    @Override
    public Epic createInstance(Type type) {
        return new Epic("temporary", "temporary", 0);
    }
}