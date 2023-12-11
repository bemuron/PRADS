package bruca.prads.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import java.util.Locale;
import java.util.Random;

import bruca.prads.R;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 7/17/2017.
 */

public class ConfessionsCategoriesActivity extends AppCompatActivity {
    private FeedReaderDbHelper dbhelper;
    private Cursor c,cursor;
    private SimpleCursorAdapter adapter;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positive_confessions_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        dbhelper = new FeedReaderDbHelper(this);
        //async to do stuff in background
        new loadCategories().execute();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //get the confessions categories from the db
    private class loadCategories extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            //c = dbhelper.getAllPhrases();//all content in the db
            //max = c.getCount();//count them
            //rand = new Random();//random object to select a random phrase id
            //c.close();
            //get categories from db
            cursor = dbhelper.getConfessionsCategories();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            displayCategories();
        }
    }

    public void displayCategories(){

        String[] FROM = {FeedReaderDbHelper.KEY_CATEGORY_NAME, FeedReaderDbHelper.KEY_CATEGORY_IMAGE};
        int[] TO = {R.id.category_name,R.id.category_pic};
        adapter = new SimpleCursorAdapter(this, R.layout.grid_item2, cursor, FROM, TO,0);
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
        adapter.setViewBinder(viewBinder);
        gridView.setOnItemClickListener(new ListClickHandler());

    }//closing display categories

    final SimpleCursorAdapter.ViewBinder viewBinder = new SimpleCursorAdapter.ViewBinder(){
        @Override
        public boolean setViewValue(View view, Cursor c, int columnIndex ){
            if (view.getId() !=R.id.category_pic)//checking if this is the view we want
                return false;//otherwise the adapter will handle things its usual way

            //get the category image
            String imagename = c.getString(columnIndex).toLowerCase(Locale.US);
            int imageId = getResources().getIdentifier(imagename,
                    "drawable", getPackageName());
            ((ImageView) view).setImageResource(imageId);
            return true;
        }
    };

    public class ListClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            // TODO Auto-generated method stub
            int id = cursor.getInt(0);

            Intent intent = new Intent(ConfessionsCategoriesActivity.this, PositiveConfessionsActivity.class);
            intent.putExtra("item-ID", id);
            startActivity(intent);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.positive_confessions_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about_positive_confessions) {
            AboutPositiveConfessions.Show(ConfessionsCategoriesActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
