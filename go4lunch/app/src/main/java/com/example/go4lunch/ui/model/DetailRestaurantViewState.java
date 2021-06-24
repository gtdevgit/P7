package com.example.go4lunch.ui.model;

import com.example.go4lunch.ui.model.SimpleUserViewState;

import java.util.List;

public class DetailRestaurantViewState {
    private String placeId;
    private String urlPicture;
    private String name;
    private String info;
    private boolean isChosenByCurrentUser;
    private int star1Color;
    private int star2Color;
    private int star3Color;
    private String phoneNumber;
    private boolean isLikedByCurrentUser;
    private String website;
    private List<SimpleUserViewState> workmates;

    public DetailRestaurantViewState(String placeId, String urlPicture, String name, String info, boolean isChosenByCurrentUser, int star1Color, int star2Color, int star3Color, String phoneNumber, boolean isLikedByCurrentUser, String website, List<SimpleUserViewState> workmates) {
        this.placeId = placeId;
        this.urlPicture = urlPicture;
        this.name = name;
        this.info = info;
        this.isChosenByCurrentUser = isChosenByCurrentUser;
        this.star1Color = star1Color;
        this.star2Color = star2Color;
        this.star3Color = star3Color;
        this.phoneNumber = phoneNumber;
        this.isLikedByCurrentUser = isLikedByCurrentUser;
        this.website = website;
        this.workmates = workmates;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public boolean isChosenByCurrentUser() {
        return isChosenByCurrentUser;
    }

    public int getStar1Color() {
        return star1Color;
    }

    public int getStar2Color() {
        return star2Color;
    }

    public int getStar3Color() {
        return star3Color;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }

    public String getWebsite() {
        return website;
    }

    public List<SimpleUserViewState> getWorkmates() {
        return workmates;
    }
}
