package com.x1unix.avi.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class KPMovie {
    @SerializedName("filmID")
    public String id;

    @SerializedName("nameRU")
    public String nameRu;

    @SerializedName("nameEN")
    public String nameEn;

    @SerializedName("description")
    public String description;

    @SerializedName("posterURL")
    public String posterUrl;

    @SerializedName("year")
    public String year;

    @SerializedName("filmLength")
    public String duration;

    @SerializedName("county")
    public String country;

    @SerializedName("genre")
    public String genre;

    @SerializedName("rating")
    public String rating;

    public KPMovie(String id, String nameRu, String nameEn, String description, String posterUrl,
                    String year, String duration, String country, String genre, String rating) {
        this.id = id;
        this.nameRu = nameRu;
        this.nameEn = nameEn;
        this.description = description;
        this.posterUrl = posterUrl;
        this.year = year;
        this.duration = duration;
        this.country = country;
        this.genre = genre;
        this.rating = rating;
    }

    public String getTitle() {
        return this.nameRu;
    }

    public String getReleaseDate() {
        return this.year;
    }

    public String getDescription() {
        return this.description;
    }

    public double getVoteAverage() {
        String splited[] = this.rating.split(" ");
        return (splited.length > 0) ? Double.parseDouble(splited[0]) : 0;
    }
}
