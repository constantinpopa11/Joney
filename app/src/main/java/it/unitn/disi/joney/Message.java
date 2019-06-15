package it.unitn.disi.joney;

public class Message {
    int senderId;
    int receiverId;
    String date;
    String message;

    public Message() {
    }

    public Message(int senderId, int receiverId, String date, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.date = date;
        this.message = message;
    }

    public int getSenderId() { return senderId; }

    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }

    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public  boolean isDifferentDate(Message message)
    {
        boolean result = false;

        String thisDay = this.getDate().substring(8,10);
        String thisMonth = this.getDate().substring(5,7);
        String thisYear = this.getDate().substring(0,4);

        String messageDay = message.getDate().substring(8,10);
        String messageMonth = message.getDate().substring(5,7);
        String messageYear = message.getDate().substring(0,4);

        if(!thisDay.equals(messageDay) || !thisMonth.equals(messageMonth) || !thisYear.equals(messageYear))
            result = true;

        return  result;
    }

    public String dateToString()
    {
        String thisDay = this.getDate().substring(8,10);
        String thisMonth = this.getDate().substring(5,7);
        String thisYear = this.getDate().substring(0,4);

        return  thisDay + "/" + thisMonth + "/" + thisYear;
    }
}
