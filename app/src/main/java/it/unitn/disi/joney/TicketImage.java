package it.unitn.disi.joney;

public class TicketImage {
    String source;
    int ticketId;

    public TicketImage() {
    }

    public TicketImage(String source, int ticketID) {
        this.source = source;
        this.ticketId = ticketID;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }
}
