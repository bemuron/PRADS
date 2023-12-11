package bruca.prads.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bruca.prads.R;
import bruca.prads.activities.SelfEvaluationTestsActivity;
import bruca.prads.models.TestEntry;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 5/8/2017.
 */

public class GraphFragment extends Fragment implements AdapterView.OnItemSelectedListener  {
    View rootView;
    private LineChart lineChart, depressionLineChart, anxietyLineChart, stressLineChart;
    private Spinner testsSpinner;
    private String spinnerTestSelection;
    private FeedReaderDbHelper dbhelper;
    private TextView graphTitle,graphTitle1,graphTitle2,graphTitle3;
    private Button viewFilteredResults, filter;
    private Cursor cursor, depressionCursor,anxietyCursor,stressCursor,wellbeingCursor;
    private Resources res;
    private Dialog spinnerDialog;
    private TestEntry currentTestEntry = null;
    private List<Entry> depressionEntry= new ArrayList<>();
    private List<Entry> stressEntry = new ArrayList<>();
    private List<Entry> anxietyEntry = new ArrayList<>();
    private List<Entry> generalWellbeingEntry = new ArrayList<>();
    private List<Entry> singleEntry = new ArrayList<>();
    private List<String> dates = new ArrayList<>();
    private List<String> anxietyDates = new ArrayList<>();
    private List<String> stressDates = new ArrayList<>();
    private List<String> depressionDates = new ArrayList<>();
    private View line1, line2, line3;
    private String dateTimeFormat = "MMM dd, yyyy - h:mm a";

    public GraphFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GraphFragment newInstance() {
        GraphFragment fragment = new GraphFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.graph_view, container, false);
        dbhelper = new FeedReaderDbHelper(SelfEvaluationTestsActivity.getInstance());
        getAllWidgets(rootView);
        res = getResources();
       // myMonthsArray = res.getStringArray(R.array.myMonths);
        setSpinnerAdapter();
        //async to do stuff in background
        //new getTestData().execute();
        //new getSingleCategoryTestData().execute();

