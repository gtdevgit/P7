package com.example.go4lunch.data.googleplace.model.autocomplete;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Autocomplete implements Serializable
{

    @SerializedName("predictions")
    @Expose
    private List<Prediction> predictions = null;
    @SerializedName("status")
    @Expose
    private String status;
    private final static long serialVersionUID = 892035575631774573L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Autocomplete() {
    }

    /**
     *
     * @param predictions
     * @param status
     */
    public Autocomplete(List<Prediction> predictions, String status) {
        super();
        this.predictions = predictions;
        this.status = status;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}