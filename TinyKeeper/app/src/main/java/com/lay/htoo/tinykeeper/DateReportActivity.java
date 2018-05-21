package com.lay.htoo.tinykeeper;

import android.content.Intent;
import android.os.Bundle;

public class DateReportActivity extends MainMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        String date = getIntent().getStringExtra("date");
        getSupportActionBar().setTitle(date);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                ReportTodayFragment.newInstance(date))
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
