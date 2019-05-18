package it.unitn.disi.joney;

public class JobCandidate {
    int jobId;
    int candidateId;

    public JobCandidate() {
    }

    public JobCandidate(int jobID, int candidateID) {
        this.jobId = jobID;
        this.candidateId = candidateID;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }
}
