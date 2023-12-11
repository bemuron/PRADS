package bruca.prads.fragments;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bruca.prads.R;
import bruca.prads.activities.DisplayNoteActivity;
import bruca.prads.activities.MoodDiaryActivity;
import bruca.prads.activities.NewMoodActivity;
import bruca.prads.activities.SingleDiaryEntryActivity;
import bruca.prads.adapters.DiaryEntriesAdapter;
import bruca.prads.helpers.DividerItemDecoration;
import bruca.prads.models.DiaryEntry;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 5/8/2017.
 */

public class DiaryEntriesFragment extends Fragment implements
        DiaryEntriesAdapter.DiaryEntriesAdapterListener {
    View rootView;
    private RecyclerView recyclerView;
    private List<DiaryEntry> diaryEntriesList = new ArrayList<>();
    private Cursor cursor;
    private FeedReaderDbHelper dbhelper;
    private TypedArray allImages;
    private DiaryEntriesAdapter diaryEntriesAdapter;
    //private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;

    public DiaryEntriesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DiaryEntriesFragment newInstance() {
        DiaryEntriesFragment fragment = new DiaryEntriesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.diary_entries_fragment, container, false);
        dbhelper = new FeedReaderDbHelper(MoodDiaryActivity.getInstance());
        //async to do stuff in background
        new getDiaryEntries().execute();

        getAllWidgets(rootView);
        setAdapter();
        return rootView;
    }

    public void getAllWidgets(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_diary_entries);
    }

    private void setAdapter() {

        diaryEntriesAdapter = new DiaryEntriesAdapter(MoodDiaryActivity.getInstance(),
                diaryEntriesList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MoodDiaryActivity.getInstance());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(MoodDiaryActivity.getInstance(),
                LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(diaryEntriesAdapter);

    }

    //async task to get stuff from db
    private class getDiaryEntries extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            cursor = dbhelper.getDiaryEntries();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            getAllDiaryEntries();
        }
    }

    private void getAllDiaryEntries(){
        //swipeRefreshLayout.setRefreshing(true);
        if (cursor != null){
            cursor.moveToFirst();
            //if (diaryEntriesList != null) {
                diaryEntriesList.clear();
            //}
            while (!cursor.isAfterLast()){

                DiaryEntry diaryEntry = new DiaryEntry();
                diaryEntry.setId(cursor.getInt(cursor.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                diaryEntry.setTitle(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_NAME)));
                diaryEntry.setNoteContent(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_NOTES)));
                diaryEntry.setDateCreated(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_DATE_TIME_TAKEN)));
                diaryEntry.setTestScore(cursor.getInt(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_SCORE)));
                diaryEntry.setDateMoodTaken(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_DATE_TAKEN)));
                diaryEntry.setTimeMoodTaken(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_MOOD_TIME_TAKEN)));

                //get the image name and save the id
                String imagename = diaryEntry.getTitle().toLowerCase(Locale.US);
                //remove space between the name
                imagename = imagename.replace(" ","");
                int imageId = getResources().getIdentifier(imagename,
                        "drawable", getActivity().getPackageName());
                diaryEntry.setImageID(imageId);
                //diaryEntry.setPicture(diaryEntry.getTitle().toLowerCase());
                diaryEntry.setColor(getRandomMaterialColor("400"));

                diaryEntriesList.add(diaryEntry);
                cursor.moveToNext();
            }
            cursor.close();
        }
        diaryEntriesAdapter.notifyDataSetChanged();
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
        DiaryEntry diaryEntry = diaryEntriesList.get(position);
        diaryEntry.setRead(true);
        //testEntry.set(position, testEntry);
        Intent intent = new Intent(MoodDiaryActivity.getInstance(), SingleDiaryEntryActivity.class);
        intent.putExtra("entry-ID", diaryEntry.getId());
        startActivity(intent);
        diaryEntriesAdapter.notifyDataSetChanged();
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
        diaryEntriesAdapter.toggleSelection(position);
        int count = diaryEntriesAdapter.getSelectedItemCount();

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
            diaryEntriesAdapter.clearSelections();
            //swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    diaryEntriesAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteMessages() {
        diaryEntriesAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                diaryEntriesAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            diaryEntriesAdapter.removeData(selectedItemPositions.get(i));
        }

        //diaryEntriesAdapter.notifyDataSetChanged();
    }

}
