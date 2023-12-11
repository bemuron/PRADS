package bruca.prads.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;

import bruca.prads.fragments.MyMentalHealthFragment;
import bruca.prads.fragments.MyStudiesFragment;

/**
 * Created by Emo on 5/8/2017.

 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class MainTabsPagerAdapter extends FragmentPagerAdapter {

    int tabCount;

    public MainTabsPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return MyMentalHealthFragment.newInstance();
            case 1:
                return MyStudiesFragment.newInstance();
            default:
                return MyMentalHealthFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        //return 3;
        return tabCount;
    }
}
