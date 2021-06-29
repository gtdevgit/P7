package com.example.go4lunch.data.googleplace.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Geometry implements Serializable
{

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("viewport")
    @Expose
    private Viewport viewport;
    private final static long serialVersionUID = -6855626806320107760L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Geometry() {
    }

    /**
     *
     * @param viewport
     * @param location
     */
    public Geometry(Location location, Viewport viewport) {
        super();
        this.location = location;
        this.viewport = viewport;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Geometry withLocation(Location location) {
        this.location = location;
        return this;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public Geometry withViewport(Viewport viewport) {
        this.viewport = viewport;
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.viewport == null)? 0 :this.viewport.hashCode()));
        result = ((result* 31)+((this.location == null)? 0 :this.location.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Geometry) == false) {
            return false;
        }
        Geometry rhs = ((Geometry) other);
        return (((this.viewport == rhs.viewport)||((this.viewport!= null)&&this.viewport.equals(rhs.viewport)))&&((this.location == rhs.location)||((this.location!= null)&&this.location.equals(rhs.location))));
    }

}
