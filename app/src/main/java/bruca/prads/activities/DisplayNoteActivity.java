package bruca.prads.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import bruca.prads.helpers.Note;
import bruca.prads.R;
import bruca.prads.helpers.ReminderManager;
import database.FeedReaderDbHelper;

public class DisplayNoteActivity extends AppCompatActivity{
    private FeedReaderDbHelper dbhelper;
    private Cursor c;
    private EditText mTitleEditText,mContentEditText;
    private int noteId;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Dialog dateDialog;
    private Note mCurrentNote = null;

    //
    // Date Format
    //
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "kk:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_note_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        dbhelper = new FeedReaderDbHelper(this);

        //extras = getIntent().getExtras();
        //if(extras != null) {
            //noteId = extras.getInt("id");
            //if (noteId > 0) {
                //async to do stuff in background
        Intent intent = getIntent();
        noteId = intent.getIntExtra("note-ID",0);
        if (noteId == 0){
            displayViews();
        }else {
            new getSingleNote().execute();
        }
          //  }
        //}

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveNote()) {
                    makeToast(noteId > 0 ? "Goal updated" : "Goal saved");
                }
            }
        });


    }//closing onCreate

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //async task to get stuff from db
    private class getSingleNote extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            //get intent from which this activity is called
            Intent intent = getIntent();
            //if (intent.getIntExtra("score_explanation-ID",0) == 0){
                c = dbhelper.getNote(noteId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            getSingleNote();
            displayNote();
        }
    }

    private void displayNote() {
        mTitleEditText = (EditText) findViewById(R.id.edit_text_title);
        mTitleEditText.setText(mCurrentNote.getTitle());
        mTitleEditText.setTextSize(15);

        mContentEditText = (EditText) findViewById(R.id.edit_text_note);
        mContentEditText.setText(mCurrentNote.getContent());
        mContentEditText.setTextSize(20);
    }

    private void getSingleNote(){
        //swipeRefreshLayout.setRefreshing(true);
        //Cursor cursor = dbhelper.getNotes();
        if (c != null){
            c.moveToFirst();
            while (!c.isAfterLast()){

                mCurrentNote = new Note();
                mCurrentNote.setId(c.getInt(c.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                mCurrentNote.setTitle(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_NOTE_TITLE)));
                mCurrentNote.setContent(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_NOTE_CONTENT)));
                mCurrentNote.setDateCreated(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_CREATED_TIME)));
                //notes.add(score_explanation);
                c.moveToNext();
            }
            c.close();
        }
        //mAdapter.notifyDataSetChanged();
        //swipeRefreshLayout.setRefreshing(false);
    }

    private void displayViews(){
        mTitleEditText = (EditText)findViewById(R.id.edit_text_title);
        mContentEditText = (EditText)findViewById(R.id.edit_text_note);
    }

    public void promptForDelete(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Delete " + mTitleEditText.getText().toString() + " ?");
        alertDialog.setMessage("Are you sure you want to delete the goal " + mTitleEditText.getText().toString() + "?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dbhelper.delete(noteId);
                dbhelper.delete(mCurrentNote.getId());
                makeToast(mTitleEditText.getText().toString() + " deleted");
                Intent intent = new Intent(getApplicationContext(),
                        GoalsListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private boolean saveNote(){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault());
        String formattedDate = df.format(c.getTime());

        String title = mTitleEditText.getText().toString();
        if (TextUtils.isEmpty(title)){
            mTitleEditText.setError("Title is required");
            return false;
        }

        String content = mContentEditText.getText().toString();
        if (TextUtils.isEmpty(content)){
            mContentEditText.setError("Content is required");
            return false;
        }

        if (noteId > 0) {
            dbhelper.update(noteId,mTitleEditText.getText().toString(),
                    mContentEditText.getText().toString(),formattedDate);
        }
        else {

            dbhelper.create(mTitleEditText.getText().toString(),
                    formattedDate,mContentEditText.getText().toString());
            Intent intent = new Intent(DisplayNoteActivity.this, GoalsListActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    private void dateTimePicker(){
        dateDialog = new Dialog(DisplayNoteActivity.this);
        dateDialog.setTitle("Set reminder date and time");
        dateDialog.setContentView(R.layout.date_time_picker);
        //find our pickers
        datePicker = (DatePicker)dateDialog.findViewById(R.id.datePicker);
        timePicker = (TimePicker)dateDialog.findViewById(R.id.timePicker);
        Button setValue = (Button)dateDialog.findViewById(R.id.set_reminder);

        //add the date and time to the calendar
        setValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get the date the user sets
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                //get the time the user sets
                int hour;
                int minute;

                if (Build.VERSION.SDK_INT >= 23){
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                }else{
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }
                //create a new calendar set to the date chosen
                Calendar calendar = Calendar.getInstance();
                //calendar.set(year,month,day,hour,minute);
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                calendar.set(Calendar.HOUR_OF_DAY,hour);
                calendar.set(Calendar.MINUTE, minute);
                System.out.println("************ hour = " + hour);
                System.out.println("************ minute = " + minute);
                System.out.println("************reminderTime = " + calendar.getTime());
                if (saveReminderDate(calendar)) {
                    makeToast(noteId > 0 ? "Reminder set" : "Goal saved and reminder set");
                }
                dateDialog.dismiss();
            }
        });
        dateDialog.show();

    }

    private boolean saveReminderDate(Calendar cal){

        String title = mTitleEditText.getText().toString();
        if (TextUtils.isEmpty(title)){
            mTitleEditText.setError("Title is required");
            dateDialog.dismiss();
            return false;
        }

        String content = mContentEditText.getText().toString();
        if (TextUtils.isEmpty(content)){
            mContentEditText.setError("Content is required");
            dateDialog.dismiss();
            return false;
        }

        //Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + cal.getTime());
        SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        String reminderDate = df.format(cal.getTime());

        //get current time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault());
        String date = sdf.format(c.getTime());

        if (noteId > 0) {
            dbhelper.updateReminderTime(noteId,reminderDate);
        }else {

            int id = dbhelper.createWithReminder(mTitleEditText.getText().toString(),date,
                    reminderDate,mContentEditText.getText().toString());
            if(id > 0){
                noteId = id;
                makeToast("note-ID " + noteId);
            }
        }
        new ReminderManager(this).setReminder(noteId, cal);
        return true;
    }

    private void makeToast(String message){
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_goal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, GoalsListActivity.class);
            startActivity(intent);
            finish();
            return true;

        }else if (id == R.id.action_save){
            //save goal
            if (saveNote()) {
                makeToast(noteId > 0 ? "Goal updated" : "Goal saved");
            }
            } else if (id == R.id.action_delete){
            //delete score_explanation
                promptForDelete();

            } else if (id == R.id.action_set_reminder){
            //set reminder
            dateTimePicker();
        }

        return super.onOptionsItemSelected(item);
    }

}
