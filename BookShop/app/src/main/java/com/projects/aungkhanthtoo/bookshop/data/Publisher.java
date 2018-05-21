package com.projects.aungkhanthtoo.bookshop.data;


import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Publisher implements Comparable<Publisher>{

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("pname")
    @Expose
    private String pname;

    public Publisher() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    @Override
    public int compareTo(@NonNull Publisher publisher) {
        return this.getPname().compareTo(publisher.getPname());
    }
}
