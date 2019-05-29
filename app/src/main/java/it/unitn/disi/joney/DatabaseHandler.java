package it.unitn.disi.joney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
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
    private static final String COL_USER_PASSWORD = "password";

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
    private static final String COL_JOB_COMPLETED = "completed";
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

    private static String CREATE_TABLE_USERS  = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS  +
            "(" + COL_USER_ID + " integer PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL_USER_EMAIL + " varchar(100) NOT NULL UNIQUE, " +
            COL_USER_FIRST_NAME + " varchar(50) NOT NULL, " +
            COL_USER_LAST_NAME + " varchar(50) NOT NULL, " +
            COL_USER_PASSWORD + " varchar(255) NOT NULL);";

    private static String CREATE_TABLE_JOBS = "CREATE TABLE IF NOT EXISTS " + TABLE_JOBS +
            "(" + COL_JOB_ID + " integer PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL_JOB_TITLE + " varchar(100) NOT NULL, " +
            COL_JOB_DESCRIPTION + " varchar(1000), " +
            COL_JOB_COMPLETED + " boolean NOT NULL DEFAULT(0), " +
            COL_JOB_CREATED_AT + " date NOT NULL, " +
            COL_JOB_LATITUDE + " float NOT NULL, " +
            COL_JOB_LONGITUDE + " float NOT NULL, " +
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
            COL_FEEDBACK_AUTHOR_ID + " integer NOT NULL, " +
            COL_FEEDBACK_RECEIVER_ID + " integer NOT NULL, " +
            "FOREIGN KEY (" + COL_FEEDBACK_AUTHOR_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
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


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_JOBS);
        db.execSQL(CREATE_TABLE_JOB_CANDIDATES);
        db.execSQL(CREATE_TABLE_JOB_CATEGORIES);
        db.execSQL(CREATE_TABLE_JOB_IMAGES);
        db.execSQL(CREATE_TABLE_TICKETS);
        db.execSQL(CREATE_TABLE_TICKET_IMAGES);
        db.execSQL(CREATE_TABLE_FEEDBACKS);

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOB_CANDIDATES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOB_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOB_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKET_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACKS);

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
        values.put(COL_USER_PASSWORD, user.getPassword());

        // Inserting Row
        db.insert(TABLE_USERS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
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
                new String[] { COL_USER_FIRST_NAME, COL_USER_LAST_NAME, COL_USER_EMAIL },
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
        }
        return user;
    }

    // code to add the new job
    void addJob(Job job) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_JOB_TITLE, job.getTitle());
        values.put(COL_JOB_DESCRIPTION, job.getDescription());
        values.put(COL_JOB_COMPLETED, job.isCompleted());
        values.put(COL_JOB_CREATED_AT, job.getCreatedAt());
        values.put(COL_JOB_LATITUDE, job.getLatitude());
        values.put(COL_JOB_LONGITUDE, job.getLongitude());
        values.put(COL_JOB_JOB_CATEGORY_ID, job.getCategoryId());
        values.put(COL_JOB_AUTHOR_ID, job.getAuthorId());
        values.put(COL_JOB_WORKER_ID, job.getWorkerId());

        // Inserting Row
        db.insert(TABLE_JOBS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    void addJobCategory(SQLiteDatabase db, JobCategory jobCategory) {

        ContentValues values = new ContentValues();
        values.put(COL_JOB_CAT_NAME, jobCategory.getName());
        values.put(COL_JOB_CAT_DESCRIPTION, jobCategory.getDescription());

        // Inserting Row
        db.insert(TABLE_JOB_CATEGORIES, null, values);
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

    //code to get all user's posted jobs
    public List<Job> getUserPostedJobs(int currentUserId) {
        List<Job> postedJobs = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_JOBS,
                new String[] {COL_JOB_ID, COL_JOB_TITLE, COL_JOB_DESCRIPTION,
                        COL_JOB_JOB_CATEGORY_ID, COL_JOB_COMPLETED, COL_JOB_CREATED_AT,
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
                job.setCategoryId(cursor.getInt(3));
                job.setCompleted(Boolean.parseBoolean(cursor.getString(4)));
                job.setCreatedAt(cursor.getString(5));
                job.setLatitude(cursor.getFloat(6));
                job.setLongitude(cursor.getFloat(7));
                job.setAuthorId(cursor.getInt(8));
                job.setWorkerId(cursor.getInt(9));
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
                new String[] {COL_JOB_ID, COL_JOB_TITLE, COL_JOB_DESCRIPTION,
                        COL_JOB_JOB_CATEGORY_ID, COL_JOB_COMPLETED, COL_JOB_CREATED_AT,
                        COL_JOB_LATITUDE, COL_JOB_LONGITUDE, COL_JOB_AUTHOR_ID, COL_JOB_WORKER_ID},
                "(" +
                        "(" + COL_JOB_WORKER_ID + "=?) " +
                        "OR (" + COL_JOB_CANDIDATE_CANDIDATE_ID + "=? AND " + COL_JOB_ID + "=" + COL_JOB_CANDIDATE_JOB_ID + ")" +
                        ") AND " + COL_JOB_COMPLETED + "=0",
                new String[] {Integer.toString(currentUserId), Integer.toString(currentUserId)},
                null,
                null,
                COL_JOB_COMPLETED,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Job job = new Job();
                job.setId(cursor.getInt(0));
                job.setTitle(cursor.getString(1));
                job.setDescription(cursor.getString(2));
                job.setCategoryId(cursor.getInt(3));
                job.setCompleted(Boolean.parseBoolean(cursor.getString(4)));
                job.setCreatedAt(cursor.getString(5));
                job.setLatitude(cursor.getFloat(6));
                job.setLongitude(cursor.getFloat(7));
                job.setAuthorId(cursor.getInt(8));
                job.setWorkerId(cursor.getInt(9));
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
                new String[] {COL_JOB_ID, COL_JOB_TITLE, COL_JOB_DESCRIPTION,
                        COL_JOB_JOB_CATEGORY_ID, COL_JOB_COMPLETED, COL_JOB_CREATED_AT,
                        COL_JOB_LATITUDE, COL_JOB_LONGITUDE, COL_JOB_AUTHOR_ID, COL_JOB_WORKER_ID},
                COL_JOB_WORKER_ID + "=? AND " + COL_JOB_COMPLETED + "=1",
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
                job.setCategoryId(cursor.getInt(3));
                job.setCompleted(Boolean.parseBoolean(cursor.getString(4)));
                job.setCreatedAt(cursor.getString(5));
                job.setLatitude(cursor.getFloat(6));
                job.setLongitude(cursor.getFloat(7));
                job.setAuthorId(cursor.getInt(8));
                job.setWorkerId(cursor.getInt(9));
                completedJobs.add(job);

            } while (cursor.moveToNext());
        }

        // return job list
        return completedJobs;
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