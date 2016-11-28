package com.x1unix.avi.model;

import com.google.gson.annotations.SerializedName;

public class KPPeople {
    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private String type;

    @SerializedName("nameRU")
    private String nameRU;

    @SerializedName("nameEN")
    private String nameEN;

    @SerializedName("professionKey")
    private String professionKey;



    public KPPeople(String iid, String itype, String inameRU, String inameEN, String iprofessionKey) {
        id = iid;
        type = itype;
        nameRU = inameRU;
        nameEN = inameEN;
        professionKey = iprofessionKey;
    }

    public String getName(String currentLocale) {
        String value;

        try {
            Boolean isSlavic = ( currentLocale.equals("ru") || currentLocale.equals("uk") );
            Boolean isSlavicAvailable = (nameRU != null) || (nameRU.length() > 0);
            Boolean isLatinAvailable = (nameEN != null) || (nameEN.length() > 0);


            if (isSlavic) {
                if (isSlavicAvailable) {
                    value = nameRU;
                } else {
                    value = nameEN;
                }
            } else {
                if (isLatinAvailable) {
                    value = nameEN;
                } else {
                    value = nameRU;
                }
            }
        } catch (Exception ex) {
            value = null;
        }

        return value;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getRole() {
        return (professionKey == null) ? "unknown" : professionKey;
    }

    public boolean isActor() {
        return getRole().equals("actor");
    }

    public boolean isDirector() {
        return getRole().equals("director");
    }

    public boolean isProducer() {
        return getRole().equals("producer");
    }
}
