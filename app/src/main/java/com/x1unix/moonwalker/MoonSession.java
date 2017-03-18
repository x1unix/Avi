package com.x1unix.moonwalker;

import java.io.IOException;
import java.net.URI;

public class MoonSession {
    private String host;
    private String CSRFToken;
    private String playerHtml;
    private String playerUrl;

    private Grabber grabber;
    private ManifestCollection playlist;

    // banners_script_clickunder
    private String videoToken;      // video_token
    private String contentType;     // content_type
    private String key;             // mw_key
    private String pid;             // mw_pid
    private String domainId;        // p_domain_id
    private String versionControl;  // version_control;
    private String megaVersion;     // New header
    private String detectTrue;      // detect_true
    private String adAttr = "0";    // ad_attr (true if AdBlock detected)
    private String debug = "false"; // debug

    // JS / JSON Props
    public static final String JSON_PROP_VIDEO_TOKEN = "video_token";
    public static final String JSON_PROP_CONTENT_TYPE = "content_type";
    public static final String JSON_PROP_KEY = "mw_key";
    public static final String JSON_PROP_PID = "mw_pid";
    public static final String JSON_PROP_P_DOMAIN_ID = "p_domain_id";
    public static final String JSON_PROP_VERSION_CONTROL = "version_control";
    public static final String JSON_PROP_AD_ATTR = "ad_attr";
    public static final String JSON_PROP_DEBUG = "debug";
    public static final String JSON_PROP_DETECT_TRUE = "detect_true";

    public MoonSession(String playerHtmlSource, String playerUri) throws MoonException {
        try {

            URI uri = new URI(playerUri);
            host = uri.getScheme() + "://" + uri.getHost();
            uri = null;

            playerHtml = playerHtmlSource;
            playerUrl = playerUri;

            // versionControl = Parser.getPlayerVersionControl(playerHtml);  - (DEPRECATED ?)
            CSRFToken = Parser.getCSRFToken(playerHtml);
            megaVersion = Parser.getMegaVersion(playerHtml);
            detectTrue = Parser.getDetectTrueValue(playerHtml);

            // Assign props from JS
            contentType = getJsonProp(JSON_PROP_CONTENT_TYPE, true);
            videoToken = getJsonProp(JSON_PROP_VIDEO_TOKEN, true);
            domainId = getJsonProp(JSON_PROP_P_DOMAIN_ID, false);
            pid = getJsonProp(JSON_PROP_PID, false);
            key = getJsonProp(JSON_PROP_KEY, true);;

        } catch (Exception ex) {
            throw new MoonException("Failed to build Moonwalk session: " + ex.getMessage());
        }
    }

    private String getJsonProp(String key, boolean isString) throws MoonException {
        return Parser.getJsonPropertyFromHtml(playerHtml, key, isString);
    }

    public String getUserAgent() {
        return grabber.getUserAgent();
    }

    public String getDetectTrue() {
        return detectTrue;
    }

    public String getMegaVersion() {
        return megaVersion;
    }

    public String getHost() {
        return host;
    }

    public String getCSRFToken() {
        return CSRFToken;
    }

    public String getPlayerHtml() {
        return playerHtml;
    }

    public String getPlayerUrl() {
        return playerUrl;
    }

    public String getVideoToken() {
        return videoToken;
    }

    public String getContentType() {
        return contentType;
    }

    public String getKey() {
        return key;
    }

    public String getPid() {
        return pid;
    }

    public String getDomainId() {
        return domainId;
    }

    public String getVersionControl() {
        return versionControl;
    }

    public String getAdAttr() {
        return adAttr;
    }

    public String isDebug() {
        return debug;
    }

    public MoonSession setGrabber(Grabber grabber) {
        this.grabber = grabber;

        return this;
    }

    public ManifestCollection getPlaylist() throws IOException, MoonException {
        if (playlist == null) {
            String playlistJson = grabber.getPlaylist(this);
            playlist = Parser.getManifestFromJson(playlistJson);

            playlistJson = null;
        }

        return playlist;
    }

    public static MoonSession fromPlayerUrl(String playerUrl, Grabber grabber) throws IOException, MoonException {
        String playerHtml = grabber.getResource(playerUrl);
        return new MoonSession(playerHtml, playerUrl).setGrabber(grabber);
    }

}
