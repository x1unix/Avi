package com.x1unix.moonwalker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser {
    private static String EXP_IFRAME_URL = "(http:\\/\\/moonwalk.co\\/api\\/iframe\\/?[A-Za-z0-9=&_\\-\\?\\s\\.]+)";
    private static String EXP_PLAYER_FRAME_SERIAL = "(http:\\/\\/moonwalk.cc\\/serial\\/[A-Za-z0-9=&_\\-\\?\\s\\.]+\\/iframe)";


    public static String getFrameUrlFromScript(String scriptHtml) throws MoonException {
        Pattern p = Pattern.compile(Parser.EXP_IFRAME_URL);
        Matcher m = p.matcher(scriptHtml);

        String a = "";
        if (m.find()) {
            a = m.group(1);
        } else {
            throw new MoonException("Failed to extract iframe url from script");
        }

        return a;
    }

    public static String getPlayerFrameUrlFromHtml(String frameHtml) throws MoonException {
        Pattern p = Pattern.compile(Parser.EXP_PLAYER_FRAME_SERIAL);
        Matcher m = p.matcher(frameHtml);

        String result = "";
        if (m.find()) {
            result = m.group(1);
        } else {
            throw new MoonException("Failed to extract player's iframe from root iframe's HTML");
        }

        return result;
    }
}
