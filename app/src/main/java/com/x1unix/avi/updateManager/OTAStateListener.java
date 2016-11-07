package com.x1unix.avi.updateManager;

import com.x1unix.avi.model.AviSemVersion;

public class OTAStateListener {
    public OTAStateListener(boolean success, AviSemVersion receivedVersion) {
        if (success) {
            AviSemVersion current = AviSemVersion.getApplicationVersion();
            if (current.isYoungerThan(receivedVersion)) {
                onUpdateAvailable(receivedVersion, current);
            } else {
                onUpdateMissing(receivedVersion, current);
            }
        } else {
            onError();
        }
    }

    protected void onUpdateAvailable(AviSemVersion availableVersion, AviSemVersion currentVersion) {

    }

    protected void onUpdateMissing(AviSemVersion availableVersion, AviSemVersion currentVersion) {

    }

    protected void onError() {

    }
}
