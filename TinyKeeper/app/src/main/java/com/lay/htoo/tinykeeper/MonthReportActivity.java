package com.lay.htoo.tinykeeper;

import android.content.Intent;
import android.os.Bundle;

public class MonthReportActivity extends MainMenuActivity {

    public static int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        String month = getIntent().getStringExtra("month");
        i = getIntent().getIntExtra("position", 0);
        getSupportActionBar().setTitle(ReportActivity.months[i]);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                ReportTodayFragment.newInstance(month))
                .commitNow();
    }

    @Override
    public void showDatePicker() {
       Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("picker", "date");
       startActivity(intent);
        finish();
    }

    @Override
    void showMonthPicker() {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("picker", "month");
        startActivity(intent);
        finish();
    }


}
