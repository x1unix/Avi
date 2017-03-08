package com.x1unix.moonwalker;

import okhttp3.Call;
import okhttp3.Response;

public class Listener {
    public void onSuccess(String result, Response response) {}

    public void onError(Exception ex, Call call) {}
}
