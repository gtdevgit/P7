package com.example.go4lunch.models.googleplaces.palcesdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlaceDetails implements Serializable
{

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("result")
    @Expose
    private Result result;
    @SerializedName("status")
    @Expose
    private String status;
    private final static long serialVersionUID = 5809378465647978071L;

    /**
     * No args constructor for use in serialization
     *
     */
    public PlaceDetails() {
    }

    /**
     *
     * @param result
     * @param htmlAttributions
     * @param status
     */
    public PlaceDetails(List<Object> htmlAttributions, Result result, String status) {
        super();
        this.htmlAttributions = htmlAttributions;
        this.result = result;
        this.status = status;
    }

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
