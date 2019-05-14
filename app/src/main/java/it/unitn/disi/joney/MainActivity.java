package it.unitn.disi.joney;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnSignup;

    SQLiteDatabase dbJoney;
    String dbName = "JoneyDB";
    String createTableUser  = "CREATE TABLE IF NOT EXISTS User(UserID int NOT NULL, Email varchar(100) NOT NULL UNIQUE, FirstName varchar(50) NOT NULL, LastName varchar(50) NOT NULL, Password varchar(255) NOT NULL, PRIMARY KEY(UserID));";
    String createTableJob  = "CREATE TABLE IF NOT EXISTS Job(JobID int NOT NULL, Title varchar(50) NOT NULL, Description varchar(1000), Completed boolean NOT NULL, Latitude float NOT NULL, Longitude float NOT NULL, Category int, AuthorID int NOT NULL, WorkerID int, PRIMARY KEY(JobID), FOREIGN KEY (Category) REFERENCES JobCategory(JCID), FOREIGN KEY (AuthorID) REFERENCES User(UserID), FOREIGN KEY (WorkerID) REFERENCES User(UserID));";
    String createTableFeedback  = "CREATE TABLE IF NOT EXISTS Feedback(FBID int NOT NULL, Rating int NOT NULL, Comment varchar(1000), AuthorID int NOT NULL, ReceiverID int NOT NULL, PRIMARY KEY(FBID), FOREIGN KEY (AuthorID) REFERENCES User(UserID), FOREIGN KEY (ReceiverID) REFERENCES User(UserID));";
    String createTableJobCandidate  = "CREATE TABLE IF NOT EXISTS JobCandidate(JobID int NOT NULL, CandidateID int NOT NULL, PRIMARY KEY (JobID,CandidateID), FOREIGN KEY (JobID) REFERENCES Job(JobID), FOREIGN KEY (CandidateID) REFERENCES User(UserID));";
    String createTableJobCategory  = "CREATE TABLE IF NOT EXISTS JobCategory(JCID int NOT NULL, Name varchar(30) NOT NULL, Description varchar(1000), PRIMARY KEY(JCID));";
    String createTableJobImage  = "CREATE TABLE IF NOT EXISTS JobImage(Source varchar(255) NOT NULL, JobID int NOT NULL, PRIMARY KEY (Source), FOREIGN KEY (JobID) REFERENCES Job(JobID));";
    String createTableTicket  = "CREATE TABLE IF NOT EXISTS Ticket(TicketID int NOT NULL, JobID int NOT NULL, Issue varchar(1000) NOT NULL, PRIMARY KEY (TicketID), FOREIGN KEY (JobID) REFERENCES Job(JobID));";
    String createTableTicketImage  = "CREATE TABLE IF NOT EXISTS TicketImage(Source varchar(255) NOT NULL, TicketID int NOT NULL, PRIMARY KEY(Source), FOREIGN KEY (TicketID) REFERENCES Ticket(TicketID));";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btn_signup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignup = new Intent(MainActivity.this,SignupActivity.class);
                startActivity(intSignup);
            }
        });

    }

    public void createDatabase()
    {
        dbJoney = openOrCreateDatabase(dbName,MODE_PRIVATE,null);
    }


}
