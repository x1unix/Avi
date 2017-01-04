package com.x1unix.avi.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.x1unix.avi.BuildConfig;

import java.util.regex.Pattern;

public class AviSemVersion {
    public int major = 0;
    public int minor = 0;
    public int patch = 0;

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

    public AviSemVersion(String semVerString, boolean isStable, String tag, String date,
                         int downs, String apkUrl, String homepage) {
        this.original = semVerString;
        this.stable = isStable;
        this.tag = tag;
        this.date = date;
        this.downloadsCount = downs;
        this.apkUrl = apkUrl;
        this.homepage = homepage;

        apply();
    }

    public AviSemVersion(String semVerString) {
        this.original = semVerString;
        apply();
    }

    public void apply() {
        String[] components = this.original.split(Pattern.quote("."));
        if (components.length == 3) {
            this.major = Integer.parseInt(components[0]);
            this.minor = Integer.parseInt(components[1]);
            this.patch = Integer.parseInt(components[2]);
        }
    }

    public String getApkUrl() {
        return this.apkUrl;
    }

    public String getHomePageUrl() {
        return this.homepage;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getPatch() {
        return this.patch;
    }

    public boolean isYoungerThan(AviSemVersion compared) {

        boolean result = false;
        if (this.major <= compared.major) {
            if (this.major < compared.major) {
                result = true;
            } else {
                if (this.minor <= compared.minor) {
                    if (this.minor < compared.minor) {
                        result = true;
                    } else {
                        if (this.patch < compared.patch) {
                            result = true;
                        }
                    }
                } else {
                    result = false;
                }
            }
        } else {
            result = false;
        }

        return result;
    }

    public static AviSemVersion getApplicationVersion() {
        return new AviSemVersion(BuildConfig.VERSION_NAME);
    }

    public boolean isStable() {
        return stable;
    }

    public String toString() {
        return String.valueOf(major) + "." + String.valueOf(minor) + "." + String.valueOf(patch);
    }

}
