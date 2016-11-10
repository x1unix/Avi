package com.x1unix.avi.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class KPMovieSearchResult {
    @SerializedName("keyword")
    private String keyword;

    @SerializedName("pagesCount")
    private String pagesTotal;

    @SerializedName("searchFilms")
    private List<KPMovie> results = new ArrayList<KPMovie>();

    public KPMovieSearchResult(String keyword, String pagesTotal, List<KPMovie> results) {
        this.keyword = keyword;
        this.pagesTotal = pagesTotal;
        this.results = results;
    }

    public int getTotalPages() {
        return Integer.parseInt(this.pagesTotal);
    }

    public void setTotalPages(String total) {
        this.pagesTotal = total;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setResults(List<KPMovie> items) {
        this.results = items;
    }

    public List<KPMovie> getResults() {
        return this.results;
    }

}
