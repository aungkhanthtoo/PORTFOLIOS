package com.lay.htoo.tinykeeper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lenovo on 10/22/2017.
 */

public class SimplePagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 2;
    private String mSelection;
    public static final String CURRENT_DATE = "current";

    public SimplePagerAdapter(FragmentManager fm, String report) {
        super(fm);
        mSelection = report;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        if (mSelection.equals(CURRENT_DATE)) {
            mSelection = getCurrentDate();
        }
        Fragment current;
        switch (position) {
            case 0:
                current = new BuyReportFragment(mSelection);
                break;
            case 1:
                current = new SellReportFragment(mSelection);
                break;
            default:
                current = null;
        }
        return current;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "BUY";
                break;
            case 1:
                title = "SELL";
                break;
            default:
               title = null;
        }
        return title;
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd-MM-yyyy").format(new Date(System.currentTimeMillis()));
    }
}
