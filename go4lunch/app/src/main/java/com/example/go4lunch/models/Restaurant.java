package com.example.go4lunch.models;

import java.text.DecimalFormat;

public class Restaurant {
    private String placeId;
    private String name;
    private double latitude;
    private double longitude;
    private float distance;
    private String info;
    private String hours;
    private int workmatesCount;
    private double rating;
    private String urlPicture;

    public Restaurant(String placeId, String name, double latitude, double longitude, float distance, String info, String hours, int workmatesCount, double rating, String urlPicture) {
        this.placeId = placeId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.info = info;
        this.hours = hours;
        this.workmatesCount = workmatesCount;
        this.rating = rating;
        this.urlPicture = urlPicture;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public int getWorkmatesCount() {
        return workmatesCount;
    }

    public void setWorkmatesCount(int workmatesCount) {
        this.workmatesCount = workmatesCount;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
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
