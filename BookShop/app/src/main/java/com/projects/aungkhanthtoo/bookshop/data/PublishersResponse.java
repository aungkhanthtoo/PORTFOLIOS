package com.projects.aungkhanthtoo.bookshop.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PublishersResponse {

    @SerializedName("result_all_publisher")
    @Expose
    private List<Publisher> result = null;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;

    public List<Publisher> getResult() {
        return result;
    }

    public void setResult(List<Publisher> result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}






