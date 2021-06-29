package com.example.go4lunch.data.googleplace.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Southwest implements Serializable
{

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    private final static long serialVersionUID = -1219630286090900334L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Southwest() {
    }

    /**
     *
     * @param lng
     * @param lat
     */
    public Southwest(Double lat, Double lng) {
        super();
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Southwest withLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Southwest withLng(Double lng) {
        this.lng = lng;
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.lng == null)? 0 :this.lng.hashCode()));
        result = ((result* 31)+((this.lat == null)? 0 :this.lat.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Southwest) == false) {
            return false;
        }
        Southwest rhs = ((Southwest) other);
        return (((this.lng == rhs.lng)||((this.lng!= null)&&this.lng.equals(rhs.lng)))&&((this.lat == rhs.lat)||((this.lat!= null)&&this.lat.equals(rhs.lat))));
    }

}
