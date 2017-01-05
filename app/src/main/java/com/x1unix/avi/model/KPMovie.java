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
    private List<KPPeople[]> creators;

    public KPMovie(String ifilmID, String inameRU, String inameEN, String iyear, String ifilmLength,
                   String icountry, String igenre, String idescription, String iratingMPAA,
                   String iratingAgeLimits, String itype, List<KPPeople[]> icreators) {
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
        String value;
        if (ratingMPAA != null) {
            value = ratingMPAA + " (" + ratingAgeLimits + "+)";
        } else if (ratingAgeLimits != null) {
            value = ratingAgeLimits;
        } else {
            value = "-";
        }
        return value;
    }

    public String getRatingAgeLimits() {
        return ratingAgeLimits;
    }

    public String getType() {
        return type;
    }

    public KPPeople[] getDirectors() {
        KPPeople[] result;
        boolean found = false;
        if ((creators == null) || (creators.size() == 0)) {
            result = new KPPeople[]{};
        } else {
            found = true;
            result = creators.get(0);
        }
        return result;
    }

    public KPPeople[] getActors() {
        KPPeople[] result;
        if ((creators == null) || (creators.size() < 2)) {
            result = new KPPeople[]{};
        } else {
            result = creators.get(1);
        }
        return result;
    }

    public KPPeople[] getProducers() {
        KPPeople[] result;
        if ((creators == null) || (creators.size() < 3)) {
            result = new KPPeople[]{};
        } else {
            result = creators.get(2);
        }
        return result;
    }

    public String getNameRU() {
        return this.nameRU;
    }

    public String getNameEN() {
        return this.nameEN;
    }

    public List<KPPeople[]> getCreators() {
        return creators;
    }

}
