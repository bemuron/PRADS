package bruca.prads.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import bruca.prads.R;
import bruca.prads.adapters.MoodDiaryTabsPagerAdapter;
import bruca.prads.helpers.SessionManager;
import database.FeedReaderDbHelper;

public class MoodDiaryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static MoodDiaryActivity instance;
    private FeedReaderDbHelper db;
    private SessionManager session;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_diary);
        instance = this;

        //dbhelper = new FeedReaderDbHelper(this);
        //async to do stuff in background
        //new loadCategories().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Mood Diary");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Diary Entries"));
        tabLayout.addTab(tabLayout.newTab().setText("Graph"));
        tabLayout.addTab(tabLayout.newTab().setText("Calendar"));
        //tabLayout.setupWithViewPager(mViewPager);

        // SqLite database handler
        db = new FeedReaderDbHelper(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        //String email = user.get("email");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoodDiaryActivity.this, NewMoodActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set up the ViewPager with the MainTabsPagerAdapter.
        mViewPager = (ViewPager) findViewById(R.id.container_mood_diary);
        mViewPager.setOffscreenPageLimit(2);
        final PagerAdapter adapter = new MoodDiaryTabsPagerAdapter
                (getSupportFragmentManager(),
                        tabLayout.getTabCount());
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener (new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0){
                    fab.setVisibility(View.VISIBLE);
                }else {
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView txtName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.profile_username);
        txtName.setText(name);

    }

    public static MoodDiaryActivity getInstance() {
        return instance;
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);
        session.logoutUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mood_diary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, NewMoodActivity.class);
            startActivity(intent);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.useful_resources) {

            startActivity(new Intent(MoodDiaryActivity.this, UsefulContacts.class));
        }
        else if (id == R.id.useful_contacts){
            startActivity(new Intent(MoodDiaryActivity.this, UsefulResources.class));
        }/*
        else if (id == R.id.about_sets) {
            Toast.makeText(getBaseContext(), "Favs",
                    Toast.LENGTH_SHORT).show();
        }*/
        else if (id == R.id.about_focus) {
            AboutFocus.Show(MoodDiaryActivity.this);

        }/*
        else if (id == R.id.settings) {
            startActivity(new Intent(MoodDiaryActivity.this, SettingsActivity.class));

        }
        */else if (id == R.id.logout) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    }
