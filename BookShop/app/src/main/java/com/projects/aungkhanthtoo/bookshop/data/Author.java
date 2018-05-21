package com.projects.aungkhanthtoo.bookshop.data;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Author implements Comparable<Author>{

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("author_name")
    @Expose
    private String authorName;

    public Author() {
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public int compareTo(@NonNull Author author) {
        return this.getAuthorName().compareTo(author.getAuthorName());
    }
}

