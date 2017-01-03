package com.x1unix.avi.updateManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.rollbar.android.Rollbar;
import com.x1unix.avi.model.AviSemVersion;

public class OTAUpdateChecker {
    public static void checkForUpdates(final OTAStateListener otaEventListener, final boolean allowNightlies) {
        OTARepoClientInterface repoClient = OTARestClient.getClient().create(OTARepoClientInterface.class);
        Call<AviSemVersion> call = repoClient.getLatestRelease();
        call.enqueue(new Callback<AviSemVersion>() {
            @Override
            public void onResponse(Call<AviSemVersion>call, Response<AviSemVersion> response) {
                int statusCode = response.code();
                AviSemVersion receivedVersion = response.body();
                AviSemVersion current = AviSemVersion.getApplicationVersion();

                receivedVersion.apply();

                boolean isNew = current.isYoungerThan(receivedVersion);
                boolean isStable = receivedVersion.isStable();
                boolean isSuitable = (isStable || allowNightlies);

                if (isNew && isSuitable) {
                    otaEventListener.onUpdateAvailable(receivedVersion, current);
                } else {
                    otaEventListener.onUpdateMissing(receivedVersion, current);
                }
            }

            @Override
            public void onFailure(Call<AviSemVersion>call, Throwable t) {
                // Log error here since request failed
                otaEventListener.onError(t);
            }
        });
    }
}
