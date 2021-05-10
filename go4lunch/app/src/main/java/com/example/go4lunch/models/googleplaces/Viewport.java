package com.example.go4lunch.models.googleplaces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Viewport implements Serializable
{

    @SerializedName("northeast")
    @Expose
    private Northeast northeast;
    @SerializedName("southwest")
    @Expose
    private Southwest southwest;
    private final static long serialVersionUID = 8897347512861737876L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Viewport() {
    }

    /**
     *
     * @param southwest
     * @param northeast
     */
    public Viewport(Northeast northeast, Southwest southwest) {
        super();
        this.northeast = northeast;
        this.southwest = southwest;
    }

    public Northeast getNortheast() {
        return northeast;
    }

    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }

    public Viewport withNortheast(Northeast northeast) {
        this.northeast = northeast;
        return this;
    }

    public Southwest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Southwest southwest) {
        this.southwest = southwest;
    }

    public Viewport withSouthwest(Southwest southwest) {
        this.southwest = southwest;
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.southwest == null)? 0 :this.southwest.hashCode()));
        result = ((result* 31)+((this.northeast == null)? 0 :this.northeast.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Viewport) == false) {
            return false;
        }
        Viewport rhs = ((Viewport) other);
        return (((this.southwest == rhs.southwest)||((this.southwest!= null)&&this.southwest.equals(rhs.southwest)))&&((this.northeast == rhs.northeast)||((this.northeast!= null)&&this.northeast.equals(rhs.northeast))));
    }

}
