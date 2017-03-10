package com.x1unix.moonwalker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser {
    private static final String EXP_IFRAME_URL = "(http:\\/\\/moonwalk.co\\/api\\/iframe\\/?[A-Za-z0-9=&_\\-\\?\\s\\.]+)";
    private static final String EXP_PLAYER_FRAME_SERIAL = "(http:\\/\\/moonwalk.cc\\/serial\\/[A-Za-z0-9=&_\\-\\?\\s\\.]+\\/iframe)";

    // Session
    private static final String EXP_PLAYER_CSRF_TOKEN = "<meta name=\"csrf-token\" content=\"([\\S]+)\"";
    private static final String EXP_VERSION_CONTROL = "version_control = '([\\S]+)';";
    private static final String EXP_JSON_STRING_TEMPLATE = "@PROP: '([\\S]+)'";
    private static final String EXP_JSON_INT_TEMPLATE = "@PROP: ([0-9]+)";

    public static String getFrameUrlFromScript(String scriptHtml) throws MoonException {
        String result = match(Parser.EXP_IFRAME_URL, scriptHtml);

        if (isNull(result)) {
            throw new MoonException("Failed to extract iframe url from script");
        }

        return result;
    }

    public static String getPlayerFrameUrlFromHtml(String frameHtml) throws MoonException {
        String result = match(Parser.EXP_PLAYER_FRAME_SERIAL, frameHtml);

        if (isNull(result)) {
            throw new MoonException("Failed to extract player's iframe from root iframe's HTML");
        }

        return result;
    }

    public static String getPlayerVersionControl(String playerHtml) throws MoonException {
        String result = match(Parser.EXP_VERSION_CONTROL, playerHtml);

        if (isNull(result)) {
            throw new MoonException("Failed to extract player's version control");
        }

        return result;
    }

    public static String getCSRFToken(String playerHtml) throws MoonException {
        String result = match(Parser.EXP_PLAYER_CSRF_TOKEN, playerHtml);

        if (isNull(result)) {
            throw new MoonException("Failed to extract CSRF security token");
        }

        return result;
    }


    public static String getJsonPropertyFromHtml(String html, String key, boolean isString) throws MoonException {
        Pattern p = Parser.getJsonPropertyPattern(key, isString);

        String result = match(p, html);

        if (isNull(result)) {
            throw new MoonException("Failed to get value of JSON property {'" + key + "'}");
        }

        return result;
    }

    private static Pattern getJsonPropertyPattern(String key, boolean isString) {
        String tpl = (isString ? Parser.EXP_JSON_STRING_TEMPLATE : Parser.EXP_JSON_INT_TEMPLATE);
        String regex = tpl.replaceAll("@PROP", key);

        tpl = null;
        return Pattern.compile(regex);
    }

    private static boolean isNull(String val) {
        return val == null;
    }

    private static String match(String regexp, String html) {
        Pattern p = Pattern.compile(regexp);
        return match(p, html);
    }

    private static String match(Pattern p, String html) {
        Matcher m = p.matcher(html);

        String result = null;

        if (m.find()) {
            result = m.group(1);
        } else {
            result = null;
        }

        p = null;
        m = null;

        return result;
    }
}
