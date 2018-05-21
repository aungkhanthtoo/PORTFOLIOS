package com.lay.htoo.tinykeeper.data;

/**
 * Created by Lenovo on 10/26/2017.
 */

public class Report {

    private String info;
    private float total;
    private String date;

    public String getInfo() {
        return info;
    }

    public Report(String info, float total, String date) {
        this.info = info;
        this.total = total;
        this.date = date;
    }

    public float getTotal() {
        return total;

    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return info + " --> " + total + " --> " + date;
    }
}
