package bruca.prads.helpers;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import database.FeedReaderDbHelper;

/**
 * Created by Emo on 6/7/2017.
 */

public class Note {
    private int id;
    private String title;
    private String content;
    private String dateCreated;
    private Long dataModified;
    FeedReaderDbHelper dbhelper;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getDataModified() {
        return dataModified;
    }

    public void setDataModified(Long dataModified) {
        this.dataModified = dataModified;
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

    public static Note getNotefromCursor(Cursor c){
        Note note = new Note();
        note.setId(c.getInt(c.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
        note.setTitle(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_NOTE_TITLE)));
        note.setContent(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_NOTE_CONTENT)));

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
