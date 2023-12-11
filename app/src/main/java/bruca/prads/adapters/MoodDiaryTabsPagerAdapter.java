package bruca.prads.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import bruca.prads.fragments.DiaryCalendarFragment;
import bruca.prads.fragments.DiaryEntriesFragment;
import bruca.prads.fragments.DiaryGraphFragment;

/**
 * Created by Emo on 5/8/2017.

 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class MoodDiaryTabsPagerAdapter extends FragmentPagerAdapter {

    int tabCount;

    public MoodDiaryTabsPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return DiaryEntriesFragment.newInstance();
            case 1:
                return DiaryGraphFragment.newInstance();
            case 2:
                return DiaryCalendarFragment.newInstance();
            default:
                return DiaryEntriesFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        //return 3;
        return tabCount;
    }
}
