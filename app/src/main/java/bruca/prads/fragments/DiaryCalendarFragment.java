package bruca.prads.fragments;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bruca.prads.R;
import bruca.prads.activities.MoodDiaryActivity;
import bruca.prads.models.DiaryEntry;
import database.FeedReaderDbHelper;

import static android.support.v4.content.ContextCompat.getDrawable;

/**
 * Created by Emo on 5/8/2017.
 */

public class DiaryCalendarFragment extends Fragment {
    View rootView;
    CaldroidFragment caldroidFragment;
    private CalendarView calendar;
    private FeedReaderDbHelper dbhelper;
    private Cursor c,cursor;
    private List<String> moodDatesList = new ArrayList<>();
    private TextView numVeryGood,numOkay,numGood,numDepressed,numAnxious,numStressed;
    private SimpleCursorAdapter adapter;
    private FrameLayout calendarContainer;
    private DiaryEntry currentDiaryEntry = null;
    private List<Entry> depressionEntry = new ArrayList<>();
    List<String> dates = new ArrayList<>();
    // the labels that should be drawn on the XAxis
    final String[] dateTaken = new String[] { "19/05/2017", "20/05/2017",
            "21/05/2017", "22/05/2017" };

        public DiaryCalendarFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DiaryCalendarFragment newInstance() {
            DiaryCalendarFragment fragment = new DiaryCalendarFragment();
            return fragment;
        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.diary_calendar_view, container, false);
            if (caldroidFragment != null) {
                caldroidFragment = null;
                caldroidCalendar();
                //getActivity().getSupportFragmentManager().beginTransaction().
                  //      replace(R.id.cal_container, caldroidFragment).commit();
            }else{
                caldroidCalendar();

            }
                dbhelper = new FeedReaderDbHelper(MoodDiaryActivity.getInstance());
            getAllWidgets(rootView);
            //async to do stuff in background
            new getTestData().execute();

            //depressionGraph();
            return rootView;
        }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }else{
            caldroidCalendar();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getAllWidgets(View view){
        calendarContainer = (FrameLayout)view.findViewById(R.id.cal_container);
        numAnxious = (TextView)view.findViewById(R.id.num_anxious);
        numDepressed = (TextView)view.findViewById(R.id.num_depressed);
        numGood = (TextView)view.findViewById(R.id.num_good);
        numOkay = (TextView)view.findViewById(R.id.num_okay);
        numStressed = (TextView)view.findViewById(R.id.num_stressed);
        numVeryGood = (TextView)view.findViewById(R.id.num_verygood);
        //calendar = (CalendarView) view.findViewById(R.id.diaryCalendar);
    }

    //method to embed the caldroid calendar
    private void caldroidCalendar(){
        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

        caldroidFragment.setCaldroidListener(listener);

        caldroidFragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.cal_container, caldroidFragment).commit();

    }

    //async task to get stuff from db
    private class getTestData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            cursor = dbhelper.getDiaryEntries();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            getCalendarData();
        }
    }


    private void getCalendarData(){
        int counter = 0;
        if (cursor != null){
            cursor.moveToFirst();

            while(counter < cursor.getCount())
            {
                if (cursor.moveToPosition(counter)) {
                    currentDiaryEntry = new DiaryEntry();
                    currentDiaryEntry.setId(cursor.getInt(cursor.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                    currentDiaryEntry.setTitle(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_NAME)));
                    currentDiaryEntry.setNoteContent(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_NOTES)));
                    currentDiaryEntry.setDateCreated(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_DATE_TIME_TAKEN)));
                    currentDiaryEntry.setTestScore(cursor.getInt(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_SCORE)));
                    currentDiaryEntry.setDateMoodTaken(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_DATE_TAKEN)));
                    currentDiaryEntry.setTimeMoodTaken(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_TIME_TAKEN)));
