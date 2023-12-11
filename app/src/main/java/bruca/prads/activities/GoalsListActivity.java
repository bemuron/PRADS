package bruca.prads.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bruca.prads.fragments.MyStudiesFragment;
import bruca.prads.helpers.Note;
import bruca.prads.R;
import bruca.prads.adapters.GoalListAdapter;
import bruca.prads.helpers.DividerItemDecoration;
import database.FeedReaderDbHelper;

public class GoalsListActivity extends AppCompatActivity implements
        GoalListAdapter.GoalListAdapterListener{
    private ListView listView;
    FeedReaderDbHelper dbhelper;
    private Cursor c,cursor;
    SimpleCursorAdapter adapter;
    private List<Note> notes = new ArrayList<>();
    private RecyclerView recyclerView;
    private GoalListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_note_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        dbhelper = new FeedReaderDbHelper(this);

        recyclerView = (RecyclerView) findViewById(R.id.goalsListRecyclerView);
        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        //swipeRefreshLayout.setOnRefreshListener(this);
        //async to do stuff in background
        new getNoteTitles().execute();

        mAdapter = new GoalListAdapter(this, notes, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        actionModeCallback = new ActionModeCallback();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent intent = new Intent(getApplicationContext(), DisplayNoteActivity.class);
            //intent.putExtras(dataBundle);
            intent.putExtra("note-ID", 0);
            startActivity(intent);
            finish();

            }
        });
/*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        */


    }//closing onCreate

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //async task to get stuff from db
    private class getNoteTitles extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            cursor = dbhelper.getNotes();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            getAllGoalNotes();
        }
    }

    private void getAllGoalNotes(){
        //swipeRefreshLayout.setRefreshing(true);
        //Cursor cursor = dbhelper.getNotes();
        if (cursor != null){
            cursor.moveToFirst();
            notes.clear();
            while (!cursor.isAfterLast()){

                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(FeedReaderDbHelper.KEY_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_NOTE_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_NOTE_CONTENT)));
                note.setDateCreated(cursor.getString(cursor.getColumnIndex(FeedReaderDbHelper.KEY_CREATED_TIME)));
                note.setColor(getRandomMaterialColor("400"));

                notes.add(note);
                cursor.moveToNext();
            }
            cursor.close();
        }
        //mAdapter.notifyDataSetChanged();
        //swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.goals_menu, menu);
        return true;
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
            finish();
            return true;
        }
        else if (id == R.id.action_add) {
            Intent intent = new Intent(getApplicationContext(), DisplayNoteActivity.class);
            intent.putExtra("note-ID", 0);
            startActivity(intent);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    @Override
    public void onIconImportantClicked(int position) {
        // Star icon is clicked,
        // mark the message as important
        Note note = notes.get(position);
        note.setImportant(!note.isImportant());
        notes.set(position, note);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
            Note note = notes.get(position);
            note.setRead(true);
            notes.set(position, note);
            Intent intent = new Intent(GoalsListActivity.this, DisplayNoteActivity.class);
            intent.putExtra("note-ID", note.getId());
            startActivity(intent);
            finish();
            mAdapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(), "Read: " + note.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

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
            mAdapter.clearSelections();
            //swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteMessages() {
        mAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }
/*
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.favs) {

            //Intent intent = new Intent(this, FavouritesActivity.class);
            //startActivity(intent);

            Toast.makeText(getBaseContext(), "Favs",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.randomWord){

            randomPhraseID = rand.nextInt((max - min) + 1) + min;
            Intent intent = new Intent(MainActivity.this, PhraseListActivity.class);
            intent.putExtra("P-ID", randomPhraseID);
            startActivity(intent);

        }
        else if (id == R.id.about_ateso) {
            // AboutAteso.Show(MainActivity.this);
            Toast.makeText(getBaseContext(), "Favs",
                    Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND);
            String shareBody = "\n I am learning to speak Ateso using the" +
                    "Learn Ateso app." +
                    "\nDownload the Learn Ateso app from the Google Playstore.";
            //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            sharingIntent.setType("text/plain");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        else if (id == R.id.settings) {
            Toast.makeText(getApplicationContext(), "Settings",
                    Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
*/
}
