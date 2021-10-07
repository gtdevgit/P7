package com.example.gtlabgo4lunch.ui.main.model;

public class SimpleRestaurant {
    private final String placeId;
    private final String name;

    public SimpleRestaurant(String placeId, String name) {
        this.placeId = placeId;
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }
}
