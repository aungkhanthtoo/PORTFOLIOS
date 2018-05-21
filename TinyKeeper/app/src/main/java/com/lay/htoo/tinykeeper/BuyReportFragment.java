package com.lay.htoo.tinykeeper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lay.htoo.tinykeeper.data.InventoryContract;
import com.lay.htoo.tinykeeper.data.Report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by Lenovo on 10/22/2017.
 */

public class BuyReportFragment extends Fragment {
    List<Report> reports;
    View rootView;
    String mSelection;


    public BuyReportFragment(String selection) {
        mSelection = selection;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.report_list, container, false);

        Cursor cursorPurchases = getContext().getContentResolver().query(
                InventoryContract.PurchaseReport.CONTENT_URI,
                new String[]{InventoryContract.ItemEntry.COLUMN_NAME,
                        "SUM(" + InventoryContract.PurchasesEntry.COLUMN_QUANTITY + ")",
                        "AVG(" + InventoryContract.PurchasesEntry.COLUMN_PRICE + ")",
                        InventoryContract.PurchasesEntry.COLUMN_DATE},
                InventoryContract.PurchasesEntry.COLUMN_DATE + " LIKE ?",
                new String[]{mSelection},
                null
        );
        if (cursorPurchases != null) {
            String report = "";
            reports = new ArrayList<>();
            while (cursorPurchases.moveToNext()) {
                String qty = cursorPurchases.getString(1);
                String info = cursorPurchases.getString(0) + "\n" + qty;
                float total = cursorPurchases.getFloat(2) * Float.parseFloat(qty);
                String date = cursorPurchases.getString(3);
                reports.add(new Report(info, total, date));
                report += info + " --> " + String.valueOf(total) + "\n";
            }
            Log.d(TAG, "Show Purchase Data : " + report);
            cursorPurchases.close();
            setUpChart(rootView, reports);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setUpChart(View rootView, List<Report> reports) {
        List<PieEntry> pieEntries = new ArrayList<>();
        float outcome = 0;
        for (Report report :
                reports) {
            outcome += report.getTotal();
            pieEntries.add(new PieEntry(report.getTotal(), report.getInfo()));
        }
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ReportActivity.MP_CHART_COLORS);
        PieData data = new PieData(dataSet);

        PieChart chart = (PieChart) rootView.findViewById(R.id.pie_chart);
        chart.setData(data);
        chart.animateY(1500);
        Description des = new Description();
        if (mSelection.startsWith("%")) { // %-10-2017
            mSelection = ReportActivity.months[MonthReportActivity.i];
        }
        des.setText(mSelection);
        des.setTextSize(14);
        chart.setDescription(des);
        chart.setDrawHoleEnabled(false);
        chart.setDrawCenterText(true);
        chart.setCenterText(String.format(Locale.getDefault(), "%d Ks",(int)outcome));
        chart.invalidate();
    }


}
