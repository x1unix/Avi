package com.x1unix.avi.model;

import com.google.gson.annotations.SerializedName;
import com.x1unix.avi.BuildConfig;

public class AviSemVersion {
    protected int major = 0;
    protected int minor = 0;
    protected int patch = 0;

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

        applyString();
    }

    public AviSemVersion(String semVerString) {
        this.original = semVerString;
        applyString();
    }

    protected void applyString() {
        String[] components = this.original.split(".");
        if (components.length == 3) {
            this.major = Integer.valueOf(components[0]);
            this.minor = Integer.valueOf(components[1]);
            this.patch = Integer.valueOf(components[2]);
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
        return (this.getMajor() < compared.getMajor()) ||
                (this.getMinor() < compared.getMinor()) ||
                (this.getPatch() < compared.getPatch());
    }

    public static AviSemVersion getApplicationVersion() {
        return new AviSemVersion(BuildConfig.VERSION_NAME);
    }

}
