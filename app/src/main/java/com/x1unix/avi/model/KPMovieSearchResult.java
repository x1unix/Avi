package com.x1unix.avi.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class KPMovieSearchResult {
    @SerializedName("keyword")
    private String keyword;

    @SerializedName("pagesCount")
    private String pagesTotal;

    @SerializedName("searchFilmsCountResult")
    private String itemsTotal;

    @SerializedName("searchFilms")
    private List<KPMovieItem> results = new ArrayList<KPMovieItem>();

    public KPMovieSearchResult(String keyword, String pagesTotal, String itemsTotal, List<KPMovieItem> results) {
        this.keyword = keyword;
        this.pagesTotal = pagesTotal;
        this.itemsTotal = itemsTotal;
        this.results = results;
    }

    public int getTotalPages() {
        return Integer.parseInt(this.pagesTotal);
    }

    public void setTotalPages(String total) {
        this.pagesTotal = total;
    }

    public int getTotalItens() {
        return Integer.parseInt(this.itemsTotal);
    }

    public void setTotalItems(String itemsTotal) {
        this.itemsTotal = itemsTotal;
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
