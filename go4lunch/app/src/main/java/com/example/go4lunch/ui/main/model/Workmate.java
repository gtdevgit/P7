package com.example.go4lunch.ui.main.model;

public class Workmate {
    private String userName;
    private String userUrlPicture;
    private String placeId;
    private String restaurantName;

    public Workmate(String userName, String userUrlPicture, String placeId, String restaurantName) {
        this.userName = userName;
        this.userUrlPicture = userUrlPicture;
        this.placeId = placeId;
        this.restaurantName = restaurantName;
    }

    public String getText(){
        if (restaurantName.isEmpty()) {
            return userName;
        } else {
            return userName + " (" + restaurantName + ")";
        }
    }

    public String getUserUrlPicture() {
        return userUrlPicture;
    }

    public String getPlaceId() {
        return placeId;
    }
}
