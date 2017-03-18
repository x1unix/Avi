package com.x1unix.avi.updateManager;

import org.apache.commons.lang3.time.DateUtils;
import com.x1unix.avi.BuildConfig;
import java.util.Calendar;
import java.util.Date;

public class BuildParser {
    public static boolean compareBuild(Calendar newCal) {
        int build = BuildConfig.VERSION_CODE;
        boolean isNew = false;

        try {
            Calendar cal = BuildParser.getBuildDate();

            long time = cal.getTime().getTime();
            long timeNew = newCal.getTime().getTime();

            isNew = (timeNew > time);
        } catch(Exception ex) {
            isNew = true;
        }

        return isNew;

    }

    public static Calendar getBuildDate() {
        int build = BuildConfig.VERSION_CODE;
        String buildString = String.valueOf(build);
        int year = Integer.valueOf(buildString.substring(0, 4));
        int month = Integer.valueOf(buildString.substring(4, 6));
        int day = Integer.valueOf(buildString.substring(6, 8));

        if (month > 0) month--;

        buildString = null;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        return cal;
    }
}
