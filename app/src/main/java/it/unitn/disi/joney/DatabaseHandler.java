package it.unitn.disi.joney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "joneyDB";

    private static final String TABLE_USERS = "Users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_FIRST_NAME = "firstName";
    private static final String COL_USER_LAST_NAME = "lastName";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_DESCRIPTION = "description";
    private static final String COL_USER_PASSWORD = "password";

    private static final String TABLE_USER_PROFILE_IMAGES = "UserProfileImages";
    private static final String COL_USER_PROFILE_IMG_SOURCE = "source";
    private static final String COL_USER_PROFILE_IMG_USER_ID = "userId";

    private static final String TABLE_TICKET_IMAGES = "TicketImages";
    private static final String COL_TICKET_IMG_SOURCE = "source";
    private static final String COL_TICKET_IMG_TICKET_ID = "ticketId";

    private static final String TABLE_TICKETS = "Tickets";
    private static final String COL_TICKET_ID = "id";
    private static final String COL_TICKET_JOB_ID = "jobId";
    private static final String COL_TICKET_ISSUE = "issue";

    private static final String TABLE_JOB_IMAGES = "JobImages";
    private static final String COL_JOB_IMG_SOURCE = "source";
    private static final String COL_JOB_IMG_JOB_ID = "jobId";

    private static final String TABLE_JOB_CATEGORIES = "JobCategories";
    private static final String COL_JOB_CAT_ID = "id";
    private static final String COL_JOB_CAT_NAME = "name";
    private static final String COL_JOB_CAT_DESCRIPTION = "description";

    private static final String TABLE_JOB_CANDIDATES = "JobCandidates";
    private static final String COL_JOB_CANDIDATE_JOB_ID = "jobId";
    private static final String COL_JOB_CANDIDATE_CANDIDATE_ID = "candidateId";

    private static final String TABLE_JOBS = "Jobs";
    private static final String COL_JOB_ID = "id";
    private static final String COL_JOB_TITLE = "title";
    private static final String COL_JOB_DESCRIPTION = "description";
    private static final String COL_JOB_PAY = "pay";
    private static final String COL_JOB_STATUS = "status";
    private static final String COL_JOB_CREATED_AT = "createdAt";
    private static final String COL_JOB_LATITUDE = "latitude";
    private static final String COL_JOB_LONGITUDE = "longitude";
    private static final String COL_JOB_JOB_CATEGORY_ID = "categoryId";
    private static final String COL_JOB_AUTHOR_ID = "authorId";
    private static final String COL_JOB_WORKER_ID = "workerId";

    private static final String TABLE_FEEDBACKS = "Feedbacks";
    private static final String COL_FEEDBACK_ID = "id";
    private static final String COL_FEEDBACK_RATING = "rating";
    private static final String COL_FEEDBACK_COMMENT = "comment";
    private static final String COL_FEEDBACK_AUTHOR_ID = "authorId";
    private static final String COL_FEEDBACK_RECEIVER_ID = "receiverId";
    private static final String COL_FEEDBACK_JOB_ID = "jobId";
    private static final String COL_FEEDBACK_DATE = "date";

    private static final String TABLE_MESSAGES = "Messages";
    private static final String COL_MESSAGE_SENDER = "senderId";
    private static final String COL_MESSAGE_RECEIVER = "receiverId";
    private static final String COL_MESSAGE_DATE = "date";
    private static final String COL_MESSAGE_TEXT = "message";

    private static String CREATE_TABLE_USERS  = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS  +
            "(" + COL_USER_ID + " integer PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL_USER_EMAIL + " varchar(100) NOT NULL UNIQUE, " +
            COL_USER_FIRST_NAME + " varchar(50) NOT NULL, " +
            COL_USER_LAST_NAME + " varchar(50) NOT NULL, " +
            COL_USER_DESCRIPTION + " varchar(300), " +
            COL_USER_PASSWORD + " varchar(255) NOT NULL);";

    private static String CREATE_TABLE_USER_PROFILE_IMAGES  = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_PROFILE_IMAGES +
            "(" + COL_USER_PROFILE_IMG_SOURCE + " varchar(255) PRIMARY KEY NOT NULL, " +
            COL_USER_PROFILE_IMG_USER_ID + " integer NOT NULL, " +
            "FOREIGN KEY (" + COL_USER_PROFILE_IMG_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "));";

    private static String CREATE_TABLE_JOBS = "CREATE TABLE IF NOT EXISTS " + TABLE_JOBS +
            "(" + COL_JOB_ID + " integer PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL_JOB_TITLE + " varchar(100) NOT NULL, " +
            COL_JOB_DESCRIPTION + " varchar(1000), " +
            COL_JOB_PAY + " integer NOT NULL, " +
            COL_JOB_STATUS + " integer NOT NULL, " +
            COL_JOB_CREATED_AT + " date NOT NULL, " +
            COL_JOB_LATITUDE + " double NOT NULL, " +
            COL_JOB_LONGITUDE + " double NOT NULL, " +
            COL_JOB_JOB_CATEGORY_ID + " integer NOT NULL, " +
            COL_JOB_AUTHOR_ID + " integer NOT NULL, " +
            COL_JOB_WORKER_ID + " integer, " +
            "FOREIGN KEY (" + COL_JOB_JOB_CATEGORY_ID + ") REFERENCES " + TABLE_JOB_CATEGORIES + "(" + COL_JOB_CAT_ID + "), " +
            "FOREIGN KEY (" + COL_JOB_AUTHOR_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
            "FOREIGN KEY (" + COL_JOB_WORKER_ID + ") REFERENCES " + TABLE_USERS + " (" + COL_USER_ID + "));";

    private static String CREATE_TABLE_FEEDBACKS = "CREATE TABLE IF NOT EXISTS " + TABLE_FEEDBACKS +
            "(" + COL_FEEDBACK_ID + " integer PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL_FEEDBACK_RATING + " integer NOT NULL, " +
            COL_FEEDBACK_COMMENT + " varchar(1000), " +
            COL_FEEDBACK_DATE + " date NOT NULL, " +
            COL_FEEDBACK_AUTHOR_ID + " integer NOT NULL, " +
            COL_FEEDBACK_RECEIVER_ID + " integer NOT NULL, " +
            COL_FEEDBACK_JOB_ID + " integer NOT NULL, " +
            "FOREIGN KEY (" + COL_FEEDBACK_AUTHOR_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
            "FOREIGN KEY (" + COL_FEEDBACK_JOB_ID + ") REFERENCES " + TABLE_JOBS + "(" + COL_JOB_ID + "), " +
            "FOREIGN KEY (" + COL_FEEDBACK_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "));";

    private static String CREATE_TABLE_JOB_CANDIDATES  = "CREATE TABLE IF NOT EXISTS " + TABLE_JOB_CANDIDATES +
            "(" + COL_JOB_CANDIDATE_JOB_ID + " integer NOT NULL, " +
            COL_JOB_CANDIDATE_CANDIDATE_ID + " integer NOT NULL, " +
            "PRIMARY KEY (" + COL_JOB_CANDIDATE_JOB_ID + ", " + COL_JOB_CANDIDATE_CANDIDATE_ID + "), " +
            "FOREIGN KEY (" + COL_JOB_CANDIDATE_JOB_ID + ") REFERENCES " + TABLE_JOBS + "(" + COL_JOB_ID + "), " +
            "FOREIGN KEY (" + COL_JOB_CANDIDATE_CANDIDATE_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID +"));";

    private static String CREATE_TABLE_JOB_CATEGORIES  = "CREATE TABLE IF NOT EXISTS " + TABLE_JOB_CATEGORIES +
            "(" + COL_JOB_CAT_ID + " integer PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL_JOB_CAT_NAME + " varchar(50) NOT NULL, " +
            COL_JOB_CAT_DESCRIPTION + " varchar(255));";

    private static String CREATE_TABLE_JOB_IMAGES  = "CREATE TABLE IF NOT EXISTS " + TABLE_JOB_IMAGES +
            "(" + COL_JOB_IMG_SOURCE + " varchar(255) PRIMARY KEY NOT NULL, " +
            COL_JOB_IMG_JOB_ID + " integer NOT NULL, " +
            "FOREIGN KEY (" + COL_JOB_IMG_JOB_ID + ") REFERENCES " + TABLE_JOBS + "(" + COL_JOB_ID + "));";


    private static String CREATE_TABLE_TICKETS  = "CREATE TABLE IF NOT EXISTS " + TABLE_TICKETS +
            "(" + COL_TICKET_ID + " integer PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL_TICKET_JOB_ID + " integer NOT NULL, " +
            COL_TICKET_ISSUE + " varchar(1000) NOT NULL, " +
            "FOREIGN KEY (" + COL_TICKET_JOB_ID + ") REFERENCES " + TABLE_JOBS +  "(" + COL_JOB_ID + "));";


    private static String CREATE_TABLE_TICKET_IMAGES  = "CREATE TABLE IF NOT EXISTS " + TABLE_TICKET_IMAGES +
            "(" + COL_TICKET_IMG_SOURCE + " varchar(255) PRIMARY KEY NOT NULL, " +
            COL_TICKET_IMG_TICKET_ID + " integer NOT NULL, " +
            "FOREIGN KEY (" + COL_TICKET_IMG_TICKET_ID + ") REFERENCES " + TABLE_TICKETS + "(" + COL_TICKET_ID + "));";

    private static String CREATE_TABLE_MESSAGES  = "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGES +
            "(" + COL_MESSAGE_SENDER + " integer NOT NULL, " +
            COL_MESSAGE_RECEIVER + " integer NOT NULL, " +
            COL_MESSAGE_DATE + " date NOT NULL, " +
            COL_MESSAGE_TEXT + " varchar(100));";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_USER_PROFILE_IMAGES);
        db.execSQL(CREATE_TABLE_JOBS);
        db.execSQL(CREATE_TABLE_JOB_CANDIDATES);
        db.execSQL(CREATE_TABLE_JOB_CATEGORIES);
        db.execSQL(CREATE_TABLE_JOB_IMAGES);
        db.execSQL(CREATE_TABLE_TICKETS);
        db.execSQL(CREATE_TABLE_TICKET_IMAGES);
        db.execSQL(CREATE_TABLE_FEEDBACKS);
        db.execSQL(CREATE_TABLE_MESSAGES);

        JobCategory jobCategory = new JobCategory("Teaching and Tutoring", null);
        addJobCategory(db, jobCategory);

        jobCategory = new JobCategory("Creative Design", null);
        addJobCategory(db, jobCategory);

        jobCategory = new JobCategory("Mobile App Development", null);
        addJobCategory(db, jobCategory);

        jobCategory = new JobCategory("Gardening", null);
        addJobCategory(db, jobCategory);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOB_CANDIDATES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOB_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOB_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKET_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);

        // Create tables again
        onCreate(db);
    }

    // code to add the new user
    void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USER_FIRST_NAME, user.getFirstName());
        values.put(COL_USER_LAST_NAME, user.getLastName());
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_DESCRIPTION, "Hello, I'm new on Joney!");
        values.put(COL_USER_PASSWORD, user.getPassword());

        // Inserting Row
        db.insert(TABLE_USERS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    void addJobCandidate(JobCandidate jobCandidate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_JOB_CANDIDATE_CANDIDATE_ID, jobCandidate.getCandidateId());
        values.put(COL_JOB_CANDIDATE_JOB_ID, jobCandidate.getJobId());

        // Inserting Row
        db.insert(TABLE_JOB_CANDIDATES, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    void removeJobCandidate(int candidateId, int jobId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_JOB_CANDIDATES,
                COL_JOB_CANDIDATE_CANDIDATE_ID + "=? AND " + COL_JOB_CANDIDATE_JOB_ID + "=?",
                new String[] { Integer.toString(candidateId), Integer.toString(jobId) });

        db.close(); // Closing database connection
    }

    public List<JobCandidate> getCandidatesByJobId(int jobId){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_JOB_CANDIDATES,
                new String[] { COL_JOB_CANDIDATE_CANDIDATE_ID, COL_JOB_CANDIDATE_JOB_ID },
                COL_JOB_IMG_JOB_ID + "=?",
                new String[] { Integer.toString(jobId) },
                null,
                null,
                null,
                null);

        List<JobCandidate> jobCandidates = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do{
                JobCandidate jobCandidate = new JobCandidate();
                jobCandidate.setCandidateId(cursor.getInt(0));
                jobCandidate.setJobId(cursor.getInt(1));
                jobCandidates.add(jobCandidate);
            } while(cursor.moveToNext());
        }

        return jobCandidates;
    }

    User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[] { COL_USER_ID, COL_USER_PASSWORD },
                COL_USER_EMAIL + "=?",
                new String[] { email },
                null,
                null,
                null,
                null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setPassword(cursor.getString(1));;
        }
        return user;
    }

    User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[] { COL_USER_FIRST_NAME, COL_USER_LAST_NAME, COL_USER_EMAIL, COL_USER_DESCRIPTION },
                COL_USER_ID + "=?",
                new String[] { Integer.toString(userId) },
                null,
                null,
                null,
                null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(userId);
            user.setFirstName(cursor.getString(0));
            user.setLastName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setDescription(cursor.getString(3));
        }
        return user;
    }

    // code to add the new job
    int addJob(Job job) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_JOB_TITLE, job.getTitle());
        values.put(COL_JOB_DESCRIPTION, job.getDescription());
        values.put(COL_JOB_PAY, job.getPay());
        values.put(COL_JOB_STATUS, job.getStatus());
        values.put(COL_JOB_CREATED_AT, job.getCreatedAt());
        values.put(COL_JOB_LATITUDE, job.getLatitude());
        values.put(COL_JOB_LONGITUDE, job.getLongitude());
        values.put(COL_JOB_JOB_CATEGORY_ID, job.getCategoryId());
        values.put(COL_JOB_AUTHOR_ID, job.getAuthorId());
        values.put(COL_JOB_WORKER_ID, job.getWorkerId());

        // Inserting Row
        int jobId = (int) db.insert(TABLE_JOBS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection

        return jobId;
    }

    void addJobCategory(SQLiteDatabase db, JobCategory jobCategory) {

        ContentValues values = new ContentValues();
        values.put(COL_JOB_CAT_NAME, jobCategory.getName());
        values.put(COL_JOB_CAT_DESCRIPTION, jobCategory.getDescription());

        // Inserting Row
        db.insert(TABLE_JOB_CATEGORIES, null, values);
    }

    int addTicket(Ticket ticket){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TICKET_JOB_ID,ticket.getJobId());
        values.put(COL_TICKET_ISSUE, ticket.getIssue());

        int ticketId = (int) db.insert(TABLE_TICKETS,null,values);
        db.close();

        return ticketId;
    }

    void addTicketImage(TicketImage ticketImage){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TICKET_IMG_SOURCE, ticketImage.getSource());
        values.put(COL_TICKET_IMG_TICKET_ID, ticketImage.getTicketId());

        db.insert(TABLE_TICKET_IMAGES,null,values);
        db.close();
    }

    void addJobImage(JobImage jobImage){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_JOB_IMG_SOURCE, jobImage.getSource());
        values.put(COL_JOB_IMG_JOB_ID, jobImage.getJobId());

        db.insert(TABLE_JOB_IMAGES,null,values);
        db.close();
    }

    void addUserProfileImage(UserProfileImage userProfileImage){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USER_PROFILE_IMG_SOURCE, userProfileImage.getSource());
        values.put(COL_USER_PROFILE_IMG_USER_ID, userProfileImage.getUserId());

        db.insert(TABLE_USER_PROFILE_IMAGES,null,values);
        db.close();
    }

    void addMessage(Message message){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_MESSAGE_SENDER, message.getSenderId());
        values.put(COL_MESSAGE_RECEIVER, message.getReceiverId());
        values.put(COL_MESSAGE_DATE, message.getDate());
        values.put(COL_MESSAGE_TEXT, message.getMessage());

        db.insert(TABLE_MESSAGES,null,values);
        db.close();
        //Log.d("Message","inserted");
    }

    void addFeedback(Feedback feedback){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_FEEDBACK_RATING, feedback.getRating());
        values.put(COL_FEEDBACK_COMMENT, feedback.getComment());
        values.put(COL_FEEDBACK_DATE, feedback.getDate());
        values.put(COL_FEEDBACK_JOB_ID, feedback.getJobId());
        values.put(COL_FEEDBACK_AUTHOR_ID, feedback.getAuthorId());
        values.put(COL_FEEDBACK_RECEIVER_ID, feedback.getReceiverId());

        db.insert(TABLE_FEEDBACKS,null,values);
        db.close();
        //Log.d("Message","inserted");
    }

    void removeUserProfileImage(int userId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_USER_PROFILE_IMAGES,COL_USER_PROFILE_IMG_USER_ID + "=?",new String[] { Integer.toString(userId) });
    }


    Job getJobById(int jobId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_JOBS,
                new String[] { COL_JOB_TITLE, COL_JOB_DESCRIPTION,
                        COL_JOB_PAY, COL_JOB_STATUS, COL_JOB_CREATED_AT,
                        COL_JOB_LATITUDE, COL_JOB_LONGITUDE, COL_JOB_JOB_CATEGORY_ID,
                        COL_JOB_AUTHOR_ID, COL_JOB_WORKER_ID },
                COL_JOB_ID + "=?",
                new String[] { Integer.toString(jobId) },
                null,
                null,
                null,
                null);

        Job job = null;
        if (cursor != null && cursor.moveToFirst()) {
            job = new Job();
            job.setId(jobId);
            job.setTitle(cursor.getString(0));
            job.setDescription(cursor.getString(1));
            job.setPay(cursor.getInt(2));
            job.setStatus(cursor.getInt(3));
            job.setCreatedAt(cursor.getString(4));
            job.setLatitude(cursor.getDouble(5));
            job.setLongitude(cursor.getDouble(6));
            job.setCategoryId(cursor.getInt(7));
            job.setAuthorId(cursor.getInt(8));
            job.setWorkerId(cursor.getInt(9));
        }
        return job;
    }


    //code to get all job categories
    public List<JobCategory> getAllJobCategories() {
        List<JobCategory> jobCategoryList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_JOB_CATEGORIES,
                new String[] { COL_JOB_CAT_ID, COL_JOB_CAT_NAME },
                null,
                null,
                null,
                null,
                COL_JOB_CAT_NAME,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                JobCategory jobCategory = new JobCategory();
                jobCategory.setId(cursor.getInt(0));
                jobCategory.setName(cursor.getString(1));
                // Adding contact to list
                jobCategoryList.add(jobCategory);
            } while (cursor.moveToNext());
        }

        // return contact list
        return jobCategoryList;
    }

    public void updateJobStatus(int jobId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(COL_JOB_STATUS, status);

        db.update(TABLE_JOBS, newValues, COL_JOB_ID + "=?", new String[] { String.valueOf(jobId) });
        db.close();
    }

    public void updateJobWorker(int jobId, int workerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(COL_JOB_WORKER_ID, workerId);

        db.update(TABLE_JOBS, newValues, COL_JOB_ID + "=?", new String[] { String.valueOf(jobId) });
        db.close();
    }

    //code to get all jobs from user
    public List<Job> getAllUserJobs(int userId) {
        List<Job> jobList = new ArrayList<>();

        jobList.addAll(getUserCompletedJobs(userId));
        jobList.addAll(getOwnCompletedJobs(userId));
        jobList.addAll(getAcceptedUserPendingJobs(userId));

        return jobList;
    }

    //code to get all user's posted jobs
    public List<Job> getUserPostedJobs(int currentUserId) {
        List<Job> postedJobs = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_JOBS,
                new String[] {COL_JOB_ID, COL_JOB_TITLE, COL_JOB_DESCRIPTION, COL_JOB_PAY,
                        COL_JOB_JOB_CATEGORY_ID, COL_JOB_STATUS, COL_JOB_CREATED_AT,
                        COL_JOB_LATITUDE, COL_JOB_LONGITUDE, COL_JOB_AUTHOR_ID, COL_JOB_WORKER_ID},
                COL_JOB_AUTHOR_ID + "=?",
                new String[] {Integer.toString(currentUserId)},
                null,
                null,
                null,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Job job = new Job();
                job.setId(cursor.getInt(0));
                job.setTitle(cursor.getString(1));
                job.setDescription(cursor.getString(2));
                job.setPay(cursor.getInt(3));
                job.setCategoryId(cursor.getInt(4));
                job.setStatus(cursor.getInt(5));
                job.setCreatedAt(cursor.getString(6));
                job.setLatitude(cursor.getDouble(7));
                job.setLongitude(cursor.getDouble(8));
                job.setAuthorId(cursor.getInt(9));
                job.setWorkerId(cursor.getInt(10));
                postedJobs.add(job);

            } while (cursor.moveToNext());
        }

        // return job list
        return postedJobs;
    }

    //code to get all user's pending jobs
    public List<Job> getUserPendingJobs(int currentUserId) {
        List<Job> pendingJobs = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_JOBS + ", " + TABLE_JOB_CANDIDATES,
                new String[] {COL_JOB_ID, COL_JOB_TITLE, COL_JOB_DESCRIPTION, COL_JOB_PAY,
                        COL_JOB_JOB_CATEGORY_ID, COL_JOB_STATUS, COL_JOB_CREATED_AT,
                        COL_JOB_LATITUDE, COL_JOB_LONGITUDE, COL_JOB_AUTHOR_ID, COL_JOB_WORKER_ID},
                "(" +
                        COL_JOB_WORKER_ID + "=? " +
                        "OR " + COL_JOB_CANDIDATE_CANDIDATE_ID + "=?" +
                        ") AND " + COL_JOB_STATUS + "<" + Constants.JOB_STATUS_COMPLETED +
                        " AND " + COL_JOB_ID + "=" + COL_JOB_CANDIDATE_JOB_ID,
                new String[] {Integer.toString(currentUserId), Integer.toString(currentUserId)},
                null,
                null,
                COL_JOB_STATUS,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Job job = new Job();
                job.setId(cursor.getInt(0));
                job.setTitle(cursor.getString(1));
                job.setDescription(cursor.getString(2));
                job.setPay(cursor.getInt(3));
                job.setCategoryId(cursor.getInt(4));
                job.setStatus(cursor.getInt(5));
                job.setCreatedAt(cursor.getString(6));
                job.setLatitude(cursor.getDouble(7));
                job.setLongitude(cursor.getDouble(8));
                job.setAuthorId(cursor.getInt(9));
                job.setWorkerId(cursor.getInt(10));
                pendingJobs.add(job);

            } while (cursor.moveToNext());
        }

        // return job list
        return pendingJobs;
    }

    //code to accepted user pending jobs
    public List<Job> getAcceptedUserPendingJobs(int currentUserId) {
        List<Job> pendingJobs = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_JOBS + ", " + TABLE_JOB_CANDIDATES,
                new String[] {COL_JOB_ID, COL_JOB_TITLE, COL_JOB_DESCRIPTION, COL_JOB_PAY,
                        COL_JOB_JOB_CATEGORY_ID, COL_JOB_STATUS, COL_JOB_CREATED_AT,
                        COL_JOB_LATITUDE, COL_JOB_LONGITUDE, COL_JOB_AUTHOR_ID, COL_JOB_WORKER_ID},
                "(" +
                        COL_JOB_WORKER_ID + "=? " +
                        ") AND " + COL_JOB_STATUS + "<" + Constants.JOB_STATUS_COMPLETED +
                        " AND " + COL_JOB_ID + "=" + COL_JOB_CANDIDATE_JOB_ID,
                new String[] {Integer.toString(currentUserId)},
                null,
                null,
                COL_JOB_STATUS,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Job job = new Job();
                job.setId(cursor.getInt(0));
                job.setTitle(cursor.getString(1));
                job.setDescription(cursor.getString(2));
                job.setPay(cursor.getInt(3));
                job.setCategoryId(cursor.getInt(4));
                job.setStatus(cursor.getInt(5));
                job.setCreatedAt(cursor.getString(6));
                job.setLatitude(cursor.getDouble(7));
                job.setLongitude(cursor.getDouble(8));
                job.setAuthorId(cursor.getInt(9));
                job.setWorkerId(cursor.getInt(10));
                pendingJobs.add(job);

            } while (cursor.moveToNext());
        }

        // return job list
        return pendingJobs;
    }

    //code to get all user's posted jobs
    public List<Job> getUserCompletedJobs(int currentUserId) {
        List<Job> completedJobs = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_JOBS,
                new String[] {COL_JOB_ID, COL_JOB_TITLE, COL_JOB_DESCRIPTION, COL_JOB_PAY,
                        COL_JOB_JOB_CATEGORY_ID, COL_JOB_STATUS, COL_JOB_CREATED_AT,
                        COL_JOB_LATITUDE, COL_JOB_LONGITUDE, COL_JOB_AUTHOR_ID, COL_JOB_WORKER_ID},
                COL_JOB_WORKER_ID + "=? AND " + COL_JOB_STATUS + "=" + Constants.JOB_STATUS_COMPLETED,
                new String[] {Integer.toString(currentUserId)},
                null,
                null,
                null,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Job job = new Job();
                job.setId(cursor.getInt(0));
                job.setTitle(cursor.getString(1));
                job.setDescription(cursor.getString(2));
                job.setPay(cursor.getInt(3));
                job.setCategoryId(cursor.getInt(4));
                job.setStatus(cursor.getInt(5));
                job.setCreatedAt(cursor.getString(6));
                job.setLatitude(cursor.getDouble(7));
                job.setLongitude(cursor.getDouble(8));
                job.setAuthorId(cursor.getInt(9));
                job.setWorkerId(cursor.getInt(10));
                completedJobs.add(job);

            } while (cursor.moveToNext());
        }

        // return job list
        return completedJobs;
    }

    //code to get own completed job
    public List<Job> getOwnCompletedJobs(int currentUserId) {
        List<Job> completedJobs = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_JOBS,
                new String[] {COL_JOB_ID, COL_JOB_TITLE, COL_JOB_DESCRIPTION, COL_JOB_PAY,
                        COL_JOB_JOB_CATEGORY_ID, COL_JOB_STATUS, COL_JOB_CREATED_AT,
                        COL_JOB_LATITUDE, COL_JOB_LONGITUDE, COL_JOB_AUTHOR_ID, COL_JOB_WORKER_ID},
                COL_JOB_AUTHOR_ID + "=? AND " + COL_JOB_STATUS + "=" + Constants.JOB_STATUS_COMPLETED,
                new String[] {Integer.toString(currentUserId)},
                null,
                null,
                null,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Job job = new Job();
                job.setId(cursor.getInt(0));
                job.setTitle(cursor.getString(1));
                job.setDescription(cursor.getString(2));
                job.setPay(cursor.getInt(3));
                job.setCategoryId(cursor.getInt(4));
                job.setStatus(cursor.getInt(5));
                job.setCreatedAt(cursor.getString(6));
                job.setLatitude(cursor.getDouble(7));
                job.setLongitude(cursor.getDouble(8));
                job.setAuthorId(cursor.getInt(9));
                job.setWorkerId(cursor.getInt(10));
                completedJobs.add(job);

            } while (cursor.moveToNext());
        }

        // return job list
        return completedJobs;
    }


    //code to get job results based on search filters
    public List<Job> getJobsBySearchFilters(int currentUserId, Pair<Double, Double> location, int distance, int jobCategory, int minPay, int maxPay) {
        List<Job> eligibleJobs = new ArrayList<>();

        String whereClause = COL_JOB_AUTHOR_ID + "<>? AND " +
                COL_JOB_STATUS + "=" + Constants.JOB_STATUS_AWAITING_CANDIDATES + "  AND " +
                COL_JOB_PAY + ">=? " +
                "AND " + COL_JOB_PAY + "<=? " +
                "AND " + COL_JOB_JOB_CATEGORY_ID;

        if(jobCategory == Constants.INVALID_ITEM_VALUE)
            whereClause = whereClause.concat(">?");
        else
            whereClause = whereClause.concat("=?");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_JOBS,
                new String[] {COL_JOB_ID, COL_JOB_TITLE, COL_JOB_DESCRIPTION, COL_JOB_PAY,
                        COL_JOB_JOB_CATEGORY_ID, COL_JOB_STATUS, COL_JOB_CREATED_AT,
                        COL_JOB_LATITUDE, COL_JOB_LONGITUDE, COL_JOB_AUTHOR_ID, COL_JOB_WORKER_ID},
                whereClause,
                new String[] {Integer.toString(currentUserId), Integer.toString(minPay), Integer.toString(maxPay), Integer.toString(jobCategory)},
                null,
                null,
                null,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                boolean alreadyCandidate = false;
                List<JobCandidate> jobCandidates = getCandidatesByJobId(cursor.getInt(0));
                for(JobCandidate jobCandidate : jobCandidates) {
                    if(jobCandidate.getCandidateId() == currentUserId)
                        alreadyCandidate = true;
                }

                if(!alreadyCandidate) {
                    double lat = cursor.getDouble(7);
                    double lon = cursor.getDouble(8);
                    int tempDistance = (int) Utils.getDistance(lat, lon, location.first, location.second);

                    if(tempDistance < distance) {
                        Job job = new Job();
                        job.setId(cursor.getInt(0));
                        job.setTitle(cursor.getString(1));
                        job.setDescription(cursor.getString(2));
                        job.setPay(cursor.getInt(3));
                        job.setCategoryId(cursor.getInt(4));
                        job.setStatus(cursor.getInt(5));
                        job.setCreatedAt(cursor.getString(6));
                        job.setLatitude(cursor.getDouble(7));
                        job.setLongitude(cursor.getDouble(8));
                        job.setAuthorId(cursor.getInt(9));
                        job.setWorkerId(cursor.getInt(10));
                        eligibleJobs.add(job);
                    }
                }

            } while (cursor.moveToNext());
        }

        // return job list
        return eligibleJobs;
    }

    public ArrayList<Message> getUserMessages(int currentUserId,int receiverId) {
        ArrayList<Message> messages = new ArrayList<Message>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_MESSAGES,
                new String[] {COL_MESSAGE_SENDER, COL_MESSAGE_RECEIVER, COL_MESSAGE_DATE,
                        COL_MESSAGE_TEXT},
                COL_MESSAGE_SENDER + "=? AND " + COL_MESSAGE_RECEIVER + "=?"
                + " OR " + COL_MESSAGE_SENDER + "=? AND " + COL_MESSAGE_RECEIVER + "=?",
                new String[] {Integer.toString(currentUserId),Integer.toString(receiverId),
                        Integer.toString(receiverId),Integer.toString(currentUserId)},
                null,
                null,
                COL_MESSAGE_DATE,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setSenderId(cursor.getInt(0));
                message.setReceiverId(cursor.getInt(1));
                message.setDate(cursor.getString(2));
                message.setMessage(cursor.getString(3));
                messages.add(message);
                //Log.d("Going","trough");
            } while (cursor.moveToNext());
        }

        // return messages list
        return messages;
    }

    public boolean isFeedbackGiven(int jobId, int authorId) {
        boolean isGiven = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_FEEDBACKS,
                new String[] { COL_FEEDBACK_JOB_ID, COL_FEEDBACK_COMMENT, COL_FEEDBACK_AUTHOR_ID},
                COL_FEEDBACK_AUTHOR_ID + "=? AND " + COL_FEEDBACK_JOB_ID + "=?",
                new String[] { String.valueOf(authorId),String.valueOf(jobId) },
                null,
                null,
                null,
                null);

        Feedback feedback = null;
        if (cursor != null && cursor.moveToFirst()) {
            feedback = new Feedback();
            feedback.setJobId(cursor.getInt(0));
            feedback.setComment(cursor.getString(1));
            feedback.setAuthorId(cursor.getInt(2));
        }
        if (feedback != null)
            isGiven = true;
        return isGiven;
    }

    public ArrayList<Feedback> getUserFeedbacks(int currentUserId) {
        ArrayList<Feedback> feedbacks = new ArrayList<Feedback>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_FEEDBACKS,
                new String[] {COL_FEEDBACK_COMMENT, COL_FEEDBACK_DATE, COL_FEEDBACK_RATING,
                        COL_FEEDBACK_AUTHOR_ID, COL_FEEDBACK_JOB_ID},
                COL_FEEDBACK_RECEIVER_ID + "=?",
                new String[] {Integer.toString(currentUserId)},
                null,
                null,
                COL_FEEDBACK_DATE,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Feedback feedback = new Feedback();
                feedback.setComment(cursor.getString(0));
                feedback.setDate(cursor.getString(1));
                feedback.setRating(cursor.getInt(2));
                feedback.setAuthorId(cursor.getInt(3));
                feedback.setJobId(cursor.getInt(4));
                feedbacks.add(feedback);
                //Log.d("Going","trough");
            } while (cursor.moveToNext());
        }

        // return feedbacks list
        return feedbacks;
    }

    public Feedback getUserFeedbackForJob(int jobId, int authorId){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FEEDBACKS,
                new String[] {COL_FEEDBACK_ID, COL_FEEDBACK_RATING, COL_FEEDBACK_COMMENT, COL_FEEDBACK_DATE,
                        COL_FEEDBACK_JOB_ID, COL_FEEDBACK_AUTHOR_ID, COL_FEEDBACK_RECEIVER_ID
                },
                COL_FEEDBACK_JOB_ID + "=? AND " + COL_FEEDBACK_AUTHOR_ID + "=?",
                new String[] { Integer.toString(jobId), Integer.toString(authorId) },
                null,
                null,
                null,
                null);

        Feedback feedback = null;
        if (cursor != null && cursor.moveToFirst()) {
            feedback = new Feedback();
            feedback.setId(cursor.getInt(0));
            feedback.setRating(cursor.getInt(1));
            feedback.setComment(cursor.getString(2));
            feedback.setDate(cursor.getString(3));
            feedback.setJobId(cursor.getInt(4));
            feedback.setAuthorId(cursor.getInt(5));
            feedback.setReceiverId(cursor.getInt(6));
        }
        return feedback;
    }



    JobCategory getJobCategoryById(int jobCategoryId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_JOB_CATEGORIES,
                new String[] { COL_JOB_CAT_NAME, COL_JOB_CAT_DESCRIPTION },
                COL_JOB_CAT_ID + "=?",
                new String[] { Integer.toString(jobCategoryId) },
                null,
                null,
                null,
                null);

        JobCategory jobCategory = null;
        if (cursor != null && cursor.moveToFirst()) {
            jobCategory = new JobCategory();
            jobCategory.setId(jobCategoryId);
            jobCategory.setName(cursor.getString(0));
            jobCategory.setDescription(cursor.getString(1));;
        }
        return jobCategory;
    }



    public void updatePassword(String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(COL_USER_PASSWORD, password);

        db.update(TABLE_USERS, newValues, "email=?", new String[] { String.valueOf(email) });
    }

    public void updateUserDescription(int id,String description)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(COL_USER_DESCRIPTION, description);

        db.update(TABLE_USERS, newValues, "id=?", new String[] { String.valueOf(id) });
    }

    public List<JobImage> getJobImages(int jobId){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_JOB_IMAGES,
                new String[] { COL_JOB_IMG_SOURCE, COL_JOB_IMG_JOB_ID },
                COL_JOB_IMG_JOB_ID + "=?",
                new String[] { Integer.toString(jobId) },
                null,
                null,
                null,
                null);

        List<JobImage> jobImages = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do{
                JobImage jobImage = new JobImage();
                jobImage.setSource(cursor.getString(0));
                jobImage.setJobId(cursor.getInt(1));
                jobImages.add(jobImage);
            } while(cursor.moveToNext());
        }

        return jobImages;
    }

    public void updateUserProfileImage(int userId,String source)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(COL_USER_PROFILE_IMG_SOURCE, source);

        db.update(TABLE_USER_PROFILE_IMAGES, newValues, "userId=?", new String[] { String.valueOf(userId) });
    }

    public UserProfileImage getUserProfileImage(int userId){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER_PROFILE_IMAGES,
                new String[] { COL_USER_PROFILE_IMG_SOURCE, COL_USER_PROFILE_IMG_USER_ID },
                COL_USER_PROFILE_IMG_USER_ID + "=?",
                new String[] { Integer.toString(userId) },
                null,
                null,
                null,
                null);

        UserProfileImage userProfileImage = null;
        if (cursor != null && cursor.moveToFirst()) {
            userProfileImage = new UserProfileImage();
            userProfileImage.setSource(cursor.getString(0));
            userProfileImage.setUserId(cursor.getInt(1));;
        }
        return userProfileImage;
    }

    //SOTTO CI SONO ESEMPI DI QUERY DA SEGUIRE PER SCRIVERE FUTURE QUERY

    /*// code to add the new contact
    void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return contact;
    }
    */

    /*

    // code to update the single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }*/

}