/*
                    //get the date the mood was created inorder to separate the time and date
                    String dateTaken = currentDiaryEntry.getDateCreated();

                    SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    SimpleDateFormat df1 = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    Date dateMood, timeMood;
                    String moodDate = null;
                    String moodTime = null;
                    try {
                        dateMood = df.parse(dateTaken);
                        moodDate = df.format(dateMood);
                        timeMood = df1.parse(dateTaken);
                        moodTime = df1.format(timeMood);
                    } catch (ParseException e) {
                        e.getMessage();
                        e.printStackTrace();
                    }
                    //set the date and time the mood was recorded
                    currentDiaryEntry.setDateMoodTaken(moodDate);
                    currentDiaryEntry.setTimeMoodTaken(moodTime);
*/
                    moodDatesList.add(currentDiaryEntry.getDateMoodTaken());
                    //get the image name and save the id
                    String imagename = currentDiaryEntry.getTitle().toLowerCase(Locale.US);
                    //remove space between the name
                    imagename = imagename.replace(" ","");
                    int imageId = getResources().getIdentifier(imagename,
                            "drawable", getActivity().getPackageName());

                    dateTimeTaken(imageId,currentDiaryEntry.getDateMoodTaken());

                    //labels for the xaxis
                    dates.add(counter, currentDiaryEntry.getDateMoodTaken());
                    //yaxis values
                    try {
                        depressionEntry.add(new Entry((float) counter, currentDiaryEntry.getTestScore()));

                    }catch (ArrayIndexOutOfBoundsException e){
                        e.getMessage();
                        e.printStackTrace();
                    }

                }

                counter++;
            }
        }else{
            Toast.makeText(MoodDiaryActivity.getInstance(), "No data to show",
                    Toast.LENGTH_LONG).show();
        }
    }

    //method to get day mood stats
    private void getMoodDayStats(String date){
        String[]moods = {"Very Good","Okay","Good","Depressed","Anxious","Stressed"};
        int num = 0;
        for (String mood : moods) {
            c = dbhelper.getMoodDayStats(mood, date);
            if (c != null) {
                c.moveToFirst();
                num = c.getCount();
                switch (mood) {
                    case "Very Good":
                        numVeryGood.setText(String.valueOf(num));
                        break;
                    case "Good":
                        numGood.setText(String.valueOf(num));
                        break;
                    case "Okay":
                        numOkay.setText(String.valueOf(num));
                        break;
                    case "Depressed":
                        numDepressed.setText(String.valueOf(num));
                        break;
                    case "Stressed":
                        numStressed.setText(String.valueOf(num));
                        break;
                    case "Anxious":
                        numAnxious.setText(String.valueOf(num));
                        break;
                    default:
                        //numVeryGood.setText("0");
                }
            }
            c.close();
        }

    }

    //method to add the mood icons as background images to the date cells
    private void dateTimeTaken(int iconId, String dateTaken){
        //String dateTaken = "Jun 17, 2017 - 10:06 PM";
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = df.parse(dateTaken);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Drawable drawable1 = getDrawable(MoodDiaryActivity.getInstance(),iconId);

        caldroidFragment.setBackgroundDrawableForDate(drawable1,date);
        caldroidFragment.refreshView();
    }

        final CaldroidListener listener = new CaldroidListener() {
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

            @Override
            public void onSelectDate(Date date, View view) {
                for (int i = 0; i<moodDatesList.size(); i++){
                    if (moodDatesList.get(i).equals(df.format(date)) ){

                        getMoodDayStats(moodDatesList.get(i));
                        //numVeryGood.setText("5");
                    }
                }
            }

            @Override
            public void onChangeMonth(int month, int year) {
            }

            @Override
            public void onLongClickDate(Date date, View view) {
            }

            @Override
            public void onCaldroidViewCreated() {
                //Toast.makeText(MoodDiaryActivity.getInstance(),
                  //      "Caldroid view is created",
                    //    Toast.LENGTH_SHORT).show();
                //caldroidFragment.getWeekdayGridView();
                // Supply your own adapter to weekdayGridView (SUN, MON, etc)
                //caldroidFragment.getWeekdayGridView().setAdapter(YOUR_ADAPTER);
            }

        };




}
