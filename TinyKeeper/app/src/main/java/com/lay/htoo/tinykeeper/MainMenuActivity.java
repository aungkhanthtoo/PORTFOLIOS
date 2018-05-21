package com.lay.htoo.tinykeeper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Lenovo on 10/28/2017.
 */

public class MainMenuActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public DatePickerFragment datePicker;
    MonthPickerFragment monthPicker;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_report_by_date:
                showDatePicker();
                return true;
            case R.id.action_report_by_month:
                showMonthPicker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void showMonthPicker() {
    }

    public void showDatePicker() {
        datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this, String.valueOf(i), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private OnDateSetListener mListener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            return new DatePickerDialog(getActivity(),
                    this,
                    year,
                    month,
                    day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            int month = i1 + 1;
            mListener.onDateSet(i2, month, i);
        }

        public interface OnDateSetListener {
            void onDateSet(int day, int month, int year);
        }

        public void setOnDateSetListener(OnDateSetListener listener) {
            mListener = listener;
        }
    }


    public static class MonthPickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private OnDateSetListener mListener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                    this,
                    year,
                    month,
                    day);
            dialog.setTitle("Choose a day in your month");
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            int month = i1 + 1;
            mListener.onDateSet(i2, month, i);

        }


        public interface OnDateSetListener {
            void onDateSet(int day, int month, int year);
        }

        public void setOnDateSetListener(OnDateSetListener listener) {
            mListener = listener;
        }
    }

}

