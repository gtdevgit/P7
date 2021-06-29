package com.example.go4lunch.ui.detailrestaurant.viewmodel;

public class CacheDetailRestaurantViewModel {
    private String uid;
    private String placeId;
    private boolean chosenByCurrentUser = false;
    private boolean likedByCurrentUser = false;

    public CacheDetailRestaurantViewModel(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public boolean isChosenByCurrentUser() {
        return chosenByCurrentUser;
    }

    public void setChosenByCurrentUser(boolean chosenByCurrentUser) {
        this.chosenByCurrentUser = chosenByCurrentUser;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }
}
