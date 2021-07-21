package com.example.go4lunch.ui.main.model;

import java.text.DecimalFormat;

public class Restaurant {
    private final String placeId;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final float distance;
    private final String info;
    private final int openNowResourceString;
    private final int workmatesCount;
    private final double rating;
    private final String urlPicture;
    private final int countLike;

    public Restaurant(String placeId, String name, double latitude, double longitude, float distance, String info, int openNowResourceString, int workmatesCount, double rating, String urlPicture, int countLike) {
        this.placeId = placeId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.info = info;
        this.openNowResourceString = openNowResourceString;
        this.workmatesCount = workmatesCount;
        this.rating = rating;
        this.urlPicture = urlPicture;
        this.countLike = countLike;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getDistance() {
        return distance;
    }

    public String getInfo() {
        return info;
    }

    public int getOpenNowResourceString() {
        return openNowResourceString;
    }

    public int getWorkmatesCount() {
        return workmatesCount;
    }

    public double getRating() {
        return rating;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public int getCountLike() {
        return countLike;
    }

    /**
     *
     * @return roundedDistance in meter
     */
    public String getFormatedDistance(){
        // roundedDistance in meter
        DecimalFormat df = new DecimalFormat("#");
        return String.format("%s m", df.format(this.getDistance()));
    }
}
