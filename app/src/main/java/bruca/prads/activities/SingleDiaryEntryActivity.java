package bruca.prads.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import bruca.prads.R;
import bruca.prads.models.DiaryEntry;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 6/23/2017.
 */

public class SingleDiaryEntryActivity extends AppCompatActivity {
    private FeedReaderDbHelper dbhelper;
    private Cursor c;
    private EditText mTitleEditText,userNote;
    private TextView dateTaken, timeTaken, moodTaken,notesLabel,testNote;
    private ImageView moodImage;
    private int entryId;
    private Button save;
    private DiaryEntry mCurrentEntry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_single_diary_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        dbhelper = new FeedReaderDbHelper(this);

        //async to do stuff in background
        Intent intent = getIntent();
        entryId = intent.getIntExtra("entry-ID",0);
            new getSingleEntry().execute();

        save = (Button)findViewById(R.id.mood_update);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEntry();
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
    private class getSingleEntry extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            c = dbhelper.getSingleDiaryEntry(entryId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            getSingleNote();
            displayEntry();
        }
    }

    private void displayEntry() {
        dateTaken = (TextView) findViewById(R.id.date_mood_taken);
        dateTaken.setText(mCurrentEntry.getDateCreated());

        timeTaken = (TextView) findViewById(R.id.time_mood_taken);
        timeTaken.setText(mCurrentEntry.getTimeMoodTaken());

        moodTaken = (TextView) findViewById(R.id.mood_taken);
        moodTaken.setText(mCurrentEntry.getTitle());

        notesLabel = (TextView)findViewById(R.id.mood_notes_label);
        notesLabel.setText("My notes;");
        // if (c.moveToFirst()) {
        userNote = (EditText) findViewById(R.id.mood_taken_notes);
        userNote.setText(mCurrentEntry.getNoteContent());

        moodImage = (ImageView)findViewById(R.id.mood_taken_icon);
        String imagename = mCurrentEntry.getTitle().toLowerCase(Locale.US);
        //remove space between the name
        imagename = imagename.replace(" ","");
        int imageId = getResources().getIdentifier(imagename,
                "drawable", getPackageName());
        moodImage.setImageResource(imageId);

        //}
    }

    private void getSingleNote(){

        if (c != null){
            c.moveToFirst();
            while (!c.isAfterLast()){

                mCurrentEntry = new DiaryEntry();
                mCurrentEntry.setId(c.getInt(c.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                mCurrentEntry.setTitle(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_NAME)));
                mCurrentEntry.setNoteContent(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_NOTES)));
                mCurrentEntry.setDateCreated(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_DATE_TAKEN)));
                mCurrentEntry.setTestScore(c.getInt(c.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_SCORE)));
                mCurrentEntry.setTimeMoodTaken(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_TIME_TAKEN)));
                c.moveToNext();
            }
            c.close();
        }
    }

    private boolean updateEntry(){

        String title = userNote.getText().toString();
        if (TextUtils.isEmpty(title)){
            mTitleEditText.setError("Title is required");
            return false;
        }

        if (entryId > 0) {

            dbhelper.updateDiaryEntry(entryId,userNote.getText().toString());
            Intent intent = new Intent(SingleDiaryEntryActivity.this, MoodDiaryActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.goals_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MoodDiaryActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
