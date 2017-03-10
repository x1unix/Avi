package com.x1unix.moonwalker;

import java.io.IOException;

public class MoonSession {
    private String CSRFToken;
    private String playerHtml;
    private String playerUrl;

    // banners_script_clickunder
    private String videoToken;      // video_token
    private String contentType;     // content_type
    private String key;             // mw_key
    private String pid;             // mw_pid
    private String pDomainId;       // p_domain_id
    private String versionControl;  // version_control;
    private int adAttr = 0;         // ad_attr (true if AdBlock detected)
    private boolean debug = false;  // debug

    // JS / JSON Props
    public static final String JSON_PROP_VIDEO_TOKEN = "video_token";
    public static final String JSON_PROP_CONTENT_TYPE = "content_type";
    public static final String JSON_PROP_KEY = "mw_key";
    public static final String JSON_PROP_PID = "mw_pid";
    public static final String JSON_PROP_P_DOMAIN_ID = "p_domain_id";
    public static final String JSON_PROP_VERSION_CONTROL = "version_control";
    public static final String JSON_PROP_AD_ATTR = "ad_attr";
    public static final String JSON_PROP_DEBUG = "debug";

    public MoonSession(String playerHtmlSource, String playerUri) throws MoonException {
        try {
            playerHtml = playerHtmlSource;
            playerUrl = playerUri;

            versionControl = Parser.getPlayerVersionControl(playerHtml);
            CSRFToken = Parser.getCSRFToken(playerHtml);

            // Assign props from JS
            contentType = getJsonProp(JSON_PROP_CONTENT_TYPE, true);
            videoToken = getJsonProp(JSON_PROP_VIDEO_TOKEN, true);
            pDomainId = getJsonProp(JSON_PROP_P_DOMAIN_ID, false);
            pid = getJsonProp(JSON_PROP_PID, false);
            key = getJsonProp(JSON_PROP_KEY, true);

        } catch (Exception ex) {
            throw new MoonException("Failed to build Moonwalk session: " + ex.getMessage());
        }
    }

    private String getJsonProp(String key, boolean isString) throws MoonException {
        return Parser.getJsonPropertyFromHtml(playerHtml, key, isString);
    }

    public static MoonSession fromPlayerUrl(String playerUrl, Grabber grabber) throws IOException, MoonException {
        String playerHtml = grabber.getResource(playerUrl);
        return new MoonSession(playerHtml, playerUrl);
    }

}
