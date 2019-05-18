package it.unitn.disi.joney;

public class Job {
    int id;
    String title;
    String description;
    boolean completed;
    float latitude;
    float longitude;
    int categoryId;
    int authorId;
    int workerId;

    public Job() {
    }

    public Job(int jobID, String title, String description, boolean completed, float latitude, float longitude, int categoryID, int authorID, int workerID) {
        this.id = jobID;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categoryId = categoryID;
        this.authorId = authorID;
        this.workerId = workerID;
    }

    public Job(String title, String description, boolean completed, float latitude, float longitude, int categoryId, int authorId, int workerId) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categoryId = categoryId;
        this.authorId = authorId;
        this.workerId = workerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }
}
