package com.x1unix.moonwalker;

import java.io.IOException;

public class MoonVideo {
    private String url;
    private String playerHtml;
    private Grabber grabber;
    private ManifestCollection manifest;
    private MoonSession session;

    private boolean isSerial;
    private int quality = MoonVideo.QUALITY_480P;

    private int season;
    private int episode;

    private int[] episodes;
    private int[] seasons;

    public static final int QUALITY_360P = 640;
    public static final int QUALITY_480P = 854;
    public static final int QUALITY_720P = 1280;
    public static final int QUALITY_1080P = 1920;

    public MoonVideo(String playerUrl, Grabber localGrabber) {
        url = playerUrl;
        grabber = localGrabber;
    }

    public MoonVideo fetch() throws IOException, MoonException{
        playerHtml = grabber.getResource(url);
        session = new MoonSession(playerHtml, url)
                .setGrabber(grabber);

        extractConfiguration();

        manifest = session.getPlaylist();

        return this;
    }

    private void extractConfiguration() {

    }

    public String getPlayerHtml() {
        return playerHtml;
    }

    public Grabber getGrabber() {
        return grabber;
    }

    public ManifestCollection getPlaylist() {
        return manifest;
    }

    public MoonSession getSession() {
        return session;
    }

    public boolean isSerial() {
        return isSerial;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int newQuality) {
        quality = newQuality;
    }

    public int getSeason() {
        return season;
    }

    public MoonVideo setSeason(int newSeason) {
        season = newSeason;
        return this;
    }

    public int getEpisode() {
        return episode;
    }

    public MoonVideo setEpisode(int newEpisode) {
        episode = newEpisode;
        return this;
    }

    public int[] getEpisodes() {
        return episodes;
    }

    public int[] getSeasons() {
        return seasons;
    }

    public String getVideoUrl() {
        return url;
    }

    public String getVideoUrl(int season, int episode) {
        return url + "?season=" + season + "&episode=" + episode;
    }

    public String getVideoUrl(int season) {
        return getVideoUrl(season, 1);
    }

    public static MoonVideo from(String playerUrl, Grabber localGrabber) {
        return new MoonVideo(playerUrl, localGrabber);
    }
}
