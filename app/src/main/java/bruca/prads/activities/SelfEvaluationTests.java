package bruca.prads.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bruca.prads.R;
import bruca.prads.models.Image;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 5/13/2017.
 */

public class SelfEvaluationTests extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    Button next;
    RadioButton opt_1;
    RadioGroup radgrp;
    ArrayList<String> radioOptions;
    private String opt_Selected,qsn8_selection,qsn9_selection,
            qsn10_selection,qsn11_selection, testSelected;
    private TextView question,instruction,count;
    FeedReaderDbHelper dbhelper;
    private Cursor c,cursor,c2;
    Intent intent;
    int counter = 0, score,current_score = 0,
            previous_score = 0,final_score = 0;
    int col1, col2, col3, col4, col5, col1_prev,
            col2_prev, col3_prev, col4_prev, col5_prev,
            qsn8,qsn9,qsn10,qsn11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_selfeval);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        dbhelper = new FeedReaderDbHelper(this);

        count = (TextView)findViewById(R.id.count);
        instruction = (TextView) findViewById(R.id.intro_text);
        radioOptions = new ArrayList<>();//array list to hold responses
        //get the intent from which this activity is called
        intent = getIntent();
        testSelected = intent.getStringExtra("test");
        new getEvaluationTest().execute();
        //fetch value from key-value pair and run the respective test
        switch (testSelected) {
            case "depression":
                instruction.setText(R.string.mdi_instructions);
                //async to do stuff in background
                //new getEvaluationTest().execute();
                break;
            case "stress":
                instruction.setText(R.string.uss_instructions);
                //async to do stuff in background
                //new getEvaluationTest().execute();
                break;
            case "anxiety":
                instruction.setText(R.string.gad7_instructions);
                //async to do stuff in background
                //new getEvaluationTest().execute();
                break;
            case "who":
                instruction.setText(R.string.who5_instructions);
                //async to do stuff in background
                //new getEvaluationTest().execute();
                break;
            default:
                instruction.setText(R.string.who5_instructions);
                break;
        }

    }// closing onCreate

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //async task to get stuff from db
    private class getEvaluationTest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            //getting today's date
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String dateToday = dateFormat.format(cal.getTime());

            c = dbhelper.getTest(testSelected);
            //compare today's date with nextTestDates in the db for a result
            String testName = testSelected.substring(0, 1).toUpperCase() + testSelected.substring(1);
            if (testName.equals("Who")){
                cursor = dbhelper.checkNextTestDate("General Wellbeing",dateToday);
                c2 = dbhelper.getSingleCategoryEntry("General Wellbeing");
            }else{
                cursor = dbhelper.checkNextTestDate(testName,dateToday);
                c2 = dbhelper.getSingleCategoryEntry(testName);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            runTest();
            nextRow();
            //run test if today is the date or it past otherwise let the user
            //know when the next test is
            if (cursor.getCount() == 0 && c2.getCount() == 0){
                runTest();
                nextRow();
            }else if (c2.getCount() > 0) {
                //if today is date of the test, run test
                if (getNextTestDate()){
                    runTest();
                    nextRow();
                }
            }
        }
    }

    //get the next date for the SET and let the user know
    private boolean getNextTestDate(){
        Calendar cal = Calendar.getInstance();
        if(c2 != null) {
            if (c2.getCount() > 0) {
                c2.moveToLast(); //go to the most recent entry/the last entry to be saved

                int dateColumnIndex = c2.getColumnIndex(FeedReaderDbHelper.KEY_NEXT_TEST_DATE);
                String dateTime = c2.getString(dateColumnIndex);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

                Date nextTestDate = null, todayDate = null;
                try {
                    //next test date from the db
                    nextTestDate = dateFormat.parse(dateTime);
                    //today's date
                    String dateToday = dateFormat.format(cal.getTime());
                    todayDate = dateFormat.parse(dateToday);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //compare the dates
                if (nextTestDate != null && todayDate != null) {
                    if (nextTestDate.before(todayDate)){
                        notifyUser(dateTime);
                        return false;

                    }else if (nextTestDate.equals(todayDate)){
                        return true;

                    }else if (nextTestDate.after(todayDate)){
                        return true;
                    }
                }
                //notifyUser(dateTime);
                c2.close();
            }
        }
        return false;
    }

    //dialog to let user know when next test is
    public void notifyUser(String date){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Next test date not yet reached.");
        //To prevent dialog box from getting dismissed on back key pressed
        alertDialog.setCancelable(false);
        //to prevent dialog box from getting dismissed on outside touch
        //alertDialog.setCanceledOnTouchOutside(false);
        if (testSelected.equals("who")){
            alertDialog.setMessage("Your next general wellbeing assessment test date is " + date + ". You will be reminded" +
                    " when this date arrives." );
        } else{
            alertDialog.setMessage("Your next "+ testSelected +  " assessment test date is " + date + ". You will be reminded" +
                    " when this date arrives." );
        }
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(SelfEvaluationTests.this, SelfEvaluationTestsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.show();
    }

    private void displayMetaInfo(int position) {
        count.setText((position + 1) + " of " + c.getCount());
    }

    public void nextRow(){
        //button to go to next question
        next = (Button) this.findViewById(R.id.next);
        next.setVisibility(View.INVISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next.setVisibility(View.INVISIBLE);
                previous_score = final_score;
                //current column score becomes previous column score
                col1_prev = col1;
                col2_prev = col2;
                col3_prev = col3;
                col4_prev = col4;
                col5_prev = col5;

                counter++;

                    if (counter != c.getCount()) {
                        runTest();
                    } else {
                        //handle the depression score
                        switch (testSelected) {
                            case "depression": {
                                final_score = col1 + col2 + col3 + col4 +
                                        col5;
                                Bundle bundle_score = new Bundle();
                                bundle_score.putInt("final_score", final_score);
                                bundle_score.putString("test", "Depression");
                                launchTestScoreActivity(bundle_score);
                                break;
                            }
                            //handle the stress score
                            case "stress": {
                                final_score = col1 + col2 + col3;
                                Bundle bundle_score = new Bundle();
                                bundle_score.putInt("final_score", final_score);
                                bundle_score.putString("test", "Stress");
                                launchTestScoreActivity(bundle_score);
                                break;
                            }
                            //handle the anxiety score
                            case "anxiety": {
                                final_score = col1 + col2 + col3;
                                Bundle bundle_score = new Bundle();
                                bundle_score.putInt("final_score", final_score);
                                bundle_score.putString("test", "Anxiety");
                                launchTestScoreActivity(bundle_score);
                                break;
                            }
                            case "who": {
                                //handle the General wellbeing score
                                final_score = col1 + col2 + col3 + col4 +
                                        col5;
                                Bundle bundle_score = new Bundle();
                                bundle_score.putInt("final_score", final_score);
                                bundle_score.putString("test", "General Wellbeing");
                                launchTestScoreActivity(bundle_score);
                                break;
                            }
                        }

                    }
            }
        });
    }

    /*start the evaluation test*/
    public void runTest(){
        if (c.moveToPosition(counter)) {

            //display the posiion of the current question
            displayMetaInfo(counter);
            //init the text view to hold the question
            question = (TextView) findViewById(R.id.question);
            question.setText(c.getString(1));

            radgrp = (RadioGroup) findViewById(R.id.radiogroup);
            radgrp.setOnCheckedChangeListener(this);
            radgrp.removeAllViews();

            for (int i = 2; i < 8; i++) {
                if (c.getString(i) != null) {
                    opt_1 = new RadioButton(this);
                    opt_1.setGravity(Gravity.CENTER);
                   // opt_1.setButtonDrawable(android.R.color.transparent);
                    opt_1.setText(c.getString(i));
                    opt_1.setPadding(5, 5, 5, 5);
                    opt_1.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    opt_1.setTextSize(18);
                    radgrp.addView(opt_1);
                }
            }
        }
    }

    //launch the test score activity passing the relevant data to it
    private void launchTestScoreActivity(Bundle bundle){
        Intent intent = new Intent(SelfEvaluationTests.this, TestScoreActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCheckedChanged(RadioGroup radiogrp, int i) {
        int radioButtonId = radiogrp.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)radiogrp.findViewById(radioButtonId);
        //oSelectedCount++;
        opt_Selected = (String) radioButton.getText();
        next.setVisibility(View.VISIBLE);
        //making the text underlined
        //radioButton.setPaintFlags(radioButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //make the text bold and change the color
        //radioButton.setTypeface(null, Typeface.BOLD);
        //radioButton.setTextColor(Color.parseColor("#5FCC00"));

        if(testSelected.equals("depression")){
            if(counter == 7){
                //check the user selection and return the score
                qsn8 = mdiCheckScore(opt_Selected);
                qsn8_selection = opt_Selected;
            }else if (counter == 8){
                //check the user selection and return the score
                qsn9 = mdiCheckScore(opt_Selected);
                qsn9_selection = opt_Selected;

                //get highest score of both
                if(qsn8 > qsn9){
                    who5MdiScoring(qsn8_selection);
                }else if(qsn9 > qsn8){
                    who5MdiScoring(qsn9_selection);
                }else{
                    who5MdiScoring(qsn9_selection);
                }
            } else if (counter == 9){
                qsn10 = mdiCheckScore(opt_Selected);
                qsn10_selection = opt_Selected;
            }else if (counter == 10){
                qsn11 = mdiCheckScore(opt_Selected);
                qsn11_selection = opt_Selected;

                if(qsn10 > qsn11){
                    who5MdiScoring(qsn10_selection);
                }else if(qsn11 > qsn10){
                    who5MdiScoring(qsn11_selection);
                }else{
                    who5MdiScoring(qsn11_selection);
                }
            }else{
                who5MdiScoring(opt_Selected);
            }

        }else if(intent.getStringExtra("test").equals("stress")){
            ussScoring(opt_Selected);

        }else if(intent.getStringExtra("test").equals("anxiety")){
            gad7Scoring(opt_Selected);
        } else {
            who5MdiScoring(opt_Selected);
        }
        /*
        if(opt_Selected.equals("me")){
            radioButton.setPaintFlags(radioButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
        */
    }

    //scoring methods
    public int mdiCheckScore(String uSelection){
        switch (uSelection){
            case "All of the time":
                score = 5;//the score that this selection/option has
                break;
            case "Most of the time":
                score = 4;//the score that this selection/option has
                break;
            case "Slightly more than half the time":
                score = 3;//the score that this selection/option has
                break;
            case "Slightly less than half the time":
                score = 2;//the score that this selection/option has
                break;
            case "Some of the time":
                score = 1;//the score that this selection/option has
                break;
            case "At no time":
                score = 0;//the score that this selection/option has
                break;
            default:
                Toast.makeText(this, "select", Toast.LENGTH_SHORT).show();
        }
        return score;
    }

    public int who5MdiScoring(String uSelection){
        switch (uSelection){
            case "All of the time":
                score = 5;//the score that this selection/option has
                current_score = score;
                col5 = col5_prev + current_score;
                break;
            case "Most of the time":
                score = 4;//the score that this selection/option has
                current_score = score;
                col4 = col4_prev + current_score;
                break;
            case "Slightly more than half the time":
                score = 3;//the score that this selection/option has
                current_score = score;
                col3 = col3_prev + current_score;
                break;
            case "Slightly less than half the time":
                score = 2;//the score that this selection/option has
                current_score = score;
                col2 = col2_prev + current_score;
                break;
            case "Some of the time":
                score = 1;//the score that this selection/option has
                current_score = score;
                col1 = col1_prev + current_score;
                break;
            case "At no time":
                score = 0;//the score that this selection/option has
                current_score = score;
                //final_score = current_score + previous_score;
                break;
            default:
                Toast.makeText(this, "select", Toast.LENGTH_SHORT).show();
        }
        return final_score;
    }

    public void ussScoring(String uSelection){
        switch (uSelection){
            case "Not at all":
                score = 0;//the score that this selection/option has
                current_score = score;
                //final_score = current_score + previous_score;
                break;
            case "Sometimes":
                score = 1;//the score that this selection/option has
                current_score = score;
                col1 = current_score + col1_prev;
                break;
            case "Frequently":
                score = 2;//the score that this selection/option has
                current_score = score;
                col2 = current_score + col2_prev;
                break;
            case "Constantly":
                score = 3;//the score that this selection/option has
                current_score = score;
                col3 = current_score + col3_prev;
                break;
            default:
                Toast.makeText(this, "select", Toast.LENGTH_SHORT).show();
        }
    }

    public int gad7Scoring(String uSelection){
        switch (uSelection){
            case "Not at all sure":
                score = 0;//the score that this selection/option has
                current_score = score;
                //gad_col0 = current_score + gad_col0_prev;
                break;
            case "Several days":
                score = 1;//the score that this selection/option has
                current_score = score;
                col1 = current_score + col1_prev;
                break;
            case "Over half the days":
                score = 2;//the score that this selection/option has
                current_score = score;
                col2 = current_score + col2_prev;
                break;
            case "Nearly everyday":
                score = 3;//the score that this selection/option has
                current_score = score;
                col3 = current_score + col3_prev;
                break;
            default:
                Toast.makeText(this, "Select", Toast.LENGTH_SHORT).show();
        }
        return final_score;
    }
}
