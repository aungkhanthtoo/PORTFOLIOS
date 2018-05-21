package com.projects.aungkhanthtoo.bookshop.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Lenovo on 2/22/2018.
 */

public class SearchResponse {

    @SerializedName("result_all_book")
    @Expose
    private List<ResultSearch> result = null;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;

    public List<ResultSearch> getResult() {
        return result;
    }

    public void setResult(List<ResultSearch> result) {
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
