package com.x1unix.moonwalker;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser {
    private static String EXP_PLAYER_URL = "(http:\\/\\/moonwalk.co\\/api\\/iframe\\/?[A-Za-z0-9=&_\\-\\?\\s\\.]+)";

    public static String getFrameUrlFromScript(String scriptHtml) throws MoonException {
        Pattern p = Pattern.compile(Parser.EXP_PLAYER_URL);
        Matcher m = p.matcher(scriptHtml);

        String a = "";
        if (m.find()) {
            a = m.group(1);
        } else {
            throw new MoonException("Failed to extract player's iframe url from script");
        }

        return a;
    }
}
