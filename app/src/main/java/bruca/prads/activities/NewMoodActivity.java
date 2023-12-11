package bruca.prads.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import bruca.prads.R;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 5/25/2016.
 */
public class NewMoodActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    private TextView todayDate;
    private FeedReaderDbHelper dbhelper;
    private EditText moodText;
    RadioButton opt_1;
    RadioGroup goodMoodGroup, badMoodGroup;
    private Button saveMood;
    int moodId, moodScore;
    String moodSelected;
    private String[] goodMoods = new String[]{"Okay", "Good", "Very Good"};
    private String[] badMoods = new String[]{"Depressed", "Anxious", "Stressed"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_new_mood);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        dbhelper = new FeedReaderDbHelper(this);

            displayViews();


        saveMood = (Button) findViewById(R.id.save_mood);
        saveMood.setVisibility(View.GONE);
        saveMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiaryEntry();
                Intent intent = new Intent(NewMoodActivity.this, MoodDiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void displayViews(){
        todayDate = (TextView)findViewById(R.id.todayMood);
        moodText = (EditText)findViewById(R.id.mood_note);
        saveMood = (Button)findViewById(R.id.save_mood);

        //setting the mood icons to the radio buttons
        goodMoodGroup = (RadioGroup) findViewById(R.id.goodMoodsRadioGroup);
        badMoodGroup = (RadioGroup) findViewById(R.id.badMoodsRadioGroup);
        goodMoodGroup.setOnCheckedChangeListener(this);
        badMoodGroup.setOnCheckedChangeListener(this);
        goodMoodGroup.removeAllViews();
        badMoodGroup.removeAllViews();

        for (int i = 0; i < goodMoods.length; i++) {
            opt_1 = new RadioButton(this);
            opt_1.setText(goodMoods[i]);
            opt_1.setGravity(Gravity.BOTTOM);
            opt_1.setButtonDrawable(android.R.color.transparent);
            opt_1.setPadding(20, 5, 20, 5);
            opt_1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            opt_1.setTextSize(14);

            if (i == 0){
                opt_1.setBackgroundResource(R.drawable.mood_diary_okay);
            } else if(i == 1){
                opt_1.setBackgroundResource(R.drawable.mood_diary_good);
            }else if (i == 2){
                opt_1.setBackgroundResource(R.drawable.mood_diary_very_good);
            }
                goodMoodGroup.addView(opt_1);
        }

        for (int i = 0; i < badMoods.length; i++) {
            RadioButton opt = new RadioButton(this);
            opt.setText(badMoods[i]);
            opt.setGravity(Gravity.BOTTOM);
            opt.setButtonDrawable(android.R.color.transparent);
            opt.setPadding(20, 5, 20, 5);
            opt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            opt.setTextSize(14);

            if (i == 0){
                opt.setBackgroundResource(R.drawable.mood_diary_depressed);
            } else if(i == 1){
                opt.setBackgroundResource(R.drawable.mood_diary_anxious);
            }else if (i == 2){
                opt.setBackgroundResource(R.drawable.mood_diary_stressed);
            }
            badMoodGroup.addView(opt);
        }

        //get current time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String date = sdf.format(c.getTime());
        todayDate.setText(date);
    }

    private boolean saveDiaryEntry(){
        //getting the date and time the test was taken
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault());
        String formattedDate = df.format(c.getTime());

        //getting the date alone
        SimpleDateFormat df1 = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String moodDate = df1.format(c.getTime());

        //getting the time alone
        SimpleDateFormat df2 = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String moodTime = df2.format(c.getTime());

        dbhelper.createDiaryEntry(moodSelected,formattedDate,
                moodScore, moodText.getText().toString(),moodDate,moodTime);

        return true;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radiogrp, int i) {
        int radioButtonId = radiogrp.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)radiogrp.findViewById(radioButtonId);
        saveMood.setVisibility(View.VISIBLE);
        moodSelected = (String) radioButton.getText();

        switch (moodSelected) {
            case "Very Good":
                moodScore = 6;
                Toast.makeText(this, moodSelected, Toast.LENGTH_LONG).show();
                //making the text underlined
                radioButton.setPaintFlags(radioButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                //make the text bold and change the color
                radioButton.setTypeface(null, Typeface.BOLD);
                radioButton.setTextColor(Color.parseColor("#5FCC00"));
                break;
            case "Good":
                moodScore = 5;
                Toast.makeText(this, moodSelected, Toast.LENGTH_LONG).show();
                //making the text underlined
                radioButton.setPaintFlags(radioButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                //make the text bold and change the color
                radioButton.setTypeface(null, Typeface.BOLD);
                radioButton.setTextColor(Color.parseColor("#5FCC00"));
                break;
            case "Okay":
                moodScore = 4;
                Toast.makeText(this, moodSelected, Toast.LENGTH_LONG).show();
                //making the text underlined
                radioButton.setPaintFlags(radioButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                //make the text bold and change the color
                radioButton.setTypeface(null, Typeface.BOLD);
                radioButton.setTextColor(Color.parseColor("#5FCC00"));
                break;
            case "Stressed":
                moodScore = 3;
                Toast.makeText(this, moodSelected, Toast.LENGTH_LONG).show();
                //making the text underlined
                radioButton.setPaintFlags(radioButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                //make the text bold and change the color
                radioButton.setTypeface(null, Typeface.BOLD);
                radioButton.setTextColor(Color.parseColor("#5FCC00"));
                break;
            case "Anxious":
                moodScore = 2;
                Toast.makeText(this, moodSelected, Toast.LENGTH_LONG).show();
                //making the text underlined
                radioButton.setPaintFlags(radioButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                //make the text bold and change the color
                radioButton.setTypeface(null, Typeface.BOLD);
                radioButton.setTextColor(Color.parseColor("#5FCC00"));
                break;
            case "Depressed":
                moodScore = 1;
                Toast.makeText(this, moodSelected, Toast.LENGTH_LONG).show();
                //making the text underlined
                radioButton.setPaintFlags(radioButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                //make the text bold and change the color
                radioButton.setTypeface(null, Typeface.BOLD);
                radioButton.setTextColor(Color.parseColor("#5FCC00"));
                break;
            default:
                radioButton.setTextColor(Color.parseColor("#000000"));
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MoodDiaryActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
