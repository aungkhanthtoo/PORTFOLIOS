package com.lay.htoo.tinykeeper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class SellReportFragment extends Fragment{

    List<Report> reports;
    View rootView;
    String mSelection;
    public SellReportFragment(String report) {
        mSelection = report;
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
        rootView = inflater.inflate(R.layout.report_list_sell, container, false);

        Cursor cursorSale = getContext().getContentResolver().query(
                InventoryContract.SalReport.CONTENT_URI,
                new String[]{InventoryContract.ItemEntry.COLUMN_NAME,
                        "SUM("+ InventoryContract.SalesEntry.COLUMN_QUANTITY+")",
                        "SUM("+ InventoryContract.SalesEntry.COLUMN_PRICE+")",
                        InventoryContract.SalesEntry.COLUMN_DATE},
                InventoryContract.SalesEntry.COLUMN_DATE + " LIKE ?",
                new String[]{mSelection},
                null
        );


        if (cursorSale != null) {
            String currentSales = "";
            reports = new ArrayList<>();
            while (cursorSale.moveToNext()) {
                currentSales += cursorSale.getString(0) + "     :   " +
                        cursorSale.getString(1) + "     :   " +
                        cursorSale.getString(2) + "     :   " +
                        cursorSale.getString(3) + "\n";
                String info = cursorSale.getString(0) + "\n" + cursorSale.getString(1);
                float total = cursorSale.getFloat(2);
                String date = cursorSale.getString(3);
                reports.add(new Report(info, total, date));
            }
            Log.d(TAG, "showSaleData: " + reports);
            cursorSale.close();
            setUpChart(reports);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setUpChart(List<Report> reports) {
        List<PieEntry> pieEntries = new ArrayList<>();
        float income = 0;
        for (Report report :
                reports) {
            income += report.getTotal();
            pieEntries.add(new PieEntry(report.getTotal(), report.getInfo()));
        }
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ReportActivity.MP_CHART_COLORS);
        PieData data = new PieData(dataSet);
        PieChart chart = (PieChart) rootView.findViewById(R.id.pie_chart);
        chart.setData(data);
        chart.animateX(1500);
        Description des = new Description();
        if (mSelection.startsWith("%")) {
            mSelection = ReportActivity.months[MonthReportActivity.i];
        }
        des.setText(mSelection);
        des.setTextSize(14);
        chart.setDescription(des);
        chart.setDrawCenterText(true);
        chart.setCenterText(String.format(Locale.getDefault(), "%d Ks", (int)income));
        chart.setCenterTextSize(14);
        chart.invalidate();
    }

}
