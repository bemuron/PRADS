package bruca.prads.fragments;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class DiaryGraphFragment extends Fragment {
    View rootView;
    ArrayList<Drawable> allDrawableImages = new ArrayList<>();
    private LineChart lineChart;
    private FeedReaderDbHelper dbhelper;
    private Button getData;
    private Cursor c,cursor;
    private TextView graphTitle;
    private String dateTimeFormat = "MMM dd, yyyy - h:mm a";
    private List<Entry> depressionEntry = new ArrayList<>();
    private String[] moods = new String[]{"VeryGood","Okay", "Good","Stressed","Anxious","Depressed" };
    List<String> dates = new ArrayList<>();

        public DiaryGraphFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DiaryGraphFragment newInstance() {
            DiaryGraphFragment fragment = new DiaryGraphFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.diary_graph_view, container, false);
            dbhelper = new FeedReaderDbHelper(MoodDiaryActivity.getInstance());
            getAllWidgets(rootView);
            buttonGetGraphData();

            //depressionGraph();
            return rootView;
        }

    public void getAllWidgets(View view){
        getData = (Button)view.findViewById(R.id.mood_graph_button);
        graphTitle = (TextView)view.findViewById(R.id.mood_graph_title);
        lineChart = (LineChart)view.findViewById(R.id.diaryLineChart);
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
            if (cursor.getCount()>1) {
                getGraphData();
                depressionGraph();
            }else{
                Toast.makeText(MoodDiaryActivity.getInstance(), "Insufficient data to be represented" +
                                " on the graph",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    //method to get the data from the db
    private void buttonGetGraphData(){
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //async to do stuff in background
                    new getTestData().execute();
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();

                }catch (Exception e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }

            }
        });
    }

    private void getGraphData(){
        int counter = 0;

        if (cursor != null){
            cursor.moveToFirst();

            while(counter < 5)
            {
                if (cursor.moveToPosition(counter)) {

                    DiaryEntry diaryEntry = new DiaryEntry();
                    diaryEntry.setId(cursor.getInt(cursor.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                    diaryEntry.setTitle(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_NAME)));
                    diaryEntry.setNoteContent(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_NOTES)));
                    diaryEntry.setDateCreated(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_DATE_TIME_TAKEN)));
                    diaryEntry.setTestScore(cursor.getInt(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_SCORE)));
                    diaryEntry.setDateMoodTaken(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_DATE_TAKEN)));
                    diaryEntry.setTimeMoodTaken(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_TIME_TAKEN)));

                    //get the day only
                    String day = dayOfTheWeek(diaryEntry.getDateCreated(),dateTimeFormat);

                    //graph title
                    int numResults = counter + 1;
                    graphTitle.setText("Mood trend for last " + numResults+ " entries.");

                    try {
                        //labels for the xaxis
                        dates.add(counter, day+" "+diaryEntry.getDateMoodTaken());

                        //yaxis values
                        depressionEntry.add(new Entry((float) counter, diaryEntry.getTestScore()));
                    }catch (IndexOutOfBoundsException e){
                        System.out.println(e.getMessage());
                    }
                }

                counter++;
            }
            cursor.close();

        }
    }

    //method to get only the day in short form e.g Tue
    private String dayOfTheWeek(String inputDate, String format){

        //convert the string to the month format required
        //SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault());
        Date date = null;
        try {
            date = new SimpleDateFormat(format,Locale.getDefault()).parse(inputDate);
        } catch (ParseException e) {
            e.getMessage();
            e.printStackTrace();
        }
        return new SimpleDateFormat("EE", Locale.ENGLISH).format(date);

    }

    private void depressionGraph(){
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates.get((int) value);
                //return dates[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            //@Override
            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(1f);
        yAxis.setAxisMaximum(6f);
        yAxis.setLabelCount(6,true);
        //yAxis.setValueFormatter(yAxisFormat);

        LineDataSet depressionDataSet =new LineDataSet(depressionEntry,"Mood trend");
        depressionDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        depressionDataSet.setColor(Color.BLUE);
        depressionDataSet.setHighlightEnabled(true);//Set this to true to allow highlighting via touch for this specific

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(depressionDataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.setDescription(null);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.invalidate();

    }

    IAxisValueFormatter yAxisFormat = new IAxisValueFormatter() {
        private Drawable drawable1 = getDrawable(MoodDiaryActivity.getInstance(),R.drawable.verygood);
        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            //if ((int)value == 1)
                return moods[(int) value];

            //return dates[(int) value];
        }

        // we don't draw numbers, so no decimal digits needed
        //@Override
        public int getDecimalDigits() {  return 0; }
    };


}
