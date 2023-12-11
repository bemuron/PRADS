package bruca.prads.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import bruca.prads.R;
import bruca.prads.helpers.ReminderManager;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 5/25/2016.
 */
public class TestScoreActivity extends AppCompatActivity{
    private TextView points, score_explanation,continu,test_name,score_name, recommendation,recommendation2;
    private EditText testScoreText;
    private String test;
    private FeedReaderDbHelper dbhelper;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_test_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        dbhelper = new FeedReaderDbHelper(this);

        points = (TextView)findViewById(R.id.score_points);
        test_name = (TextView)findViewById(R.id.test_name);
        score_name = (TextView)findViewById(R.id.score_name);
        score_explanation = (TextView)findViewById(R.id.score_explanation);
        recommendation = (TextView)findViewById(R.id.score_recommendation);
        recommendation2 = (TextView)findViewById(R.id.score_recommendation2);
        testScoreText = (EditText)findViewById(R.id.test_score_text);

        //Intent score_intent = getIntent();
        Bundle bundle_score = getIntent().getExtras();
        if(bundle_score != null) {
            //fetch value from key-value pair and make it visible on textView
            int test_score = bundle_score.getInt("final_score");
            test = bundle_score.getString("test");
            points.setText(String.valueOf(test_score));
            test_name.setText(test);
            test_name.append(" Self-evaluation test results");

            displayRelevantText(test,test_score);
        }

