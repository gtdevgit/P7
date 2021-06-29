package com.example.go4lunch.data.googleplace.model.placedetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AddressComponent implements Serializable
{

    @SerializedName("long_name")
    @Expose
    private String longName;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("types")
    @Expose
    private List<String> types = null;
    private final static long serialVersionUID = -3478794137664958642L;

    /**
     * No args constructor for use in serialization
     *
     */
    public AddressComponent() {
    }

    /**
     *
     * @param types
     * @param shortName
     * @param longName
     */
    public AddressComponent(String longName, String shortName, List<String> types) {
        super();
        this.longName = longName;
        this.shortName = shortName;
        this.types = types;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

}
