package com.x1unix.avi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ascii on 25.11.2016.
 */

public class KPSearchResponse {
    @SerializedName("resultCode")
    protected int resultCode = 0;

    @SerializedName("message")
    protected String message = "";

    @SerializedName("data")
    protected KPMovieSearchResult data;

    public KPSearchResponse(int code, String msg, KPMovieSearchResult res) {
        resultCode = code;
        message = msg;
        data = res;
    }

    public KPMovieSearchResult getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getResultCode() {
        return resultCode;
    }
}
