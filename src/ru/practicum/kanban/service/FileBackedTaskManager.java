package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.SubTask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.model.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static String toString(Task task) {
        String[] stringTask = {
                Integer.toString(task.getId()), getTaskType(task).toString(), task.getName(),
                task.getTaskStatus().toString(), task.getDescription(), Integer.toString(findEpicIdForSubtask(task))};
        return String.join(",", stringTask);
    }

    private static int findEpicIdForSubtask(Task task) {
        if (task instanceof SubTask) {
            return ((SubTask) task).getEpicId();
        }
        return -1;
    }

    private static TaskType getTaskType(Task task) {
        switch (task.getClass().getSimpleName()) {
            case "SubTask":
                return TaskType.SUBTASK;
            case "Epic":
                return TaskType.EPIC;
            default:
                return TaskType.TASK;
        }
    }

    public void save() {
        try {
            if (!Files.exists(file.toPath())) {
                Files.createFile(file.toPath());
            }
            boolean isEmpty = true;
            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                writer.write("id,type,name,status,description,epic\n");
                for (Task task : getAllTasks()) {
                    writer.write(toString(task) + "\n");
                    isEmpty = false;
                }
                for (Epic epic : getAllEpics()) {
                    writer.write(toString(epic) + "\n");
                    isEmpty = false;
                }
                for (SubTask subTask : getAllSubTasks()) {
                    writer.write(toString(subTask) + "\n");
                    isEmpty = false;
                }
            }
            if (isEmpty) {
                throw new ManagerSaveException("File is empty");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error working with the file");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            throw new ManagerSaveException("File is null");
        }
        if (!file.isFile()) {
            if (file.isDirectory()) {
                throw new ManagerSaveException("Directory is located at the specified path");
            } else {
                throw new ManagerSaveException("Path is specified incorrectly");
            }
        }
        if (file.length() == 0) {
            throw new ManagerSaveException("File is empty");
        }

        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.createEpic((Epic) task);
                } else if (task instanceof SubTask) {
                    manager.createSubTask((SubTask) task);
                } else {
                    manager.createTask(task);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Failed to load tasks from file", e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] restoreTask = value.split(",");
        int id = Integer.parseInt(restoreTask[0]);
        TaskType taskType = TaskType.valueOf(restoreTask[1]);
        String name = restoreTask[2];
        TaskStatus taskStatus = TaskStatus.valueOf(restoreTask[3]);
        String description = restoreTask[4];
        int EpicId = taskType == TaskType.SUBTASK ? Integer.parseInt(restoreTask[5]) : -1;
        switch (taskType) {
            case TASK:
                Task task = new Task(name, description, id, taskStatus);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, id);
                epic.getSubTasks().get(EpicId);
                return epic;
            case SUBTASK:
                SubTask subTask = new SubTask(name, description, id, taskStatus, EpicId);
                return subTask;
            default:
                return null;
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public void addTaskToHistoryList(Task task) {
        super.addTaskToHistoryList(task);
        save();
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public List<SubTask> getAllSubtasksByEpicId(int id) {
        return super.getAllSubTasks();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Task createTask(Task newTask) {
        super.createTask(newTask);
        save();
        return newTask;
    }

    @Override
    public void createSubTask(SubTask newSubTask) {
        super.createSubTask(newSubTask);
        save();
    }

    @Override
    public void createEpic(Epic newEpic) {
        super.createEpic(newEpic);
        save();
    }

    @Override
    public void updateTask(Task updateTask) {
        super.updateTask(updateTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask myUpdateSubTask) {
        super.updateSubTask(myUpdateSubTask);
        save();
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        save();
        return history;
    }

    @Override
    public int idGenerator() {
        return super.idGenerator();
    }
}