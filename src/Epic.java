import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private Map<Integer, SubTask> subTasks;

    public Epic(String name, String description, int id, TaskStatus taskStatus) {
        super(name, description, id, taskStatus);
        this.subTasks = new HashMap<>();
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void updateEpicStatus() {
        if (getSubTasks().isEmpty()) {
            setTaskStatus(TaskStatus.NEW);
            return;
        }
        boolean allTasksClosed = true;
        for (SubTask subTask : getSubTasks().values()) {
            if (subTask.getTaskStatus() != TaskStatus.DONE) {
                allTasksClosed = false;
                break;
            }
        }

        if (allTasksClosed) {
            setTaskStatus(TaskStatus.DONE);
        } else {
            setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void addSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }

    public void removeSubTask() {
        subTasks.clear();
    }

    @Override
    public String toString() {
        return "Epic {" +
                "subTasks=" + subTasks +
                ", " + super.toString() +
                '}';
    }
}
