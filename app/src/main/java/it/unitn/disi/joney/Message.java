package it.unitn.disi.joney;

public class Message {
    int senderId;
    int receiverId;
    String date;
    String message;

    public Message() {
    }

    public Message(int senderId, int receiverId, String date, String text) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.date = date;
        this.message = text;
    }

    public int getSenderId() { return senderId; }

    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }

    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}
