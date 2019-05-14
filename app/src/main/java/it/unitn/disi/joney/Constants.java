package it.unitn.disi.joney;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Constants {
    private static Constants instance;

    // Global variables
    private String dbName = "JoneyDB";
    private String createTableUser  = "CREATE TABLE IF NOT EXISTS User(UserID integer PRIMARY KEY AUTOINCREMENT NOT NULL, Email varchar(100) NOT NULL UNIQUE, FirstName varchar(50) NOT NULL, LastName varchar(50) NOT NULL, Password varchar(255) NOT NULL);";
    private String createTableJob  = "CREATE TABLE IF NOT EXISTS Job(JobID integer PRIMARY KEY AUTOINCREMENT NOT NULL, Title varchar(50) NOT NULL, Description varchar(1000), Completed boolean NOT NULL, Latitude float NOT NULL, Longitude float NOT NULL, Category integer, AuthorID integer NOT NULL, WorkerID integer, FOREIGN KEY (Category) REFERENCES JobCategory(JCID), FOREIGN KEY (AuthorID) REFERENCES User(UserID), FOREIGN KEY (WorkerID) REFERENCES User(UserID));";
    private String createTableFeedback  = "CREATE TABLE IF NOT EXISTS Feedback(FBID integer PRIMARY KEY AUTOINCREMENT NOT NULL, Rating int NOT NULL, Comment varchar(1000), AuthorID integer NOT NULL, ReceiverID integer NOT NULL, FOREIGN KEY (AuthorID) REFERENCES User(UserID), FOREIGN KEY (ReceiverID) REFERENCES User(UserID));";
    private String createTableJobCandidate  = "CREATE TABLE IF NOT EXISTS JobCandidate(JobID integer NOT NULL, CandidateID integer NOT NULL, PRIMARY KEY (JobID,CandidateID), FOREIGN KEY (JobID) REFERENCES Job(JobID), FOREIGN KEY (CandidateID) REFERENCES User(UserID));";
    private String createTableJobCategory  = "CREATE TABLE IF NOT EXISTS JobCategory(JCID integer PRIMARY KEY AUTOINCREMENT NOT NULL, Name varchar(30) NOT NULL, Description varchar(1000));";
    private String createTableJobImage  = "CREATE TABLE IF NOT EXISTS JobImage(Source varchar(255) NOT NULL, JobID integer NOT NULL, PRIMARY KEY (Source), FOREIGN KEY (JobID) REFERENCES Job(JobID));";
    private String createTableTicket  = "CREATE TABLE IF NOT EXISTS Ticket(TicketID integer PRIMARY KEY AUTOINCREMENT NOT NULL, JobID integer NOT NULL, Issue varchar(1000) NOT NULL, FOREIGN KEY (JobID) REFERENCES Job(JobID));";
    private String createTableTicketImage  = "CREATE TABLE IF NOT EXISTS TicketImage(Source varchar(255) NOT NULL, TicketID integer NOT NULL, PRIMARY KEY(Source), FOREIGN KEY (TicketID) REFERENCES Ticket(TicketID));";


    // Restrict the constructor from being instantiated
    private Constants(){}

    public String getDbName(){
        return this.dbName;
    }

    public String getCreateTableUser() {
        return createTableUser;
    }

    public String getCreateTableJob() {
        return createTableJob;
    }

    public String getCreateTableFeedback() {
        return createTableFeedback;
    }

    public String getCreateTableJobCandidate() {
        return createTableJobCandidate;
    }

    public String getCreateTableJobCategory() {
        return createTableJobCategory;
    }

    public String getCreateTableJobImage() {
        return createTableJobImage;
    }

    public String getCreateTableTicket() {
        return createTableTicket;
    }

    public String getCreateTableTicketImage() {
        return createTableTicketImage;
    }

    //hashing function
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static synchronized Constants getInstance(){
        if(instance==null){
            instance=new Constants();
        }
        return instance;
    }

}
