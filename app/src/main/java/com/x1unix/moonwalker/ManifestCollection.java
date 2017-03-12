package com.x1unix.moonwalker;

import com.google.gson.annotations.SerializedName;

public class ManifestCollection {
    @SerializedName("manifest_dash")
    private String dash;

    @SerializedName("manifest_f4m")
    private String f4m;

    @SerializedName("manifest_m3u8")
    private String m3u8;

    @SerializedName("manifest_mp4")
    private String mp4;

    public ManifestCollection(String dashUrl, String f4mUrl, String m3u8Url, String mp4Url) {
        dash = dashUrl;
        f4m = f4mUrl;
        m3u8 = m3u8Url;
        mp4 = mp4Url;
    }

    public String getDashManifest() {
        return dash;
    }

    public void setDashManifest(String dash) {
        this.dash = dash;
    }

    public String getF4mManifest() {
        return f4m;
    }

    public void setF4mManifest(String f4m) {
        this.f4m = f4m;
    }

    public String getM3u8Manifest() {
        return m3u8;
    }

    public void setM3u8Manifest(String m3u8) {
        this.m3u8 = m3u8;
    }

    public String getMp4Manifest() {
        return mp4;
    }

    public void setMp4Manifest(String mp4) {
        this.mp4 = mp4;
    }
}
