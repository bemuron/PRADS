package bruca.prads.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bruca.prads.helpers.Note;
import bruca.prads.R;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 5/8/2017.
 */

public class GoalsListFragment extends Fragment {
    View rootView;
    private ListView listView;
    FeedReaderDbHelper dbhelper;
    SimpleCursorAdapter adapter;
    OnNoteSelectedListener mCallback;
    private ArrayList<Note> mNoteList = new ArrayList<>();
    Cursor c;
    ArrayList<String> allNotes = new ArrayList<>();

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public GoalsListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static GoalsListFragment newInstance() {
            GoalsListFragment fragment = new GoalsListFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

/*
            // create ContextThemeWrapper from the original Activity Context with the custom theme
            final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MyStudiesTheme);

            // clone the inflater using the ContextThemeWrapper
            LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
*/
            rootView = inflater.inflate(R.layout.goals_list, container, false);
           // Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

            dbhelper = new FeedReaderDbHelper(getContext());
           // listView = (ListView) rootView.findViewById(R.id.listview1);

            //async to do stuff in background
            //new getNoteTitles().execute();
            getAllNotes();

            //Report that this fragment would like to participate in
            // populating the options menu by receiving a call to
            setHasOptionsMenu(true);

            return rootView;
        }

    // Container Activity must implement this interface
    public interface OnNoteSelectedListener {
        void noteSelected();
    }

    @Override
    public void onAttach(Context activity) { //Try Context context as the parameter. It is not deprecated
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNoteSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOptionSelectedListener");
        }
    }

    private void getAllNotes(){
        Note note = new Note();
        c = dbhelper.getNotes();
        c.moveToFirst();

        while (!c.isAfterLast()) {
            note.setId(c.getInt(c.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
            note.setTitle(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_NOTE_TITLE)));
            note.setContent(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_NOTE_CONTENT)));
            mNoteList.add(note);
            c.moveToNext();
        }
    }
/*
    private void displayGoalsList()
    {

        String[] FROM = {FeedReaderDbHelper.KEY_NOTE_TITLE,
                FeedReaderDbHelper.KEY_CREATED_TIME,
                FeedReaderDbHelper.KEY_NOTE_CONTENT,
                FeedReaderDbHelper.KEY_ID};
        int[] TO = {R.id.txtnamerow, R.id.txtdate, R.id.txtremark, R.id.txtidrow};
        adapter = new SimpleCursorAdapter(getContext(), R.layout.note_list_item, c, FROM, TO,0);
        // adapter = new SimpleCursorAdapter(this, R.layout.article_list_row, c, FROM, TO, 0);
        //dbhelper.close();
        //articles = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        //adapter.setViewBinder(viewBinder);
        listView.setOnItemClickListener(new ListClickHandler());

    }
*/
    public class ListClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            // TODO Auto-generated method stub

            LinearLayout linearLayoutParent = (LinearLayout) view;
            LinearLayout linearLayoutChild = (LinearLayout) linearLayoutParent
                    .getChildAt(0);
            TextView m = (TextView) linearLayoutChild.getChildAt(1);
            Bundle dataBundle = new Bundle();
            dataBundle.putInt("id",
                    Integer.parseInt(m.getText().toString()));
            //mCallback.noteSelected(dataBundle);
        }
    }

    //async task to get stuff from db
    private class getNoteTitles extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            c = dbhelper.getNotes();
            //max = c.getCount();
            //rand = new Random();
            //c.close();
            /*
            if (c.moveToFirst()){
                do{
                    String note_title,user_weight,user_height;
                    note_title = c.getString(c.getColumnIndex(dbhelper.KEY_NOTE_TITLE));
                    //user_weight = cursor.getString(cursor.getColumnIndex(Information.WEIGHT));
                    //user_height = cursor.getString(cursor.getColumnIndex(Information.HEIGHT));
                    Note score_explanation = new Note(note_title);
                    mNoteList.add(score_explanation);
                }while (c.moveToNext());
            }
*/
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //displayGoalsList();
        }
    }
}
