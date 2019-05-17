package it.unitn.disi.joney;

public class Queries {

    public static String CREATE_TABLE_USERS  = "CREATE TABLE IF NOT EXISTS Users(userID integer PRIMARY KEY AUTOINCREMENT NOT NULL, email varchar(100) NOT NULL UNIQUE, firstName varchar(50) NOT NULL, lastName varchar(50) NOT NULL, password varchar(255) NOT NULL);";
    public static String CREATE_TABLE_JOBS  = "CREATE TABLE IF NOT EXISTS Jobs(jobID integer PRIMARY KEY AUTOINCREMENT NOT NULL, title varchar(50) NOT NULL, description varchar(1000), completed boolean NOT NULL, latitude float NOT NULL, longitude float NOT NULL, categoryID integer, authorID integer NOT NULL, workerID integer, FOREIGN KEY (categoryID) REFERENCES JobCategory(jcID), FOREIGN KEY (authorID) REFERENCES Users(userID), FOREIGN KEY (workerID) REFERENCES User(userID));";
    public static String CREATE_TABLE_FEEDBACKS  = "CREATE TABLE IF NOT EXISTS Feedbacks(fbID integer PRIMARY KEY AUTOINCREMENT NOT NULL, rating int NOT NULL, comment varchar(1000), authorID integer NOT NULL, receiverID integer NOT NULL, FOREIGN KEY (authorID) REFERENCES Users(userID), FOREIGN KEY (receiverID) REFERENCES Users(userID));";
    public static String CREATE_TABLE_JOB_CANDIDATES  = "CREATE TABLE IF NOT EXISTS JobCandidates(jobID integer NOT NULL, candidateID integer NOT NULL, PRIMARY KEY (jobID,candidateID), FOREIGN KEY (jobID) REFERENCES Jobs(jobID), FOREIGN KEY (candidateID) REFERENCES Users(userID));";
    public static String CREATE_TABLE_JOB_CATEGORIES  = "CREATE TABLE IF NOT EXISTS JobCategories(jcID integer PRIMARY KEY AUTOINCREMENT NOT NULL, name varchar(30) NOT NULL, description varchar(255));";
    public static String CREATE_TABLE_JOB_IMAGES  = "CREATE TABLE IF NOT EXISTS JobImages(source varchar(255) NOT NULL, jobID integer NOT NULL, PRIMARY KEY (source), FOREIGN KEY (jobID) REFERENCES Jobs(jobID));";
    public static String CREATE_TABLE_TICKETS  = "CREATE TABLE IF NOT EXISTS Tickets(ticketID integer PRIMARY KEY AUTOINCREMENT NOT NULL, jobID integer NOT NULL, issue varchar(1000) NOT NULL, FOREIGN KEY (jobID) REFERENCES Jobs(jobID));";
    public static String CREATE_TABLE_TICKET_IMAGES  = "CREATE TABLE IF NOT EXISTS TicketImages(source varchar(255) NOT NULL, ticketID integer NOT NULL, PRIMARY KEY(source), FOREIGN KEY (ticketID) REFERENCES Tickets(ticketID));";

    public static String INSERT_USER = "INSERT INTO Users(email, firstName, lastName, password) VALUES(?, ?, ?, ?);";

    public static String FETCH_PASSWORD_BY_EMAIL = "SELECT password FROM Users WHERE email=?;";
}