        //depressionGraph();
        return rootView;
    }

    public void getAllWidgets(View view){
        //dialog to filter the results
        spinnerDialog = new Dialog(SelfEvaluationTestsActivity.getInstance());
        spinnerDialog.setContentView(R.layout.filter_test_results);
        spinnerDialog.setTitle("Choose which data to show");
        //spinners in dialog
        testsSpinner = (Spinner) spinnerDialog.findViewById(R.id.tests_spinner);

        viewFilteredResults = (Button)spinnerDialog.findViewById(R.id.test_graph_view);

        testsSpinner.setOnItemSelectedListener(this);

        graphTitle = (TextView)view.findViewById(R.id.test_graph_title);
        graphTitle1 = (TextView)view.findViewById(R.id.test_graph_title1);
        graphTitle2 = (TextView)view.findViewById(R.id.test_graph_title2);
        graphTitle3 = (TextView)view.findViewById(R.id.test_graph_title3);
        filter = (Button)view.findViewById(R.id.test_graph_filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterTestResults();
            }
        });
        lineChart = (LineChart)view.findViewById(R.id.linechart);
        depressionLineChart = (LineChart)view.findViewById(R.id.linechart1);
        anxietyLineChart = (LineChart)view.findViewById(R.id.linechart2);
        stressLineChart = (LineChart)view.findViewById(R.id.linechart3);
        line1 = view.findViewById(R.id.line1);
        line2 = view.findViewById(R.id.line2);
        line3 = view.findViewById(R.id.line3);
    }

    public void setSpinnerAdapter(){
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(SelfEvaluationTestsActivity.getInstance(),
                R.array.SelfEvaluationTests, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        testsSpinner.setAdapter(adapter);
    }

    private class getSingleCategoryTestData extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(SelfEvaluationTestsActivity.getInstance(), "",
                    "Preparing data", true, false);

            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    new getSingleCategoryTestData().execute().cancel(true);
                }
            });
        }

        @Override
        protected Void doInBackground(Void... arg0) {

                if (spinnerTestSelection.equals("All")){
                    depressionCursor = dbhelper.getSingleCategoryEntry("Depression");
                    anxietyCursor = dbhelper.getSingleCategoryEntry("Anxiety");
                    stressCursor = dbhelper.getSingleCategoryEntry("Stress");
                    cursor = dbhelper.getSingleCategoryEntry("General Wellbeing");
                }else {
                    cursor = dbhelper.getSingleCategoryEntry(spinnerTestSelection);
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (spinnerTestSelection.equals("All")){
                //general wellbeing
                if (cursor.getCount() > 1) {
                    getFilteredGraphData();
                    singleEntryGraph();
                } else {
                    Toast.makeText(SelfEvaluationTestsActivity.getInstance(), "Insufficient data to be represented" +
                                    " on the graph for general well-being",
                            Toast.LENGTH_LONG).show();
                }
                //depression
                if (depressionCursor.getCount() > 1) {
                    depressionLineChart.setVisibility(View.VISIBLE);
                    graphTitle1.setVisibility(View.VISIBLE);
                    line1.setVisibility(View.VISIBLE);
                    getDepressionGraphData();
                    depressionGraph();
                } else {
                    Toast.makeText(SelfEvaluationTestsActivity.getInstance(), "Insufficient data to be represented" +
                                    " on the graph for depression",
                            Toast.LENGTH_LONG).show();
                }
                //anxiety
                if (anxietyCursor.getCount() > 1) {
                    anxietyLineChart.setVisibility(View.VISIBLE);
                    graphTitle2.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.VISIBLE);
                    getAnxietyGraphData();
                    anxietyGraph();
                } else {
                    Toast.makeText(SelfEvaluationTestsActivity.getInstance(), "Insufficient data to be represented" +
                                    " on the graph for anxiety",
                            Toast.LENGTH_LONG).show();
                }
                //stress
                if (stressCursor.getCount() > 1) {
                    stressLineChart.setVisibility(View.VISIBLE);
                    graphTitle3.setVisibility(View.VISIBLE);
                    line3.setVisibility(View.VISIBLE);
                    getStressGraphData();
                    stressGraph();
                } else {
                    Toast.makeText(SelfEvaluationTestsActivity.getInstance(), "Insufficient data to be represented" +
                                    " on the graph for stress",
                            Toast.LENGTH_LONG).show();
                }
            }else {
                if (cursor.getCount() > 1) {
                    depressionLineChart.setVisibility(View.GONE);
                    anxietyLineChart.setVisibility(View.GONE);
                    stressLineChart.setVisibility(View.GONE);
                    graphTitle1.setVisibility(View.GONE);
                    graphTitle2.setVisibility(View.GONE);
                    graphTitle3.setVisibility(View.GONE);
                    line1.setVisibility(View.GONE);
                    line2.setVisibility(View.GONE);
                    line3.setVisibility(View.GONE);
                    getFilteredGraphData();
                    singleEntryGraph();
                } else {
                    Toast.makeText(SelfEvaluationTestsActivity.getInstance(), "Insufficient data to be represented" +
                                    " on the graph",
                            Toast.LENGTH_LONG).show();
                }
            }
            pd.dismiss();
        }
    }

    //method to get only the date without the time
    private String dateTaken(String inputDate, String format){

        //convert the string to the month format required
        //SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault());
        Date date = null;
        try {
            date = new SimpleDateFormat(format,Locale.getDefault()).parse(inputDate);
        } catch (ParseException e) {
            e.getMessage();
            e.printStackTrace();
        }
        return new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(date);

    }

    private void test(Cursor c){
        int cpos;
        c.moveToLast();
        int counter2 = 1;

       // for(int i = c.getCount(); i>=0; i-- ){
            while(c.moveToPrevious()){
                counter2++;
                if(counter2 == 5 ){
                    cpos = c.getPosition();
                    int score = c.getInt(c.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE));
                    //Toast.makeText(SelfEvaluationTestsActivity.getInstance(), "5 = " + cpos + score,
                      //      Toast.LENGTH_LONG).show();
                }
            }


        //}
    }

    private void getFilteredGraphData(){
        int counter = 0;
        if (cursor != null){

            test(cursor);
            cursor.moveToFirst();

            while(counter < 5)
            {
                if (cursor.moveToPosition(counter)) {

                        currentTestEntry = new TestEntry();
                        currentTestEntry.setId(cursor.getInt(cursor.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                        currentTestEntry.setTitle(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE_NAME)));
                        currentTestEntry.setNoteContent(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NOTE)));
                        currentTestEntry.setTestName(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NAME)));
                        currentTestEntry.setDateCreated(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_DATE_TAKEN)));
                        currentTestEntry.setTestScore(cursor.getInt(cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE)));
                        currentTestEntry.setTestMonth(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_MONTH_TAKEN)));

                        //set the graph title
                        int numResults = counter + 1;
                        graphTitle.setText(currentTestEntry.getTestName() +" previous "+ numResults + " entries");

                        //get the date
                        String date = dateTaken(currentTestEntry.getDateCreated(),dateTimeFormat);

                        //labels for the xaxis
                        try {
                            dates.add(counter, date);
                            //yaxis values
                            singleEntry.add(new Entry((float) counter, currentTestEntry.getTestScore()));

                        }catch (ArrayIndexOutOfBoundsException e){
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                }

                counter++;
            }
            cursor.close();
        }else{
            Toast.makeText(SelfEvaluationTestsActivity.getInstance(), "No data to show",
                    Toast.LENGTH_LONG).show();
        }
    }

    //dialog to filter the results
    private void filterTestResults(){
        viewFilteredResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    try {
                        //async to do stuff in background
                        new getSingleCategoryTestData().execute();

                        currentTestEntry = null;
                        //dates.clear();
                        singleEntry.clear();
                        depressionDates.clear();
                        depressionEntry.clear();
                        stressEntry.clear();
                        stressDates.clear();
                        anxietyEntry.clear();
                        anxietyDates.clear();

                    }catch (IndexOutOfBoundsException e){
                        e.printStackTrace();

                    }catch (Exception e){
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }

                    spinnerDialog.dismiss();
                }
        });
        spinnerDialog.show();
    }

    private void singleEntryGraph(){
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return dates.get((int) value);
            }

            // we don't draw numbers, so no decimal digits needed
            //@Override
            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setDrawAxisLine(false);
        //xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(formatter);

        LineDataSet depressionDataSet =new LineDataSet(singleEntry,currentTestEntry.getTestName());
        depressionDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        depressionDataSet.setColor(Color.BLUE);
        depressionDataSet.setHighlightEnabled(true);//Set this to true to allow highlighting via touch for this specific

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(depressionDataSet);
        //dataSets.add(anxietyDataSet);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        //lineChart.animateX(3000, Easing.EasingOption.EaseInOutBounce);
        lineChart.setDescription(null);
        //Saves the current chart state as an image to the gallery.
        // Don't forget to add "WRITE_EXTERNAL_STORAGE" permission to your manifest.
        //lineChart.saveToGallery("testresults",1);
        lineChart.invalidate();

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        int parentId = parent.getId();
        int testsSpinnerItemId = testsSpinner.getId();
        if (parentId == testsSpinnerItemId){
            spinnerTestSelection = (String)parent.getItemAtPosition(pos);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    //method to handle depression data only
    private void getDepressionGraphData(){
        int counter = 0;
        if (depressionCursor != null){
            depressionCursor.moveToFirst();

            while(counter < 5)
            {
                if (depressionCursor.moveToPosition(counter)) {

                    TestEntry depressionTestEntry = new TestEntry();
                    depressionTestEntry.setId(depressionCursor.getInt(depressionCursor.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                    depressionTestEntry.setTitle(depressionCursor.getString(depressionCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE_NAME)));
                    depressionTestEntry.setNoteContent(depressionCursor.getString(depressionCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NOTE)));
                    depressionTestEntry.setTestName(depressionCursor.getString(depressionCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NAME)));
                    depressionTestEntry.setDateCreated(depressionCursor.getString(depressionCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_DATE_TAKEN)));
                    depressionTestEntry.setTestScore(depressionCursor.getInt(depressionCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE)));
                    depressionTestEntry.setTestMonth(depressionCursor.getString(depressionCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_MONTH_TAKEN)));

                    //set the graph title
                    int numResults = counter + 1;
                    graphTitle1.setText(depressionTestEntry.getTestName() +" previous "+numResults+ " entries");

                    //get the date
                    String date = dateTaken(currentTestEntry.getDateCreated(),dateTimeFormat);

                    //labels for the xaxis
                    try {
                        depressionDates.add(counter, date);
                        //yaxis values
                        depressionEntry.add(new Entry((float) counter, depressionTestEntry.getTestScore()));

                    }catch (ArrayIndexOutOfBoundsException e){
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }

                counter++;
            }
            depressionCursor .close();
        }else{
            Toast.makeText(SelfEvaluationTestsActivity.getInstance(), "No data to show",
                    Toast.LENGTH_LONG).show();
        }
    }

    //display graph with depression data
    private void depressionGraph(){
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return depressionDates.get((int) value);
                //return dates[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            //@Override
            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = depressionLineChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setDrawAxisLine(false);
        //xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(formatter);

        LineDataSet depressionDataSet =new LineDataSet(depressionEntry,"Depression");
        depressionDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        depressionDataSet.setColor(Color.BLUE);
        depressionDataSet.setHighlightEnabled(true);//Set this to true to allow highlighting via touch for this specific

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(depressionDataSet);
        LineData data = new LineData(dataSets);
        depressionLineChart.setData(data);
        depressionLineChart.setDescription(null);
        //Saves the current chart state as an image to the gallery.
        // Don't forget to add "WRITE_EXTERNAL_STORAGE" permission to your manifest.
        // lineChart.saveToGallery("Evaluation Test Results");
        depressionLineChart.invalidate();

    }

    //method to handle depression data only
    private void getAnxietyGraphData(){
        int counter = 0;
        if (anxietyCursor != null){
            anxietyCursor.moveToFirst();

            while(counter < 5)
            {
                if (anxietyCursor.moveToPosition(counter)) {

                    TestEntry testEntry = new TestEntry();
                    testEntry.setId(anxietyCursor.getInt(anxietyCursor.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                    testEntry.setTitle(anxietyCursor.getString(anxietyCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE_NAME)));
                    testEntry.setNoteContent(anxietyCursor.getString(anxietyCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NOTE)));
                    testEntry.setTestName(anxietyCursor.getString(anxietyCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NAME)));
                    testEntry.setDateCreated(anxietyCursor.getString(anxietyCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_DATE_TAKEN)));
                    testEntry.setTestScore(anxietyCursor.getInt(anxietyCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE)));
                    testEntry.setTestMonth(anxietyCursor.getString(anxietyCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_MONTH_TAKEN)));

                    //set the graph title
                    int numResults = counter + 1;
                    graphTitle2.setText(testEntry.getTestName() +" previous "+numResults+ " entries");

                    //get the date
                    String date = dateTaken(currentTestEntry.getDateCreated(),dateTimeFormat);

                    //labels for the xaxis
                    try {
                        anxietyDates.add(counter, date);
                        //yaxis values
                        anxietyEntry.add(new Entry((float) counter, testEntry.getTestScore()));

                    }catch (ArrayIndexOutOfBoundsException e){
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }

                counter++;
            }
            anxietyCursor .close();
        }else{
            Toast.makeText(SelfEvaluationTestsActivity.getInstance(), "No data to show",
                    Toast.LENGTH_LONG).show();
        }
    }

    //anxiety graph
    private void anxietyGraph(){
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return anxietyDates.get((int) value);
            }

            // we don't draw numbers, so no decimal digits needed
            //@Override
            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = anxietyLineChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setDrawAxisLine(false);
        //xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(formatter);

        LineDataSet dataSet =new LineDataSet(anxietyEntry,"Anxiety");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.BLACK);
        dataSet.setHighlightEnabled(true);//Set this to true to allow highlighting via touch for this specific

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);
        //dataSets.add(anxietyDataSet);

        LineData data = new LineData(dataSets);
        anxietyLineChart.setData(data);
        //lineChart.animateX(3000, Easing.EasingOption.EaseInOutBounce);
        anxietyLineChart.setDescription(null);
        //Saves the current chart state as an image to the gallery.
        // Don't forget to add "WRITE_EXTERNAL_STORAGE" permission to your manifest.
        // lineChart.saveToGallery("Evaluation Test Results");
        anxietyLineChart.invalidate();

    }

    //method to handle depression data only
    private void getStressGraphData(){
        int counter = 0;
        if (stressCursor != null){
            stressCursor.moveToFirst();

            while(counter < 5)
            {
                if (stressCursor.moveToPosition(counter)) {

                    TestEntry testEntry = new TestEntry();
                    testEntry.setId(stressCursor.getInt(stressCursor.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                    testEntry.setTitle(stressCursor.getString(stressCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE_NAME)));
                    testEntry.setNoteContent(stressCursor.getString(stressCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NOTE)));
                    testEntry.setTestName(stressCursor.getString(stressCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NAME)));
                    testEntry.setDateCreated(stressCursor.getString(stressCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_DATE_TAKEN)));
                    testEntry.setTestScore(stressCursor.getInt(stressCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE)));
                    testEntry.setTestMonth(stressCursor.getString(stressCursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_MONTH_TAKEN)));

                    //set the graph title
                    int numResults = counter + 1;
                    graphTitle3.setText(testEntry.getTestName() +" previous "+numResults+ " entries");

                    //get the date
                    String date = dateTaken(currentTestEntry.getDateCreated(),dateTimeFormat);

                    //labels for the xaxis
                    try {
                        stressDates.add(counter, date);
                        //yaxis values
                        stressEntry.add(new Entry((float) counter, testEntry.getTestScore()));

                    }catch (ArrayIndexOutOfBoundsException e){
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }

                counter++;
            }
            stressCursor .close();
        }else{
            Toast.makeText(SelfEvaluationTestsActivity.getInstance(), "No data to show",
                    Toast.LENGTH_LONG).show();
        }
    }

    //stress graph
    private void stressGraph(){
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return stressDates.get((int) value);
            }

            // we don't draw numbers, so no decimal digits needed
            //@Override
            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = stressLineChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setDrawAxisLine(false);
        //xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(formatter);

        LineDataSet dataSet =new LineDataSet(stressEntry,"Stress");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.GREEN);
        dataSet.setHighlightEnabled(true);//Set this to true to allow highlighting via touch for this specific

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);
        //dataSets.add(anxietyDataSet);

        LineData data = new LineData(dataSets);
        stressLineChart.setData(data);
        //lineChart.animateX(3000, Easing.EasingOption.EaseInOutBounce);
        stressLineChart.setDescription(null);
        //Saves the current chart state as an image to the gallery.
        // Don't forget to add "WRITE_EXTERNAL_STORAGE" permission to your manifest.
        // lineChart.saveToGallery("Evaluation Test Results");
        stressLineChart.invalidate();

    }



}
