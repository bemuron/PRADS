package bruca.prads.fragments;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import bruca.prads.R;
import bruca.prads.models.TestEntry;
import bruca.prads.activities.SelfEvaluationTestsActivity;
import bruca.prads.activities.SingleTestActivity;
import bruca.prads.helpers.DividerItemDecoration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bruca.prads.adapters.TestEntriesAdapter;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 5/8/2017.
 */

public class TestEntriesFragment extends Fragment implements
        TestEntriesAdapter.TestEntriesAdapterListener {
    View rootView;
    private RecyclerView recyclerView;
    ArrayList<Drawable> allDrawableImages = new ArrayList<>();
    private List<TestEntry> testEntriesList = new ArrayList<>();
    private Cursor cursor;
    FeedReaderDbHelper dbhelper;
    private TypedArray allImages;
    private TestEntriesAdapter testEntriesAdapter;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;

    public TestEntriesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TestEntriesFragment newInstance() {
        TestEntriesFragment fragment = new TestEntriesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.test_entries_fragment, container, false);
        dbhelper = new FeedReaderDbHelper(SelfEvaluationTestsActivity.getInstance());
        //async to do stuff in background
        new getTestEntries().execute();

        getAllWidgets(rootView);
        setAdapter();
        return rootView;
    }

    public void getAllWidgets(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_test_entries);
        allImages = getResources().obtainTypedArray(R.array.icons);
    }

    private void setAdapter() {
        for (int i = 0; i < allImages.length(); i++) {
            allDrawableImages.add(allImages.getDrawable(i));
        }

        testEntriesAdapter = new TestEntriesAdapter(SelfEvaluationTestsActivity.getInstance(),
                testEntriesList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SelfEvaluationTestsActivity.getInstance());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(SelfEvaluationTestsActivity.getInstance(),
                LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(testEntriesAdapter);

        actionModeCallback = new ActionModeCallback();
    }

    //async task to get stuff from db
    private class getTestEntries extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
                cursor = dbhelper.getTestEntries();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
                getAllTestEntries();

        }
    }

    private void getAllTestEntries(){
        if (cursor != null){
            cursor.moveToFirst();
            testEntriesList.clear();
            while (!cursor.isAfterLast()){

                TestEntry testEntry = new TestEntry();
                testEntry.setId(cursor.getInt(cursor.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                testEntry.setTitle(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE_NAME)));
                testEntry.setNoteContent(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NOTE)));
                testEntry.setTestName(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_NAME)));
                testEntry.setDateCreated(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_DATE_TAKEN)));
                testEntry.setTestScore(cursor.getInt(cursor.getColumnIndex(FeedReaderDbHelper.KEY_TEST_SCORE)));
                testEntry.setColor(getRandomMaterialColor("400"));

                //get the date the mood was created inorder to separate the time and date
                String dateTaken = testEntry.getDateCreated();

                SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("h:mm a", Locale.getDefault());
                Date tDate, tTime;
                String testDate = null;
                String testTime = null;
                try {
                    tDate = df.parse(dateTaken);
                    testDate = df.format(tDate);
                    tTime = df1.parse(dateTaken);
                    testTime = df1.format(tTime);
                } catch (ParseException e) {
                    e.getMessage();
                    e.printStackTrace();
                }
                //set the date and time the mood was recorded
                testEntry.setDateTestTaken(testDate);
                testEntry.setTimeTestTaken(testTime);

                testEntriesList.add(testEntry);
                cursor.moveToNext();
            }
            cursor.close();
        }
        testEntriesAdapter.notifyDataSetChanged();
        //swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array",
                getActivity().getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }


    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            //actionMode = getActivity().startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    @Override
    public void onIconImportantClicked(int position) {

    }

    @Override
    public void onMessageRowClicked(int position) {
        // read the message which removes bold from the row
        TestEntry testEntry = testEntriesList.get(position);
        testEntry.setRead(true);
        //testEntry.set(position, testEntry);
        Intent intent = new Intent(SelfEvaluationTestsActivity.getInstance(), SingleTestActivity.class);
        intent.putExtra("entry-ID", testEntry.getId());
        startActivity(intent);
        testEntriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRowLongClicked(int position) {

    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            //actionMode = getActivity().startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        testEntriesAdapter.toggleSelection(position);
        int count = testEntriesAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            // disable swipe refresh if action mode is enabled
            // swipeRefreshLayout.setEnabled(false);
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // delete all the selected messages
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            testEntriesAdapter.clearSelections();
            //swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    testEntriesAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteMessages() {
        testEntriesAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                testEntriesAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            testEntriesAdapter.removeData(selectedItemPositions.get(i));
        }
        //testEntriesAdapter.notifyDataSetChanged();
    }
}
