package com.x1unix.avi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ascii on 27.11.2016.
 */

public class KPMovieDetailViewResponse extends KPSearchResponse {
    @SerializedName("data")
    protected KPMovie data;

    public KPMovieDetailViewResponse(int code, String msg, KPMovie res) {
        super(code, msg, null);
        resultCode = code;
        message = msg;
        data = res;
    };

}
