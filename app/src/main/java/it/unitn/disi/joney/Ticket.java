package it.unitn.disi.joney;

public class Ticket {
    int id;
    int jobId;
    String issue;

    public Ticket() {
    }

    public Ticket(int ticketID, int jobID, String issue) {
        this.id = ticketID;
        this.jobId = jobID;
        this.issue = issue;
    }

    public Ticket(int jobId, String issue) {
        this.jobId = jobId;
        this.issue = issue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
