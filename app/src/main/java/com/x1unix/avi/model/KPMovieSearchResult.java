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
    private List<KPMovieItem> results = new ArrayList<KPMovieItem>();

    public KPMovieSearchResult(String keyword, String pagesTotal, String itemsTotal, List<KPMovieItem> results) {
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

    public void setResults(List<KPMovieItem> items) {
        this.results = items;
    }

    public List<KPMovieItem> getResults() {
        return this.results;
    }

}