        continu = (Button) findViewById(R.id.continu);
        continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTestEntry();
            }
        });

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //method to display relevant text and recommendations based on user test score
    public void displayRelevantText(String test, int score){
        switch (test){
            case "Depression":
                if(score >= 0 && score <= 19){
                    //no depression
                    score_name.setText(R.string.no_depression);
                    score_explanation.setText(R.string.no_depression_desc);
                    recommendation.setText(R.string.positive_confessions);
                    recommendation2.setText(R.string.usefulResources);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(2,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));
                }

                else if(score >= 20 && score <= 24){
                    //mild depression
                    score_name.setText(R.string.mild_depression);
                    score_explanation.setText(R.string.mild_depression_desc);
                    //recommendations: +ve confessions(when depressed), useful resources
                    recommendation.setText(R.string.useful_contacts);
                    recommendation2.setText(R.string.usefulResources);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(2,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

                }else if(score >= 25 && score <= 29){
                    //moderate depression
                    points.setTextColor(Color.YELLOW);
                    score_name.setText(R.string.moderate_depression);
                    score_explanation.setText(R.string.moderate_depression_desc);
                    //recommendations: +ve confessions(when depressed), useful resources
                    recommendation.setText(R.string.useful_contacts);
                    recommendation2.setText(R.string.usefulResources);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(1,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

                }else if(score >= 30){
                    //severe depression
                    points.setTextColor(Color.RED);
                    score_name.setText(R.string.severe_depression);
                    score_explanation.setText(R.string.severe_depression_desc);
                    //recommendations: make appointment, useful contacts
                    recommendation.setText(R.string.make_appointment);
                    recommendation2.setText(R.string.useful_contacts);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(2,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));
                }
                break;
            case "Anxiety":
                if(score >= 0 && score <= 4){
                    //No anxiety
                    points.setTextColor(Color.GREEN);
                    score_name.setText(R.string.no_anxiety);
                    score_explanation.setText(R.string.no_anxiety_desc);
                    //recommendations: +ve confessions(when anxious), useful resources
                    recommendation.setText(R.string.positive_confessions);
                    recommendation2.setText(R.string.usefulResources);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(2,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

                }else if(score >= 5 && score <= 9){
                    //mild anxiety
                    score_name.setText(R.string.mild_anxiety);
                    score_explanation.setText(R.string.mild_anxiety_desc);
                    //recommendations: contacts,resources
                    recommendation.setText(R.string.useful_contacts);
                    recommendation2.setText(R.string.usefulResources);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(2,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

                }else if(score >= 10 && score <= 14){
                    //moderate anxiety
                    score_name.setText(R.string.moderate_anxiety);
                    score_explanation.setText(R.string.moderate_anxiety_desc);
                    //recommendations: appointment,contacts
                    recommendation.setText(R.string.make_appointment);
                    recommendation2.setText(R.string.useful_contacts);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(2,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

                }else if(score >= 15){
                    //severe anxiety
                    points.setTextColor(Color.RED);
                    score_name.setText(R.string.severe_anxiety);
                    score_explanation.setText(R.string.severe_anxiety_desc);
                    //recommendations: appointment, contacts
                    recommendation.setText(R.string.make_appointment);
                    recommendation2.setText(R.string.useful_contacts);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(2,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

                }
                break;
            case "Stress":
                if(score == 0){
                    points.setTextColor(Color.GREEN);
                    score_name.setText(R.string.no_stress);
                    score_explanation.setText(R.string.no_stress_desc);
                    //recommendations: +ve confessions(when anxious), useful resources
                    recommendation.setText(R.string.positive_confessions);
                    recommendation2.setText(R.string.usefulResources);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(2,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

                }else if(score >= 1 && score <=12){
                score_name.setText(R.string.mild_stress);
                score_explanation.setText(R.string.mild_stress_desc);
                    //recommendations: +ve confessions(when anxious), useful contacts
                    recommendation.setText(R.string.positive_confessions);
                    recommendation2.setText(R.string.useful_contacts);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(2,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

            } else if(score >= 13){
                    //score predictive of psychological distress
                    points.setTextColor(Color.RED);
                    score_name.setText(R.string.stress);
                    score_explanation.setText(R.string.stress_desc);
                    //recommendations: make appointment, useful contacts
                    recommendation.setText(R.string.make_appointment);
                    recommendation2.setText(R.string.useful_contacts);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(3,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));
                }
                break;
            case "General Wellbeing":
                if(score == 0){
                    //worst possible quality of life
                    points.setTextColor(Color.RED);
                    score_name.setText(R.string.worst_quality);
                    score_explanation.setText(R.string.worst_gen_wellbeing_desc);
                    //recommendations: +ve confessions(on love), appointment
                    recommendation.setText(R.string.make_appointment);
                    recommendation2.setText(R.string.useful_contacts);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(3,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

                }else if(score >= 1 && score <= 7){
                    //likely depression, further testing required
                    score_name.setText(R.string.poor_quality);
                    score_explanation.setText(R.string.poor_gen_wellbeing_desc);
                    //recommendations: +ve confessions(on love), useful contacts
                    recommendation.setText(R.string.useful_contacts);
                    recommendation2.setText(R.string.usefulResources);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(3,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

                }else if(score >= 8 && score <= 12){
                    //low moods
                    score_name.setText(R.string.low_quality);
                    score_explanation.setText(R.string.low_gen_wellbeing_desc);
                    //recommendations: +ve confessions(on love), useful contacts
                    recommendation.setText(R.string.positive_confessions);
                    recommendation2.setText(R.string.usefulResources);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(3,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

                }else if(score >= 13 && score <= 17){
                    //okay moods
                    score_name.setText(R.string.okay_quality);
                    score_explanation.setText(R.string.okay_gen_wellbeing_desc);
                    //recommendations: +ve confessions(on love), useful contacts
                    recommendation.setText(R.string.positive_confessions);
                    recommendation2.setText(R.string.usefulResources);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(3,this,recommendation.getText().toString()));
                    recommendation2.setPaintFlags(recommendation2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation2.setTextColor(Color.BLUE);
                    recommendation2.setOnClickListener(new MyOnClickListener(3,this,recommendation2.getText().toString()));

                }else if(score >= 18 && score <= 24){
                    //High and good moods
                    points.setTextColor(Color.GREEN);
                    score_name.setText(R.string.good_quality);
                    score_explanation.setText(R.string.good_gen_wellbeing_desc);
                    //recommendations: +ve confessions(on love)
                    recommendation.setText(R.string.positive_confessions);
                    recommendation2.setVisibility(View.GONE);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(3,this,recommendation.getText().toString()));

                }else if(score >= 25){
                    //Best possible quality of life
                    points.setTextColor(Color.GREEN);
                    score_name.setText(R.string.best_quality);
                    score_explanation.setText(R.string.best_gen_wellbeing_desc);
                    //recommendations: +ve confessions(on love)
                    recommendation.setText(R.string.positive_confessions);
                    recommendation2.setVisibility(View.GONE);
                    recommendation.setPaintFlags(recommendation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    recommendation.setTextColor(Color.BLUE);
                    recommendation.setOnClickListener(new MyOnClickListener(3,this,recommendation.getText().toString()));
                }
                break;
        }
    }

    private void saveTestEntry(){
/*
        String title = testScoreText.getText().toString();
        if (TextUtils.isEmpty(title)){
            testScoreText.setError("Title is required");
            return false;
        }
*/
        //getting the date the test was taken
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault());
        String formattedDate = df.format(c.getTime());

        //get the month alone
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        String monthTaken = monthFormat.format(c.getTime());

        switch (test) {
            case "Depression":
            case "Anxiety": {
                // get the next test date
                c.add(Calendar.DATE, 14); //same with c.add(Calendar.DAY_OF_MONTH, 14);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                String NextDate = dateFormat.format(c.getTime());

                //get the score value from the text view
                int score = Integer.valueOf(points.getText().toString());

                dbhelper.createTestEntry(test, formattedDate,
                        score_name.getText().toString(), NextDate, score,
                        testScoreText.getText().toString(), monthTaken, score_explanation.getText().toString());

                notifyUser(NextDate);
                Intent intent = new Intent(TestScoreActivity.this, SelfEvaluationTestsActivity.class);
                startActivity(intent);
                finish();
                //set the reminder for the next test date
                new ReminderManager(this).setTestReminder(
                        test.toLowerCase(Locale.US), c);

                break;
            }
            case "Stress": {
                // get the next test date
                c.add(Calendar.DATE, 30); //same with c.add(Calendar.DAY_OF_MONTH, 30);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                String NextDate = dateFormat.format(c.getTime());

                //get the score value from the text view
                int score = Integer.valueOf(points.getText().toString());

                dbhelper.createTestEntry(test, formattedDate,
                        score_name.getText().toString(), NextDate, score,
                        testScoreText.getText().toString(), monthTaken, score_explanation.getText().toString());

                notifyUser(NextDate);
                Intent intent = new Intent(TestScoreActivity.this, SelfEvaluationTestsActivity.class);
                startActivity(intent);
                finish();
                //set the reminder for the next test date
                new ReminderManager(this).setTestReminder(
                        test.toLowerCase(Locale.US), c);

                break;
            }
            case "General Wellbeing": {
                // get the next test date
                c.add(Calendar.DATE, 14);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                String NextDate = dateFormat.format(c.getTime());

                //get the score value from the text view
                int score = Integer.valueOf(points.getText().toString());

                dbhelper.createTestEntry(test, formattedDate,
                        score_name.getText().toString(), NextDate, score,
                        testScoreText.getText().toString(), monthTaken, score_explanation.getText().toString());

                notifyUser(NextDate);
                Intent intent = new Intent(TestScoreActivity.this, SelfEvaluationTestsActivity.class);
                startActivity(intent);
                finish();
                //set the reminder for the next test date
                new ReminderManager(this).setTestReminder("who", c);
                break;
            }
        }
    }

    //dialog to let user know when next test is
    public void notifyUser(String date){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(test +" Self-evaluation test saved.");
        alertDialog.setMessage("Your next assessment date is " + date + ". A reminder has been automatically " +
                "set and you will be notified when the date arrives." );
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(TestScoreActivity.this, SelfEvaluationTestsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //custom onclick to handle clicks on the recommendations
    public class MyOnClickListener implements View.OnClickListener {

        int id;
        Context context;
        String textClicked;
        public MyOnClickListener(int id,Context context,String textClicked)
        {
            this.id = id;
            this.context=context;
            this.textClicked = textClicked;
        }

        @Override
        public void onClick(View arg0) {
            switch (textClicked){
                case "Positive confessions":
                    saveTestEntry();
                    Intent intent = new Intent(TestScoreActivity.this, PositiveConfessionsActivity.class);
                    intent.putExtra("item-ID", id);
                    startActivity(intent);
                    finish();
                    break;
                case "Useful resources":
                    saveTestEntry();
                    Intent intent1 = new Intent(TestScoreActivity.this, UsefulResources.class);
                    startActivity(intent1);
                    finish();
                    break;
                case "Useful contacts":
                    saveTestEntry();
                    Intent intent2 = new Intent(TestScoreActivity.this, UsefulContacts.class);
                    startActivity(intent2);
                    finish();
                    break;
                case "Make appointment":
                    saveTestEntry();
                    Intent intent3 = new Intent(TestScoreActivity.this, MakeAppointment.class);
                    startActivity(intent3);
                    finish();
                    break;
            }


        }
    }
}
