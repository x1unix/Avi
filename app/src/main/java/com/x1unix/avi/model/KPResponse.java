package com.x1unix.avi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ascii on 27.11.2016.
 */

public class KPResponse {
    @SerializedName("resultCode")
    protected int resultCode = 0;

    @SerializedName("message")
    protected String message = "";


    protected Object data;

    public KPResponse(int code, String message) {
        this.message = message;
        this.resultCode = code;
    }

    public Object getResult() {
        return data;
    }

    public String getMessage() {
        return (message == null) ? "" : message;
    }

    public int getResultCode() {
        return resultCode;
    }
}
