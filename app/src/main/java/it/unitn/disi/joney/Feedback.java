package it.unitn.disi.joney;

public class Feedback {
    int id;
    int rating;
    String comment;
    int authorId;
    int receiverId;

    public Feedback() {
    }

    public Feedback(int fbID, int rating, String comment, int authorID, int receiverID) {
        this.id = fbID;
        this.rating = rating;
        this.comment = comment;
        this.authorId = authorID;
        this.receiverId = receiverID;
    }

    public Feedback(int rating, String comment, int authorId, int receiverId) {
        this.rating = rating;
        this.comment = comment;
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
}
