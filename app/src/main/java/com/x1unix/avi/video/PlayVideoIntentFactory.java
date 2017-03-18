package com.x1unix.avi.video;

import com.x1unix.avi.MoviePlayerActivity;
import com.x1unix.avi.PlayerActivity;
import com.x1unix.avi.R;
import com.x1unix.avi.model.KPMovie;
import com.x1unix.avi.model.KPMovieItem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PlayVideoIntentFactory {

    public static Intent getVideoPlayIntent(Activity activity, String kpId, String title, String subtitle) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(activity.getBaseContext());

        boolean useLegacyPlayer = preferences.getBoolean(
                activity.getResources().getString(R.string.prop_use_legacy_player),
                false
        );

        Intent i;

        if (useLegacyPlayer) {
            i = PlayVideoIntentFactory.getLegacyVideoPlayerIntent(activity, kpId, title);
        } else {
            i = PlayVideoIntentFactory.getModernVideoPlayerIntent(activity, kpId, title, subtitle);
        }

        return i;
    }

    public static Intent getVideoPlayIntent(Activity activity, KPMovieItem movie) {
        return PlayVideoIntentFactory.getVideoPlayIntent(
                activity,
                movie.getId(),
                movie.getLocalizedTitle(
                        activity.getResources().getConfiguration().locale.getLanguage()
                ),
                movie.getDescription()
        );
    }

    public static Intent getVideoPlayIntent(Activity activity, KPMovie movie) {
        return PlayVideoIntentFactory.getVideoPlayIntent(
                activity,
                movie.getId(),
                movie.getLocalizedTitle(
                        activity.getResources().getConfiguration().locale.getLanguage()
                ),
                movie.getShortDescription()
        );
    }

    public static Intent getModernVideoPlayerIntent(Activity activity, String kpId, String title, String subtitle) {
        Intent i = new Intent(activity, PlayerActivity.class);
        i.putExtra(PlayerActivity.ARG_KPID, kpId);
        i.putExtra(PlayerActivity.ARG_TITLE, title);
        i.putExtra(PlayerActivity.ARG_DESCRIPTION, subtitle);

        return i;
    }

    public static Intent getLegacyVideoPlayerIntent(Activity activity, String kpId, String title) {
        Intent i = new Intent(activity, MoviePlayerActivity.class);

        // Put id and title
        i.putExtra("movieId", kpId);
        i.putExtra("movieTitle", title);

        return i;
    }
}
