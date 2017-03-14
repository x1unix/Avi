package com.x1unix.moonwalker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoonVideo {
    private String url;
    private String playerHtml;
    private Grabber grabber;
    private ManifestCollection manifest;
    private MoonSession session;

    private boolean isSerial;
    private int quality = MoonVideo.QUALITY_480P;

    private String selectedSeason;
    private String selectedEpisode;
    private int selectedSeasonIndex;
    private int selectedEpisodeIndex;

    private ArrayList<String> episodes = new ArrayList<String>();
    private ArrayList<String> seasons = new ArrayList<String>();

    public static final int QUALITY_360P = 640;
    public static final int QUALITY_480P = 854;
    public static final int QUALITY_720P = 1280;
    public static final int QUALITY_1080P = 1920;
    public static String IS_SERIAL = "serial";

    public MoonVideo(String playerUrl, Grabber localGrabber) {
        url = playerUrl;
        grabber = localGrabber;
    }

    public MoonVideo fetch() throws IOException, MoonException{
        playerHtml = grabber.getResource(url);
        session = new MoonSession(playerHtml, url)
                .setGrabber(grabber);

        isSerial = (session.getContentType().equals(IS_SERIAL));

        extractConfiguration();

        manifest = session.getPlaylist();

        return this;
    }

    private void extractConfiguration() {
        Document doc = Jsoup.parse(session.getPlayerHtml(), session.getPlayerUrl());
        Elements seasons = doc.select("select[name=season]#season > option");
        Elements episodes = doc.select("select[name=episode]#episode > option");

        selectedSeasonIndex = Parser.extractOptionsFromSelectorNode(seasons, this.seasons);
        selectedEpisodeIndex = Parser.extractOptionsFromSelectorNode(episodes, this.episodes);

        this.selectedSeason = this.seasons.get(selectedSeasonIndex);
        this.selectedEpisode = this.episodes.get(selectedEpisodeIndex);

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

    public String getSeason() {
        return selectedSeason;
    }

    public MoonVideo setSeason(String newSeason) {
        selectedSeason = newSeason;
        return this;
    }

    public String getEpisode() {
        return selectedEpisode;
    }

    public MoonVideo setEpisode(String newEpisode) {
        selectedEpisode = newEpisode;
        return this;
    }

    public List<String> getEpisodes() {
        return episodes;
    }

    public List<String> getSeasons() {
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
