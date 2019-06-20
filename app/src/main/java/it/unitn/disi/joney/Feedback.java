package it.unitn.disi.joney;

public class Feedback {
    int id;
    int rating;
    String comment;
    String date;
    int jobId;
    int authorId;
    int receiverId;

    public Feedback() {
    }

    public Feedback(int id, int rating, String comment, String date, int jobId, int authorId, int receiverId) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.jobId = jobId;
        this.authorId = authorId;
        this.receiverId = receiverId;
    }

    public Feedback(int rating, String comment, String date, int jobId, int authorId, int receiverId) {
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.jobId = jobId;
        this.authorId = authorId;
        this.receiverId = receiverId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public int getJobId() { return jobId; }

    public void setJobId(int jobId) { this.jobId = jobId; }

    public String dateToString()
    {
        String thisDay = this.getDate().substring(8,10);
        String thisMonth = this.getDate().substring(5,7);
        String thisYear = this.getDate().substring(0,4);

        return  thisDay + "/" + thisMonth + "/" + thisYear;
    }
}
