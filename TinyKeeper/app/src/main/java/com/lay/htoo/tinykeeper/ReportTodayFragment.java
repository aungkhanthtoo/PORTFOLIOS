package com.lay.htoo.tinykeeper;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportTodayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportTodayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REPORT = "option";

    // TODO: Rename and change types of parameters
    private String mReportOption;



    public ReportTodayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param report Report Options - today / date / month .
     * @
     * @return A new instance of fragment ReportTodayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportTodayFragment newInstance(String report) {
        ReportTodayFragment fragment = new ReportTodayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REPORT, report);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mReportOption = getArguments().getString(ARG_REPORT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_report_today, container, false);
        // Find the ViewPager that allows the user to swipe between fragments
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        // Create the PagerAdapter that knows which fragment should be shown on each page
        SimplePagerAdapter pagerAdapter = new SimplePagerAdapter(getFragmentManager(), mReportOption);

        // Set Adapter into the ViewPager
        viewPager.setAdapter(pagerAdapter);

        // Find the TabLayout that shows the tabs
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);

        // Connect the TabLayout with ViewPager. This will
        // 1. Update the TabLayout when the ViewPager is swiped
        // 2. Update the ViewPager when the TabLayout is selected
        // 3. Set the Tab names with titles from PagerAdapter by calling getPageTitle()
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

}
