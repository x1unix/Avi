package com.x1unix.avi.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.x1unix.avi.BuildConfig;
import com.x1unix.avi.updateManager.ISO8601;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.regex.Pattern;

public class AviSemVersion implements Serializable{
    @SerializedName("version")
    protected String original = "0.0.0";

    @SerializedName("stable")
    protected boolean stable = true;

    @SerializedName("tag")
    protected String tag;

    @SerializedName("date")
    protected String date;

    @SerializedName("downs")
    protected int downloadsCount = 0;

    @SerializedName("apk")
    protected String apkUrl;

    @SerializedName("homepage")
    protected String homepage;

    @SerializedName("changelog")
    protected String changelog;

    public AviSemVersion(String semVerString, boolean isStable, String tag, String date,
                         int downs, String apkUrl, String homepage) {
        this.original = semVerString;
        this.stable = isStable;
        this.tag = tag;
        this.date = date;
        this.downloadsCount = downs;
        this.apkUrl = apkUrl;
        this.homepage = homepage;
    }

    public AviSemVersion(String semVerString) {
        this.original = semVerString;
    }

    public String getApkUrl() {
        return this.apkUrl;
    }

    public String getHomePageUrl() {
        return this.homepage;
    }

    public String toString() {
        return this.original;
    }

    public Calendar getReleaseDate() throws ParseException {
        return ISO8601.toCalendar(this.date);
    }

    public boolean hasChangelog() {
        return this.changelog != null;
    }
    public String getChangelog() {
        return this.changelog;
    }

    public String getTag() {
        return this.tag;
    }

    public static AviSemVersion getApplicationVersion() {
        return new AviSemVersion(BuildConfig.VERSION_NAME);
    }

    public boolean isStable() {
        return stable;
    }

}
