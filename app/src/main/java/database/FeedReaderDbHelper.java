package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import bruca.prads.helpers.Note;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import android.util.Log;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;

public class FeedReaderDbHelper extends SQLiteAssetHelper {
    private static final String TAG = FeedReaderDbHelper.class.getSimpleName();

	private static final String DATABASE_NAME = "prads.db";
    private static final int DATABASE_VERSION = 1;

    //Table Names
    public static final String TESTS_TABLE = "tests";
    public static final String MOOD_DIARY_TABLE = "mood_diary";
    public static final String TEST_RESULTS_TABLE = "test_results";
    public static final String STUDY_TIPS_TABLE = "study_tips";
    public static final String NOTES_TABLE = "notes";
    public static final String USERS_TABLE = "user";
    public static final String CONFESSIONS_CATEGORIES_TABLE = "confessions_categories";

    // Login Table Columns names
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_PASS = "password";
    private static final String KEY_USER_CREATED_AT = "created_at";

    //Common column names
    public static final String KEY_ID = "_id";
    public static final String KEY_CAT_ID = "cat_id";
    public static final String KEY_TEST = "test";
    public static final String KEY_DATE = "date_taken";
    public static final String KEY_SCORE = "score";
    public static final String KEY_STUDY_TIP_CATEGORY = "tip_category";

    //Notepad table column names
    public static final String KEY_NOTE_TITLE = "title";
    public static final String KEY_NOTE_CONTENT = "content";
    public static final String KEY_REMINDER_DATE = "reminder_date";
    public static final String KEY_CREATED_TIME = "created_time";

    //Categories table column names
    public static final String KEY_CATEGORY_NAME = "category";
    public static final String KEY_CATEGORY_IMAGE = "imagename";

    //Test entries table column names
    public static final String KEY_TEST_NAME = "test";
    public static final String KEY_TEST_SCORE_NAME = "test_score_name";
    public static final String KEY_TEST_SCORE = "score";
    public static final String KEY_TEST_NOTE = "note";
    public static final String KEY_TEST_DATE_TAKEN = "date_taken";
    public static final String KEY_NEXT_TEST_DATE = "next_test_date";
    public static final String KEY_TEST_MONTH_TAKEN = "month";
    public static final String KEY_TEST_SCORE_DESCRIPTION = "score_description";

    //Diary entries table column names
    public static final String KEY_MOOD_NAME = "mood_name";
    public static final String KEY_MOOD_SCORE= "mood_score";
    public static final String KEY_MOOD_NOTES = "mood_notes";
    public static final String KEY_MOOD_DATE_TAKEN = "mood_date_taken";
    public static final String KEY_MOOD_TIME_TAKEN = "mood_time_taken";
    public static final String KEY_MOOD_DATE_TIME_TAKEN = "date_time_taken";
    public static final String KEY_MOOD_MONTH_TAKEN = "mood_month";

