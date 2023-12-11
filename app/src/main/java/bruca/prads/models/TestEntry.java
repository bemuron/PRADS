package bruca.prads.models;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import database.FeedReaderDbHelper;

/**
 * Created by Emo on 6/7/2017.
 */

public class TestEntry {
    private int id;
    private String title;
    private int test_score;
    private String test_name;
    private String noteContent;
    private String dateCreated;
    private Long dataModified;
    private String dateTestTaken;
    private String timeTestTaken;
    private String scoreDescription;
    private String testMonth;
    private String timestamp;
    private String picture;
    private boolean isImportant;
    private boolean isRead;
    private int color = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTestScore() {
        return test_score;
    }

    public void setTestScore(int test_score) {
        this.test_score = test_score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTestName() {
        return test_name;
    }

    public void setTestName(String test_name) {
        this.test_name = test_name;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateTestTaken() {
        return dateTestTaken;
    }

    public void setDateTestTaken(String dateTestTaken) {
        this.dateTestTaken = dateTestTaken;
    }

    public String getTimeTestTaken() {
        return timeTestTaken;
    }

    public void setTimeTestTaken(String timeTestTaken) {
        this.timeTestTaken = timeTestTaken;
    }

    public String getTestMonth() {
        return testMonth;
    }

    public void setTestMonth(String testMonth) {
        this.testMonth = testMonth;
    }

    public Long getDataModified() {
        return dataModified;
    }

    public void setDataModified(Long dataModified) {
        this.dataModified = dataModified;
    }

    public String getScoreDescription() {
        return scoreDescription;
    }

    public void setScoreDescription(String scoreDescription) {
        this.scoreDescription = scoreDescription;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getReadableModifiedDate(){
    /*
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault());
        sdf.setTimeZone(getDataModified().getTimeZone());
        Date modifiedDate = getDataModified().getTime();
        String displayDate = sdf.format(modifiedDate);
        return displayDate;
        */
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault());
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static TestEntry getNotefromCursor(Cursor c){
        TestEntry note = new TestEntry();
        note.setId(c.getInt(c.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
        note.setTitle(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_NOTE_TITLE)));
        note.setNoteContent(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_NOTE_CONTENT)));

        //get Calendar instance
        Calendar calendar = GregorianCalendar.getInstance();

        //set the calendar time to date created
        note.setDateCreated(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_CREATED_TIME)));
        //score_explanation.setDateCreated(calendar);

        //set the calendar time to date modified
        calendar.setTimeInMillis(c.getLong(c.getColumnIndex(FeedReaderDbHelper.KEY_REMINDER_DATE)));
       // score_explanation.setDataModified(calendar);
        return note;
    }


}
