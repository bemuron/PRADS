package bruca.prads.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bruca.prads.R;
import bruca.prads.adapters.NotesTabsPagerAdapter;

/**
 * Created by Emo on 6/9/2017.
 */

public class NoteTabsFragment extends Fragment {
    View rootView;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public NoteTabsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NoteTabsFragment newInstance() {
        NoteTabsFragment fragment = new NoteTabsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.note_tabs_fragment, container, false);

        //dbhelper = new FeedReaderDbHelper(this);
        //async to do stuff in background
        //new loadCategories().execute();

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Goals/Objectives"));
        tabLayout.addTab(tabLayout.newTab().setText("Other Notes"));
        //tabLayout.setupWithViewPager(mViewPager);

        // Set up the ViewPager with the MainTabsPagerAdapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.container1);
        final PagerAdapter adapter = new NotesTabsPagerAdapter
                (getChildFragmentManager(),
                        tabLayout.getTabCount());
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener (new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return rootView;

    }
}
