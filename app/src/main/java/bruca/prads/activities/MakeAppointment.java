package bruca.prads.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;

import bruca.prads.R;
import bruca.prads.helpers.SessionManager;
import bruca.prads.models.Mail;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 7/5/2017.
 */

public class MakeAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Button makeAppointment;
    private EditText subject,message;
    private FeedReaderDbHelper db;
    private Spinner daysSpinner,timeSpinner;
    private String spinnerDaySelection, spinnerTimeSelection;
    private String title, content, username, pass, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_make_appointment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        // SqLite database handler
        db = new FeedReaderDbHelper(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        username = user.get("name").trim();
        email = user.get("email").trim();
        pass = user.get("pass").trim();

        displayViews();
        setSpinnerAdapter();

        makeAppointment = (Button) findViewById(R.id.button_make_appointment);
        makeAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = subject.getText().toString();
                content = message.getText().toString();
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)){
                    if (TextUtils.isEmpty(title)){
                        subject.setError("Short title is required");
                    }
                    else if (TextUtils.isEmpty(content)){
                        message.setError("Content is required");
                    }
                } else if (spinnerDaySelection.equals("Select") || spinnerTimeSelection.equals("Select")) {
                    if (spinnerDaySelection.equals("Select")){

                        Snackbar.make(v, "Select a day for the appointment", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else if (spinnerTimeSelection.equals("Select")){
                        Snackbar.make(v, "Select a time for the appointment", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }else {
                    new SendMailTask().execute(email, pass);
                }

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

    private class SendMailTask extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            //user's email and password
            Mail m = new Mail(params[0], params[1]); //user's email and password

            String[] toArr = {"bae1@hw.ac.uk"}; //email recipient
            m.setTo(toArr);
            m.setFrom(params[0]); //user's email
            m.setSubject(title);
            m.setBody(content +
                    "\n" + " appointment day: " + spinnerDaySelection +
                    "\n" + "appointment time: " + spinnerTimeSelection+
                    "\n" + username + "@ Focus App");

            try {
                //m.addAttachment("/sdcard/filelocation");
                m.send();
                return true;
            } catch(Exception e) {
                //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                Log.e("MailApp", "Could not send email", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                Toast.makeText(MakeAppointment.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                subject.getText().clear();
                message.getText().clear();
            } else {
                Toast.makeText(MakeAppointment.this, "Email was not sent.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setSpinnerAdapter(){
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> daysAdapter = ArrayAdapter.createFromResource(MakeAppointment.this,
                R.array.days, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(MakeAppointment.this,
                R.array.time, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        daysSpinner.setAdapter(daysAdapter);
        timeSpinner.setAdapter(timeAdapter);
    }

    private void displayViews(){
        timeSpinner = (Spinner)findViewById(R.id.time_spinner);
        timeSpinner.setOnItemSelectedListener(this);
        daysSpinner = (Spinner)findViewById(R.id.days_spinner);
        daysSpinner.setOnItemSelectedListener(this);
        subject = (EditText)findViewById(R.id.appointment_title);
        message = (EditText)findViewById(R.id.editTextMessage);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        int parentId = parent.getId();
        int timeSpinnerItemId = timeSpinner.getId();
        int daySpinnerItemId = daysSpinner.getId();

        if (parentId == timeSpinnerItemId){
            spinnerTimeSelection = (String)parent.getItemAtPosition(pos);
        }else if (parentId == daySpinnerItemId){
            spinnerDaySelection = (String)parent.getItemAtPosition(pos);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
