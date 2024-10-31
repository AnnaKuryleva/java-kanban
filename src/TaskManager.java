import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> task = new HashMap<>();
    private Map<Integer, SubTask> subTask = new HashMap<>();
    private Map<Integer, Epic> epic = new HashMap<>();

    private int idCounter = 1;

    public List<Task> getAllTasks() {
        return new ArrayList<>(task.values());
    }

    public List<SubTask> getAllSubTasks() {
        List<SubTask> subTaskList = new ArrayList<>(subTask.values());
        return subTaskList;
    }

    public List<Epic> getAllEpics() {
        List<Epic> epicList = new ArrayList<>(epic.values());
        return epicList;
    }

    public void deleteAllTasks() {
        task.clear();
    }

    public void deleteAllSubTasks() {
        subTask.clear();
        for (Epic relatedEpic : epic.values()) {
            relatedEpic.removeSubTask();
            relatedEpic.updateEpicStatus();
        }
    }

    public void deleteAllEpics() {
        epic.clear();
        deleteAllSubTasks();
    }

    public Task getTaskById(int id) {
        return task.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTask.get(id);
    }

    public Epic getEpicById(int id) {
        return epic.get(id);
    }

    public Task createTask(Task newTask) {
        newTask.setId(idCounter);
        task.put(newTask.getId(), newTask);
        idCounter++;
        return newTask;
    }

    public void createSubTask(SubTask newSubTask) {
        for (SubTask sTask : subTask.values()) {
            if (sTask.getId() == newSubTask.getId()) {
                return;
            }
        }
        int epicId = newSubTask.getEpicId();
        Epic relatedEpic = epic.get(epicId);
        if (relatedEpic != null) {
            relatedEpic.addSubTask(newSubTask);
            relatedEpic.updateEpicStatus();
        } else {
            return;
        }
        newSubTask.setId(idCounter);
        subTask.put(newSubTask.getId(), newSubTask);
        idCounter++;
    }

    public void createEpic(Epic newEpic) {
        newEpic.setId(idCounter);
        epic.put(newEpic.getId(), newEpic);
        idCounter++;
    }

    public void updateTask(Task updateTask) {
        Integer idTask = updateTask.getId();
        if (!task.containsKey(idTask)) {
            return;
        }
        task.replace(idTask, updateTask);
    }

    public void updateSubTask(SubTask updateSubTask) {
        for (SubTask sTask : subTask.values()) {
            if (sTask.getId() != updateSubTask.getId()) {
                return;
            }
        }
        int epicId = updateSubTask.getEpicId();
        Epic relatedEpic = epic.get(epicId);
        if (relatedEpic != null) {
            relatedEpic.addSubTask(updateSubTask);
            relatedEpic.updateEpicStatus();
        } else {
            return;
        }
        subTask.put(updateSubTask.getId(), updateSubTask);
    }

    public void updateEpic(Epic updateEpic) {
        Integer epicId = updateEpic.getId();
        if (!epic.containsKey(epicId)) {
            return;
        }
        epic.put(updateEpic.getId(), updateEpic);
    }
}
