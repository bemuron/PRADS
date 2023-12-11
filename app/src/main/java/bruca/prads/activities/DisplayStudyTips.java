package bruca.prads.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import bruca.prads.fragments.DisplayStudyTipsfragment;
import bruca.prads.R;
import bruca.prads.models.SquareImageView;
import database.FeedReaderDbHelper;

/**
 * Created by Emo on 5/13/2017.
 */

public class DisplayStudyTips extends AppCompatActivity{
    FeedReaderDbHelper dbhelper;
    SimpleCursorAdapter adapter;
    CollapsingToolbarLayout collapsingToolbar;
    SquareImageView backgroundImage;
    Cursor c;
    private ViewPager mViewPager;
    int pageCount;
    Intent intent;
    private TextView lblCount;
    private int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_study_tips);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        dbhelper = new FeedReaderDbHelper(this);

        // Set up the ViewPager with the MainTabsPagerAdapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        lblCount = (TextView) findViewById(R.id.lbl_count);

        //get the intent from which this activity is called
        intent = getIntent();
        int category_id = intent.getIntExtra("cat_ID",0);
        c = dbhelper.getStudyTips(category_id);
        pageCount = c.getCount();
        c.moveToFirst();

        final PagerAdapter adapter = new StudyTipsPagerAdapter
                (getSupportFragmentManager(), c);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(pageChangeListener);
        setCurrentItem(selectedPosition);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        backgroundImage = (SquareImageView)findViewById(R.id.bkImage);

        //new getStudyTips().execute();

    }// closing onCreate

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            //remove app name from toolbar
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }
/*
    //async task to get stuff from db
    private class getStudyTips extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            //fetch value from key-value pair and pull the respective content
            //from the db
            c = dbhelper.getStudyTips(intent.getIntExtra("studyTipCategory",0));
            pageCount = c.getCount();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            //displaySingleArticle();
        }
    }
    */

    private void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //	page change listener
    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + pageCount);

        //Image image = images.get(position);
        //lblTitle.setText(image.getName());
        //lblDate.setText(image.getTimestamp());
    }

    //pager adapter to represent the study tips row objects

    private class StudyTipsPagerAdapter extends FragmentStatePagerAdapter {
        private Cursor cursorData;

        public StudyTipsPagerAdapter(FragmentManager fm, Cursor cursor) {
            super(fm);
            this.cursorData = cursor;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            DisplayStudyTipsfragment studyTipsfragment = new DisplayStudyTipsfragment();
            Bundle data = new Bundle();

            if (cursorData.moveToPosition(position)){
                String articleTitle = cursorData.getString(1);
                String articleContent = cursorData.getString(3);
                String bgImage = cursorData.getString(2);

                data.putString("Title",articleTitle);
                data.putString("Content",articleContent);
                data.putString("bgImage",bgImage);
                studyTipsfragment.setArguments(data);


            }

            //mViewPager.getAdapter().notifyDataSetChanged();
            return studyTipsfragment;
        }

        @Override
        public int getItemPosition(Object item){
            DisplayStudyTipsfragment studyTipsfragment = (DisplayStudyTipsfragment)item;
            //String title = studyTipsfragment.title;
            int position = cursorData.getPosition();
/*
            if (position >= 0){
                return position;
            }else {
                return POSITION_NONE;
            }
*/
            return ((DisplayStudyTipsfragment) item).getId();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            //return 3;
            return cursorData.getCount();
        }
    }

}
