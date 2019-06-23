package it.unitn.disi.joney;

public class Job {
    int id;
    String title;
    String description;
    int pay;
    int status;
    String createdAt;
    double latitude;
    double longitude;
    int categoryId;
    JobCategory jobCategory;
    int authorId;
    User author;
    Integer workerId; //this can be null, so Integer is needed
    User worker;

    public Job() {
    }

    public Job(int jobID, String title, String description, int pay, int status, String createdAt, double latitude, double longitude, int categoryID, int authorID, Integer workerID) {
        this.id = jobID;
        this.title = title;
        this.description = description;
        this.pay = pay;
        this.status = status;
        this.createdAt = createdAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categoryId = categoryID;
        this.authorId = authorID;
        this.workerId = workerID;
    }

    public Job(String title, String description, int pay, int status, String createdAt,  double latitude, double longitude, int categoryId, int authorId, Integer workerId) {
        this.title = title;
        this.description = description;
        this.pay = pay;
        this.status = status;
        this.createdAt = createdAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categoryId = categoryId;
        this.authorId = authorId;
        this.workerId = workerId;
    }

    public Job(int jobID, String title, String description)
    {
        this.id = jobID;
        this.title = title;
        this.description = description;
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

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
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

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public JobCategory getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(JobCategory jobCategory) {
        this.jobCategory = jobCategory;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getWorker() {
        return worker;
    }

    public void setWorker(User worker) {
        this.worker = worker;
    }

    @Override
    public String toString() {
        return title;
    }
}
