package com.example.go4lunch.ui.detailrestaurant;

public class CacheDetailRestaurantViewModel {
    private String uid;
    private String placeId;
    private boolean chosenByCurrentUser = false;
    private boolean likedByCurrentUser = false;

    public CacheDetailRestaurantViewModel(String uid, String placeId) {
        this.uid = uid;
        this.placeId = placeId;
    }

    public String getUid() {
        return uid;
    }

    public String getPlaceId() {
        return placeId;
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
