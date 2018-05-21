package com.lay.htoo.tinykeeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ReportActivity extends MainMenuActivity {
    public static final String[] months =
            {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
            "November", "December"};

    public static final int[] MP_CHART_COLORS = {rgb("#009246"), rgb("#1a472a"), rgb("#ce2b37"), Color.rgb(207, 248, 246), Color.rgb(148, 212, 212), Color.rgb(136, 180, 187),
            Color.rgb(118, 174, 175), Color.rgb(42, 109, 130),  Color.rgb(217, 80, 138), Color.rgb(254, 149, 7), Color.rgb(254, 247, 120),
            Color.rgb(106, 167, 134), Color.rgb(53, 194, 209), Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53),  Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
            Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                ReportTodayFragment.newInstance(SimplePagerAdapter.CURRENT_DATE))
                .commitNow();

        checkIntent();

    }

    private void checkIntent() {
        String options = getIntent().getStringExtra("picker");
        if (options != null) {
            if (options.equals("date")) {
                showDatePicker();
            } else if (options.equals("month")) {
                showMonthPicker();
            }
        }

    }

    @Override
    public void showDatePicker() {
        super.showDatePicker();
        datePicker.setOnDateSetListener(new DatePickerFragment.OnDateSetListener() {
            @Override
            public void onDateSet(int day, int month, int year) {
                String date = String.format(Locale.getDefault(), "%d-%d-%d", day, month, year);
                Intent intent = new Intent(ReportActivity.this, DateReportActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    void showMonthPicker() {
        final int month = Calendar.getInstance().get(Calendar.MONTH);
        final int year = Calendar.getInstance().get(Calendar.YEAR);
        ArrayList<String> monthLists = new ArrayList<>();
        for(int i= 0; i <= month; i++){
            monthLists.add(months[i]);
        }
      new AlertDialog.Builder(this)
              .setTitle("Choose A Month")
              .setSingleChoiceItems(
                      new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                              monthLists), 0, new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                              int reportMonth = i + 1;
                              String reportOption = "%-" + String.valueOf(reportMonth)+"-"+String.valueOf(year);
                              dialogInterface.dismiss();
                              Intent intent = new Intent(ReportActivity.this, MonthReportActivity.class);
                              intent.putExtra("month", reportOption);
                              intent.putExtra("position", i);
                              startActivity(intent);
                              finish();
                          }
                      }
              )
              .create().show();
    }
    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }
}
