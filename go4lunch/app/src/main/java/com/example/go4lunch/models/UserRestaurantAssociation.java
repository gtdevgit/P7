package com.example.go4lunch.models;

public class UserRestaurantAssociation {
    /**
     * firebase User Id
     */
    private String userUid;
    /**
     * Google restaurant placeId;
     */
    private String placeId;
    /**
     * created date
     */
    private long currentTime;

    /**
     * need no-argument constructor for deserialize firestore document to object
     */
    public UserRestaurantAssociation() {}

    public UserRestaurantAssociation(String userUid, String placeId) {
        this.userUid = userUid;
        this.placeId = placeId;
        currentTime = System.currentTimeMillis();
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
