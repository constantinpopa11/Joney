package it.unitn.disi.joney;

public class JobImage {
    String source;
    int jobId;

    public JobImage() {
    }

    public JobImage(String source, int jobID) {
        this.source = source;
        this.jobId = jobID;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }
}