	    //constructor
    	public FeedReaderDbHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }

	    @Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    	//on upgrade drop old tables
            db.execSQL("DROP TABLE IF EXISTS " + TESTS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TEST_RESULTS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + STUDY_TIPS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MOOD_DIARY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + CONFESSIONS_CATEGORIES_TABLE);
	        //create new tables
	        onCreate(db);
	    }

    //-----------------User table methods---------------//
    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name); // Name
        values.put(KEY_USER_EMAIL, email); // Email
        values.put(KEY_USER_PASS, password); // Email
        //values.put(KEY_USER_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(USERS_TABLE, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public boolean registerUser(String name, String email, String password, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name); // Name
        values.put(KEY_USER_EMAIL, email); // Email
        values.put(KEY_USER_PASS, password); // pass
        values.put(KEY_USER_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(USERS_TABLE, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
        return true;
    }

    //checking user login credentials
    public Cursor checkUser(String email,String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + USERS_TABLE + " WHERE "
                + KEY_USER_EMAIL + " =? AND " + KEY_USER_PASS + " = ?";
        Cursor c = db.rawQuery(sql,new String[]{email,password});
        //c.close();
        return c;
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + USERS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("pass", cursor.getString(3));
            //user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(USERS_TABLE, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

  //-----------------My Mental Health table methods---------------//

    //getting a test from the db
    public Cursor getTest(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TESTS_TABLE + " WHERE " + KEY_TEST + " =?";
        Cursor c = db.rawQuery(sql,new String[]{value});
        return c;
    }

    //-----------------My Studies table methods---------------//

    //getting relevant content from the db
    public Cursor getStudyTips(int cat_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + STUDY_TIPS_TABLE + " WHERE " + KEY_STUDY_TIP_CATEGORY + " =?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(cat_id)});
        return c;
    }

    //-----------------Goals table methods---------------//

    //getting all the goal-notes out of the db
    public  Cursor getNotes(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + NOTES_TABLE + ";";
        Cursor c =db.rawQuery(sql, null);
        return c;
    }

    public boolean create(String name, String dates, String note_content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_TITLE, name);
        values.put(KEY_NOTE_CONTENT, note_content);
        values.put(KEY_CREATED_TIME, dates);
        db.insert(NOTES_TABLE, null, values);
        return true;
    }

    public int createWithReminder(String name, String dates,
                          String reminder_date, String note_content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_TITLE, name);
        values.put(KEY_NOTE_CONTENT, note_content);
        values.put(KEY_CREATED_TIME, dates);
        values.put(KEY_REMINDER_DATE, reminder_date);
        //int id = (int)((long)rowId);
        return (int)db.insert(NOTES_TABLE, null, values);
    }

    public boolean update(int id, String name, String note_content,
                          String dates) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_TITLE, name);
        values.put(KEY_NOTE_CONTENT, note_content);
        values.put(KEY_CREATED_TIME, dates);
        values.put(KEY_REMINDER_DATE, dates);
        db.update(NOTES_TABLE, values, KEY_ID + " =?",
                new String[] { Integer.toString(id) });
        return true;
    }

    public boolean updateReminderTime(int id, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_REMINDER_DATE, date);
        db.update(NOTES_TABLE, values, KEY_ID + " =?",
                new String[] { Integer.toString(id) });
        return true;
    }

    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOTES_TABLE, KEY_ID + " =?",
                new String[] { Integer.toString(id) });
    }

    public Cursor getNote(int id) {
        Note note;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + NOTES_TABLE + " WHERE " + KEY_ID + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        /*
        if (cursor != null){
            cursor.moveToFirst();
            note = getNotefromCursor(cursor);
            return note;
        }
*/
        return cursor;
    }

    //----------------------using the note class ------------------

    public int create(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_TITLE, note.getTitle());
        values.put(KEY_NOTE_CONTENT, note.getContent());
        values.put(KEY_CREATED_TIME, System.currentTimeMillis());
        //long id = Long.parseLong(result.getLastPathSegment());
        return (int)db.insert(NOTES_TABLE, null, values);
        //return id;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<Note>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + NOTES_TABLE + ";";
        Cursor cursor =db.rawQuery(sql, null);
        //Cursor cursor = mContext.getContentResolver().query(NoteContentProvider.CONTENT_URI, Constants.COLUMNS, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                notes.add(Note.getNotefromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return notes;
    }
