package com.x1unix.avi.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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

    @SerializedName("creators")
    private List<KPPeople>[] creators;

    public KPMovie(String ifilmID, String inameRU, String inameEN, String iyear, String ifilmLength,
                   String icountry, String igenre, String idescription, String iratingMPAA,
                   String iratingAgeLimits, String itype, List<KPPeople>[] icreators) {
        filmID = ifilmID;
        nameRU = inameRU;
        nameEN = inameEN;
        year = iyear;
        filmLength = ifilmLength;
        country = icountry;
        genre = igenre;
        description = idescription;
        ratingMPAA = iratingMPAA;
        ratingAgeLimits = iratingAgeLimits;
        type = itype;
        creators = icreators;
    }

    public String getId() {
        return filmID;
    }

    public String getYear() {
        return year;
    }

    public String getFilmLength() {
        return filmLength;
    }

    public String getCountry() {
        return country;
    }

    public String getDescription() {
        return (description == null) ? "" : description;
    }

    public String getGenre() {
        return genre;
    }

    public String getRatingMPAA() {
        return ratingMPAA;
    }

    public String getRatingAgeLimits() {
        return ratingAgeLimits;
    }

    public String getType() {
        return type;
    }

    public List<KPPeople> getDirectors() {
        List<KPPeople> result;
        if ((creators == null) || (creators.length == 0)) {
            result = new ArrayList<KPPeople>();
        } else {
            result = creators[0];
        }
        return result;
    }

    public List<KPPeople> getActors() {
        List<KPPeople> result;
        if ((creators == null) || (creators.length < 2)) {
            result = new ArrayList<KPPeople>();
        } else {
            result = creators[1];
        }
        return result;
    }

    public List<KPPeople> getProducers() {
        List<KPPeople> result;
        if ((creators == null) || (creators.length < 3)) {
            result = new ArrayList<KPPeople>();
        } else {
            result = creators[2];
        }
        return result;
    }

}
