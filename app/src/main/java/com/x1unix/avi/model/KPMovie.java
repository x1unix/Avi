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

    public KPMovie(String id, String nameRu, String nameEn, String description, String posterUrl,
                    String year, String duration, String country, String genre) {
        this.id = id;
        this.nameRu = nameRu;
        this.nameEn = nameEn;
        this.description = description;
        this.posterUrl = posterUrl;
        this.year = year;
        this.duration = duration;
        this.country = country;
        this.genre = genre;
    }
}
