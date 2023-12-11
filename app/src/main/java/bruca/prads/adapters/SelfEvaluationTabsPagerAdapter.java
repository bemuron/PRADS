package bruca.prads.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import bruca.prads.fragments.GraphFragment;
import bruca.prads.fragments.TestEntriesFragment;
import bruca.prads.fragments.TestsFragment;

/**
 * Created by Emo on 5/8/2017.

 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class SelfEvaluationTabsPagerAdapter extends FragmentPagerAdapter {

    int tabCount;

    public SelfEvaluationTabsPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return TestEntriesFragment.newInstance();
            case 1:
                return GraphFragment.newInstance();
            case 2:
                return TestsFragment.newInstance();
            default:
                return TestEntriesFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        //return 3;
        return tabCount;
    }
}
