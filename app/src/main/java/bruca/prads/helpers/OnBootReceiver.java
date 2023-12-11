package bruca.prads.helpers;

/**
 * Created by Emo on 6/17/2017.
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.database.Cursor;
import android.util.Log;

import bruca.prads.activities.DisplayNoteActivity;
import database.FeedReaderDbHelper;

public class OnBootReceiver extends BroadcastReceiver {

    private static final String TAG = ComponentInfo.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {

        ReminderManager reminderMgr = new ReminderManager(context);

        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        //dbHelper.open();

        Cursor cursor = dbHelper.getNotes();

        if(cursor != null) {
            cursor.moveToFirst();

            int rowIdColumnIndex = cursor.getColumnIndex(FeedReaderDbHelper.KEY_ID);
            int dateTimeColumnIndex = cursor.getColumnIndex(FeedReaderDbHelper.KEY_REMINDER_DATE);

            while(!cursor.isAfterLast()) {

                Log.d(TAG, "Adding alarm from boot.");
                Log.d(TAG, "Row Id Column Index - " + rowIdColumnIndex);
                Log.d(TAG, "Date Time Column Index - " + dateTimeColumnIndex);

                int rowId = cursor.getInt(rowIdColumnIndex);
                String dateTime = cursor.getString(dateTimeColumnIndex);

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat(
                        DisplayNoteActivity.DATE_TIME_FORMAT, Locale.getDefault());

                try {
                    java.util.Date date = format.parse(dateTime);
                    cal.setTime(date);

                    reminderMgr.setReminder(rowId, cal);
                } catch (java.text.ParseException e) {
                    Log.e("OnBootReceiver", e.getMessage(), e);
                }

                cursor.moveToNext();
            }
            cursor.close() ;
        }

        //get reminders for the SETS
        getTestReminders(dbHelper,reminderMgr);

        dbHelper.close();
    }

    //method to get reminders for the SETS from the db
    private void getTestReminders(FeedReaderDbHelper db, ReminderManager rm){
        Cursor cursor = db.getTestEntries();

        if(cursor != null) {
            cursor.moveToFirst();

            int dateTimeColumnIndex = cursor.getColumnIndex(FeedReaderDbHelper.KEY_NEXT_TEST_DATE);
            int testNameIndex = cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NAME);

            while(!cursor.isAfterLast()) {

                String dateTime = cursor.getString(dateTimeColumnIndex);
                String testName = cursor.getString(testNameIndex);

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

                try {
                    java.util.Date date = format.parse(dateTime);
                    cal.setTime(date);
                    String test =  testName.toLowerCase(Locale.US);
                    if (test.equals("general wellbeing")){
                        String who_test = "who";
                        rm.setTestReminder(who_test,cal);
                    }else {
                        rm.setTestReminder(test,cal);
                    }
                } catch (java.text.ParseException e) {
                    Log.e("OnBootReceiver", e.getMessage(), e);
                }

                cursor.moveToNext();
            }
            cursor.close() ;
        }
    }
}
