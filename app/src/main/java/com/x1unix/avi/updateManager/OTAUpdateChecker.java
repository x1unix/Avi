package com.x1unix.avi.updateManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.rollbar.android.Rollbar;
import com.x1unix.avi.R;
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

    public static AlertDialog.Builder makeDialog(final Activity owner, final AviSemVersion newVer) {
        Resources res = owner.getResources();
        AlertDialog.Builder dialInstallUpdate = new AlertDialog.Builder(owner);
        String modConfimText = res.getString(R.string.upd_confirm);
        modConfimText = modConfimText.replace("@version", (newVer.isStable()) ? newVer.toString() : newVer.toString() + " Beta");

        dialInstallUpdate.setMessage(modConfimText);
        dialInstallUpdate.setTitle(res.getString(R.string.upd_new_available))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newVer.getApkUrl()));
                        owner.startActivity(browserIntent);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return dialInstallUpdate;
    }
}
