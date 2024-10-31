public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, int id, TaskStatus taskStatus, int epicId) {
        super(name, description, id, taskStatus);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask {" +
                "epicId= " + epicId +
                ", " + super.toString() +
                '}';
    }
}
