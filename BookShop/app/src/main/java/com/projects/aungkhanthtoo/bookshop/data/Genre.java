package com.projects.aungkhanthtoo.bookshop.data;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Genre implements Comparable<Genre> {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("genre_name")
    @Expose
    private String genreName;

    public Genre() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    @Override
    public int compareTo(@NonNull Genre genre) {
        return this.genreName.compareTo(genre.genreName);
    }
}