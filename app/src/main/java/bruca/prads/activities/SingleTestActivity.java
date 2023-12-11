package bruca.prads.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import bruca.prads.R;
import bruca.prads.models.TestEntry;
import database.FeedReaderDbHelper;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;

/**
 * Created by Emo on 6/23/2017.
 */

public class SingleTestActivity extends AppCompatActivity {
    private final static int WRITE_EXTERNAL_RESULT = 100;
    private FeedReaderDbHelper dbhelper;
    private Cursor c;
    private EditText mTitleEditText,mContentEditText,userNote;
    private TextView reportHeader, testOfficialName, timetaken,
            testScore,testResult,scoreDescription,testNote;
    int entryId;
    private Button save;
    private File pdfFile;
    private TestEntry mCurrentEntry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_single_test_entry);
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
        entryId = intent.getIntExtra("entry-ID",0);
            new getSingleEntry().execute();
        //  }
        //}

        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter(){
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu){
                //do something with the menu items or return false if they are not to be
                //shown
                return true;
            }
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem){
                int id = menuItem.getItemId();

                if (id == R.id.action_save_pdf){
                    //checking if we have been given the permission
                    boolean result = requestWriteExternalStoragePermission();
                    if (result){
                        try {
                            createPdf();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            e.getMessage();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }

                }
                return false;
            }
        });

        save = (Button)findViewById(R.id.test_report_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestWriteExternalStoragePermission();
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
            c = dbhelper.getSingleEntry(entryId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            getSingleNote();
            displayEntry();
        }
    }

    private void createPdf()throws FileNotFoundException, DocumentException {
        // Create Directory in External Storage
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Test_Reports");
        if (!myDir.mkdirs()) {
            Log.i(LOG_TAG, "Directory not created");
        }
        //myDir.mkdirs();

        // Destination Folder and File name
        String fileName = mCurrentEntry.getTestName() + "_"+ mCurrentEntry.getDateCreated() + ".pdf";
        //String FILE = Environment.getExternalStorageDirectory().toString()
          //      +"/Test_Reports/"+ "test.pdf";
        pdfFile = new File(myDir, fileName);
        //File FILE = new File(getExternalFilesDir("Test_reports"), "test.pdf");
        OutputStream fos = new FileOutputStream(pdfFile);

        // Create New Blank Document
        Document document = new Document(PageSize.A4);

        // Create Pdf Writer for Writting into New Created Document
        PdfWriter.getInstance(document, fos);
        // Open Document for Writting into document
        document.open();
        //add document properties
        documentProperties(document);
        //add the content of the report
        writeReport(document);
        // Close Document after writing all content
        document.close();

        //ask if user wants to view the file
        promptForNextAction(myDir);

    }

    // Set PDF document Properties
    public void documentProperties(Document document)
    {
        document.addTitle(mCurrentEntry.getTestName());
        document.addSubject("Self evaluation test");
        document.addKeywords("prevention, score, results");
        //document.addAuthor("TAG");
        //document.addCreator("TAG");
    }

    private void writeReport(Document document)throws DocumentException{
        // Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                | Font.UNDERLINE, BaseColor.BLACK);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        // Start New Paragraph
        Paragraph reportTitle = new Paragraph();
        // Set Font in this Paragraph
        reportTitle.setFont(titleFont);
        // Add item into Paragraph
        //write the title of the report
        reportTitle.add(reportHeader.getText().toString() + "\n");
        reportTitle.setFont(catFont);
        //write the official name of the test
        reportTitle.add("\n" + testOfficialName.getText().toString() + "\n");
        reportTitle.setAlignment(Element.ALIGN_CENTER);
        // Add all above details into Document
        document.add(reportTitle);

        //New Paragraph for test score and date taken
        Paragraph dateScore = new Paragraph();
        dateScore.setFont(smallBold);
        dateScore.add(timetaken.getText().toString() + "\n");
        dateScore.add(testScore.getText().toString());
        dateScore.add(" "+ testResult.getText().toString());
        dateScore.setAlignment(Element.ALIGN_CENTER);
        document.add(dateScore);

        //new paragraph for score description
        Paragraph describeScore = new Paragraph();
        describeScore.setFont(smallBold);
        describeScore.add("\n \n Score Description : \n ");
        describeScore.setFont(normal);
        describeScore.add("\n" + scoreDescription.getText().toString());
        describeScore.setFont(smallBold);
        document.add(describeScore);

        //new paragraph for score description
        Paragraph userNotes = new Paragraph();
        userNotes.setFont(smallBold);
        userNotes.add("\n \n My notes : \n ");
        userNotes.setFont(normal);
        userNotes.add("\n" + userNote.getText().toString());
        userNotes.setFont(smallBold);
        document.add(userNotes);

        // Create new Page in PDF
        document.newPage();
    }

    private boolean requestWriteExternalStoragePermission() {
        //checking for marshmallow devices and above in order to execute runtime
        //permissions
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Request")
                            .setMessage("Permission is required for the app to write and read from storage")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(SingleTestActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            WRITE_EXTERNAL_RESULT);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(SingleTestActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_RESULT);
                }
                return false;
            }else {
                return true;
            }
        }else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_RESULT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // allowed
                    try {
                        createPdf();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        e.getMessage();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
            } else {
                // denied
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_LONG).show();
            }
            break;
        }
    }
}

    //method to copy start email client and copy relevant text and the pdf file
    private void emailPdf()
    {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT,reportHeader.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, userNote.getText().toString());
        Uri uri = Uri.parse(pdfFile.getAbsolutePath());
        email.putExtra(Intent.EXTRA_STREAM, uri);
        //need this to prompt email client only
        email.setType("message/rfc822");
        startActivity(email);
    }

    private void promptForNextAction(File storageDir)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mCurrentEntry.getTestName() + "_"+ mCurrentEntry.getDateCreated() + ".pdf Created");
        builder.setMessage("File saved in "+ storageDir + ". What do you want to do next?");
        builder.setPositiveButton("View file",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewPdf();
            }
        });

        builder.setNeutralButton("Email pdf",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                emailPdf();
            }
        });

        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    //method to open the pdf file created
    private void viewPdf(){
    Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    startActivity(intent);
    }

    //method to display the test results
    private void displayEntry() {
        reportHeader = (TextView) findViewById(R.id.test_report_header);
        reportHeader.setText(mCurrentEntry.getTestName());
        reportHeader.append(" self-evaluation test");

        testOfficialName = (TextView) findViewById(R.id.test_official_name);
        testResult = (TextView)findViewById(R.id.test_report_result);

        testResult.setText(String.valueOf(mCurrentEntry.getTestScore()));

        switch(mCurrentEntry.getTestName()){
            case "Depression":
                testOfficialName.setText(R.string.MDI);
                testResult.append(" (" + mCurrentEntry.getTitle() + ")");
                break;
            case "Anxiety":
                testOfficialName.setText(R.string.GAD7);
                testResult.append(" (" + mCurrentEntry.getTitle() + ")");
                break;
            case "Stress":
                testOfficialName.setText(R.string.uss_scale);
                testResult.append("(" + mCurrentEntry.getTitle() + ")");
                break;
            case "General Wellbeing":
                testOfficialName.setText(R.string.WHO5);
                testResult.append(" (" + mCurrentEntry.getTitle() + ")");
                break;
        }

        timetaken = (TextView) findViewById(R.id.test_report_time);
        timetaken.setText("Test taken on "+ mCurrentEntry.getDateCreated());

        testScore = (TextView)findViewById(R.id.test_report_score);
        testScore.setText("Test score: ");

        scoreDescription = (TextView)findViewById(R.id.test_score_description);
        scoreDescription.setText(mCurrentEntry.getScoreDescription());

        testNote = (TextView)findViewById(R.id.test_report_note);
        testNote.setText("My notes;");

        userNote = (EditText) findViewById(R.id.test_user_note);
        userNote.setText(mCurrentEntry.getNoteContent());
        //userNote.setTextSize(15);
    }

    private void getSingleNote(){
        //swipeRefreshLayout.setRefreshing(true);
        //Cursor cursor = dbhelper.getNotes();
        if (c != null){
            c.moveToFirst();
            while (!c.isAfterLast()){

                mCurrentEntry = new TestEntry();
                mCurrentEntry.setId(c.getInt(c.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                mCurrentEntry.setTitle(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE_NAME)));
                mCurrentEntry.setNoteContent(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NOTE)));
                mCurrentEntry.setTestName(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NAME)));
                mCurrentEntry.setDateCreated(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_TEST_DATE_TAKEN)));
                mCurrentEntry.setTestScore(c.getInt(c.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE)));
                mCurrentEntry.setScoreDescription(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE_DESCRIPTION)));
                c.moveToNext();
            }
            c.close();
        }
    }

    /*
    method to update user note
   */
    private boolean updateEntry(){

        String title = userNote.getText().toString();
        if (TextUtils.isEmpty(title)){
            mTitleEditText.setError("Title is required");
            return false;
        }

        if (entryId > 0) {
            dbhelper.updateEntry(entryId,userNote.getText().toString());
            Intent intent = new Intent(SingleTestActivity.this, SelfEvaluationTestsActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fab_tests_speed_dial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, SelfEvaluationTestsActivity.class);
            startActivity(intent);
            this.finish();
            return true;
        }
        if (id == R.id.action_save_pdf){
            //checking if we have been given the permission
            boolean result = requestWriteExternalStoragePermission();
            if (result){
                try {
                    createPdf();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    e.getMessage();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