/*
    public Note getNote(int id) {
        Note note;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + NOTES_TABLE + " WHERE " + KEY_ID + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        if (cursor != null){
            cursor.moveToFirst();
            note = Note.getNotefromCursor(cursor);
            return note;
        }
        return null;
    }
*/
    public void update(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_TITLE, note.getTitle());
        values.put(KEY_NOTE_CONTENT, note.getContent());
        values.put(KEY_CREATED_TIME, System.currentTimeMillis());
        db.update(NOTES_TABLE, values, KEY_ID + " =?",
                new String[] { Integer.toString(note.getId()) });
    }

    public void delete(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOTES_TABLE, KEY_ID + " =?",
                new String[] { Integer.toString(note.getId()) });
    }

    //-----------------Test Entries methods---------------//

    //getting all the test entries out of the db
    public  Cursor getTestEntries(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TEST_RESULTS_TABLE + ";";
        Cursor c =db.rawQuery(sql, null);
        return c;
    }

    public boolean updateEntry(int id, String note_content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TEST_NOTE, note_content);
        db.update(TEST_RESULTS_TABLE, values, KEY_ID + " =?",
                new String[] { Integer.toString(id) });
        return true;
    }

    public int createTestEntry(String test_name, String date_taken,
                              String score_name,String next_test_date,
                              int score,String note_content,String month_taken, String score_description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TEST_NAME, test_name);
        values.put(KEY_TEST_DATE_TAKEN, date_taken);
        values.put(KEY_TEST_SCORE_NAME, score_name);
        values.put(KEY_NEXT_TEST_DATE, next_test_date);
        values.put(KEY_TEST_SCORE, score);
        values.put(KEY_TEST_NOTE, note_content);
        values.put(KEY_TEST_MONTH_TAKEN, month_taken);
        values.put(KEY_TEST_SCORE_DESCRIPTION, score_description);

        return (int)db.insert(TEST_RESULTS_TABLE, null, values);
    }

    //getting single entry for possible editing
    public Cursor getSingleEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TEST_RESULTS_TABLE + " WHERE " + KEY_ID + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        return cursor;
    }

    //getting all data from a single category for the graph
    public Cursor getSingleCategoryEntry(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TEST_RESULTS_TABLE + " WHERE " + KEY_TEST_NAME + " =?";
        Cursor c = db.rawQuery(sql,new String[]{value});
        return c;
    }

    //getting all data from a single category for the graph
    public Cursor checkNextTestDate(String testName,String dateToday) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TEST_RESULTS_TABLE + " WHERE "
                + KEY_TEST_NAME + " =? AND " + KEY_NEXT_TEST_DATE + " = ?";
        Cursor c = db.rawQuery(sql,new String[]{testName,dateToday});
        return c;
    }

    //getting items in specific range
    public Cursor getFilteredTestData(String testName,String month){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TEST_RESULTS_TABLE + " WHERE "
                + KEY_TEST_NAME + " = ? AND " + KEY_TEST_MONTH_TAKEN + " = ?";

        Cursor c = db.rawQuery(sql, new String[] { testName,month});
        //c.moveToFirst();
        return c;
    }

    //-----------------Diary Entries methods---------------//

    //getting all the diary entries out of the db
    public  Cursor getDiaryEntries(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + MOOD_DIARY_TABLE + ";";
        Cursor c =db.rawQuery(sql, null);
        return c;
    }

    public boolean updateDiaryEntry(int id, String note_content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MOOD_NOTES, note_content);
        db.update(MOOD_DIARY_TABLE, values, KEY_ID + " =?",
                new String[] { Integer.toString(id) });
        return true;
    }

    public Cursor getSingleDiaryEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + MOOD_DIARY_TABLE + " WHERE " + KEY_ID + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        return cursor;
    }

    public int createDiaryEntry(String mood_name, String date_taken,
                               int score,String note_content,
                                String mood_date,String moodTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MOOD_NAME, mood_name);
        values.put(KEY_MOOD_DATE_TIME_TAKEN, date_taken);
        values.put(KEY_MOOD_SCORE, score);
        values.put(KEY_MOOD_NOTES, note_content);
        values.put(KEY_MOOD_DATE_TAKEN, mood_date);
        values.put(KEY_MOOD_TIME_TAKEN, moodTime);

        return (int)db.insert(MOOD_DIARY_TABLE, null, values);
    }

    public int getMoodMonthStats(String moodName,String month){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + MOOD_DIARY_TABLE + " WHERE "
                + KEY_MOOD_NAME + " = ? AND " + KEY_MOOD_MONTH_TAKEN + " = ?";

        Cursor c = db.rawQuery(sql , new String[] { moodName,month});
        c.moveToFirst();
        return c.getCount();
    }

    public Cursor getMoodDayStats(String moodName,String date){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + MOOD_DIARY_TABLE + " WHERE "
                + KEY_MOOD_NAME + " = ? AND " + KEY_MOOD_DATE_TAKEN + " = ?";

        Cursor c = db.rawQuery(sql, new String[] { moodName,date});
        //c.moveToFirst();
        return c;
    }

    //-----------------Positive Confessions table methods---------------//

    //getting all the confessions categories out of the db
    public  Cursor getConfessionsCategories(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + CONFESSIONS_CATEGORIES_TABLE + ";";
        Cursor c =db.rawQuery(sql, null);
        return c;
    }

} //closing FeedReaderDbHelper class
