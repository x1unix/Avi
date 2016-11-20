package com.x1unix.avi.model;

import com.google.gson.annotations.SerializedName;

public class KPMovie {
    @SerializedName("filmID")
    private String filmID;

    @SerializedName("nameRU")
    private String nameRU;

    @SerializedName("nameEN")
    private String nameEN;

    @SerializedName("year")
    private String year;

    @SerializedName("filmLength")
    private String filmLength;

    @SerializedName("county")
    private String country;

    @SerializedName("genre")
    private String genre;

    @SerializedName("description")
    private String description;

    @SerializedName("ratingMPAA")
    private String ratingMPAA;

    @SerializedName("ratingAgeLimits")
    private String ratingAgeLimits;

    @SerializedName("type")
    private String type;


}
