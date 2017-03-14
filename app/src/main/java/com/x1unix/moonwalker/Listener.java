package com.x1unix.moonwalker;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class Listener {
    public void onSuccess(MoonVideo video, OkHttpClient client) {}

    public void onError(Exception ex) {}
}
