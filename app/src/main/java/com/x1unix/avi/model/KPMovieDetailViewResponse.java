package com.x1unix.avi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ascii on 27.11.2016.
 */

public class KPMovieDetailViewResponse {
    @SerializedName("resultCode")
    protected int resultCode = 0;

    @SerializedName("message")
    protected String message = "";

    @SerializedName("data")
    protected KPMovie data;

    public KPMovieDetailViewResponse(int code, String msg, KPMovie res) {
        resultCode = code;
        message = msg;
        data = res;
    };

    public KPMovie getResult() {
        return data;
    }

    public String getMessage() {
        return (message == null) ? "" : message;
    }

}
