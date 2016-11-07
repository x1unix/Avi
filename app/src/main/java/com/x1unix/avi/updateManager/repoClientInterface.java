package com.x1unix.avi.updateManager;

import com.x1unix.avi.model.AviSemVersion;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RepoClientInterface {
    @GET("repo/latest-release/")
    Call<AviSemVersion> getLatestRelease();
}
