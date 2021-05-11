package com.example.go4lunch.models;

public class Restaurant {
    private String name;
    private double latitude;
    private double longitude;
    private String info;
    private String hours;
    private String distance;
    private String workmate;
    private String rating;
    private String urlPicture;

    public Restaurant(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Restaurant(String name, double latitude, double longitude, String info, String hours, String distance, String workmate, String rating) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.info = info;
        this.hours = hours;
        this.distance = distance;
        this.workmate = workmate;
        this.rating = rating;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getWorkmate() {
        return workmate;
    }

    public void setWorkmate(String workmate) {
        this.workmate = workmate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }
}
